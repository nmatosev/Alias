package com.example.alias.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alias.entities.Role;
import com.example.alias.utils.Constants;
import com.example.alias.utils.DatabaseHelper;
import com.example.alias.R;
import com.example.alias.utils.Utilities;
import com.example.alias.entities.Answer;
import com.example.alias.entities.CurrentGameEntity;
import com.example.alias.entities.Player;
import com.example.alias.entities.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameplayActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    Button startButton;
    TextView countDownTextView;
    TextView readerTextView;
    TextView guesserTextView;
    TextView wordTextView;
    TextView scoreTextView;
    TextView scoreTotalTextView;
    private SoundPool soundPool;
    int correctSound, passSound, tickSound, endSound;

    CountDownTimer timer;
    Button correctButton;
    Button passButton;

    List<String> dictionary = new ArrayList<>();

    int roundDuration;
    int resumeFrom;
    int scoreCounter = 0;
    int wordCounter = 0;
    int queue;

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Cannot go back :(", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        soundPool = Utilities.getSoundPool();

        correctSound = soundPool.load(this, R.raw.correct, 1);
        passSound = soundPool.load(this, R.raw.incorrect, 1);
        tickSound = soundPool.load(this, R.raw.tick, 1);
        endSound = soundPool.load(this, R.raw.whistle, 1);
        dictionary = Utilities.getDictionary();
        Log.d("Total word count", "Count: " + dictionary.size());

        SharedPreferences settingsPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("score_prefs", MODE_PRIVATE).edit();
        editor.putInt("score", scoreCounter);
        editor.apply();

        roundDuration = settingsPreferences.getInt("roundDuration", 30);

        readerTextView = (TextView) findViewById(R.id.reader_text_view);
        guesserTextView = (TextView) findViewById(R.id.text_view_guesser);
        correctButton = (Button) findViewById(R.id.button_correct);
        passButton = (Button) findViewById(R.id.button_pass);
        wordTextView = (TextView) findViewById(R.id.text_view_word);
        scoreTextView = (TextView) findViewById(R.id.text_view_score);
        scoreTotalTextView = (TextView) findViewById(R.id.text_view_score_total);

        databaseHelper = new DatabaseHelper(this);

        queue = CurrentGameEntity.getInstance().getTeamQueue();
        final Map<Integer, Team> teams = CurrentGameEntity.getInstance().getTeams();

        Player player1;
        Player player2;

        Log.d("Teams present", " " + teams.entrySet());

        final Team team = teams.get(queue);
        if (team == null) {
            throw new NullPointerException("Team data invalid ot missing");
        }
        player1 = team.getPlayer1();
        player2 = team.getPlayer2();

        setReaderGuesser(player1, player2);

        String totalScore = "Ukupno: " + team.getCurrentScore();
        scoreTotalTextView.setText(totalScore);
        team.getRoundSummary().put(team.getRound(), new ArrayList<>());

        startButton = (Button) findViewById(R.id.button_start);
        countDownTextView = (TextView) findViewById(R.id.text_view_count_down);
        startButton.setOnClickListener(v -> {

            if (startButton.getText().equals("Start")) {

                wordTextView.setText(dictionary.get(wordCounter));
                startButton.setText("Pauza");

                correctButton.setOnClickListener(v1 -> {
                    String currentWord = dictionary.get(wordCounter);
                    Answer answer = new Answer(currentWord, true);
                    Objects.requireNonNull(team.getRoundSummary().get(team.getRound())).add(answer);

                    currentWord = dictionary.get(wordCounter += 1);
                    wordTextView.setText(currentWord);
                    scoreCounter++;
                    soundPool.play(correctSound, 1, 1, 0, 0, 1);

                    Log.i("Current score", "Score " + scoreCounter + " word cnt " + wordCounter);
                    String currentScoreMsg = "Trenutni rezultat: " + scoreCounter;
                    scoreTextView.setText(currentScoreMsg);

                });

                passButton.setOnClickListener(v2 -> {
                    String currentWord = dictionary.get(wordCounter);
                    Answer answer = new Answer(currentWord, false);
                    Objects.requireNonNull(team.getRoundSummary().get(team.getRound())).add(answer);

                    currentWord = dictionary.get(wordCounter += 1);
                    wordTextView.setText(currentWord);
                    scoreCounter--;
                    soundPool.play(passSound, 1, 1, 0, 0, 1);

                    String currentScoreMsg = "Trenutni rezultat " + scoreCounter + " bodova";
                    scoreTextView.setText(currentScoreMsg);

                });
                timerStart(roundDuration);
            } else if (startButton.getText().equals("Pauza")) {
                startButton.setText("Nastavi");
                timerPause();
            } else if (startButton.getText().equals("Nastavi")) {
                startButton.setText("Pauza");
                timerResume();
            }
        });

        toggleRole(player1);
        toggleRole(player2);
    }

    private void timerStart(int roundDuration) {
        timer = new CountDownTimer(roundDuration * 1000, 1000) {
            int counter = roundDuration;

            @Override
            public void onTick(long millisUntilFinished) {
                countDownTextView.setText(String.valueOf(counter));
                if (counter <= 10) {
                    soundPool.play(tickSound, 1, 1, 0, 0, 1);
                }
                Log.d("Counter", "Ticks " + counter);
                counter--;
                resumeFrom = counter;
            }

            @Override
            public void onFinish() {
                Team team1 = CurrentGameEntity.getInstance().getTeams().get(queue);
                if (team1 == null) {
                    throw new RuntimeException("Null pointer exception");
                }

                int total = team1.getCurrentScore() + scoreCounter;
                team1.setCurrentScore(total);
                countDownTextView.setText(Constants.ROUND_FINISHED);
                soundPool.play(endSound, 1, 1, 0, 0, 1);
                startActivity(new Intent(GameplayActivity.this, CurrentScoreActivity.class));

            }

        };
        timer.start();
    }

    public void timerPause() {
        correctButton.setEnabled(false);
        passButton.setEnabled(false);
        timer.cancel();
    }

    private void timerResume() {
        Log.i("Resume from: ", Long.toString(resumeFrom));
        correctButton.setEnabled(true);
        passButton.setEnabled(true);
        timerStart(resumeFrom);
    }


    /**
     * Sets reader/guesser text views in GUI
     *
     * @param player1 Player1 object representation
     * @param player2 Player2 object representation
     */
    private void setReaderGuesser(Player player1, Player player2) {
        String readerText;
        String guesserText;
        if (player1.getRole().equals(Role.READER)) {
            readerText = Constants.READS + player1.getName();
            guesserText = Constants.GUESSES + player2.getName();
        } else {
            readerText = Constants.READS + player2.getName();
            guesserText = Constants.GUESSES + player1.getName();
        }

        readerTextView.setText(readerText);
        guesserTextView.setText(guesserText);
    }

    /**
     * Toggles players role to reader/listener.
     *
     * @param player Player object representation
     */
    private void toggleRole(Player player) {
        if (player.getRole().equals(Role.READER)) {
            player.setRole(Role.LISTENER);
        } else if (player.getRole().equals(Role.LISTENER)) {
            player.setRole(Role.READER);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }


}

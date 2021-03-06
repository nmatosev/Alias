package com.example.alias;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    Button correctButton;
    Button passButton;

    List<String> words = new ArrayList<>();

    int roundDuration;
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

        parseTestFile("res/raw/test.txt");
        words = parseFile(Constants.WORDS_PATH);

        Log.d("Total word count", "Count: " + words.size());

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
        player1 = team.getPlayer1();
        player2 = team.getPlayer2();

        setReaderGuesser(player1, player2);

        String totalScore = "Ukupno: " + team.getCurrentScore();
        scoreTotalTextView.setText(totalScore);
        team.getRoundSummary().put(team.getRound(), new ArrayList<Answer>());

        startButton = (Button) findViewById(R.id.button_start);
        countDownTextView = (TextView) findViewById(R.id.text_view_count_down);
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                wordTextView.setText(words.get(wordCounter));
                startButton.setEnabled(false);

                correctButton.setOnClickListener(v1 -> {
                    String currentWord = words.get(wordCounter);
                    Answer answer = new Answer(currentWord, true);
                    Objects.requireNonNull(team.getRoundSummary().get(team.getRound())).add(answer);

                    currentWord = words.get(wordCounter += 1);
                    wordTextView.setText(currentWord);
                    scoreCounter++;
                    soundPool.play(correctSound, 1, 1, 0, 0, 1);

                    Log.i("Current score", "Score " + scoreCounter + " word cnt " + wordCounter);
                    String currentScoreMsg = "Trenutni rezultat: " + scoreCounter;
                    scoreTextView.setText(currentScoreMsg);

                });

                passButton.setOnClickListener(v2 -> {
                    String currentWord = words.get(wordCounter);
                    Answer answer = new Answer(currentWord, false);
                    Objects.requireNonNull(team.getRoundSummary().get(team.getRound())).add(answer);

                    currentWord = words.get(wordCounter += 1);
                    wordTextView.setText(currentWord);
                    scoreCounter--;
                    soundPool.play(passSound, 1, 1, 0, 0, 1);

                    String currentScoreMsg = "Trenutni rezultat " + scoreCounter + " bodova";
                    scoreTextView.setText(currentScoreMsg);

                });


                new CountDownTimer(roundDuration * 1000, 1000) {
                    int counter = roundDuration;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        countDownTextView.setText(String.valueOf(counter));
                        if (counter <= 10) {
                            soundPool.play(tickSound, 1, 1, 0, 0, 1);
                        }
                        Log.d("Counter", "Ticks " + counter);
                        counter--;
                    }

                    @Override
                    public void onFinish() {
                        Team team = teams.get(queue);
                        int total = team.getCurrentScore() + scoreCounter;
                        team.setCurrentScore(total);
                        countDownTextView.setText(Constants.ROUND_FINISHED);
                        soundPool.play(endSound, 1, 1, 0, 0, 1);
                        startActivity(new Intent(GameplayActivity.this, CurrentScoreActivity.class));

                    }
                }.start();
            }
        });

        toggleRole(player1);
        toggleRole(player2);
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
        if (player1.isReader()) {
            readerText = Constants.READS + player1.getName();
            guesserText = Constants.GUESS + player2.getName();
        } else {
            readerText = Constants.READS + player2.getName();
            guesserText = Constants.GUESS + player1.getName();
        }
        readerTextView.setText(readerText);
        guesserTextView.setText(guesserText);
    }

    /**
     * Toggles players role to reader/guesser.
     *
     * @param player Player object representation
     */
    private void toggleRole(Player player) {
        if (player.isReader()) {
            player.setReader(false);
        } else {
            player.setReader(true);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }


    /**
     * Parses test file in case there is any
     * @param fileName
     */
    private void parseTestFile(String fileName) {

        List<String> words = new ArrayList<>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String lineInFile;
        try {
            while ((lineInFile = reader.readLine()) != null) {
                Log.d("line in file ", lineInFile);
                words.addAll(Arrays.asList(lineInFile.split(",")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Alias words - tests", words.toString());

    }


    /**
     * Parses raw file with questions and stores them in a list.
     *
     * @param fileName source file where questions are stored.
     * @return parsedQuestions
     */
    public List<String> parseFile(String fileName) {
        List<String> words = new ArrayList<>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String lineInFile;
        try {
            while ((lineInFile = reader.readLine()) != null) {
                Log.d("line in file ", lineInFile);
                words.addAll(Arrays.asList(lineInFile.split(",")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Alias words - List size", "words count " + words.size());

        //trims and removes empty strings
        words = words.stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());

        //remove duplicates
        Set<String> removedDuplicates = new HashSet<>(words);

        checkDuplicates(words, removedDuplicates);

        Log.d("Alias words - Set size", "words count " + removedDuplicates.size());

        //clear list and add removed duplicates set to empty list
        words.clear();
        words.addAll(removedDuplicates);
        Collections.shuffle(words);
        return words;
    }

    /**
     * Logs all duplicates
     * @param words
     * @param removedDuplicates
     */
    private void checkDuplicates(List<String> words, Set<String> removedDuplicates) {
        Log.d("Check dupl", "list size " + words.size() + " set size " + removedDuplicates.size());

        final Set<String> duplicates = new HashSet<>();
        final Set<String> checkSet = new HashSet<>();

        for(String word:words){
            word = word.toLowerCase();
            if(!checkSet.add(word)){
                duplicates.add(word);
            }
        }
        Log.d("Alias words - Duplicates", duplicates.toString());
        Log.d("Alias words - Duplicates size", "Duplicates " + duplicates.size());

    }


}

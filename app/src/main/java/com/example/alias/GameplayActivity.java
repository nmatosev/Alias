package com.example.alias;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameplayActivity extends AppCompatActivity {

    private static final String TAG = "GameplayActivity";
    DatabaseHelper databaseHelper;
    Button startButton;
    TextView countDownTextView;
    TextView readerTextView;
    TextView guesserTextView;
    TextView wordTextView;
    TextView scoreTextView;
    private SoundPool soundPool;
    int correctSound, passSound;

    Button correctButton;
    Button passButton;

    List<String> words = new ArrayList<>();

    int roundDuration;
    int scoreCounter = 0;
    int wordCounter = 0;
    int queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            soundPool = new SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build();
        } else {
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        correctSound = soundPool.load(this, R.raw.correct, 1);
        passSound = soundPool.load(this, R.raw.incorrect, 1);

        words = parseFile("res/raw/words.txt");

        parseDictionary();

        Collections.shuffle(words);
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

        databaseHelper = new DatabaseHelper(this);

        queue = CurrentGameEntity.getInstance().getTeamQueue();
        final List<Team> teams = CurrentGameEntity.getInstance().getTeams();
        String player1;
        String player2;
        final Team team = teams.get(queue);
        player1 = team.getPlayers().get(0);
        player2 = team.getPlayers().get(1);

        readerTextView.setText("Čita: " + player1);
        guesserTextView.setText("Odgovara : " + player2);
        team.getRoundSummary().put(team.getRound(), new ArrayList<Answer>());

        startButton = (Button) findViewById(R.id.button_start);
        countDownTextView = (TextView) findViewById(R.id.text_view_count_down);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordTextView.setText(words.get(wordCounter));

                correctButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currentWord = words.get(wordCounter += 1);
                        wordTextView.setText(currentWord);
                        scoreCounter++;
                        soundPool.play(correctSound, 1,1,0, 0,1 );

                        Answer answer = new Answer(currentWord,true);
                        team.getRoundSummary().get(team.getRound()).add(answer);
                        Log.i("Current score", "Score " + scoreCounter + " word cnt " + wordCounter);
                        scoreTextView.setText("Trenutni rezultat " + scoreCounter + " bodova");

                    }
                });

                passButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currentWord = words.get(wordCounter += 1);

                        wordTextView.setText(words.get(wordCounter += 1));
                        Answer answer = new Answer(currentWord,false);
                        team.getRoundSummary().get(team.getRound()).add(answer);

                        scoreCounter--;
                        soundPool.play(passSound, 1,1,0, 0,1 );
                        scoreTextView.setText("Trenutni rezultat " + scoreCounter + " bodova");

                    }
                });


                new CountDownTimer(roundDuration * 1000, 1000) {
                    int counter = roundDuration;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        countDownTextView.setText(String.valueOf(counter));
                        counter--;
                    }

                    @Override
                    public void onFinish() {
                        int total = teams.get(queue).getCurrentScore() + scoreCounter;
                        teams.get(queue).setCurrentScore(total);
                        String player1 = teams.get(queue).getPlayers().get(0);
                        teams.get(queue).getPlayers().remove(0);
                        teams.get(queue).getPlayers().add(player1);
                        countDownTextView.setText("Gotova runda!");
                        startActivity(new Intent(GameplayActivity.this, CurrentScoreActivity.class));
                    }
                }.start();
            }
        });
    }

    private void showToastMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }


    /**
     * Parses raw file with questions and stores them in a list.
     *
     * @param fileName source file where questions are stored.
     * @return parsedQuestions
     */
    public List<String> parseFile(String fileName) {
        List<String> words = new ArrayList<String>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String lineInFile;
        try {
            while ((lineInFile = reader.readLine()) != null) {
                words.addAll(Arrays.asList(lineInFile.split(",")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TO DO:smanjit vracenu listu
        Collections.shuffle(words);
        return words;
    }

    /**
     * Helper method for dictionary file parsing
     */
    private void parseDictionary() {
        String patternDict = "(.+)";

        List<String> matchedWords = new ArrayList<>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("res/raw/bujica.txt");

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String st;

            Pattern pattern = Pattern.compile(patternDict);

            while ((st = br.readLine()) != null) {
                Log.i("line ", st.trim());

                Matcher matcher = pattern.matcher(st.trim());
                if (matcher.matches()) {
                    //Log.i("line M", st.trim());
                    String matched = matcher.group(1);
                    matchedWords.add(matched);
                }


                Log.i("mtc size", "" + matchedWords.size());
                Log.i("fls", matchedWords.toString());

                int cnt = 0;
                List<String> first = new ArrayList<>();
                List<String> sc = new ArrayList<>();
                List<String> thr = new ArrayList<>();

                for (String wr : matchedWords) {
                    if (cnt < 400) {
                        first.add(wr);
                    } else if (cnt >= 400 && cnt <= 800) {
                        sc.add(wr);
                    } else if (cnt > 800) {
                        thr.add(wr);
                    }
                    cnt++;
                }
                Log.i("fr", first.toString());
                Log.i("sc", sc.toString());
                Log.i("th", thr.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.example.alias;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GameplayActivity extends AppCompatActivity {

    private static final String TAG ="GameplayActivity";
    DatabaseHelper databaseHelper;
    Button startButton;
    TextView countDownTextView;
    TextView readerTextView;
    TextView guesserTextView;
    TextView wordTextView;
    TextView scoreTextView;

    Button correctButton;
    Button passButton;
    int scoreCounter = 0;
    int wordCounter = 0;
    List<String> words = Arrays.asList("čudnovati kljunaš","čubasti gnjurac",
            "logaritamska nejednadžba", "Žigmund Luksemburški" ,"Gryffindor", "Lord Voldemort",
            "Harry Potter", "Bijelo Dugme", "Lara", "zimzeleni jasmin");
    int roundDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        Collections.shuffle(words);
        SharedPreferences.Editor editor = getSharedPreferences("settings_prefs", MODE_PRIVATE).edit();
        SharedPreferences settingsPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE);
        roundDuration = settingsPreferences.getInt("roundDuration", 30);


        readerTextView = (TextView) findViewById(R.id.reader_text_view);
        guesserTextView = (TextView) findViewById(R.id.text_view_guesser);
        correctButton = (Button) findViewById(R.id.button_correct);
        passButton = (Button) findViewById(R.id.button_pass);
        wordTextView = (TextView) findViewById(R.id.text_view_word);
        scoreTextView = (TextView) findViewById(R.id.text_view_score);

        databaseHelper = new DatabaseHelper(this);
        Cursor data = databaseHelper.getData();
        final ArrayList<String> playerList = new ArrayList<>();

        if(data.getCount()==0){
            Toast.makeText(this, "DB was empty!", Toast.LENGTH_SHORT).show();
        } else{
            while (data.moveToNext()){
                playerList.add(data.getString(1));
            }
        }
        readerTextView.setText("Čita: " + playerList.get(0));
        guesserTextView.setText("Odgovara : " + playerList.get(1));

        startButton = (Button) findViewById(R.id.button_start);
        countDownTextView = (TextView) findViewById(R.id.text_view_count_down);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordTextView.setText(words.get(wordCounter));

                correctButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wordTextView.setText(words.get(wordCounter));
                        scoreCounter++;
                        wordCounter++;
                    }
                });

                passButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wordTextView.setText(words.get(wordCounter));
                        scoreCounter--;
                        wordCounter++;
                    }
                });

                new CountDownTimer(roundDuration*1000,1000) {
                    int counter = roundDuration;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        countDownTextView.setText(String.valueOf(counter));
                        counter--;
                    }
                    @Override
                    public void onFinish() {
                        countDownTextView.setText("Gotova runda!");
                        startActivity(new Intent(GameplayActivity.this, CurrentScoreActivity.class));
                    }
                }.start();
            }
        });
    }

    private void showToastMsg(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

package com.example.alias;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    EditText roundDurationEditText;
    EditText scoreToWinEditText;
    Button saveChangesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        saveChangesButton = (Button) findViewById(R.id.button_save);

        roundDurationEditText = (EditText) findViewById(R.id.round_duration_edit_text);
        scoreToWinEditText = (EditText) findViewById(R.id.score_to_win_edit_text);

        final int roundDuration = Integer.parseInt(roundDurationEditText.getText().toString());
        final int scoreToWin = Integer.parseInt(scoreToWinEditText.getText().toString());

        SharedPreferences.Editor editor = getSharedPreferences("settings_prefs", MODE_PRIVATE).edit();
        editor.putInt("roundDuration", roundDuration);
        editor.putInt("scoreToWin", scoreToWin);
        editor.apply();

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roundDuration>300 || scoreToWin>300){
                    showToastMsg("Trajanje runde mora biti manje od 5 minuta i bodovi za pobjedu moraju biti do 300.");
                } else{
                    showToastMsg("Promjene spremljene!");
                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                }
            }
        });
    }

    private void showToastMsg(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}

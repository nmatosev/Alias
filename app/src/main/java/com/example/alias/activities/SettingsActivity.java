package com.example.alias.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alias.R;

public class SettingsActivity extends AppCompatActivity {

    EditText roundDurationEditText;
    EditText scoreToWinEditText;
    Button saveChangesButton;
    int roundDuration = 30;
    int scoreToWin = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        saveChangesButton = (Button) findViewById(R.id.button_save);

        roundDurationEditText = (EditText) findViewById(R.id.round_duration_edit_text);
        scoreToWinEditText = (EditText) findViewById(R.id.score_to_win_edit_text);

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roundDuration = Integer.parseInt(roundDurationEditText.getText().toString());
                scoreToWin = Integer.parseInt(scoreToWinEditText.getText().toString());

                SharedPreferences.Editor editor = getSharedPreferences("settings_prefs", MODE_PRIVATE).edit();
                editor.putInt("roundDuration", roundDuration);
                editor.putInt("scoreToWin", scoreToWin);
                editor.apply();

                Log.i("score", "" + scoreToWin);
                Log.i("dur", "" + roundDuration);
                if (roundDuration > 300 || scoreToWin > 300 || roundDuration < 10 || scoreToWin < 10) {

                    showToastMsg("Trajanje runde mora biti manje od 10 sekundi do 5 minuta i bodovi za pobjedu moraju biti od 10 do 300.");
                } else {
                    showToastMsg("Promjene spremljene!");
                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                }
            }
        });
    }

    private void showToastMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}

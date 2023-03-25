package com.example.alias.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alias.utils.DatabaseHelper;
import com.example.alias.R;

public class PopUpActivity extends Activity {

    String firstPlayerName;
    String secondPlayerName;
    EditText firstPlayerEditText;
    EditText secondPlayerEditText;
    DatabaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_window);
        firstPlayerEditText = (EditText) findViewById(R.id.first_player_edit_text);
        secondPlayerEditText = (EditText) findViewById(R.id.second_player_edit_text);

        dataBaseHelper = new DatabaseHelper(this);
        Button saveButton = (Button) findViewById(R.id.saveButton);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.6));
        saveButton.setOnClickListener(v -> {
            saveTeam();
        });
    }

    private void saveTeam() {
        firstPlayerName = firstPlayerEditText.getText().toString();
        secondPlayerName = secondPlayerEditText.getText().toString();
        if (firstPlayerName.equals("") || secondPlayerName.equals("")) {
            showToastMsg("You must write name!");
        } else {
            addTeam(firstPlayerName, secondPlayerName);
            startActivity(new Intent(PopUpActivity.this, PairUpPlayersActivity.class));
        }
    }

    private void addTeam(String firstPlayerName, String secondPlayerName) {
        boolean inserted = dataBaseHelper.addTeamData(firstPlayerName, secondPlayerName);
        if (inserted) {
            showToastMsg("Team successfully inserted");
        } else {
            showToastMsg("Something went wrong.");
        }
    }


    private void showToastMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

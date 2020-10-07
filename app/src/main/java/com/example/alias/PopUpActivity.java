package com.example.alias;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PopUpActivity extends Activity {

    String playerName;
    EditText playerEditText;
    DatabaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_window);
        playerEditText = (EditText) findViewById(R.id.first_player_edit_text);
        dataBaseHelper = new DatabaseHelper(this);
        Button saveButton = (Button) findViewById(R.id.saveButton);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.6));
        saveButton.setOnClickListener(v -> {
            playerName = playerEditText.getText().toString();
            if (playerName.equals("")) {
                showToastMsg("You must write name!");
            } else {
                addData(playerName);
                startActivity(new Intent(PopUpActivity.this, PairUpPlayersActivity.class));
            }
        });
    }

    /**
     * Adds new player to DB
     *
     * @param newEntry
     */
    public void addData(String newEntry) {
        boolean insertData = dataBaseHelper.addPlayerData(newEntry);
        if (insertData) {
            showToastMsg("Player successfuly inserted");
        } else {
            showToastMsg("Something went wrong.");
        }
    }

    private void showToastMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

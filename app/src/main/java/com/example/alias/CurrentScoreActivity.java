package com.example.alias;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CurrentScoreActivity extends AppCompatActivity {

    Button buttonNextPair;
    TextView roundTextView;
    TextView teamResultTextView;
    String winners = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_score);
        buttonNextPair = (Button) findViewById(R.id.button_next_pair);
        roundTextView = (TextView) findViewById(R.id.round_text_view);
        teamResultTextView = (TextView) findViewById(R.id.pair_result_text_view);

        int queue = CurrentGameEntity.getInstance().getTeamQueue();
        Team team = CurrentGameEntity.getInstance().getTeams().get(queue);
        String reader = team.getPlayers().get(0);
        String listener = team.getPlayers().get(1);
        String teamName = team.getTeamName();

        roundTextView.setText("Runda: " + team.getRound());
        int currentRound = team.getRound() + 1;
        team.setRound(currentRound);
        String roundSummary = teamName + " - trenutno stanje bodova " + team.getCurrentScore();
        boolean finished = false;

        if (checkIfThereIsAWinner()) {
            finished = true;
            teamResultTextView.setText("Pobjednici " +winners + " vječna vam slava i hvala! ");
        } else {
            teamResultTextView.setText(roundSummary);

        }

        queue += 1;
        if (queue >= CurrentGameEntity.getInstance().getTeams().size()) {
            CurrentGameEntity.getInstance().setTeamQueue(0);
        } else {
            CurrentGameEntity.getInstance().setTeamQueue(queue);
        }

        if (finished) {

            buttonNextPair.setText("Glavni meni");
            buttonNextPair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CurrentScoreActivity.this, MainActivity.class));
                }
            });

        } else {
            buttonNextPair.setText("Nastavi");
            buttonNextPair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CurrentScoreActivity.this, GameplayActivity.class));
                }
            });
        }

    }

    /**
     * There are two conditions in order to be a winner:
     *      - Have the equal or grater score than scoreToWin variable value
     *      - Have equal number of rounds as any other team
     * @return
     */
    private boolean checkIfThereIsAWinner() {
        SharedPreferences settingsPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE);
        int scoreToWin = settingsPreferences.getInt("scoreToWin", 30);

        for (Team team : CurrentGameEntity.getInstance().getTeams()) {
            Team currentTeam = team;

            if (team.getCurrentScore() >= scoreToWin) {
                for (Team teamRound : CurrentGameEntity.getInstance().getTeams()) {
                    if (currentTeam.getRound() > teamRound.getRound()){
                        return false;
                    }
                }
                winners = team.getTeamName();
                return true;
            }
        }
        return false;
    }

    /**
     * Check other teams rounds.
     *
     * @param currentTeamRound
     * @return
     */
    private boolean checkIfRoundNumberEqual(int currentTeamRound) {
        for (Team team : CurrentGameEntity.getInstance().getTeams()) {
            if (team.getRound() < currentTeamRound) {
                return false;
            }
        }
        return true;
    }

}

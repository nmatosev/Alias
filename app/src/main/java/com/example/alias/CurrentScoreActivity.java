package com.example.alias;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CurrentScoreActivity extends AppCompatActivity {

    Button buttonNextPair;
    TextView teamResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_score);
        buttonNextPair = (Button) findViewById(R.id.button_next_pair);
        teamResultTextView = (TextView) findViewById(R.id.pair_result_text_view);

        int queue = CurrentGameEntity.getInstance().getTeamQueue();
        Team team = CurrentGameEntity.getInstance().getTeams().get(queue);
        if(team.getCurrentScore()<30){
            teamResultTextView.setText("par " + team.getPlayers() + " - bodovi: "+ team.getCurrentScore());
        } else {
            teamResultTextView.setText("YEEY WINNERS par " + team.getPlayers() + " - bodovi: "+ team.getCurrentScore());
        }


        queue+=1;
        if(queue >= CurrentGameEntity.getInstance().getTeams().size()){
            CurrentGameEntity.getInstance().setTeamQueue(0);
        }else{
            CurrentGameEntity.getInstance().setTeamQueue(queue);
        }

        buttonNextPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CurrentScoreActivity.this, GameplayActivity.class));
            }
        });

    }

}

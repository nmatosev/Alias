package com.example.alias.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alias.R;
import com.example.alias.entities.CurrentGameEntity;
import com.example.alias.entities.Team;

import java.util.ArrayList;
import java.util.List;

public class FinalActivity extends AppCompatActivity {

    TeamsSummaryAdapter teamsSummaryAdapter;
    TextView textViewPairs;
    List<Team> teamsList = new ArrayList<>();
    Button mainActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        mainActivityButton = (Button) findViewById(R.id.button_main_menu);
        teamsList = new ArrayList<>(CurrentGameEntity.getInstance().getTeams().values());

        final ListView teamsListView = (ListView) findViewById(R.id.summary_teams_list_view);
        teamsSummaryAdapter = new TeamsSummaryAdapter(this, teamsList);
        teamsListView.setAdapter(teamsSummaryAdapter);
        mainActivityButton.setOnClickListener(v -> startActivity(new Intent(FinalActivity.this, MainActivity.class)));
    }

    class TeamsSummaryAdapter extends ArrayAdapter<Team> {

        Context context;
        List<Team> pairs;

        TeamsSummaryAdapter(Context c, List<Team> pairs) {
            super(c, R.layout.pairs_row, R.id.player1_text_view, pairs);
            this.context = c;
            this.pairs = pairs;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.teams_summary_row, parent, false);

            textViewPairs = row.findViewById(R.id.text_view_pairs);
            Team team = pairs.get(position);
            String teamFinalSummary =  team.getTeamName() + " - Bodovi: " + team.getFinalScore();
            textViewPairs.setText(teamFinalSummary);

            return row;
        }
    }
}

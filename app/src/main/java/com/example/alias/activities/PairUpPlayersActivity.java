package com.example.alias.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alias.entities.Role;
import com.example.alias.utils.DatabaseHelper;
import com.example.alias.R;
import com.example.alias.entities.CurrentGameEntity;
import com.example.alias.entities.Player;
import com.example.alias.entities.Team;

import java.util.ArrayList;
import java.util.List;

public class PairUpPlayersActivity extends AppCompatActivity {

    Button startGameButton;
    DatabaseHelper databaseHelper;
    ImageView trashIcon;
    ArrayList<String> teamsList = new ArrayList<>();
    EditText roundDurationEditText;
    EditText scoreToWinEditText;
    int roundDuration = 60;
    int scoreToWin = 30;

    TeamsAdapter teamsAdapter;
    TextView textViewPairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_up_players);

        roundDurationEditText = (EditText) findViewById(R.id.round_duration_edit_text);
        scoreToWinEditText = (EditText) findViewById(R.id.score_to_win_edit_text);

        databaseHelper = new DatabaseHelper(this);
        Cursor teamsTable = databaseHelper.getTeams();

        fillTeamList(teamsTable, teamsList);

        final ListView teamsListView = (ListView) findViewById(R.id.teams_list_view);

        teamsAdapter = new TeamsAdapter(this, teamsList);
        teamsListView.setAdapter(teamsAdapter);

        Button createNewTeam = findViewById(R.id.create_new_team_button);

        createNewTeam.setOnClickListener(v -> {
            startActivity(new Intent(PairUpPlayersActivity.this, PopUpActivity.class));
            teamsAdapter.notifyDataSetChanged();
        });

        startGameButton = (Button) findViewById(R.id.start_game);
        startGameButton.setOnClickListener(v -> {
            if (!settingsValidated()) {
                return;
            }

            teamsAdapter.notifyDataSetChanged();
            Log.d("Teams available ", " " + teamsTable.getCount());
            if (teamsAdapter.pairs.isEmpty()) {
                showToastMsg("Please create at least one team.");
                return;
            }
            startActivity(new Intent(PairUpPlayersActivity.this, GameplayActivity.class));
        });

    }

    private boolean settingsValidated() {

        roundDuration = Integer.parseInt(roundDurationEditText.getText().toString());
        scoreToWin = Integer.parseInt(scoreToWinEditText.getText().toString());

        Log.i("Score to win", " " + scoreToWin);
        Log.i("Round duration", " " + roundDuration);
        if (roundDuration > 300 || roundDuration < 10) {
            showToastMsg("Trajanje runde mora biti od 10 sekundi do 5 minuta.");
            return false;
        }
        if (scoreToWin > 300 || scoreToWin < 10) {
            showToastMsg("Bodovi za pobjedu moraju biti od 10 do 300.");
            return false;
        }

        SharedPreferences.Editor editor = getSharedPreferences("settings_prefs", MODE_PRIVATE).edit();
        editor.putInt("roundDuration", roundDuration);
        editor.putInt("scoreToWin", scoreToWin);
        editor.apply();
        return true;
    }


    private void fillTeamList(Cursor table, List<String> list) {
        if (table.getCount() == 0) {
            Log.d("Teams", "No teams in DB");
        } else {
            int teamId = 0;
            while (table.moveToNext()) {
                String teamName = table.getString(1) + "&" + table.getString(2);
                Player player1 = new Player(table.getString(1), Role.READER);
                Player player2 = new Player(table.getString(2), Role.LISTENER);

                Team team = new Team(player1, player2, teamName);

                CurrentGameEntity.getInstance().getTeams().put(teamId, team);
                list.add(teamName);
                teamId++;
            }
        }
    }


    /**
     * Shows toast message received as an argument.
     *
     * @param message to show
     */
    private void showToastMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    class TeamsAdapter extends ArrayAdapter<String> {

        Context context;
        private List<String> pairs;

        TeamsAdapter(Context c, List<String> pairs) {
            super(c, R.layout.pairs_row, R.id.player1_text_view, pairs);
            this.context = c;
            this.pairs = pairs;
        }

        public List<String> getPairs() {
            return pairs;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.pairs_row, parent, false);

            textViewPairs = row.findViewById(R.id.text_view_pairs);
            trashIcon = row.findViewById(R.id.image_view_trash);

            Log.i("pairs ", pairs.toString());
            textViewPairs.setText("par: " + pairs.get(position));

            trashIcon.setOnClickListener(v -> new AlertDialog.Builder(PairUpPlayersActivity.this)
                    .setIcon(android.R.drawable.ic_delete)
                    .setTitle("Delete item")
                    .setMessage("Želiš li izbrisati ovaj tim?")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseHelper.deleteTeams(pairs.get(position));
                            pairs.remove(position);
                            teamsAdapter.notifyDataSetChanged();
                        }
                    }).setNegativeButton("Ne", null).show());

            return row;

        }
    }

}

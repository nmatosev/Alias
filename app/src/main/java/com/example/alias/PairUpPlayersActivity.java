package com.example.alias;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PairUpPlayersActivity extends AppCompatActivity {

    Button startGameButton;
    private static final String TAG ="GameplayActivity";
    DatabaseHelper databaseHelper;
    TextView playerTextView;
    ImageView trashIcon;
    ArrayList<String> playerList = new ArrayList<>();
    ArrayList<String> teamsList = new ArrayList<>();

    PlayersAdapter playersAdapter;
    TeamsAdapter teamsAdapter;
    CheckBox pairCheckBox;
    Button pairUpButton;
    TextView textViewPairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_up_players);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
        Cursor playerTable = databaseHelper.getPlayers();
        Cursor teamsTable = databaseHelper.getTeams();

        fillPlayerList(playerTable, playerList, "player");
        fillTeamList(teamsTable, teamsList, "team");


        Log.d("show db", databaseHelper.getPlayers().toString());
        Log.d("Players ", playerList.toString());
        Log.d("teams ", teamsList.toString());

        final ListView playersListView = (ListView) findViewById(R.id.players_list_view);
        final ListView teamsListView = (ListView) findViewById(R.id.teams_list_view);

        playersAdapter = new PlayersAdapter(this, playerList);
        playersListView.setAdapter(playersAdapter);

        teamsAdapter = new TeamsAdapter(this, teamsList);
        teamsListView.setAdapter(teamsAdapter);

        pairUpButton = (Button) findViewById(R.id.pair_players_button);
        Button createNewPlayerButton =  findViewById(R.id.create_new_player_button);

        createNewPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PairUpPlayersActivity.this, PopUpActivity.class));
            }
        });


        pairUpButton.setOnClickListener(new View.OnClickListener() {
            View row;
            int pairId = 0;

            @Override
            public void onClick(View v) {
                int selectedPlayersCount = 0;
                for(int i = 0; i < playersListView.getCount(); i++) {
                    row = playersListView.getChildAt(i);
                    CheckBox checkBox = row.findViewById(R.id.check_box_pair);
                    if(checkBox.isChecked()){
                        selectedPlayersCount++;
                    }
                }

                if(selectedPlayersCount<2){
                    showToastMsg("Manje od 2 igrača odabrana. Odaberi 2 igrača! " + selectedPlayersCount);
                } else if(selectedPlayersCount>2){
                    showToastMsg("Više od 2 igrača odabrana. Odaberi 2 igrača! " + selectedPlayersCount);
                } else {
                    LinkedList<String> joined = new LinkedList<>();

                    for(int i = 0; i < playersListView.getCount(); i++) {
                        row = playersListView.getChildAt(i);
                        CheckBox checkBox = row.findViewById(R.id.check_box_pair);
                        if(checkBox.isChecked()){
                            joined.add(playerList.get(i));
                        }
                    }
                    playerList.removeAll(joined);
                    databaseHelper.deletePlayer("'"+joined.get(0)+"'");
                    databaseHelper.deletePlayer("'"+joined.get(1)+"'");
                    addTeamToDb(joined);

                    playersAdapter.notifyDataSetChanged();
                    teamsAdapter.notifyDataSetChanged();
                    String teamName = joined.get(0)+"&"+joined.get(1);
                    Team team = new Team(joined.get(0), joined.get(1), teamName);
                    CurrentGameEntity.getInstance().getTeams().add(team);

                    finish();
                    startActivity(getIntent());                }

                selectedPlayersCount = 0;
            }
        });

        startGameButton = (Button) findViewById(R.id.start_game);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PairUpPlayersActivity.this, GameplayActivity.class));
            }
        });

    }


    private void fillPlayerList(Cursor table, List<String> list, String entity) {
        if(table.getCount()==0){
            //Toast.makeText(this, entity + " table was empty!", Toast.LENGTH_SHORT).show();
            Log.d("Players", "No players in DB");
        } else{
            while (table.moveToNext()){
                list.add(table.getString(1));
            }
        }
    }

    private void fillTeamList(Cursor table, List<String> list, String entity) {
        if(table.getCount()==0){
            //Toast.makeText(this, entity + " table was empty!", Toast.LENGTH_SHORT).show();
            Log.d("Teams", "No teams in DB");
        } else{
            while (table.moveToNext()){
                String teamName = table.getString(1) + "&" + table.getString(2);
                Team team = new Team(table.getString(1), table.getString(2), teamName);
                CurrentGameEntity.getInstance().getTeams().add(team);
                list.add(teamName);
            }
        }
    }


    /**
     * Add team to DB
     * @param joined
     */
    public void addTeamToDb(LinkedList<String> joined){
        boolean insertData = databaseHelper.addTeamData(joined.get(0), joined.get(1));
        if(insertData){
            showToastMsg("Tim " + joined.get(0) + " & " + joined.get(1) +" dodan");
        } else {
            showToastMsg("Greška s bazom podataka");
        }
    }

    /**
     * Shows toast message received as an argument.
     * @param message
     */
    private void showToastMsg(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    class PlayersAdapter extends ArrayAdapter<String>{

        Context context;
        List<String> players;

        PlayersAdapter(Context c, List<String> players){
            super(c, R.layout.row, R.id.textView, players);
            this.context = c;
            this.players = players;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){

            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);

            playerTextView = row.findViewById(R.id.player_name);
            pairCheckBox = row.findViewById(R.id.check_box_pair);
            trashIcon = row.findViewById(R.id.image_view_trash);
            playerTextView.setText(players.get(position));

            trashIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PairUpPlayersActivity.this)
                            .setIcon(android.R.drawable.ic_delete)
                            .setTitle("Delete item")
                            .setMessage("Želiš li izbrisati ovog igača?")
                            .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseHelper.deletePlayer("'"+playerList.get(position)+"'");
                                    playerList.remove(position);
                                    playersAdapter.notifyDataSetChanged();
                                }}).setNegativeButton("Ne", null).show();
                }
            });

            return row;

        }
    }


    class TeamsAdapter extends ArrayAdapter<String>{

        Context context;
        List<String> pairs;

        TeamsAdapter(Context c, List<String> pairs){
            super(c, R.layout.pairs_row, R.id.textView, pairs);
            this.context = c;
            this.pairs = pairs;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent){

            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.pairs_row, parent, false);

            textViewPairs = row.findViewById(R.id.text_view_pairs);
            trashIcon = row.findViewById(R.id.image_view_trash);


            Log.i("pairs " , pairs.toString());
            textViewPairs.setText("par: " + pairs.get(position));

            trashIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PairUpPlayersActivity.this)
                            .setIcon(android.R.drawable.ic_delete)
                            .setTitle("Delete item")
                            .setMessage("Želiš li izbrisati ovaj tim?")
                            .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseHelper.deleteTeams(pairs.get(position));
                                    pairs.remove(position);
                                    teamsAdapter.notifyDataSetChanged();
                                }}).setNegativeButton("Ne", null).show();
                }
            });

            return row;

        }
    }

}

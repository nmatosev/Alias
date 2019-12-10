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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PairUpPlayersActivity extends AppCompatActivity {

    Button startGameButton;
    private static final String TAG ="GameplayActivity";
    DatabaseHelper databaseHelper;
    TextView playerTextView;
    ImageView trashIcon;
    ArrayList<String> playerList = new ArrayList<>();
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_up_players);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
        Cursor data = databaseHelper.getData();

        if(data.getCount()==0){
            Toast.makeText(this, "DB was empty!", Toast.LENGTH_SHORT).show();
        } else{
            while (data.moveToNext()){
                playerList.add(data.getString(1));
            }
        }

        Log.d("Players ", playerList.toString());
        ListView listView = (ListView) findViewById(R.id.list_view);
        myAdapter = new MyAdapter(this, playerList);
        listView.setAdapter(myAdapter);

        /**listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final int item = position;
                Log.d("playerr", playerTextView.getText().toString());
                new AlertDialog.Builder(PairUpPlayersActivity.this)
                    .setIcon(android.R.drawable.ic_delete)
                    .setTitle("Delete item")
                    .setMessage("Do you want to delete this item?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseHelper.deleteItem("'"+playerList.get(item)+"'");
                            playerList.remove(item);
                            myAdapter.notifyDataSetChanged();
                        }}).setNegativeButton("No", null).show();
                return true;
            }
        });*/

        Button createTeamsButton =  findViewById(R.id.create_team_button);

        createTeamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PairUpPlayersActivity.this, PopUpActivity.class));
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


    private void showToastMsg(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    class MyAdapter extends ArrayAdapter<String>{

        Context context;
        List<String> titles;

        MyAdapter(Context c, List<String> titles){
            super(c, R.layout.row, R.id.textView, titles);
            this.context = c;
            this.titles = titles;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            playerTextView = row.findViewById(R.id.player_name);
            trashIcon = row.findViewById(R.id.image_view_trash);
            playerTextView.setText(titles.get(position));
            final int item = position;

            trashIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PairUpPlayersActivity.this)
                            .setIcon(android.R.drawable.ic_delete)
                            .setTitle("Delete item")
                            .setMessage("Do you want to delete this item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseHelper.deleteItem("'"+playerList.get(item)+"'");
                                    playerList.remove(item);
                                    myAdapter.notifyDataSetChanged();
                                }}).setNegativeButton("No", null).show();
                }
            });

            return row;

        }
    }

}

package com.example.alias;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This activity depicts current score.
 */
public class CurrentScoreActivity extends AppCompatActivity {

    Button buttonNextPair;
    TextView roundTextView;
    TextView teamResultTextView;
    Team winners;
    TextView wordTextView;
    SummaryAdapter summaryAdapter;
    SoundPool soundPool;
    String mainMenu = "Glavni meni";
    String continueGame = "Nastavi";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_score);
        buttonNextPair = (Button) findViewById(R.id.button_next_pair);
        roundTextView = (TextView) findViewById(R.id.round_text_view);
        teamResultTextView = (TextView) findViewById(R.id.pair_result_text_view);

        int queue = CurrentGameEntity.getInstance().getTeamQueue();

        int numberOfTeams = CurrentGameEntity.getInstance().getTeams().size();
        Map<Integer, Team> teams = CurrentGameEntity.getInstance().getTeams();
        queue = queue % numberOfTeams;
        Team team = teams.get(queue);
        int round = team.getRound();

        soundPool = Utilities.getSoundPool();

        String teamName = team.getTeamName();

        final ListView summaryListView = (ListView) findViewById(R.id.summary_list_view);
        int currentRound = team.getRound() + 1;

        summaryAdapter = new SummaryAdapter(this, team.getRoundSummary().get(round)); //TODO: CHECK THHIS - COULD BE DONE BETTER
        summaryListView.setAdapter(summaryAdapter);

        String roundPlaceHolder = "Runda: " + currentRound;
        roundTextView.setText(roundPlaceHolder);

        team.setRound(currentRound);
        String roundSummary = teamName + " - trenutno stanje bodova " + team.getCurrentScore();

        boolean isFinished = false;
        if (checkIfThereIsAWinner()) {
            String winnersMsg = "Pobjednici " + winners.getTeamName() + " ! ";
            teamResultTextView.setText(winnersMsg);
            isFinished = true;
            for (Map.Entry<Integer, Team> t : CurrentGameEntity.getInstance().getTeams().entrySet()) {
                t.getValue().setCurrentScore(0);
                t.getValue().setRoundSummary(new HashMap<Integer, List<Answer>>());
                t.getValue().setRound(0);
            }

        } else {
            teamResultTextView.setText(roundSummary);
        }

        queue += 1;
        //TODO refactor this
        //CurrentGameEntity.getInstance().setTeamQueue(queue);
        if (queue >= CurrentGameEntity.getInstance().getTeams().size()) {
            CurrentGameEntity.getInstance().setTeamQueue(0);
        } else {
            CurrentGameEntity.getInstance().setTeamQueue(queue);
        }

        if (isFinished) {

            buttonNextPair.setText(mainMenu);
            buttonNextPair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CurrentScoreActivity.this, MainActivity.class));
                }
            });
        } else {
            buttonNextPair.setText(continueGame);
            buttonNextPair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CurrentScoreActivity.this, GameplayActivity.class));
                }
            });
        }
    }

    class SummaryAdapter extends ArrayAdapter<Answer> {

        Context context;
        List<Answer> summary;

        SummaryAdapter(Context c, List<Answer> summary) {
            super(c, R.layout.summary_row, R.id.textView, summary);
            this.context = c;
            this.summary = summary;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.summary_row, parent, false);
            int tickId = getResources().getIdentifier("@drawable/tick", null, getPackageName());
            int wrongId = getResources().getIdentifier("@drawable/wrong", null, getPackageName());

            Log.d("Answers in round", summary.toString());

            wordTextView = row.findViewById(R.id.word);
            ImageView imageview = row.findViewById(R.id.tick);
            if (summary.get(position).isCorrect()) {
                Drawable res = getResources().getDrawable(tickId);
                imageview.setImageDrawable(res);
            } else {
                Drawable res = getResources().getDrawable(wrongId);
                imageview.setImageDrawable(res);
            }

            wordTextView.setText(summary.get(position).getTerm());
            return row;
        }
    }

    /**
     * There are two conditions in order to be a winner:
     * - Have the equal or grater score than scoreToWin variable value
     * - Have equal number of rounds as any other team
     *
     * @return true if there is a winner, false otherwise
     */
    private boolean checkIfThereIsAWinner() {
        SharedPreferences settingsPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE);
        int scoreToWin = settingsPreferences.getInt("scoreToWin", 30);
        TreeMap<Integer, Team> teams = CurrentGameEntity.getInstance().getTeams();
        Log.d("WinnerCheck", "Check rounds");

        //checks if every team has equal number of rounds
        for (int i = 0; i < teams.size() - 1; i++) {
            Team current = teams.get(i);
            Team nextOne = teams.get(i + 1);
            if (current.getRound() != nextOne.getRound()) {
                return false;
            }
        }
        Log.d("WinnerCheck", "All teams have equal number of rounds");

        //checks the currently best team
        winners = teams.firstEntry().getValue();
        for (Map.Entry<Integer, Team> team : teams.entrySet()) {
            if (team.getValue().getCurrentScore() > winners.getCurrentScore()) {
                winners = team.getValue();
            } else if (!team.getValue().equals(winners) && team.getValue().getCurrentScore() == winners.getCurrentScore()){
                Log.d("WinnerCheck","Tie " + winners + " & " + team);
                return false;
            }
        }

        Log.d("WinnerCheck","Currently best "+winners.getTeamName() + " " + winners.getCurrentScore());

        //checks if best team has enough points to win
        if(winners != null && winners.getCurrentScore() >= scoreToWin){
            Log.d("WinnerCheck", "Winner decided " + winners.getTeamName() + " " + winners.getCurrentScore());
            return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }
}

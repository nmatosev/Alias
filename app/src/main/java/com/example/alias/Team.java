package com.example.alias;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private List<String> players = new ArrayList<>();

    public Team(String player1, String player2) {
        players.add(player1);
        players.add(player2);
    }

    public List<String> getPlayers() {
        return players;
    }


    public void setPlayers(List<String> players) {
        this.players = players;
    }

    private int currentScore;

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }
}

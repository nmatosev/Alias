package com.example.alias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Team {

    private List<String> players = new ArrayList<>();
    private int round = 0;
    private String teamName;

    private Map<Integer, List<Answer>> roundSummary = new TreeMap<>();


    public Map<Integer, List<Answer>> getRoundSummary() {
        return roundSummary;
    }

    public void setRoundSummary(Map<Integer, List<Answer>> roundSummary) {
        this.roundSummary = roundSummary;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Team(String player1, String player2, String teamName) {
        players.add(player1);
        players.add(player2);
        this.teamName = teamName;
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

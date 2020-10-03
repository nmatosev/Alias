package com.example.alias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Team {

    private int round;
    private String teamName;
    private Player player1;
    private Player player2;

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }


    public Player getPlayer2() {
        return player2;
    }

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

    public Team(Player player1, Player player2, String teamName) {
        this.player1 = player1;
        this.player2 = player2;
        this.teamName = teamName;
    }


    private int currentScore;

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }


}

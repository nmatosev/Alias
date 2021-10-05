package com.example.alias.entities;

import java.util.TreeMap;

public class CurrentGameEntity {

    private static CurrentGameEntity instance;

    private int teamQueue = 0;

    public int getTeamQueue() {
        return teamQueue;
    }

    public void setTeamQueue(int teamQueue) {
        this.teamQueue = teamQueue;
    }

    private CurrentGameEntity() {

    }

    public static CurrentGameEntity getInstance() {
        if (instance == null) {
            instance = new CurrentGameEntity();
        }
        return instance;
    }


    private TreeMap<Integer, Team> teams = new TreeMap<>();

    public TreeMap<Integer, Team> getTeams() {
        return teams;
    }

}

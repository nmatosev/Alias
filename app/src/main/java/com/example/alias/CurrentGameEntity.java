package com.example.alias;

import java.util.ArrayList;
import java.util.List;

public class CurrentGameEntity {

    private static CurrentGameEntity instance;

    private  int teamQueue = 0;

    public int getTeamQueue() {
        return teamQueue;
    }

    public void setTeamQueue(int teamQueue) {
        this.teamQueue = teamQueue;
    }

    private CurrentGameEntity(){

    }

    public static CurrentGameEntity getInstance(){
        if(instance==null){
            instance = new CurrentGameEntity();
        }
        return instance;
    }

    private List<Team> teams = new ArrayList<>();



    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Team> getTeams() {
        return teams;
    }
}

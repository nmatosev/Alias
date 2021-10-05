package com.example.alias.entities;

public class Player {

    private String name;
    private boolean isReader;

    public Player(String name, boolean isReader) {
        this.name = name;
        this.isReader = isReader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReader() {
        return isReader;
    }

    public void setReader(boolean reader) {
        isReader = reader;
    }
}

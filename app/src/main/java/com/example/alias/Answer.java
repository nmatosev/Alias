package com.example.alias;

public class Answer {

    public Answer(String term, boolean isCorrect) {
        this.term = term;
        this.isCorrect = isCorrect;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    private String term;
    private boolean isCorrect;

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "term='" + term + '\'' +
                ", isCorrect=" + isCorrect +
                '}';
    }
}

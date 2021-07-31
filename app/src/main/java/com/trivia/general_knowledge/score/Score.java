package com.trivia.general_knowledge.score;

//This Method is for set and get our score.
public class Score {
    private int score;

    public Score() {
    }

    public Score(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return
                ""+ score ;
    }
}

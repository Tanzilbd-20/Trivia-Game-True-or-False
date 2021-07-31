package com.trivia.general_knowledge.score;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

//This class is for saving game status.
public class SaveGameState {
    public static final String CURRENT_SCORE = "currentScore";
    public static final String CURRENT_QUESTION = "current_question";
    public static final String HIGHEST_SCORE = "highest_score";
    public static final String AVOID_DOUBLE_AND_SKIP = "avoid_double_and_skip";
    private final SharedPreferences preferences;

    public SaveGameState(Activity context){
        this.preferences = context.getPreferences(Context.MODE_PRIVATE);
    }

    //Saving Current Question.
    public void saveCurrentQuestion(int current_question){
        preferences.edit().putInt(CURRENT_QUESTION,current_question).apply();
    }
    public int getCurrentQuestion(){
        return preferences.getInt(CURRENT_QUESTION,1);
    }

    //Saving Current Score.
    public void saveCurrentScore(int score){
    preferences.edit().putInt(CURRENT_SCORE,score).apply();
    }
    public int getCurrentScore(){
        return preferences.getInt(CURRENT_SCORE,0);
    }

    //Saving high score
    public void saveHighScore(int high_score){
        int last_high_score = preferences.getInt(HIGHEST_SCORE,0);
        if(high_score>last_high_score){
        preferences.edit().putInt(HIGHEST_SCORE,high_score).apply();
        }else {
            preferences.edit().putInt(HIGHEST_SCORE,last_high_score).apply();
        }
    }
    public int getHighScore(){
        return preferences.getInt(HIGHEST_SCORE,0);
    }

    //This method is saving for avoid choosing multiple answer and skip question.
    public void saveAvoidDoubleAndSkip(int avoid_and_skip){
        preferences.edit().putInt(AVOID_DOUBLE_AND_SKIP,avoid_and_skip).apply();
    }

    public int getAvoidDoubleAndSkip(){
        return preferences.getInt(AVOID_DOUBLE_AND_SKIP,0);
    }
}

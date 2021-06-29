package com.example.trivia5.data;

import com.example.trivia5.model.QuestionModel;

import java.util.ArrayList;
//This class for making sure that data is available.
public interface QuestionLoadingPart {
    void finishedProcessed(ArrayList<QuestionModel> questionModels);
}

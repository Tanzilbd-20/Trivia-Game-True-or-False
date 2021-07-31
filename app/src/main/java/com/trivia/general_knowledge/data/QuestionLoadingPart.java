package com.trivia.general_knowledge.data;

import com.trivia.general_knowledge.model.QuestionModel;

import java.util.ArrayList;
//This class for making sure that data is available.
public interface QuestionLoadingPart {
    void finishedProcessed(ArrayList<QuestionModel> questionModels);
}

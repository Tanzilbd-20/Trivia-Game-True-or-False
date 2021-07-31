package com.trivia.general_knowledge.data;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.trivia.general_knowledge.controller.AppController;
import com.trivia.general_knowledge.model.QuestionModel;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

//This class is for making request to get data from web.
public class Repository {

    ArrayList<QuestionModel> questionModelArrayList = new ArrayList<>();
    String questionLink ="https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    public List<QuestionModel> getQuestion (final QuestionLoadingPart callback){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, questionLink, null, response -> {

            for (int i = 0; i <response.length() ; i++) {
                try {
                    QuestionModel questionModel = new QuestionModel(response.getJSONArray(i)
                            .getString(0),response.getJSONArray(i).getBoolean(1));

                    questionModelArrayList.add(questionModel);

                   /* Log.d("TAG", "getQuestion: "+questionModelArrayList);*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(callback !=null) callback.finishedProcessed(questionModelArrayList);



        }, error -> {

        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        return questionModelArrayList;
    }


}

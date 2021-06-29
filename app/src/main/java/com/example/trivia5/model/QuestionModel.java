package com.example.trivia5.model;

public class QuestionModel {

    private String question;
    private boolean answer;

    public QuestionModel() {
    }

    public QuestionModel(String question, boolean answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "QuestionModel{" +
                "question='" + question + '\'' +
                ", answer=" + answer +
                '}';
    }
}

package com.obdasystems.pocmedici.network;

import java.util.List;

public class RestFormQuestion {

    private int id;
    private String text;
    private List<RestPossibleAnswer> answers;

    public RestFormQuestion(int id, String text, List<RestPossibleAnswer> answers) {
        this.id = id;
        this.text = text;
        this.answers = answers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<RestPossibleAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<RestPossibleAnswer> answers) {
        this.answers = answers;
    }
}

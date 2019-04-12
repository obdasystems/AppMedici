package com.obdasystems.pocmedici.network;

public class RestPossibleAnswer {

    private int code;
    private int id;
    private String text;

    public RestPossibleAnswer(int code, int id, String text) {
        this.code = code;
        this.id = id;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
}

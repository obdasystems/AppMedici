package com.obdasystems.pocmedici.network;

public class RestStepCounter {

    private String type;
    private int count;
    private long date;

    public RestStepCounter( int count, long date) {
        this.type = "pedometer";
        this.count = count;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}

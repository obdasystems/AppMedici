package com.obdasystems.pocmedici.network;

public class RestPosition {

    private String type;
    private String geometry;
    private long date;

    public RestPosition(String geometry, long date) {
        this.type = "gps";
        this.geometry = geometry;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}

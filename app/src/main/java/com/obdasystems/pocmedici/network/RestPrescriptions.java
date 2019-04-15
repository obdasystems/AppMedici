package com.obdasystems.pocmedici.network;

public class RestPrescriptions {

    private String url;

    public RestPrescriptions(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

package com.obdasystems.pocmedici.network;

import java.util.List;

public class RestFormPage {

    private int id;
    private int pageNumber;
    private String pageTitle;
    private List<RestFormQuestion> questions;

    public RestFormPage(int id, int pageNumber, String pageTitle, List<RestFormQuestion> questions) {
        this.id = id;
        this.pageNumber = pageNumber;
        this.pageTitle = pageTitle;
        this.questions = questions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public List<RestFormQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<RestFormQuestion> questions) {
        this.questions = questions;
    }
}

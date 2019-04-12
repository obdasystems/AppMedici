package com.obdasystems.pocmedici.network;

import java.util.List;

public class RestForm {

    private int id;
    private String formClass;
    private String periodicity;
    private String creationDate;
    private List<RestFormPage> pages;

    public RestForm(int id, String formClass, String periodicity, String creationDate, List<RestFormPage> pages) {
        this.id = id;
        this.formClass = formClass;
        this.periodicity = periodicity;
        this.creationDate = creationDate;
        this.pages = pages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFormClass() {
        return formClass;
    }

    public void setFormClass(String formClass) {
        this.formClass = formClass;
    }

    public String getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(String periodicity) {
        this.periodicity = periodicity;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public List<RestFormPage> getPages() {
        return pages;
    }

    public void setPages(List<RestFormPage> pages) {
        this.pages = pages;
    }
}

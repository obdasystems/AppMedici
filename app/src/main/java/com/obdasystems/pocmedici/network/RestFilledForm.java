package com.obdasystems.pocmedici.network;

import java.util.List;

public class RestFilledForm {

    private int formId;
    private List<RestFilledQuestion> answers;

    public RestFilledForm(int formId, List<RestFilledQuestion> answers) {
        this.formId = formId;
        this.answers = answers;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public List<RestFilledQuestion> getAnswers() {
        return answers;
    }

    public void setAnswers(List<RestFilledQuestion> answers) {
        this.answers = answers;
    }
}

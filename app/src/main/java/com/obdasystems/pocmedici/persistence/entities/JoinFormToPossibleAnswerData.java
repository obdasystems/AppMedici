package com.obdasystems.pocmedici.persistence.entities;

public class JoinFormToPossibleAnswerData {

    private int formId;
    private int pageId;
    private int questionId;
    private int answerId;

    private int pageNumber;
    private String questionText;
    private String answerText;

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {

        this.formId = formId;
    }

    public int getPageId() {

        return pageId;
    }

    public void setPageId(int pageId) {

        this.pageId = pageId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {

        this.questionId = questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {

        this.answerId = answerId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

}

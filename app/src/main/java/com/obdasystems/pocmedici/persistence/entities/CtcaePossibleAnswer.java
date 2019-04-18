package com.obdasystems.pocmedici.persistence.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import com.obdasystems.pocmedici.network.RestPossibleAnswer;

@Entity(tableName = "ctcae_form_possible_answer",
        primaryKeys = {"id", "question_id"} ,
        foreignKeys = @ForeignKey(entity = CtcaeFormQuestion.class,
                parentColumns = {"id","form_id","page_id"},
                childColumns = {"question_id","form_id","page_id"},
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"id"},
                unique = true)})
public class CtcaePossibleAnswer {

    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "form_id")
    private int formId;

    @NonNull
    @ColumnInfo(name = "page_id")
    private int pageId;

    @NonNull
    @ColumnInfo(name = "question_id")
    private int questionId;

    @NonNull
    @ColumnInfo(name = "code")
    private int code;

    @NonNull
    @ColumnInfo(name = "text")
    private String text;

    public CtcaePossibleAnswer(int id, int formId, int pageId, int questionId, int code, String text) {
        this.id = id;
        this.questionId = questionId;
        this.text = text;
        this.formId = formId;
        this.pageId = pageId;
        this.code = code;
    }

    public CtcaePossibleAnswer(RestPossibleAnswer rpa, int formId, int pageId, int questionId) {
        this.id = rpa.getId();
        this.questionId = questionId;
        this.text = rpa.getText();
        this.formId = formId;
        this.code = rpa.getCode();
        this.pageId = pageId;
    }

    @NonNull
    public int getId() {
        return this.id;
    }

    public int getFormId() {
        return formId;
    }

    public int getPageId() {
        return pageId;
    }

    @NonNull
    public int getQuestionId() {
        return this.questionId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @NonNull
    public String getText() {
        return this.text;
    }

}

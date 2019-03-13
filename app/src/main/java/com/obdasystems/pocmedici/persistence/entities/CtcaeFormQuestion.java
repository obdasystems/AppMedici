package com.obdasystems.pocmedici.persistence.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "ctcae_form_question",
        primaryKeys = {"id", "form_id", "page_id"} ,
        foreignKeys = {@ForeignKey(entity = CtcaeFormPage.class,
                        parentColumns = "id",
                        childColumns = "page_id",
                        onDelete = ForeignKey.CASCADE),
                       @ForeignKey(entity = CtcaeForm.class,
                        parentColumns = "id",
                        childColumns = "form_id",
                        onDelete = ForeignKey.CASCADE)
                      })//,
        /*indices = {@Index(value = {"id","page_id"},
                unique = true)})*/
public class CtcaeFormQuestion {


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
    @ColumnInfo(name = "text")
    private String text;

    public CtcaeFormQuestion(int id, int formId, int pageId, String text) {
        this.id = id;
        this.formId = formId;
        this.pageId = pageId;
        this.text = text;
    }

    @NonNull
    public int getId() {
        return this.id;
    }

    @NonNull
    public int getFormId() {
        return this.formId;
    }

    @NonNull
    public int getPageId() {
        return this.pageId;
    }

    @NonNull
    public String getText() {
        return this.text;
    }

}

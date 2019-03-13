package com.obdasystems.pocmedici.persistence.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "ctcae_form_question_answered",
        primaryKeys = {"proc_id", "form_id", "page_id", "question_id"} ,
        foreignKeys = { @ForeignKey(entity = CtcaeFormFillingProcess.class,
                                parentColumns = "id",
                                childColumns = "proc_id",
                                onDelete = ForeignKey.CASCADE),
                        @ForeignKey(entity = CtcaeFormQuestion.class,
                                parentColumns = {"id", "page_id", "form_id"},
                                childColumns = {"question_id","page_id", "form_id"},
                                onDelete = ForeignKey.CASCADE),
                        @ForeignKey(entity = CtcaePossibleAnswer.class,
                                parentColumns = "id",
                                childColumns = "answer_id",
                                onDelete = ForeignKey.CASCADE),

        })

public class CtcaeFormQuestionAnswered {


    @NonNull
    @ColumnInfo(name = "proc_id")
    private int fillingProcessId;

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
    @ColumnInfo(name = "answer_id")
    private int answerId;

    public CtcaeFormQuestionAnswered(int fillingProcessId, int formId, int pageId, int questionId, int answerId) {
        this.fillingProcessId = fillingProcessId;
        this.formId = formId;
        this.pageId = pageId;
        this.questionId = questionId;
        this.answerId = answerId;
    }

    public int getFillingProcessId() {
        return fillingProcessId;
    }

    public int getFormId() {
        return formId;
    }

    public int getPageId() {
        return pageId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

}

package com.obdasystems.pocmedici.persistence.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class JoinFormPageQuestionsWithPossibleAnswerData implements Parcelable {
    private int questionId;
    private int formId;
    private int pageId;
    private String questionText;
    private String possibleAnswerText;
    private int possibleAnswerId;
    private int possibleAnswerCode;

    public JoinFormPageQuestionsWithPossibleAnswerData(int questionId, int formId, int pageId, String questionText,
                                                       String possibleAnswerText, int possibleAnswerId,
                                                       int possibleAnswerCode) {
        this.questionId = questionId;
        this.formId = formId;
        this.pageId = pageId;
        this.questionText = questionText;
        this.possibleAnswerText = possibleAnswerText;
        this.possibleAnswerId = possibleAnswerId;
        this.possibleAnswerCode = possibleAnswerCode;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

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

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getPossibleAnswerText() {
        return possibleAnswerText;
    }

    public void setPossibleAnswerText(String possibleAnswerText) {
        this.possibleAnswerText = possibleAnswerText;
    }

    public int getPossibleAnswerId() {
        return possibleAnswerId;
    }

    public void setPossibleAnswerId(int possibleAnswerId) {
        this.possibleAnswerId = possibleAnswerId;
    }

    public int getPossibleAnswerCode() {
        return possibleAnswerCode;
    }

    public void setPossibleAnswerCode(int possibleAnswerCode) {
        this.possibleAnswerCode = possibleAnswerCode;
    }

    //Parcelable mathods

    public JoinFormPageQuestionsWithPossibleAnswerData(Parcel inParcel) {
        this.questionId = inParcel.readInt();
        this.formId = inParcel.readInt();
        this.pageId = inParcel.readInt();
        this.questionText = inParcel.readString();
        this.possibleAnswerText = inParcel.readString();
        this.possibleAnswerId = inParcel.readInt();
        this.possibleAnswerCode = inParcel.readInt();
    }

    public static final Creator<JoinFormPageQuestionsWithPossibleAnswerData> CREATOR = new Creator<JoinFormPageQuestionsWithPossibleAnswerData>() {
        @Override
        public JoinFormPageQuestionsWithPossibleAnswerData createFromParcel(Parcel in) {
            return new JoinFormPageQuestionsWithPossibleAnswerData(in);
        }

        @Override
        public JoinFormPageQuestionsWithPossibleAnswerData[] newArray(int size) {
            return new JoinFormPageQuestionsWithPossibleAnswerData[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.formId);
        dest.writeInt(this.formId);
        dest.writeInt(this.pageId);
        dest.writeString(this.questionText);
        dest.writeString(this.possibleAnswerText);
        dest.writeInt(this.possibleAnswerId);
        dest.writeInt(this.possibleAnswerCode);
    }
}

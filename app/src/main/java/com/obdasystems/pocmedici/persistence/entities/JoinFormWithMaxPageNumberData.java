package com.obdasystems.pocmedici.persistence.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class JoinFormWithMaxPageNumberData implements Parcelable {

    private int formId;

    private int formClass;

    private int formPeriodicity;

    private String formTitle;

    private String formInstructions;

    private int lastPageNumber;

    public JoinFormWithMaxPageNumberData(int formId, int formClass, int formPeriodicity, String formTitle, String formInstructions, int lastPageNumber) {
        this.formId = formId;
        this.formClass = formClass;
        this.formPeriodicity = formPeriodicity;
        this.formTitle = formTitle;
        this.formInstructions = formInstructions;
        this.lastPageNumber = lastPageNumber;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getFormClass() {
        return formClass;
    }

    public void setFormClass(int formClass) {
        this.formClass = formClass;
    }

    public int getFormPeriodicity() {
        return formPeriodicity;
    }

    public void setFormPeriodicity(int formPeriodicity) {
        this.formPeriodicity = formPeriodicity;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    public String getFormInstructions() {
        return formInstructions;
    }

    public void setFormInstructions(String formInstructions) {
        this.formInstructions = formInstructions;
    }

    public int getLastPageNumber() {
        return lastPageNumber;
    }

    public void setLastPageNumber(int lastPageNumber) {
        this.lastPageNumber = lastPageNumber;
    }


    //Parcelable mathods

    public JoinFormWithMaxPageNumberData(Parcel inParcel) {
        this.formId = inParcel.readInt();
        this.formClass = inParcel.readInt();
        this.formPeriodicity = inParcel.readInt();
        this.formTitle = inParcel.readString();
        this.formInstructions = inParcel.readString();
        this.lastPageNumber = inParcel.readInt();
    }

    public static final Creator<JoinFormWithMaxPageNumberData> CREATOR = new Creator<JoinFormWithMaxPageNumberData>() {
        @Override
        public JoinFormWithMaxPageNumberData createFromParcel(Parcel in) {
            return new JoinFormWithMaxPageNumberData(in);
        }

        @Override
        public JoinFormWithMaxPageNumberData[] newArray(int size) {
            return new JoinFormWithMaxPageNumberData[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.formId);
        dest.writeInt(this.formClass);
        dest.writeInt(this.formPeriodicity);
        dest.writeString(this.formTitle);
        dest.writeString(this.formInstructions);
        dest.writeInt(this.lastPageNumber);
    }
}

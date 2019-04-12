package com.obdasystems.pocmedici.persistence.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.obdasystems.pocmedici.network.RestForm;

@Entity(tableName = "ctcae_form_questionnaire")
public class CtcaeForm implements Parcelable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "class")
    private String formClass;

    @NonNull
    @ColumnInfo(name = "periodicity")
    private String formPeriodicity;

    @NonNull
    @ColumnInfo(name = "title")
    private String formTitle;

    @NonNull
    @ColumnInfo(name = "instructions")
    private String formInstructions;

    public CtcaeForm(int id, String formClass, String formPeriodicity, String formTitle, String formInstructions) {
        this.id = id;
        this.formClass = formClass;
        this.formPeriodicity = formPeriodicity;
        this.formTitle = formTitle;
        this.formInstructions = formInstructions;
    }

    public CtcaeForm(RestForm restForm) {
        this.id = restForm.getId();
        this.formClass = restForm.getFormClass();
        this.formPeriodicity = restForm.getPeriodicity();
        this.formTitle = "FORM TITLE";
        this.formInstructions = "FORM INSTRUCTIONS";
    }

    @NonNull
    public int getId() {
        return this.id;
    }

    public String getFormClass() {
        return formClass;
    }

    public String getFormPeriodicity() {
        return formPeriodicity;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public String getFormInstructions() {
        return formInstructions;
    }


    //Parcelable methods

    public CtcaeForm(Parcel inParcel) {
        this.id = inParcel.readInt();
        this.formClass = inParcel.readString();
        this.formPeriodicity = inParcel.readString();
        this.formTitle = inParcel.readString();
        this.formInstructions = inParcel.readString();
    }

    public static final Creator<CtcaeForm> CREATOR = new Creator<CtcaeForm>() {
        @Override
        public CtcaeForm createFromParcel(Parcel in) {
            return new CtcaeForm(in);
        }

        @Override
        public CtcaeForm[] newArray(int size) {
            return new CtcaeForm[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.formClass);
        dest.writeString(this.formPeriodicity);
        dest.writeString(this.formTitle);
        dest.writeString(this.formInstructions);
    }
}

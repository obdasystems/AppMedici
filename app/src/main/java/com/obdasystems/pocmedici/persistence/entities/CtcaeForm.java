package com.obdasystems.pocmedici.persistence.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "ctcae_form_questionnaire")
public class CtcaeForm implements Parcelable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "class")
    private int formClass;

    @NonNull
    @ColumnInfo(name = "periodicity")
    private int formPeriodicity;

    @NonNull
    @ColumnInfo(name = "title")
    private String formTitle;

    @NonNull
    @ColumnInfo(name = "instructions")
    private String formInstructions;

    public CtcaeForm(int id, int formClass, int formPeriodicity, String formTitle, String formInstructions) {
        this.id = id;
        this.formClass = formClass;
        this.formPeriodicity = formPeriodicity;
        this.formTitle = formTitle;
        this.formInstructions = formInstructions;
    }

    @NonNull
    public int getId() {
        return this.id;
    }

    public int getFormClass() {
        return formClass;
    }

    public int getFormPeriodicity() {
        return formPeriodicity;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public String getFormInstructions() {
        return formInstructions;
    }


    //Parcelable mathods

    public CtcaeForm(Parcel inParcel) {
        this.id = inParcel.readInt();
        this.formClass = inParcel.readInt();
        this.formPeriodicity = inParcel.readInt();
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
        dest.writeInt(this.formClass);
        dest.writeInt(this.formPeriodicity);
        dest.writeString(this.formTitle);
        dest.writeString(this.formInstructions);
    }
}

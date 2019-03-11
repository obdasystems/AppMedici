package com.obdasystems.pocmedici.persistence.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "ctcae_form_page",
        primaryKeys = {"id", "form_id"} ,
        foreignKeys = @ForeignKey(entity = CtcaeForm.class,
                parentColumns = "id",
                childColumns = "form_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"id"},
                   unique = true),
                   @Index(value = {"form_id", "page_number"},
                   unique = true)
                  }
        )
public class CtcaeFormPage implements Parcelable {

    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "form_id")
    private int formId;

    @NonNull
    @ColumnInfo(name = "page_number")
    private int pageNumber;

    @NonNull
    @ColumnInfo(name = "title")
    private String pageTitle;

    @NonNull
    @ColumnInfo(name = "instructions")
    private String pageInstructions;

    public CtcaeFormPage(int id, int formId, int pageNumber, String pageTitle, String pageInstructions) {
        this.id = id;
        this.formId = formId;
        this.pageNumber = pageNumber;
        this.pageTitle = pageTitle;
        this.pageInstructions = pageInstructions;
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
    public int getPageNumber() {
        return this.pageNumber;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getPageInstructions() {
        return pageInstructions;
    }

    //Parcelable
    public CtcaeFormPage(Parcel inParcel) {
        this.id = inParcel.readInt();
        this.formId = inParcel.readInt();
        this.pageNumber = inParcel.readInt();
        this.pageTitle = inParcel.readString();
        this.pageInstructions = inParcel.readString();
    }

    public static final Creator<CtcaeFormPage> CREATOR = new Creator<CtcaeFormPage>() {
        @Override
        public CtcaeFormPage createFromParcel(Parcel in) {
            return new CtcaeFormPage(in);
        }

        @Override
        public CtcaeFormPage[] newArray(int size) {
            return new CtcaeFormPage[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.formId);
        dest.writeInt(this.pageNumber);
        dest.writeString(this.pageTitle);
        dest.writeString(this.pageInstructions);
    }
}

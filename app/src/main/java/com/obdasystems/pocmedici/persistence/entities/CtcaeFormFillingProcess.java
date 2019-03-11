package com.obdasystems.pocmedici.persistence.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.obdasystems.pocmedici.persistence.converter.DateTypeConverter;

import java.util.GregorianCalendar;

@Entity(tableName = "ctcae_form_filling_process",
        foreignKeys = @ForeignKey(entity = CtcaeForm.class,
                parentColumns = "id",
                childColumns = "form_id",
                onDelete = ForeignKey.CASCADE))

public class CtcaeFormFillingProcess {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "form_id")
    private int formId;

    @TypeConverters(DateTypeConverter.class)
    @NonNull
    @ColumnInfo(name = "start_date")
    private GregorianCalendar startDate;

    @TypeConverters(DateTypeConverter.class)
    @ColumnInfo(name = "end_date")
    private GregorianCalendar endDate;

    @NonNull
    @ColumnInfo(name = "sent_to_server")
    private int sentToServer;

    public CtcaeFormFillingProcess(int id, GregorianCalendar startDate) {
        this.id = id;
        this.startDate = startDate;
        this.sentToServer = 0;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setStartDate(@NonNull GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    @NonNull
    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public void setEndDate(@NonNull GregorianCalendar endDate) {
        this.endDate = endDate;
    }

    public GregorianCalendar getEndDate() {
        return endDate;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getSentToServer() {
        return sentToServer;
    }

    public void setSentToServer(int sentToServer) {
        this.sentToServer = sentToServer;
    }



}

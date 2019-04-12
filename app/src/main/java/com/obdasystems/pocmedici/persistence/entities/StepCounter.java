package com.obdasystems.pocmedici.persistence.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.obdasystems.pocmedici.persistence.converter.DateTypeConverter;

import java.util.GregorianCalendar;

@Entity(tableName = "step_counter",
        primaryKeys = {"year", "month", "day"})
public class StepCounter {


    @ColumnInfo(name = "step_count")
    private int stepCount;

    @NonNull
    @ColumnInfo(name = "year")
    private int year;

    @NonNull
    @ColumnInfo(name = "month")
    private int month;

    @NonNull
    @ColumnInfo(name = "day")
    private int day;

    @NonNull
    @ColumnInfo(name = "sent_to_server")
    private int sentToServer;

    public StepCounter(int stepCount, int year, int month, int day, int sentToServer) {
        this.stepCount = stepCount;
        this.year = year;
        this.month = month;
        this.day = day;
        this.sentToServer = sentToServer;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }


    public int getSentToServer() {
        return sentToServer;
    }

    public void setSentToServer(int sentToServer) {
        this.sentToServer = sentToServer;
    }
}

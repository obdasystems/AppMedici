package com.obdasystems.pocmedici.persistence.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.obdasystems.pocmedici.persistence.converter.DateTypeConverter;

import java.util.GregorianCalendar;

@Entity(tableName = "device_position")
public class Position {



    @PrimaryKey
    @TypeConverters(DateTypeConverter.class)
    @NonNull
    @ColumnInfo(name = "timestamp")
    private GregorianCalendar timestamp;

    @NonNull
    @ColumnInfo(name = "latitude")
    private double latitude;

    @NonNull
    @ColumnInfo(name = "longitude")
    private double longitude;

    public Position(GregorianCalendar timestamp, double latitude, double longitude) {
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    public GregorianCalendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull GregorianCalendar timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

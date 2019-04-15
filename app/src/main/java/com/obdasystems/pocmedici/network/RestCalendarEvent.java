package com.obdasystems.pocmedici.network;

import android.os.Parcel;
import android.os.Parcelable;

public class RestCalendarEvent implements Parcelable {

    private Long timestamp;
    private String title;
    private String description;
    private int type;

    public RestCalendarEvent(Long timestamp, String title, String description, int type) {
        this.timestamp = timestamp;
        this.title = title;
        this.description = description;
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    protected RestCalendarEvent(Parcel in) {
        if (in.readByte() == 0) {
            timestamp = null;
        } else {
            timestamp = in.readLong();
        }
        title = in.readString();
        description = in.readString();
        type = in.readInt();
    }

    public static final Creator<RestCalendarEvent> CREATOR = new Creator<RestCalendarEvent>() {
        @Override
        public RestCalendarEvent createFromParcel(Parcel in) {
            return new RestCalendarEvent(in);
        }

        @Override
        public RestCalendarEvent[] newArray(int size) {
            return new RestCalendarEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (timestamp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(timestamp);
        }
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(type);
    }
}

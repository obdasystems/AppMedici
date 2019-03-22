package com.obdasystems.pocmedici.message.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;

public class Message implements Parcelable {
    private int id;
    private String from;
    private String to;
    private String subject;
    private String message;
    private String timestamp;
    private String picture;
    private boolean isImportant;
    private int intImportant;
    private boolean isRead;
    private int intRead;
    private int color = -1;

    public Message() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
        if(important) {
            intImportant = 1;
        }
        else {
            intImportant = 0;
        }
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
        if(read) {
            intRead = 1;
        }
        else {
            intRead = 0;
        }
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getIntImportant() {
        return intImportant;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    public void setIntImportant(int intImportant) {
        this.intImportant = intImportant;
        if(intImportant>0) {
            isImportant = true;
        }
        else {
            isImportant = false;
        }
    }

    public int getIntRead() {
        return intRead;
    }

    public void setIntRead(int intRead) {
        this.intRead = intRead;
        if(intRead>0) {
            isRead = true;
        }
        else {
            isRead = false;
        }
    }


    //Parcelable methods
    public Message(Parcel inParcel) {
        this.id = inParcel.readInt();
        this.from = inParcel.readString();
        this.to = inParcel.readString();
        this.subject = inParcel.readString();
        this.message = inParcel.readString();
        this.timestamp = inParcel.readString();
        this.picture = inParcel.readString();
        this.intImportant= inParcel.readInt();
        if(intImportant>0) {
            isImportant = true;
        }
        else {
            isImportant = false;
        }
        this.intRead = inParcel.readInt();
        if(intRead>0) {
            isRead = true;
        }
        else {
            isRead = false;
        }
        this.color = inParcel.readInt();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.from);
        dest.writeString(this.to);
        dest.writeString(this.subject);
        dest.writeString(this.message);
        dest.writeString(this.timestamp);
        dest.writeString(this.picture);
        if(isImportant) {
            dest.writeInt(1);
        }
        else {
            dest.writeInt(0);
        }
        if(isRead) {
            dest.writeInt(1);
        }
        else {
            dest.writeInt(0);
        }
        dest.writeInt(this.color);
    }
}


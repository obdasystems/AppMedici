package com.obdasystems.pocmedici.message.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.obdasystems.pocmedici.message.User;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Message implements Parcelable {
    /*private int id;
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
    private int color = -1;*/

    protected Long id;
    protected Long date;
    protected String text;
    protected String subject;
    protected Boolean read = false;
    private int intRead;
    protected Boolean adverseEvent = false;
    private int intAdverseEvent;
    protected User sender;
    protected User recipient;
    protected List<Attachment> attachments = new LinkedList<>();
    private int color = -1;



    public Message() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public int getIntRead() {
        return intRead;
    }

    public void setIntRead(int intRead) {
        this.intRead = intRead;
    }

    public Boolean getAdverseEvent() {
        return adverseEvent;
    }

    public void setAdverseEvent(Boolean adverseEvent) {
        this.adverseEvent = adverseEvent;
    }

    public int getIntAdverseEvent() {
        return intAdverseEvent;
    }

    public void setIntAdverseEvent(int intAdverseEvent) {
        this.intAdverseEvent = intAdverseEvent;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    //Parcelable methods
    public Message(Parcel inParcel) {
        this.id = inParcel.readLong();
        this.date = inParcel.readLong();
        this.text = inParcel.readString();
        this.subject = inParcel.readString();
        this.intRead= inParcel.readInt();
        if(intRead>0) {
            read = true;
        }
        else {
            read = false;
        }
        this.intAdverseEvent = inParcel.readInt();
        if(intRead>0) {
            adverseEvent = true;
        }
        else {
            adverseEvent = false;
        }
        this.sender = inParcel.readParcelable(User.class.getClassLoader());
        this.recipient = inParcel.readParcelable(User.class.getClassLoader());
        this.attachments = inParcel.createTypedArrayList(Attachment.CREATOR);
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
        dest.writeLong(this.id);
        dest.writeLong(this.date);
        dest.writeString(this.text);
        dest.writeString(this.subject);
        dest.writeInt(this.intRead);
        dest.writeInt(this.intAdverseEvent);
        dest.writeParcelable(this.sender, flags);
        dest.writeParcelable(this.recipient,flags);
        dest.writeTypedList(this.attachments);
        dest.writeInt(this.color);
        /*dest.writeInt(this.id);
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
        dest.writeInt(this.color);*/
    }
}


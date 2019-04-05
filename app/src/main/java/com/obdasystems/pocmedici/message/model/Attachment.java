package com.obdasystems.pocmedici.message.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Attachment implements Parcelable {


    protected String mimeType;

    protected String base64Content;


    /**
     * Creates a new empty attachment.
     */
    public Attachment() {

    }

    public Attachment(String type, String content) {
        this.mimeType = type;
        this.base64Content = content;
    }

    protected Attachment(Parcel in) {
        mimeType = in.readString();
        base64Content = in.readString();
    }

    public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {
        @Override
        public Attachment createFromParcel(Parcel in) {
            return new Attachment(in);
        }

        @Override
        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getBase64Content() {
        return base64Content;
    }

    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mimeType);
        dest.writeString(base64Content);
    }
}

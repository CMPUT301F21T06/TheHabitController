package com.example.thehabitcontroller_project;

import android.location.Location;
import android.graphics.Bitmap;
import java.util.Date;
import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private String title;
    private String comment; // optional comment up to 20 characters
    private Date date;
    private Location location;
    private String bitmapString;

    public Event() {
        // empty constructor
    }

    public Event(String habit, String comment, Location location, String bitmapString, Date dateEvent) {
        this.title = habit;
        this.comment = comment;
        this.date = date;
        this.location = location;
        this.bitmapString = bitmapString;
    }

    protected Event(Parcel in) {
        title = in.readString();
        comment = in.readString();
        date = new Date(in.readLong());
        // location =
        // bitmapString =
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String habit) {
        this.title = habit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDateEvent() {
        return date;
    }

    public void setDateEvent(Date dateEvent) {
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getBitmapString() {
        return bitmapString;
    }

    public void setBitmapString(String bitmapString) {
        this.bitmapString=bitmapString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(comment);
        parcel.writeLong(date.getTime());
        location.writeToParcel(parcel, i);
        parcel.writeString(bitmapString);
    }
}

package com.example.thehabitcontroller_project;

import android.location.Location;
import android.graphics.Bitmap;
import java.util.Date;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * The Event class for storing info for events.
 * This will store the title, comment, date, location, and photo
 *
 * @author Tyler
 * @version 1.0.0
 */
public class Event implements Parcelable {
    private String title;
    private String comment; // optional comment up to 20 characters
    private Date dateEvent;
    private Location location;
    private String bitmapString;

    public Event() {
        // empty constructor
    }

    /**
     * Initializes a Habit with all of its parameters
     * @param title        Title of the event
     * @param comment      Comment for the event
     * @param dateEvent    Date of the event
     * @param location     Location the event occurred
     * @param bitmapString Photo of the event
     */
    public Event(String title, String comment, Location location, String bitmapString, Date dateEvent) {
        this.title = title;
        this.comment = comment;
        this.dateEvent = dateEvent;
        this.location = location;
        this.bitmapString = bitmapString;
    }

    /**
     * Part of implementing {@link Parcelable}, we need a way
     * to instantiate an Event using a Parcel so we can send it in a {@link android.os.Bundle}
     * @param in The Parcel object containing Event information
     */
    protected Event(Parcel in) {
        title = in.readString();
        comment = in.readString();
        dateEvent = new Date(in.readLong());
        // location =
        // bitmapString =
    }

    /**
     * The Creator class that is part of implementing {@link Parcelable}
     * Implements the necessary methods to create an Event from a Parcel
     */
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

    /**
     * Getter for the Event's title
     * @return Returns a {@link String} containing the event's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the Event's title
     * @param title The title of the Event as {@link String}
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the Event's comment
     * @return The Event's comment as a {@link String}
     */
    public String getComment() {
        return comment;
    }

    /**
     * Setter for the Event's comment
     * @param comment The Event's reason as a {@link String}
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Getter for the Event's date it occurred
     * @return The full date for when the Event occurred as a {@link Date} object
     */
    public Date getDateEvent() {
        return dateEvent;
    }

    /**
     * The setter for the Event's date it occurred
     * @param dateEvent The Event's date it occurred as a {@link Date} object
     */
    public void setDateEvent(Date dateEvent) {
        this.dateEvent = dateEvent;
    }

    /**
     * The getter for the Event's location
     * @return The Event's location as a {@link Location} object
     */
    public Location getLocation() {
        return location;
    }

    /**
     * The setter for the Event's location
     * @param location The Event's location {@link Location} object
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * The getter for the Event's photo
     * @return The Event's photo as a bitmap {@link String} object
     */
    public String getBitmapString() {
        return bitmapString;
    }

    /**
     * The setter for the Event's photo
     * @param bitmapString The Event's photo as a bitmap {@link String} object
     */
    public void setBitmapString(String bitmapString) {
        this.bitmapString=bitmapString;
    }

    /**
     * Part of the implementation of {@link Parcelable}, but was left as is.
     * No modifications were done to this override other than implementing it here.
     * @return {@literal int} 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Part of the implementation of {@link Parcelable} and writes all Event attributes
     * to a {@link Parcel}
     * @param parcel the {@link Parcel} that the Event writes to, "transforming" it into one.
     * @param i the {@link int} variable for implementing this Override; was not used here.
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(comment);
        parcel.writeLong(dateEvent.getTime());
        location.writeToParcel(parcel, i);
        parcel.writeString(bitmapString);
    }
}

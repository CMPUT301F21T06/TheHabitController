package com.example.thehabitcontroller_project.Event;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

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
    private String location; // temporary until implemented
//    private Location location;
//    private Bitmap photo;
    private String photoString;
    private String habitTitle;
    public static final int MAX_PHOTO_SIZE = 1000000; // 1MB

    public Event() {
        // empty constructor
    }

    /**
     * Initializes a Habit with all of its parameters
     * @param title        Title of the event
     * @param comment      Comment for the event
     * @param dateEvent    Date of the event
     * @param location     Location the event occurred
     * @param photoString  Photo of the event
     */
    public Event(String title, String comment, Date dateEvent, String location, String photoString, String habitTitle) {
        this.title = title;
        this.comment = comment;
        this.dateEvent = dateEvent;
        this.location = location; // will change back to type Location
        this.photoString = photoString;
        this.habitTitle = habitTitle;
    }

    /**
     * Part of implementing {@link Parcelable}, we need a way
     * to instantiate an Event using a Parcel so we can send it in a {@link android.os.Bundle}
     * @param in The Parcel object containing Event information
     */
    protected Event(Parcel in) {
        title = in.readString();
        comment = in.readString();
        // limit comment length to 20
        int maxLength = 20;
        comment = comment.substring(0, maxLength);

        dateEvent = new Date(in.readLong());
        location = "location"; // temporary until implemented
//        location = new Location("Home");
//        location.setLongitude(0);
//        location.setLatitude(0);
        photoString = in.readString();
        habitTitle = in.readString();
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
    public String getLocation() {
        return location;
    } // temporary until implemented - change back type to Location

    /**
     * The setter for the Event's location
     * @param location The Event's location {@link Location} object
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Converter for the photo string into a {@link Bitmap}
     * @return The Event's photo as a bitmap {@link Bitmap} object
     */
    public Bitmap photoStringToBitmap() {
        if (this.photoString != null) {
            byte [] decodedBytes = Base64.decode(this.photoString, 0);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        return null;
    }

    /**
     * The setter for the Event's photo
     * @param photo The Event's photo as a bitmap {@link Bitmap} object
     */
    public void setPhoto(Bitmap photo) {
        if (photo != null) {

            while (photo.getByteCount() > MAX_PHOTO_SIZE) {
                double scaleFactor = 0.9;
                photo = Bitmap.createScaledBitmap(photo, (int) (photo.getWidth() * scaleFactor), (int) (photo.getHeight() * scaleFactor), true);
            }

//            this.photo = photo;
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
            this.photoString = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        }
        else {
            this.photoString = null;
        }
    }

    /**
     * The getter for the Event's photo string
     * @return The Event's photo as a string {@link String} object
     */
    public String getPhotoString() {
        return photoString;
    }

    /**
     * The setter for the Event's photo string
     * @param photoString The Event's photo as a string {@link String} object
     */
    public void setPhotoString(String photoString) {
        this.photoString = photoString;
    }

    /**
     * Getter for the Event's corresponding habit title
     * @return Returns a {@link String} containing the event's habit title
     */
    public String getHabitTitle() {
        return habitTitle;
    }

    /**
     * Setter for the Event's corresponding habit title
     * @param title The title of the Event's corresponding habit as {@link String}
     */
    public void setHabitTitle(String title) {
        this.habitTitle = habitTitle;
    }

    /**
     * Getter for the Event's comment
     * @return The Event's comment as a {@link String}
     */

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
        parcel.writeString(location); // temporary until implemented
//        location.writeToParcel(parcel, i);
        parcel.writeValue(photoString);
    }

    /**
     * Gets the Event's formatted date from the event date that is used
     * when the Event is viewed/edited or created on the creation fragment
     * Uses the formatting pattern 'EEEE, MMMM dd, yyyy' which shows the
     * Name of the day in the week, followed by the shortened Month, and then
     * the day of the month as a 2 digit number, and the year.
     * @return {@link String} of the formatted date using the {@link SimpleDateFormat} pattern of 'EEEE, MMMM dd, yyyy'
     */
    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        return formatter.format(dateEvent);
    }
}

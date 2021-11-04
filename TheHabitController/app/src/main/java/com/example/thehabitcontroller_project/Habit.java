package com.example.thehabitcontroller_project;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Steven
 * @version 1.0.0
 *
 * The Habit class for storing all information that pertains to a Habit.
 * This will store the title, reason, date start, and public status of a Habit
 */
public class Habit implements Parcelable, Comparable<Habit>{
    private String title;
    private String reason;
    private Date dateStart;
    private boolean isPublic;

    public Habit() {
        // empty constructor
    }

    /**
     * Initializes a Habit with all of its parameters
     * @param title     The title of a Habit
     * @param reason    The reason for the Habit
     * @param dateStart The start date of the habit
     * @param isPublic  If the habit is public; shared or not
     */
    public Habit(String title, String reason, Date dateStart, boolean isPublic) {
        this.title = title;
        this.reason = reason;
        this.dateStart = dateStart;
        this.isPublic = isPublic;
    }

    /**
     * Part of implementing {@link Parcelable}, we need a way
     * to instantiate a Habit using a Parcel so we can send it in a {@link android.os.Bundle}
     * @param in The Parcel object containing Habit information
     */
    protected Habit(Parcel in) {
        title = in.readString();
        reason = in.readString();
        dateStart = new Date(in.readLong());
        isPublic = in.readBoolean();
    }

    /**
     * The Creator class that is part of implementing {@link Parcelable}
     * Implements the necessary methods to create a Habit from a Parcel
     */
    public static final Creator<Habit> CREATOR = new Creator<Habit>() {
        @Override
        public Habit createFromParcel(Parcel in) {
            return new Habit(in);
        }

        @Override
        public Habit[] newArray(int size) {
            return new Habit[size];
        }
    };

    /**
     * Getter for the Habit's title
     * @return Returns a {@link String} containing the habit's Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the Habit's title
     * @param title The title of the Habit as {@link String}
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the Habit's reason
     * @return The Habit's reason as a {@link String}
     */
    public String getReason() {
        return reason;
    }

    /**
     * Setter for the Habit's reason
     * @param reason The Habit's reason as a {@link String}
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Getter for the Habit's starting date
     * @return The full date for when the Habit is to start as a {@link Date} object
     */
    public Date getDateStart() {
        return dateStart;
    }

    /**
     * The setter for the Habit's starting date
     * @param dateStart The Habit's starting date as a {@link Date} object
     */
    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    /**
     * Getter for the Habit's public status
     * @return {@literal boolean} True if Habit is set to public, False otherwise
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     * Gets the Habit's formatted date from its start date that is used
     * when the Habit is viewed/edited or created on the creation fragment
     * Uses the formatting pattern 'EEEE, MMMM dd, yyyy' which shows the
     * Name of the day in the week, followed by the shortened Month, and then
     * the day of the month as a 2 digit number, and the year.
     * @return {@link String} of the formatted date using the {@link SimpleDateFormat} pattern of 'EEEE, MMMM dd, yyyy'
     */
    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        return formatter.format(dateStart);
    }

    /**
     * Setter for the Habit's public setting
     * @param aPublic {@lteral boolean} True or False for if the Habit is to be public or not
     */
    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
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
     * Part of the implementation of {@link Parcelable} and writes all Habit attributes
     * to a {@link Parcel}
     * @param parcel the {@link Parcel} that the Habit writes to, "transforming" it into one.
     * @param i the {@link int} variable for implementing this Override; was not used here.
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(reason);
        parcel.writeLong(dateStart.getTime());
        parcel.writeBoolean(isPublic);
    }

    /**
     * Part of the implementation of {@link Comparable} so that using {@link java.util.ArrayList}
     * or other Java data structures could find and delete the object, as well as sorting if needed.
     * @param habit the Habit to compare to
     * @return the value that the Habits' titles compare to
     */
    @Override
    public int compareTo(Habit habit) {
        return title.compareTo(habit.getTitle());
    }
}

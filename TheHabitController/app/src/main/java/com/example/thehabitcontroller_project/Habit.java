package com.example.thehabitcontroller_project;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Habit implements Parcelable {
    private String title;
    private String reason;
    private Date dateStart;

    public Habit() {
        // empty constructor
    }

    public Habit(String title, String reason, Date dateStart) {
        this.title = title;
        this.reason = reason;
        this.dateStart = dateStart;
    }

    protected Habit(Parcel in) {
        title = in.readString();
        reason = in.readString();
        dateStart = new Date(in.readLong());
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(reason);
        parcel.writeLong(dateStart.getTime());
    }
}

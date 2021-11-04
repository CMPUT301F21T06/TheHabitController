package com.example.thehabitcontroller_project;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Habit implements Parcelable, Comparable<Habit>{
    private String title;
    private String reason;
    private Date dateStart;
    private boolean isPublic;

    public Habit() {
        // empty constructor
    }

    public Habit(String title, String reason, Date dateStart, boolean isPublic) {
        this.title = title;
        this.reason = reason;
        this.dateStart = dateStart;
        this.isPublic = isPublic;
    }

    protected Habit(Parcel in) {
        title = in.readString();
        reason = in.readString();
        dateStart = new Date(in.readLong());
        isPublic = in.readBoolean();
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

    public boolean isPublic() {
        return isPublic;
    }

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US);
        return formatter.format(dateStart);
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
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
        parcel.writeBoolean(isPublic);
    }

    @Override
    public int compareTo(Habit habit) {
        return title.compareTo(habit.getTitle());
    }
}

package com.limitco.agecalculator.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NotificationInfo {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "Name")
    String personName;

    @ColumnInfo(name = "Subject")
    String subject;

    @ColumnInfo(name = "Image")
    byte[] image;

    @ColumnInfo(name = "TimeInMills")
    long timeInMills;


    public NotificationInfo(String personName, String subject, byte[] image, long timeInMills) {
        this.personName = personName;
        this.subject = subject;
        this.image = image;
        this.timeInMills = timeInMills;
    }


    public int getId() {
        return id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public long getTimeInMills() {
        return timeInMills;
    }

    public void setTimeInMills(long timeInMills) {
        this.timeInMills = timeInMills;
    }
}

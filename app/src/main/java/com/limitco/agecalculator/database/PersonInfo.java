package com.limitco.agecalculator.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PersonInfo {


    @PrimaryKey (autoGenerate = true)
    int id;

    @ColumnInfo(name = "Name")
    String name;

    @ColumnInfo(name = "Subject")
    String subject;

    @ColumnInfo(name = "Image")
    byte[] image;

    @ColumnInfo(name = "Day")
    int day;

    @ColumnInfo(name = "Month")
    int month;

    @ColumnInfo(name = "Year")
    int year;

    @ColumnInfo(name = "Notification")
    int notify;

    @ColumnInfo(name = "RequestCode")
    int requestCode;


    public PersonInfo(String name, String subject, byte[] image, int day, int month, int year, int notify, int requestCode) {
        this.name = name;
        this.subject = subject;
        this.image = image;
        this.day = day;
        this.month = month;
        this.year = year;
        this.notify = notify;
        this.requestCode = requestCode;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNotify() {
        return notify;
    }

    public void setNotify(int notify) {
        this.notify = notify;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
}

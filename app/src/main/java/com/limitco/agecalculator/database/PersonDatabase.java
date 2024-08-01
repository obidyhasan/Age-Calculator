package com.limitco.agecalculator.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PersonInfo.class, NotificationInfo.class, ProfileInfo.class}, version = 1)
public abstract class PersonDatabase extends RoomDatabase {

    public abstract PersonDao personDao();

}

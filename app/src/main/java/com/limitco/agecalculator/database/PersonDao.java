package com.limitco.agecalculator.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PersonDao {

    /** for person */

    @Insert
    void insertData(PersonInfo personInfo);

    @Query("SELECT * FROM PersonInfo")
    List<PersonInfo> showData();

    @Query("DELETE FROM PersonInfo WHERE id = :id")
    void deleteById(int id);


//    @Query("UPDATE PersonTable SET Name  = :name, Password = :pass WHERE id = :id")
//    void updateById(int id, String name, String pass);


    /** for notification */
    @Insert
    void insertNotification(NotificationInfo notificationInfo);

    @Query("SELECT * FROM NotificationInfo ")
    List<NotificationInfo> showNotification();

    @Query("DELETE FROM NotificationInfo WHERE id = :id")
    void deleteNotification(int id);




    /** for profile */
    @Insert
    void insertProfile(ProfileInfo ProfileInfo);

    @Query("SELECT * FROM ProfileInfo ")
    List<ProfileInfo> showProfile();

    @Query("DELETE FROM ProfileInfo WHERE id = :id")
    void deleteProfile(int id);

    @Query("UPDATE ProfileInfo SET Name = :name, Subject = :subject, Image = :image, Day = :day, Month = :month, Year = :year WHERE id = :id")
    void updateProfile(int id,String name, String subject, byte[] image, int day, int month, int year);

}

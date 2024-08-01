package com.limitco.agecalculator.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import com.limitco.agecalculator.MainActivity;
import com.limitco.agecalculator.R;
import com.limitco.agecalculator.database.NotificationInfo;
import com.limitco.agecalculator.database.PersonDao;
import com.limitco.agecalculator.database.PersonDatabase;

/**
 * Create by Naim on 11/02/2022
 */

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "Notification";

    @Override
    public void onReceive(Context context, Intent intent) {


        String name = intent.getStringExtra("name");
        String subject = intent.getStringExtra("subject");
        byte[] image = intent.getByteArrayExtra("image");
        long timeInMills = intent.getLongExtra("time",0);

        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0, image.length);


        Intent intent1 = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_IMMUTABLE);


        /** Show Notification **/
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence channelName = "Notification Alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,channelName,importance);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setLargeIcon(bitmap);
        builder.setContentTitle(subject);
        builder.setContentText("Today is "+name+"'s "+subject+". You can wish him well");
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;

        notificationManager.notify(12,builder.build());



        /** Save notification to Notification Database */
        PersonDatabase database = Room.databaseBuilder(context, PersonDatabase.class, "Person_Database")
                .allowMainThreadQueries().build();
        PersonDao personDao = database.personDao();

        personDao.insertNotification(new NotificationInfo(name,subject,image ,timeInMills));


        /** Save notification count data to shared preferences */
        SharedPreferences sharedPreferences = context.getSharedPreferences("number",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("number",1);
        editor.commit();
        editor.apply();

    }
}

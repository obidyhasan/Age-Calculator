package com.limitco.agecalculator.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.limitco.agecalculator.MainActivity;
import com.limitco.agecalculator.R;
import com.limitco.agecalculator.database.PersonDao;
import com.limitco.agecalculator.database.PersonDatabase;
import com.limitco.agecalculator.database.ProfileInfo;

import java.io.ByteArrayOutputStream;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences onBoardingScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        /** Change Status Bar Color **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.deep_blue));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.deep_blue));
        }



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                onBoardingScreen = getSharedPreferences("onBoardingScreen",MODE_PRIVATE);
                boolean isFirstTime = onBoardingScreen.getBoolean("firstTime",true);

                if (isFirstTime){

                    SharedPreferences.Editor editor = onBoardingScreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();


                    /** Auto save demo user info to Room Database */
                    try {
                        AutoSaveUserInfo();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error"+e, Toast.LENGTH_SHORT).show();
                    }

                    startActivity(new Intent(getApplicationContext(),OnBoarding.class));
                    finish();

                }
                else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }


            }
        },1600 );

    }


    /** Save demo user info */
    private void AutoSaveUserInfo() {

        try {

            String uri = "@drawable/avatar_4";
            int imageResource = getResources().getIdentifier(uri,null,getPackageName());
            Drawable res = getDrawable(imageResource);
            byte[] imageByte = ImageViewToByte(res);

            /** Save Data to Room Database */
            PersonDatabase database = Room.databaseBuilder(getApplicationContext(), PersonDatabase.class, "Person_Database")
                    .allowMainThreadQueries().build();
            PersonDao personDao = database.personDao();

            personDao.insertProfile(new ProfileInfo(1,"Guest","Birthday",imageByte, 32,13,1,1));

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }


    }



    /** Convert image to byte[] */
    private byte[] ImageViewToByte(Drawable personImg) {

        Bitmap bitmap = ((BitmapDrawable) personImg).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,30,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;

    }


}
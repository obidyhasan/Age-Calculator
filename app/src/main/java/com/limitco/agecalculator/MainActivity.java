package com.limitco.agecalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.limitco.agecalculator.fragment.HomeFragment;
import com.limitco.agecalculator.fragment.NotificationFragment;
import com.limitco.agecalculator.fragment.ProfileFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Create by Naim on 10/02/2022
 */

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    BadgeDrawable badgeNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Push Notification from firebase */
        FirebaseMessaging.getInstance().subscribeToTopic("notifications");

        /** Night mode off **/
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        /**
         * Bottom Navigation
         */
        bottomNavigationView = findViewById(R.id.bottomNavigationId);
        // set default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainerId,new HomeFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()){

                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;

                    case R.id.nav_notification:
                        fragment = new NotificationFragment();

                        /** When user open notification fragment clear and Invisible the badge notification....*/
                        BadgeDrawable badgeNotify = bottomNavigationView.getBadge(R.id.nav_notification);
                        badgeNotify.clearNumber();
                        badgeNotify.setVisible(false);
                        /** And also remove the shared preferences data */
                        SharedPreferences sharedPreferences = getSharedPreferences("number",Context.MODE_PRIVATE);
                        sharedPreferences.edit().clear().commit();

                        break;

                    case R.id.nav_profile:
                        fragment = new ProfileFragment();
                        break;

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainerId,fragment).commit();

                return true;
            }

        });


        /** get data from shared preferences */
        SharedPreferences sharedPreferences = getSharedPreferences("number", Context.MODE_PRIVATE);
        int number = sharedPreferences.getInt("number",0);

        /** Set Notification Badge */
        badgeNotification = bottomNavigationView.getOrCreateBadge(R.id.nav_notification);
        badgeNotification.setBackgroundColor(Color.RED);
        badgeNotification.setBadgeTextColor(Color.WHITE);
        badgeNotification.setMaxCharacterCount(2);

        if (number == 0){
            badgeNotification.setVisible(false);
        }else {
            badgeNotification.setVisible(true);
            badgeNotification.setNumber(number);
        }

    }



}
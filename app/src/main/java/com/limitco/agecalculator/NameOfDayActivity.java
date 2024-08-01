package com.limitco.agecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NameOfDayActivity extends AppCompatActivity {

    /** Variable */
    EditText dayEdit, monthEdit, yearEdit;
    TextView dayName;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_of_day);

        // For banner ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        /** Change Status Bar Color **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ActivityCompat.getColor(this,R.color.card2));
            getWindow().setNavigationBarColor(ActivityCompat.getColor(this,R.color.card2));
        }


        // Edit Text Hooks
        dayEdit = findViewById(R.id.dayDayNameEditId);
        monthEdit = findViewById(R.id.monthDayNameEditId);
        yearEdit = findViewById(R.id.yearDayNameEditId);

        // Text View Hooks
        dayName = findViewById(R.id.dayNameTextId);

        /** Set focus to day edit text */
        dayEdit.requestFocus();

        /** Auto go to next edit text and more customize */
        editTextUserExperience();

        /** setBannerAds */
        setBannerAds();

    }


    private void setBannerAds() {

        // admob adView
        mAdView = findViewById(R.id.dayNameAdViewId);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    /** Calculate Name of day */
    public void calculateDayNameBtn(View view) {

        if (!valDay() | !valMonth() | !valYear()){
            return;
        }

        try {

            int day = Integer.parseInt(dayEdit.getText().toString());
            int month = Integer.parseInt(monthEdit.getText().toString());
            int year = Integer.parseInt(yearEdit.getText().toString());

            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month-1,day);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(calendar.getTime());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
            String dayOfWeekName = simpleDateFormat.format(calendar.getTime());

            dayName.setText(dayOfWeekName);
            dayName.animate().alpha(1f).setDuration(1000);

            HideKeyboardFormUser();

        }
        catch (Exception e){

            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }



    }

    /** Clear all data */
    public void clearDayNameBtn(View view) {

        dayEdit.setText("");
        monthEdit.setText("");
        yearEdit.setText("");

        // set focus to day edit text
        dayEdit.requestFocus();

        dayName.animate().alpha(0f);

    }

    // Auto go to next edit text
    private void editTextUserExperience() {

        dayEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String regex = "4|5|6|7|8|9";

                if (dayEdit.getText().toString().matches(regex)){

                    if (dayEdit.getText().toString().length() == 1){
                        monthEdit.requestFocus();
                        dayEdit.setText(0+dayEdit.getText().toString());
                    }
                }

                if (dayEdit.getText().toString().length() == 2){

                    monthEdit.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        monthEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String regex = "2|3|4|5|6|7|8|9";

                if (monthEdit.getText().toString().matches(regex)){

                    if (monthEdit.getText().toString().length() == 1){
                        yearEdit.requestFocus();
                        monthEdit.setText(0+monthEdit.getText().toString());
                    }
                }

                if (monthEdit.getText().toString().length() == 2){

                    yearEdit.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        yearEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (yearEdit.getText().toString().length() == 4){

                    HideKeyboardFormUser();

                }

            }
        });
    }
    // Hide Keyboard
    public void HideKeyboardFormUser(){
        View view = getCurrentFocus();
        InputMethodManager hideKeyboard  = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        hideKeyboard.hideSoftInputFromWindow( view.getWindowToken(), 0);
    }

    public void nameOfDayBackBtn(View view) {
        onBackPressed();
    }


    private boolean valDay(){

        String dayStr = dayEdit.getText().toString().trim();
        String regex = "[0-2][1-9]|30|31|10|1|2|3|20";

        if (dayStr.isEmpty()){
            dayEdit.setError("Filed can't be empty!");
            return false;
        }
        else if (!dayStr.matches(regex)){
            dayEdit.setError("Invalid Day");
            return false;
        }

        else {
            return true;
        }

    }

    private boolean valMonth(){

        String monthStr = monthEdit.getText().toString().trim();
        String regex = "1|02|03|04|05|06|07|08|09|10|11|12|01";

        if (monthStr.isEmpty()){
            monthEdit.setError("Filed can't be empty!");
            return false;
        }
        else if (!monthStr.matches(regex)){
            monthEdit.setError("Invalid Month");
            return false;
        }
        else {
            return true;
        }

    }

    private boolean valYear(){

        String yearStr = yearEdit.getText().toString().trim();
        if (yearStr.isEmpty()){
            yearEdit.setError("Filed can't be empty!");
            return false;
        }
        else {
            return true;
        }

    }

}
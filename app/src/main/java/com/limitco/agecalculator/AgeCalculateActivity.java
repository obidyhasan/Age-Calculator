package com.limitco.agecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Create by Naim on 13/02/2022
 */

public class AgeCalculateActivity extends AppCompatActivity {

    /** Variable **/
    EditText recentDay, recentMonth, recentYear, birthDay, birthMonth, birthYear;
    TextView ageDay, ageMonth, ageYear;
    TextView birthDayNext, birthMonthNext;
    TextView totalMonths, totalWeeks, totalDays, totalHours, totalMinutes, totalSeconds;
    ImageView todayCalender, birthCalender;

    Animation topAnim;
    LinearLayout ageYearLayout, ageMonthLayout, ageDayLayout, moreInfoLayout;

    Calendar calendar;
    SimpleDateFormat DateDay, DateMonth, DateYear;

    /** Age Calculate Variable */
    int recentDayInt, recentMonthInt, recentYearInt, birthDayInt, birthMonthInt, birthYearInt;
    int calculated_day, calculated_month, calculated_year;

    int totalsDays, tolMonth, tolYear, tolDay;
    int numOfWeek, numOfMonth, numOfHour, numOfMinutes, numOfSeconds;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_calculate);



        // For banner ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        /** Change Status Bar Color **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#FCECDF"));
            getWindow().setNavigationBarColor(Color.parseColor("#FCECDF"));
        }

        // Find Edit Text by id
        recentDay = findViewById(R.id.recentDayId);
        recentMonth = findViewById(R.id.recentMonthId);
        recentYear = findViewById(R.id.recentYearId);
        birthDay = findViewById(R.id.birthDayId);
        birthMonth = findViewById(R.id.birthMonthId);
        birthYear = findViewById(R.id.birthYearId);


        // Find Age Text View by Id
        ageDay = findViewById(R.id.ageDayText);
        ageMonth = findViewById(R.id.ageMonthText);
        ageYear = findViewById(R.id.ageYearText);


        // Find More Info Text View by Id
        totalMonths = findViewById(R.id.totalMonthTextId);
        totalWeeks = findViewById(R.id.totalWeeksTextId);
        totalDays = findViewById(R.id.totalDaysTextId);
        totalHours = findViewById(R.id.totalHoursTextId);
        totalMinutes = findViewById(R.id.totalMinutesTextId);
        totalSeconds = findViewById(R.id.totalSecondsTextId);


        // Find Next Birth Text View by  Id
        birthDayNext = findViewById(R.id.nextBirthDayTextId);
        birthMonthNext = findViewById(R.id.nextBirthMonthTextId);


        // Find Calender Icon by Id
        todayCalender = findViewById(R.id.todayDateCalendarId);
        birthCalender = findViewById(R.id.birthDateCalendarId);


        // Animation
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);

        // Layout Hooks
        ageYearLayout = findViewById(R.id.ageYearLayoutAnimId);
        ageMonthLayout = findViewById(R.id.ageMonthLayoutAnimId);
        ageDayLayout = findViewById(R.id.ageDayLayoutAnimId);
        moreInfoLayout = findViewById(R.id.moreInfoLayoutId);





        /** Auto go to next edit text and more customize */
        editTextUserExperience();
        /** Set to recent date edittext to recent date */
        setEditTextToRecentDate();
        /** Set to Calender icon To Calender */
        setCalenderToCalenderIcon();
        /** setBannerAds */
        setBannerAds();

    }

    private void setBannerAds() {

        // admob adView
        mAdView = findViewById(R.id.ageCalAdViewId);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    /** Calculate Age */
    public void calculateAgeBtn(View view) {


        if (!valRecentDay() | !valRecentMonth() | !valRecentYear() | !valBirthDay() | !valBirthMonth() | !valBirthYear()){

            // Age Clear
            ageDay.setText("");
            ageMonth.setText("");
            ageYear.setText("");

//        moreInfoLayout.animate().alpha(0f).setDuration(1000);

            // animation
            totalDays.animate().alpha(0f).setDuration(1000);
            totalMonths.animate().alpha(0f).setDuration(1000);
            totalHours.animate().alpha(0f).setDuration(1000);
            totalWeeks.animate().alpha(0f).setDuration(1000);
            totalMinutes.animate().alpha(0f).setDuration(1000);
            totalSeconds.animate().alpha(0f).setDuration(1000);


            // Animation
            ageDayLayout.animate().alpha(0f);
            ageMonthLayout.animate().alpha(0f);
            ageYearLayout.animate().alpha(0f);

            // Next BirthDay Animation
            birthDayNext.animate().alpha(0f).setDuration(1000);
            birthMonthNext.animate().alpha(0f).setDuration(1000);

            return;
        }

        try {


            // Recent Day
            String recentDayString = recentDay.getText().toString();
            String recentMonthString = recentMonth.getText().toString();
            String RecentYearString = recentYear.getText().toString();

            recentDayInt = Integer.parseInt(recentDayString);
            recentMonthInt = Integer.parseInt(recentMonthString);
            recentYearInt = Integer.parseInt(RecentYearString);

            Date now = new Date(recentYearInt,recentMonthInt,recentDayInt);

            // Birth Day
            String birthDayString = birthDay.getText().toString();
            String birthMonthString = birthMonth.getText().toString();
            String birthYearString = birthYear.getText().toString();

            birthDayInt = Integer.parseInt(birthDayString);
            birthMonthInt = Integer.parseInt(birthMonthString);
            birthYearInt = Integer.parseInt(birthYearString);

            Date dob = new Date(birthYearInt,birthMonthInt,birthDayInt);


            if (dob.after(now)) {
                Toast.makeText(AgeCalculateActivity.this, "Birthday can't in future", Toast.LENGTH_SHORT).show();

                // Age Clear
                ageDay.setText("");
                ageMonth.setText("");
                ageYear.setText("");

//        moreInfoLayout.animate().alpha(0f).setDuration(1000);

                // animation
                totalDays.animate().alpha(0f).setDuration(1000);
                totalMonths.animate().alpha(0f).setDuration(1000);
                totalHours.animate().alpha(0f).setDuration(1000);
                totalWeeks.animate().alpha(0f).setDuration(1000);
                totalMinutes.animate().alpha(0f).setDuration(1000);
                totalSeconds.animate().alpha(0f).setDuration(1000);


                // Animation
                ageDayLayout.animate().alpha(0f);
                ageMonthLayout.animate().alpha(0f);
                ageYearLayout.animate().alpha(0f);

                // Next BirthDay Animation
                birthDayNext.animate().alpha(0f).setDuration(1000);
                birthMonthNext.animate().alpha(0f).setDuration(1000);

                return;
            }

            // days of every month
            int months[] = {31, 28, 31, 30, 31, 30, 31,
                    31, 30, 31, 30, 31};


            if (birthDayInt > recentDayInt) {
                recentDayInt = recentDayInt + months[birthMonthInt - 1];
                recentMonthInt = recentMonthInt - 1;
            }


            if (birthMonthInt > recentMonthInt) {
                recentYearInt = recentYearInt - 1;
                recentMonthInt = recentMonthInt + 12;
            }


            // calculate date, month, year
            calculated_day = recentDayInt - birthDayInt;
            calculated_month = recentMonthInt - birthMonthInt;
            calculated_year = recentYearInt - birthYearInt;

            // Set Age
            ageDay.setText(String.valueOf(calculated_day));
            ageMonth.setText(String.valueOf(calculated_month));
            ageYear.setText(String.valueOf(calculated_year));

            // Add Animation
            ageDay.setAnimation(topAnim);
            ageMonth.setAnimation(topAnim);
            ageYear.setAnimation(topAnim);

            // Add Layout Animation
            ageYearLayout.animate().alpha(1f).setDuration(1000);
            ageMonthLayout.animate().alpha(1f).setDuration(1000);
            ageDayLayout.animate().alpha(1f).setDuration(1000);
            moreInfoLayout.animate().alpha(1f).setDuration(1000);

            nextBirthDayCalculated();
            showMoreInfo();

        }
        catch (Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    /** Next BirthDay Calculate */
    public void nextBirthDayCalculated(){

        Calendar current = Calendar.getInstance();
        current.set(recentYearInt, recentMonthInt, recentDayInt);

        Calendar birthday = Calendar.getInstance();
        birthday.set(birthYearInt, birthMonthInt, birthDayInt);

        long difference = birthday.getTimeInMillis() - current.getTimeInMillis();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(difference);

        birthMonthNext.setText(String.valueOf(cal.get(Calendar.MONTH)));
        birthMonthNext.animate().alpha(1f).setDuration(1000);

        birthDayNext.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)-1));
        birthDayNext.animate().alpha(1f).setDuration(1000);

    }

    /** Show More Info */
    public void showMoreInfo (){

        totalDateCalculate();

        String daysStr = String.valueOf(totalsDays);
        numOfWeek = totalsDays/7;

        numOfMonth = tolYear*12 + tolMonth;

        numOfHour = totalsDays*24;
        String hourStr = String.valueOf(numOfHour);

        numOfMinutes = numOfHour*60;
        String minutesStr = String.valueOf(numOfMinutes);

        numOfSeconds = numOfMinutes*60;
        String seconds = String.valueOf(numOfSeconds);

        totalDays.setText(daysStr + " Days");
        totalDays.animate().alpha(1f).setDuration(1000);

        totalWeeks.setText(numOfWeek+" Weeks " + totalsDays%7 + " Days");
        totalWeeks.animate().alpha(1f).setDuration(1000);

        totalMonths.setText(numOfMonth+ " Months " + tolDay + " Days");
        totalMonths.animate().alpha(1f).setDuration(1000);


        totalHours.setText(hourStr+ " Hours");
        totalHours.animate().alpha(1f).setDuration(1000);

        totalMinutes.setText(minutesStr+ " Minutes");
        totalMinutes.animate().alpha(1f).setDuration(1000);

        totalSeconds.setText(seconds+ " Seconds");
        totalSeconds.animate().alpha(1f).setDuration(1000);


    }

    /** show more info add */
    public void totalDateCalculate (){

        // Recent Day
        String recentDayString = recentDay.getText().toString();
        String recentMonthString = recentMonth.getText().toString();
        String RecentYearString = recentYear.getText().toString();

        recentDayInt = Integer.parseInt(recentDayString);
        recentMonthInt = Integer.parseInt(recentMonthString);
        recentYearInt = Integer.parseInt(RecentYearString);


        // Birth Day
        String birthDayString = birthDay.getText().toString();
        String birthMonthString = birthMonth.getText().toString();
        String birthYearString = birthYear.getText().toString();

        birthDayInt = Integer.parseInt(birthDayString);
        birthMonthInt = Integer.parseInt(birthMonthString);
        birthYearInt = Integer.parseInt(birthYearString);


        totalsDays = gregorianDays(recentYearInt, recentMonthInt,recentDayInt) - gregorianDays(birthYearInt,birthMonthInt, birthDayInt);

        tolYear = recentYearInt - birthYearInt;
        tolMonth = recentMonthInt - birthMonthInt;

        if (tolMonth < 0){
            tolYear = tolYear - 1;
            tolMonth = tolMonth + 12;
        }

        tolDay = recentDayInt - birthDayInt;
        if (tolDay < 0){

            if (tolMonth > 0){
                tolMonth = tolMonth -1;
                tolDay = tolDay + MonthsToDays(recentMonthInt - 1, recentYearInt);
            }
            else {
                tolYear = tolYear -1;
                tolMonth = 11;
                tolDay = tolDay + MonthsToDays(recentMonthInt -1, recentYearInt);
            }
        }

    }

    /** Clear Button */
    public void clearAgeCalculateBtn(View view) {

        // Date of Birth Clear
        birthDay.setText("");
        birthMonth.setText("");
        birthYear.setText("");

        // set focus to birthday edit text
        birthDay.requestFocus();

        // Age Clear
        ageDay.setText("");
        ageMonth.setText("");
        ageYear.setText("");

//        moreInfoLayout.animate().alpha(0f).setDuration(1000);

        // animation
        totalDays.animate().alpha(0f).setDuration(1000);
        totalMonths.animate().alpha(0f).setDuration(1000);
        totalHours.animate().alpha(0f).setDuration(1000);
        totalWeeks.animate().alpha(0f).setDuration(1000);
        totalMinutes.animate().alpha(0f).setDuration(1000);
        totalSeconds.animate().alpha(0f).setDuration(1000);


        // Animation
        ageDayLayout.animate().alpha(0f);
        ageMonthLayout.animate().alpha(0f);
        ageYearLayout.animate().alpha(0f);

        // Next BirthDay Animation
        birthDayNext.animate().alpha(0f).setDuration(1000);
        birthMonthNext.animate().alpha(0f).setDuration(1000);


    }


    // Set Calender icon to Calender
    private void setCalenderToCalenderIcon() {


        // Calender
        calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);



        // Today's Date
        todayCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(AgeCalculateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        month = month+1;

                        String day1 = String.valueOf(day);
                        String month1 = String.valueOf(month);
                        String year1 = String.valueOf(year);

                        recentDay.setText(day1);
                        recentMonth.setText(month1);;
                        recentYear.setText(year1);

                        recentDay.setError(null);
                        recentMonth.setError(null);
                        recentYear.setError(null);

                    }



                },year,month,day);
                datePickerDialog.show();

            }
        });

        // Date of Birth
        birthCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(AgeCalculateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        month = month+1;

                        String day1 = String.valueOf(day);
                        String month1 = String.valueOf(month);
                        String year1 = String.valueOf(year);

                        birthDay.setText(day1);
                        birthMonth.setText(month1);
                        birthYear.setText(year1);

                        birthDay.setError(null);
                        birthMonth.setError(null);
                        birthYear.setError(null);

                    }

                },year,month,day);
                datePickerDialog.show();
            }
        });

    }

    // Set Edit to Recent Date
    private void setEditTextToRecentDate() {

        Calendar calendar = Calendar.getInstance();
        // Auto set recent day
        DateDay = new SimpleDateFormat("dd");
        String dayAuto = DateDay.format(calendar.getTime());
        recentDay.setText(dayAuto);

        // Auto set recent month
        DateMonth = new SimpleDateFormat("MM");
        String monthAuto = DateMonth.format(calendar.getTime());
        recentMonth.setText(monthAuto);

        // Auto set recent year
        DateYear = new SimpleDateFormat("yyyy");
        String yearAuto = DateYear.format(calendar.getTime());
        recentYear.setText(yearAuto);
    }

    // Auto go to next edit text
    private void editTextUserExperience() {

        recentDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String regex = "4|5|6|7|8|9";

                if (recentDay.getText().toString().matches(regex)){

                    if (recentDay.getText().toString().length() == 1){
                        recentMonth.requestFocus();
                        recentDay.setText(0+recentDay.getText().toString());
                    }
                }

                if (recentDay.getText().toString().length() == 2){
                    recentMonth.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (recentDay.getText().toString().length() == 2){
                    HideKeyboardFormUser();
                }

            }
        });


        recentMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String regex = "2|3|4|5|6|7|8|9";

                if (recentMonth.getText().toString().matches(regex)){

                    if (recentMonth.getText().toString().length() == 1){
                        recentYear.requestFocus();
                        recentMonth.setText(0+recentMonth.getText().toString());
                    }
                }

                if (recentMonth.getText().toString().length() == 2){

                    recentYear.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (recentMonth.getText().toString().length() == 2){
                    HideKeyboardFormUser();
                }
            }
        });


        recentYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (recentYear.getText().toString().length() == 4){

                    birthDay.requestFocus();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (recentYear.getText().toString().length() == 4){

                    HideKeyboardFormUser();

                }

            }
        });


        birthDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String regex = "4|5|6|7|8|9";

                if (birthDay.getText().toString().matches(regex)){

                    if (birthDay.getText().toString().length() == 1){
                        birthMonth.requestFocus();
                        birthDay.setText(0+birthDay.getText().toString());
                    }
                }

                if (birthDay.getText().toString().length() == 2){

                    birthMonth.requestFocus();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        birthMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String regex = "2|3|4|5|6|7|8|9";

                if (birthMonth.getText().toString().matches(regex)){

                    if (birthMonth.getText().toString().length() == 1){
                        birthYear.requestFocus();
                        birthMonth.setText(0+birthMonth.getText().toString());
                    }
                }

                if (birthMonth.getText().toString().length() == 2){

                    birthYear.requestFocus();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        birthYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(birthYear.getText().toString().length() == 4){
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

    // Back Button
    public void ageCalculateBackBtn(View view) {
        onBackPressed();
    }


    private boolean valRecentDay (){

        String recentDayStr = recentDay.getText().toString().trim();
        String regex = "[0-2][1-9]|30|31|10|1|2|3|20";
        if (recentDayStr.isEmpty()){
            recentDay.setError("Filed can't be empty!");
            return false;
        }
        else if (!recentDayStr.matches(regex)){
            recentDay.setError("Invalid Day");
            return false;
        }
        else {
            return true;
        }
    }

    private boolean valRecentMonth (){

        String recentMonthStr = recentMonth.getText().toString().trim();
        String regex = "1|02|03|04|05|06|07|08|09|10|11|12|01";
        if (recentMonthStr.isEmpty()){
            recentMonth.setError("Filed can't be empty!");
            return false;
        }
        else if (!recentMonthStr.matches(regex)){
            recentMonth.setError("Invalid Month");
            return false;
        }
        else {
            return true;
        }
    }

    private boolean valRecentYear (){
        String recentYearStr = recentYear.getText().toString().trim();
        if (recentYearStr.isEmpty()){
            recentYear.setError("Filed can't be empty!");
            return false;
        }
        else {
            return true;
        }

    }

    private boolean valBirthDay (){

        String birthDayStr = birthDay.getText().toString().trim();
        String regex1= "[0-2][1-9]|30|31|10|1|2|3|20";
        if (birthDayStr.isEmpty()){
            birthDay.setError("Filed can't be empty!");
            return false;
        }
        else if (!birthDayStr.matches(regex1)){
            birthDay.setError("Invalid Day");
            return false;
        }
        else {
            return true;
        }
    }

    private boolean valBirthMonth (){

        String birthMonthStr = birthMonth.getText().toString().trim();
        String regex = "1|02|03|04|05|06|07|08|09|10|11|12|01";
        if (birthMonthStr.isEmpty()){
            birthMonth.setError("Filed can't be empty!");
            return false;
        }
        else if (!birthMonthStr.matches(regex)){
            birthMonth.setError("Invalid Month");
            return false;
        }
        else {
            return true;
        }
    }

    private boolean valBirthYear (){

        String birthYearStr = birthYear.getText().toString().trim();
        if (birthYearStr.isEmpty()){
            birthYear.setError("Filed can't be empty!");
            return false;
        }
        else {
            return true;
        }
    }


    // Month to Days
    public static int MonthsToDays(int tMonth, int tYear) {
        if (tMonth == 1 || tMonth == 3 || tMonth == 5 || tMonth == 7
                || tMonth == 8 || tMonth == 10 || tMonth == 12) {
            return 31;
        } else if (tMonth == 2) {
            if (tYear % 4 == 0) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 30;
        }
    } // for age calculate
    private static int gregorianDays(int year, int month, int day) {
        month = (month + 9) % 12;
        year = year - month / 10;
        return 365 * year + year / 4 - year / 100 + year / 400 + (month * 306 + 5) / 10 + (day - 1);

    } // for age calculate


}
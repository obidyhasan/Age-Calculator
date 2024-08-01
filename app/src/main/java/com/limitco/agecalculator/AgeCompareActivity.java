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
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class AgeCompareActivity extends AppCompatActivity {

    /** Variable */
    EditText dayPerson1, monthPerson1, yearPerson1, person2Day, person2Month, person2Year, person1Name, person2Name;
    TextView compareText, per1Age , per1ageMore, per1Name, per2Age, per2AgeMore, per2Name, namePer1, namePer2, difference;

    Calendar calendar;
    SimpleDateFormat DateDay, DateMonth, DateYear;

    int currentDay, currentMonth, currentYear, birthDay, birthMonth, birthYear, calculated_date, calculated_month, calculated_year;
    int dayPer1Int, monthPer1Int, yearPer1Int, dayPer2Int, monthPer2Int, yearPer2Int;

    LinearLayout detailsLayout;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_compare);


        // For banner ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        /** Change Status Bar Color **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ActivityCompat.getColor(this,R.color.card3));
            getWindow().setNavigationBarColor(ActivityCompat.getColor(this,R.color.card3));
        }


        /** Edit Text Find by Id */
        dayPerson1 = findViewById(R.id.person1DayId);
        monthPerson1 = findViewById(R.id.person1MonthId);
        yearPerson1 = findViewById(R.id.person1YearId);
        person1Name = findViewById(R.id.person1NameId);
        person2Day = findViewById(R.id.person2DayId);
        person2Month = findViewById(R.id.person2MonthId);
        person2Year = findViewById(R.id.person2YearId);
        person2Name = findViewById(R.id.person2NameId);

        /** Age Compare Text view find by Id */
        compareText = findViewById(R.id.compareAgeTextId);


        /** More Details Text View Find by Id */
        per1Name = findViewById(R.id.per1SetNameTextId);
        per1Age = findViewById(R.id.per1SetYearTextId);
        per1ageMore = findViewById(R.id.per1SetMonthDayTextId);
        per2Name = findViewById(R.id.per2SetNameTextId);
        per2Age = findViewById(R.id.per2SetYearTextId);
        per2AgeMore = findViewById(R.id.per2SetMonthDayTextId);

        /** Result Text View find by Id */
        namePer1 = findViewById(R.id.per1CompareNameId);
        namePer2 = findViewById(R.id.per2CompareNameId);
        difference = findViewById(R.id.compareNameId);
        detailsLayout = findViewById(R.id.compareDetailsLayoutId);


        /** Set Auto Focus */
        person1Name.requestFocus();

        /** Auto go to next edit text and more customize */
        editTextUserExperience();
        /** setBannerAds */
        setBannerAds();

    }

    private void setBannerAds() {

        // admob adView
        mAdView = findViewById(R.id.ageCompareAdViewId);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
    /** Calculate Person 1 Age */
    private void calculatePerson1Age() {

        // Calender
        calendar = Calendar.getInstance();

        // Auto set Date Day
        DateDay = new SimpleDateFormat("dd");
        String dayAuto = DateDay.format(calendar.getTime());

        DateMonth = new SimpleDateFormat("MM");
        String monthAuto = DateMonth.format(calendar.getTime());

        DateYear = new SimpleDateFormat("yyyy");
        String yearAuto = DateYear.format(calendar.getTime());


        // Calculate
        currentDay = Integer.parseInt(dayAuto);
        currentMonth = Integer.parseInt(monthAuto);
        currentYear = Integer.parseInt(yearAuto);

        Date now = new Date(currentYear, currentMonth, currentDay);

        birthDay = Integer.valueOf(dayPerson1.getText().toString());
        birthMonth = Integer.valueOf(monthPerson1.getText().toString());
        birthYear = Integer.valueOf(yearPerson1.getText().toString());

        Date dob = new Date(birthYear, birthMonth, birthDay);

//        if (dob.after(now)) {
//            Toast.makeText(AgeCompareActivity.this, "Birthday can't in future", Toast.LENGTH_SHORT).show();
//            compareText.setText("");
//            return;
//        }
        // days of every month
        int months[] = {31, 28, 31, 30, 31, 30, 31,
                31, 30, 31, 30, 31};


        if (birthDay > currentDay) {
            currentDay = currentDay + months[birthMonth - 1];
            currentMonth = currentMonth - 1;
        }


        if (birthMonth > currentMonth) {
            currentYear = currentYear - 1;
            currentMonth = currentMonth + 12;
        }

        // calculate date, month, year
        calculated_date = currentDay - birthDay;
        calculated_month = currentMonth - birthMonth;
        calculated_year = currentYear - birthYear;

        per1Name.setText(person1Name.getText().toString());
        per1Age.setText(calculated_year + " Years");
        per1ageMore.setText(calculated_month + " Months " + calculated_date + " Days");


    }

    /** Calculate Person 2 Age */
    private void calculatePerson2Age() {

        // Calender
        calendar = Calendar.getInstance();

        // Auto set Date Day
        DateDay = new SimpleDateFormat("dd");
        String dayAuto = DateDay.format(calendar.getTime());

        DateMonth = new SimpleDateFormat("MM");
        String monthAuto = DateMonth.format(calendar.getTime());

        DateYear = new SimpleDateFormat("yyyy");
        String yearAuto = DateYear.format(calendar.getTime());


        // Calculate
        currentDay = Integer.parseInt(dayAuto);
        currentMonth = Integer.parseInt(monthAuto);
        currentYear = Integer.parseInt(yearAuto);

        Date now = new Date(currentYear, currentMonth, currentDay);

        birthDay = Integer.valueOf(person2Day.getText().toString());
        birthMonth = Integer.valueOf(person2Month.getText().toString());
        birthYear = Integer.valueOf(person2Year.getText().toString());

        Date dob = new Date(birthYear, birthMonth, birthDay);

//        if (dob.after(now)) {
//            Toast.makeText(AgeCompareActivity.this, "Birthday can't in future", Toast.LENGTH_SHORT).show();
//            compareText.setText("");
//            return;
//        }
        // days of every month
        int months[] = {31, 28, 31, 30, 31, 30, 31,
                31, 30, 31, 30, 31};

        if (birthDay > currentDay) {
            currentDay = currentDay + months[birthMonth - 1];
            currentMonth = currentMonth - 1;
        }

        if (birthMonth > currentMonth) {
            currentYear = currentYear - 1;
            currentMonth = currentMonth + 12;
        }

        // calculate date, month, year
        calculated_date = currentDay - birthDay;
        calculated_month = currentMonth - birthMonth;
        calculated_year = currentYear - birthYear;

        per2Age.setText(calculated_year + " Years");
        per2AgeMore.setText(calculated_month + " Months " + calculated_date + " Days");
        per2Name.setText(person2Name.getText().toString());

    }

    /** Calculate Age Compare */
    public void calculateAgeCompareBtn(View view) {

        if (!valPerson1Day() | !valPerson1Month() | !valPer1Year() | !valPerson2Day() | !valPerson2Month() | !valPer2Year()){

//            detailsLayout.animate().alpha(0f).setDuration(1000);
            detailsLayout.setVisibility(View.GONE);

            dayPerson1.setText("");
            monthPerson1.setText("");
            yearPerson1.setText("");
            person2Day.setText("");
            person2Month.setText("");
            person2Year.setText("");

            person1Name.setText("");
            person2Name.setText("");

            compareText.animate().alpha(0f).setDuration(1000);

            return;
        }

        calculatePerson1Age();
        calculatePerson2Age();

        dayPer1Int = Integer.parseInt(dayPerson1.getText().toString());
        monthPer1Int = Integer.parseInt(monthPerson1.getText().toString());
        yearPer1Int = Integer.parseInt(yearPerson1.getText().toString());

        Date person1 = new Date(yearPer1Int, monthPer1Int, dayPer1Int);

        dayPer2Int = Integer.parseInt(person2Day.getText().toString());
        monthPer2Int = Integer.parseInt(person2Month.getText().toString());
        yearPer2Int = Integer.parseInt(person2Year.getText().toString());

        Date person2 = new Date(yearPer2Int, monthPer2Int, dayPer2Int);


        if (person2.after(person1)) {

            // Days of every month
            int month[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


            if (dayPer2Int < dayPer1Int) {
                dayPer2Int = dayPer2Int + month[monthPer1Int - 1];
                monthPer2Int = monthPer2Int - 1;
            }


            if (monthPer2Int < monthPer1Int) {
                yearPer2Int = yearPer2Int - 1;
                monthPer2Int = monthPer2Int + 12;
            }

            int calculated_day = dayPer2Int - dayPer1Int;
            int calculated_month = monthPer2Int - monthPer1Int;
            int calculated_year = yearPer2Int - yearPer1Int;

            compareText.setText(calculated_year + " Years " + calculated_month + " Months " + calculated_day + " Days");
            compareText.animate().alpha(1f).setDuration(1000);

            namePer1.setText(per1Name.getText().toString());
            difference.setText("bigger than");
            namePer2.setText(person2Name.getText().toString());

//            detailsLayout.animate().alpha(1f).setDuration(1000);
            detailsLayout.setVisibility(View.VISIBLE);
            HideKeyboardFormUser();

            return;

        }


        // Days of every month
        int month[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


        if (dayPer2Int > dayPer1Int){
            dayPer1Int = dayPer1Int + month[monthPer2Int - 1];
            monthPer1Int = monthPer1Int - 1;
        }


        if (monthPer2Int > monthPer1Int){
            yearPer1Int = yearPer1Int - 1;
            monthPer1Int = monthPer1Int + 12;
        }

        int calculated_day = dayPer1Int - dayPer2Int;
        int calculated_month = monthPer1Int - monthPer2Int;
        int calculated_year = yearPer1Int - yearPer2Int;

        compareText.setText(calculated_year + " Years " + calculated_month + " Months " + calculated_day + " Days");
        compareText.animate().alpha(1f).setDuration(1000);

        namePer1.setText(per1Name.getText().toString());
        difference.setText("smaller than");
        namePer2.setText(person2Name.getText().toString());

        detailsLayout.setVisibility(View.VISIBLE);
//        detailsLayout.animate().alpha(1f).setDuration(1000);

        HideKeyboardFormUser();

    }

    /** Clear All data */
    public void clearAgeCompareBtn(View view) {

        HideKeyboardFormUser();

//        detailsLayout.animate().alpha(0f).setDuration(1000);
        detailsLayout.setVisibility(View.GONE);

        dayPerson1.setText("");
        monthPerson1.setText("");
        yearPerson1.setText("");
        person2Day.setText("");
        person2Month.setText("");
        person2Year.setText("");

        person1Name.setText("");
        person2Name.setText("");

        compareText.animate().alpha(0f).setDuration(1000);

        namePer1.setText("");
        namePer2.setText("");
        difference.setText("");

        per1Name.setText("");
        per1Age.setText("");
        per1ageMore.setText("");

        per2Name.setText("");
        per2Age.setText("");
        per2AgeMore.setText("");

        /** Set Auto Focus */
        person1Name.requestFocus();


    }


    private void editTextUserExperience() {

        dayPerson1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String regex = "4|5|6|7|8|9";

                if (dayPerson1.getText().toString().matches(regex)){

                    if (dayPerson1.getText().toString().length() == 1){
                        monthPerson1.requestFocus();
                        dayPerson1.setText(0+dayPerson1.getText().toString());
                    }
                }
                if (dayPerson1.getText().toString().length() == 2){
                    monthPerson1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        monthPerson1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String regex = "2|3|4|5|6|7|8|9";

                if (monthPerson1.getText().toString().matches(regex)){

                    if (monthPerson1.getText().toString().length() == 1){
                        yearPerson1.requestFocus();
                        monthPerson1.setText(0+monthPerson1.getText().toString());
                    }
                }

                if (monthPerson1.getText().toString().length() == 2){
                    yearPerson1.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        yearPerson1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (yearPerson1.getText().toString().length() == 4){
                    person2Name.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (yearPerson1.getText().toString().length() == 4){
                    HideKeyboardFormUser();
                }
            }
        });


        person2Day.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String regex = "4|5|6|7|8|9";

                if (person2Day.getText().toString().matches(regex)){

                    if (person2Day.getText().toString().length() == 1){
                        person2Month.requestFocus();
                        person2Day.setText(0+person2Day.getText().toString());
                    }
                }

                if (person2Day.getText().toString().length() == 2){
                    person2Month.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        person2Month.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String regex = "2|3|4|5|6|7|8|9";
                if (person2Month.getText().toString().matches(regex)){

                    if (person2Month.getText().toString().length() == 1){
                        person2Year.requestFocus();
                        person2Month.setText(0+person2Month.getText().toString());
                    }
                }

                if (person2Month.getText().toString().length() == 2){

                    person2Year.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        person2Year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (person2Year.getText().toString().length() == 4){

                    HideKeyboardFormUser();

                }

            }
        });

    }

    public void HideKeyboardFormUser(){
        View view = getCurrentFocus();
        InputMethodManager hideKeyboard  = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        hideKeyboard.hideSoftInputFromWindow( view.getWindowToken(), 0);
    }

    public void ageCompareBackBtn(View view) {
        onBackPressed();
    }

    private boolean valPerson1Day (){

        String dayPer1 = dayPerson1.getText().toString().trim();
        String regex = "[0-2][1-9]|30|31|10|1|2|3|20";

        if (dayPer1.isEmpty()){
            dayPerson1.setError("Filed can't be empty!");
            return false;
        }
        else if (!dayPer1.matches(regex)){
            dayPerson1.setError("Invalid Day");
            return false;
        }
        else {
            return true;
        }
    }

    private boolean valPerson1Month (){

        String monthPer1 = monthPerson1.getText().toString().trim();
        String regex = "1|02|03|04|05|06|07|08|09|10|11|12|01";

        if (monthPer1.isEmpty()){
            monthPerson1.setError("Filed can't be empty!");
            return false;
        }
        else if (!monthPer1.matches(regex)){
            monthPerson1.setError("Invalid Month");
            return false;
        }
        else {
            return true;
        }
    }

    private boolean valPer1Year (){

        String yearPer1 = yearPerson1.getText().toString().trim();

        if (yearPer1.isEmpty()){
            yearPerson1.setError("Filed can't be empty!");
            return false;
        }
        else {
            return true;
        }
    }

    private boolean valPerson2Day (){

        String dayPer2 = person2Day.getText().toString().trim();
        String regex = "[0-2][1-9]|30|31|10|1|2|3|20";

        if (dayPer2.isEmpty()){
            person2Day.setError("Filed can't be empty!");
            return false;
        }
        else if (!dayPer2.matches(regex)){
            person2Day.setError("Invalid Day");
            return false;
        }
        else {
            return true;
        }
    }

    private boolean valPerson2Month (){

        String monthPer2 = person2Month.getText().toString().trim();
        String regex = "1|02|03|04|05|06|07|08|09|10|11|12|01";

        if (monthPer2.isEmpty()){
            person2Month.setError("Filed can't be empty!");
            return false;
        }
        else if (!monthPer2.matches(regex)){
            person2Month.setError("Invalid Month");
            return false;
        }
        else {
            return true;
        }
    }

    private boolean valPer2Year (){

        String yearPer2 = person2Year.getText().toString().trim();

        if (yearPer2.isEmpty()){
            person2Year.setError("Filed can't be empty!");
            return false;
        }
        else {
            return true;
        }
    }


}
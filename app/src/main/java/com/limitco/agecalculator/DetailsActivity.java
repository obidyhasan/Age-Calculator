package com.limitco.agecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limitco.agecalculator.database.PersonDao;
import com.limitco.agecalculator.database.PersonDatabase;
import com.limitco.agecalculator.receiver.AlarmReceiver;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Crate by Naim on 10/02/2022
 */

public class DetailsActivity extends AppCompatActivity {

    ImageView personImage;
    TextView nameText, dayText, monthText, yearText, subjectText;

    // get data variable
    String name, subject;
    int  day, month, year, id, requestCode;
    byte[] image;



    // Age Calculate variable
    int currentDay, currentMonth, currentYear, birthDay, birthMonth, birthYear;
    int calculated_day, calculated_month, calculated_year;
    String ageYearStr, ageMonthStr, ageDayStr;
    Calendar calendar;
    SimpleDateFormat DateDay, DateMonth, DateYear;

    // Age Text View
    TextView ageYear, ageMonth, ageDay;
    // Next Birth day text view
    TextView nextMonthText, nextDayText;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        personImage = findViewById(R.id.detailsImageId);
        nameText = findViewById(R.id.detailsNameId);
        dayText = findViewById(R.id.detailsDayId);
        monthText = findViewById(R.id.detailsMonthId);
        yearText = findViewById(R.id.detailsYearId);
        subjectText = findViewById(R.id.detailsSubjectId);


        // Person Age TextView
        ageDay = findViewById(R.id.perAgeDayId);
        ageMonth = findViewById(R.id.perAgeMonthId);
        ageYear = findViewById(R.id.perAgeYearId);


        // Person Next Birthday
        nextMonthText = findViewById(R.id.nextMonthBirthId);
        nextDayText = findViewById(R.id.nextDayBirthId);


        // for image round
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            personImage.setClipToOutline(true);
        }

        // Get Data From AddPerson Adapter
        name = getIntent().getStringExtra("Name").toString();
        subject = getIntent().getStringExtra("Subject").toString();
        image = getIntent().getByteArrayExtra("Image");
        day = getIntent().getIntExtra("Day",0);
        month = getIntent().getIntExtra("Month",0);
        year = getIntent().getIntExtra("Year",0);
        id = getIntent().getIntExtra("Id",0);
        requestCode = getIntent().getIntExtra("RequestCode",0);


        // set image to person image
        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
        personImage.setImageBitmap(bitmap);
        // set all data to textview
        nameText.setText(name);
        dayText.setText(""+day);
        monthText.setText(""+month);
        yearText.setText(""+year);
        subjectText.setText(subject);


        /** Calculate Person Age */
        CalculateAge();
        /** Next BirthDay Calculate */
        nextBirthDayCalculated();

    }



    /** Calculate Person Age */
    private void CalculateAge() {

        // Calender
        calendar = Calendar.getInstance();

        // Auto set Date
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



        birthDay = day;
        birthMonth = month;
        birthYear = year;

        Date dob = new Date(birthYear, birthMonth, birthDay);


        if (dob.after(now)) {
            Toast.makeText(DetailsActivity.this, "Birthday can't in future", Toast.LENGTH_SHORT).show();
            return;
        }
        // days of every month
        int months[] = {31, 28, 31, 30, 31, 30, 31,
                31, 30, 31, 30, 31};

        // if birth date is greater then current birth
        // month then do not count this month and add 30
        // to the date so as to subtract the date and
        // get the remaining days
        if (birthDay > currentDay) {
            currentDay = currentDay + months[birthMonth - 1];
            currentMonth = currentMonth - 1;
        }

        // if birth month exceeds current month, then do
        // not count this year and add 12 to the month so
        // that we can subtract and find out the difference
        if (birthMonth > currentMonth) {
            currentYear = currentYear - 1;
            currentMonth = currentMonth + 12;
        }

        // calculate date, month, year
        calculated_day = currentDay - birthDay;
        calculated_month = currentMonth - birthMonth;
        calculated_year = currentYear - birthYear;

        // Convert int to String
        ageYearStr = String.valueOf(calculated_year);
        ageMonthStr = String.valueOf(calculated_month);
        ageDayStr = String.valueOf(calculated_day);

        ageYear.setText(ageYearStr);
        ageMonth.setText(ageMonthStr);
        ageDay.setText(ageDayStr);

    }

    /** Next BirthDay Calculate */
    public void nextBirthDayCalculated(){


        Calendar current = Calendar.getInstance();
        current.set(currentYear, currentMonth, currentDay);

        Calendar birthday = Calendar.getInstance();
        birthday.set(year, month, day);

        long difference = birthday.getTimeInMillis() - current.getTimeInMillis();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(difference);

        nextMonthText.setText(String.valueOf(cal.get(Calendar.MONTH)));
        nextDayText.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)-1));


    }

    /** Delete Person data and cancel Alarm */
    public void deletePersonBtn(View view) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetailsActivity.this);
        bottomSheetDialog.setContentView(R.layout.delete_bottom_lay);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();


        LinearLayout yesBtn = bottomSheetDialog.findViewById(R.id.deleteYesBtnId);
        LinearLayout noBtn = bottomSheetDialog.findViewById(R.id.deleteNoBtnId);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** Delete Data from Room Database */
                PersonDatabase database = Room.databaseBuilder(getApplicationContext(), PersonDatabase.class, "Person_Database")
                        .allowMainThreadQueries().build();
                PersonDao personDao = database.personDao();

                personDao.deleteById(id);
                finish();

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);

                bottomSheetDialog.dismiss();
            }
        });


        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

    }


    public void detailsBackBtn(View view) {
        onBackPressed();
    }

}
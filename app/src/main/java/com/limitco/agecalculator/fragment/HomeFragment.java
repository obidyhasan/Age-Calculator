package com.limitco.agecalculator.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.limitco.agecalculator.AgeCalculateActivity;
import com.limitco.agecalculator.AgeCompareActivity;
import com.limitco.agecalculator.NameOfDayActivity;
import com.limitco.agecalculator.PersonActivity;
import com.limitco.agecalculator.R;
import com.limitco.agecalculator.database.PersonDao;
import com.limitco.agecalculator.database.PersonDatabase;
import com.limitco.agecalculator.database.ProfileInfo;
import com.limitco.agecalculator.receiver.AlarmReceiver;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Create by Naim on 10/02/2022
 */

public class HomeFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    LinearLayout addPerson, ageCalculate, nameOfDay, ageCompare;
    ImageView personImage;
    TextView personName, showDate;

    private int REQUEST_CODE = 11;


    /** Popup Variable */
    LottieAnimationView cancelLottieBtn;
    TextInputLayout nameEdit;
    RelativeLayout saveDataBtn;
    LinearLayout addImgBtn;
    ImageView personImg;
    DatePicker datePicker;
    Spinner spinner;
    TextView addProfileText;

    AlarmManager alarmManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /** If app have update than show a update Popup */
//        try {
//            showAppUpdatePopup();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        /**
         * if user first time show profile popup
         */
        showProfilePopup();


        /** Change Status Bar Color **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        }

        // Find by Id
        addPerson = view.findViewById(R.id.addPersonLay);
        ageCalculate = view.findViewById(R.id.ageCalculateLayoutId);
        nameOfDay = view.findViewById(R.id.nameOfDayLayoutId);
        ageCompare = view.findViewById(R.id.ageCompareLayoutId);

        personImage = view.findViewById(R.id.homePersonImageId);
        personName = view.findViewById(R.id.homePersonNameTextId);
        showDate = view.findViewById(R.id.homeDateTextId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            personImage.setClipToOutline(true);
        }


        // Show Date to text View
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d MMM");
        Calendar calendar = Calendar.getInstance();
        String time = format.format(calendar.getTime());
        showDate.setText(""+time);// show time to from home textView


        personImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProfileFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainContainerId,fragment).addToBackStack(null).commit();
            }
        });



        // Go to Age Calculate Activity
        ageCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AgeCalculateActivity.class));
            }
        });


        // Go to Person Activity
        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PersonActivity.class));
            }
        });

        // Go to Name Of Day Activity
        nameOfDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), NameOfDayActivity.class));
            }
        });


        // Go to Age Compare Activity
        ageCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AgeCompareActivity.class));
            }
        });



        return view;
    }





    @Override
    public void onResume() {
        super.onResume();

        /** show Data to Room Database */
        PersonDatabase database = Room.databaseBuilder(getContext(), PersonDatabase.class, "Person_Database")
                .allowMainThreadQueries().build();
        PersonDao personDao = database.personDao();

        List<ProfileInfo> list = personDao.showProfile();

        byte[] image = list.get(0).getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);

        personImage.setImageBitmap(bitmap);
        personName.setText(list.get(0).getName());

    }

    private void showProfilePopup() {

        /**
         * if user first time show profile popup
         */

        SharedPreferences profilePopup = getContext().getSharedPreferences("profilePopup",MODE_PRIVATE);
        boolean isFirstTime = profilePopup.getBoolean("showPopup",true);

        if (isFirstTime){

            SharedPreferences.Editor editor = profilePopup.edit();
            editor.putBoolean("showPopup",false);
            editor.commit();

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            View popupView = getLayoutInflater().inflate(R.layout.add_person_popup,null);
            dialogBuilder.setView(popupView);
            AlertDialog dialog = dialogBuilder.create();
            // Set background
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();



            // for popup variable
            cancelLottieBtn = popupView.findViewById(R.id.popupCancelBtnId);
            addImgBtn = popupView.findViewById(R.id.popupAddImgBtnId);
            saveDataBtn = popupView.findViewById(R.id.savePersonBtnId);
            personImg = popupView.findViewById(R.id.popupPersonImgId);
            nameEdit = popupView.findViewById(R.id.popupNameEditId);
            datePicker = popupView.findViewById(R.id.popupDataPickerId);
            spinner = popupView.findViewById(R.id.addPersonSpinnerId);
            addProfileText = popupView.findViewById(R.id.addProfileTextId);



            addProfileText.setVisibility(View.VISIBLE);

            // for set image corner
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                personImg.setClipToOutline(true);
            }

            // for spinner
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.birthday, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);


            saveDataBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Validation PersonName and date
                    if (!valPersonName() | !valPersonYear() ) {
                        return;
                    }

                    // Check Imageview is empty
                    if (personImg.getDrawable()==null){
                        personImg.setImageResource(R.drawable.avatar_4);
                    }

                    // get data from TextInputLayout
                    String personName = nameEdit.getEditText().getText().toString().trim();
                    // get data from Spinner
                    String whatDay = spinner.getSelectedItem().toString();

                    /** Set Alarm to this date 6:00 am **/
                    setAlarm(personName,whatDay,ImageViewToByte(personImg),
                            datePicker.getDayOfMonth(),datePicker.getMonth(),1);

                    /** Save Data to Room Database */
                    PersonDatabase database = Room.databaseBuilder(getContext(), PersonDatabase.class, "Person_Database")
                            .allowMainThreadQueries().build();
                    PersonDao personDao = database.personDao();

                    personDao.updateProfile(1,personName,whatDay,ImageViewToByte(personImg),
                            datePicker.getDayOfMonth(),datePicker.getMonth()+1,datePicker.getYear());

                    // add +1 because month count 0.1.2.... when add +1 than month count 1.2.3....

                    Toast.makeText(getContext(), "Save Successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    // When new data add to database than Immediately update to recyclerView
                    onResume();


                }
            });


            addImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                        // When Permission already granted

                        try {

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent,"Select Image"),1);

                        }catch (Exception e){
                            Log.e(TAG, "onClick: "+e);
                        }
                    }

                    else {
                        EasyPermissions.requestPermissions((Activity) getContext(),"Add needs to External Storage Permission",
                                101,Manifest.permission.READ_EXTERNAL_STORAGE);
                    }

                }
            });

            cancelLottieBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


        }

    }


    /** Convert image to byte[] */
    private byte[] ImageViewToByte(ImageView personImg) {

        Bitmap bitmap = ((BitmapDrawable) personImg.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,30,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;

    }



    // for select image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE){

            Toast.makeText(getContext(), "Start Download", Toast.LENGTH_SHORT).show();

            if (resultCode != RESULT_OK){
                Log.e("App Update", "onActivityResult : Error"+resultCode);
            }

        }

        else {


            if (requestCode == 1 && resultCode == RESULT_OK) {

                Uri imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMultiTouchEnabled(true)
                        .start(getContext(),HomeFragment.this);

            }


            // When crop Successfully
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {

                    Uri uri = result.getUri();// Crop Image Uri

                    // Use Picasso than add picasso dependence
                    Picasso.with(getContext())
                            .load(uri)
                            .resize(400,400)
                            .centerCrop()
                            .into(personImg);

                }

            }

        }


    }


    // For Easy Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Image"),1);

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            // When permission deny multiple time
            new AppSettingsDialog.Builder(this).build().show();
        }

        else {
            Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean valPersonYear(){

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int personYear = datePicker.getYear();

        if (currentYear < personYear) {
            Toast.makeText(getContext(), ""+spinner.getSelectedItem().toString()+" date can't in future", Toast.LENGTH_SHORT).show();
            return false;
        }

        else {
            return true;
        }

    }

    private boolean valPersonName (){

        // If TextInputLayout text is empty
        String personName = nameEdit.getEditText().getText().toString().trim();

        if (personName.isEmpty()) {
            nameEdit.setError("Filed can't be empty");
            return false;
        }

        else {
            nameEdit.setError(null);
            nameEdit.setErrorEnabled(false);
            return true;

        }

    }


    private void setAlarm(String personName, String whatDay, byte[] imageViewToByte, int day, int month, int requestCode) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.HOUR_OF_DAY,6);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        if (calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.YEAR,1);
        }


        alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("name",personName);
        intent.putExtra("subject",whatDay);
        intent.putExtra("image",imageViewToByte);
        intent.putExtra("time",calendar.getTimeInMillis());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),requestCode,intent,PendingIntent.FLAG_IMMUTABLE);


        if (this.alarmManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
            }
            else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
            }
        }

    }



}
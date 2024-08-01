package com.limitco.agecalculator.fragment;



import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.ALARM_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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
import com.limitco.agecalculator.R;
import com.limitco.agecalculator.common.PrivacyPolicy;
import com.limitco.agecalculator.database.PersonDao;
import com.limitco.agecalculator.database.PersonDatabase;
import com.limitco.agecalculator.database.ProfileInfo;
import com.limitco.agecalculator.receiver.AlarmReceiver;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Create by Naim on 14/02/2022
 */

public class ProfileFragment extends Fragment implements EasyPermissions.PermissionCallbacks{

    /** Variable */
    RelativeLayout editProfile, rateIt, share, privacyPolicy;
    ImageView imageView;
    TextView nameText, subjectText, dayText, monthText, yearText;


    String name, subject;
    int day, month, year;
    byte[] image;
    Bitmap bitmap;

    AlarmManager alarmManager;

    /** Popup Variable */
    LottieAnimationView cancelLottieBtn;
    TextInputLayout nameEdit;
    RelativeLayout saveDataBtn;
    LinearLayout addImgBtn;
    ImageView personImg;
    DatePicker datePicker;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        /** find by id */
        editProfile = view.findViewById(R.id.editProfileLayoutId);
        rateIt = view.findViewById(R.id.rateItLayoutId);
        share = view.findViewById(R.id.shareLayoutId);
        privacyPolicy = view.findViewById(R.id.privacyPolicyLayoutId);

        // find profile info by id
        imageView = view.findViewById(R.id.profileImageId);
        nameText = view.findViewById(R.id.profileNameId);
        subjectText = view.findViewById(R.id.profileSubjectTextId);
        dayText = view.findViewById(R.id.profileDayId);
        monthText = view.findViewById(R.id.profileMonthId);
        yearText = view.findViewById(R.id.profileYearId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setClipToOutline(true);
        }


        /** Change Status Bar Color **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(Color.parseColor("#FCECDF"));
        }


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileButton();
            }
        });


        rateIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id="+getContext().getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                try {
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Unable to open", Toast.LENGTH_SHORT).show();
                }

            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = "Download this Application now:-https://play.google.com/store/apps/details?id=com.limitco.agecalculator&hl=en";
                String shareSub = "Age Calculator";

                shareIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody);

                getContext().startActivity(Intent.createChooser(shareIntent,"Share Via"));

            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PrivacyPolicy.class));
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();


        /** set profile info from profile database */

        /** get data from room database */
        PersonDatabase database = Room.databaseBuilder(getContext(), PersonDatabase.class, "Person_Database")
                .allowMainThreadQueries().build();
        PersonDao personDao = database.personDao();

        List<ProfileInfo> list = personDao.showProfile();

        name = list.get(0).getName();
        subject = list.get(0).getSubject();
        image = list.get(0).getImage();
        day = list.get(0).getDay();
        month = list.get(0).getMonth();
        year = list.get(0).getYear();

        nameText.setText(name);
        subjectText.setText(subject+" :");

        if (day == 32){
            dayText.setText("00");
        }
        else {
            dayText.setText(""+day);
        }

        if (month == 13){
            monthText.setText("00");
        }else {
            monthText.setText(""+month);
        }

        if (year == 1){
            yearText.setText("0000");
        }else {
            yearText.setText(""+year);
        }


        bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
        imageView.setImageBitmap(bitmap);

    }

    private void editProfileButton() {

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


        nameEdit.getEditText().setText(name);
        personImg.setImageBitmap(bitmap);


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

                try {
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
                catch (Exception e){
                    Log.d(TAG, "onClick: "+e);
                }


            }
        });


        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    // When Permission already granted
                    // CropImage.startPickImageActivity(getContext(), ProfileFragment.this);

                    try {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,"Select Image"),1);



//                        Intent intent = new Intent();
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                        intent.setType("image/*");
//                        startActivityForResult(intent,101);

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


//        if (requestCode == 101 && resultCode == RESULT_OK) {
//
//            if (data.getData() != null) {
//
//                Uri uri = data.getData();
//
//                Picasso.with(getContext())
//                        .load(uri)
//                        .resize(400,400)
//                        .centerCrop()
//                        .into(personImg);
//
//            }
//
//        }


        if (requestCode == 1 && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMultiTouchEnabled(true)
                    .start(getContext(),ProfileFragment.this);

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
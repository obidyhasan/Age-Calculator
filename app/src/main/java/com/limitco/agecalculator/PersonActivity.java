package com.limitco.agecalculator;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.limitco.agecalculator.adapter.PersonAdapter;
import com.limitco.agecalculator.database.PersonDao;
import com.limitco.agecalculator.database.PersonDatabase;
import com.limitco.agecalculator.database.PersonInfo;
import com.limitco.agecalculator.receiver.AlarmReceiver;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Create by Naim on 10/02/2022
 */

public class PersonActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {


    RecyclerView recyclerView;
    LinearLayout emptyLay;
    PersonAdapter adapter;



    /** Popup Variable */
    LottieAnimationView cancelLottieBtn;
    TextInputLayout nameEdit;
    RelativeLayout saveDataBtn;
    LinearLayout addImgBtn;
    ImageView personImg;
    DatePicker datePicker;
    Spinner spinner;


    AlarmManager alarmManager;

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);


        // For interstitial ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });



        // find by id
        emptyLay = findViewById(R.id.recyclerEmptyLayId);
        recyclerView = findViewById(R.id.addPersonRecyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        loadInterstitialAd();

    }






    @Override
    protected void onResume() {
        super.onResume();
        SetAdapter();
    }

    /** Recycler Adapter */
    private void SetAdapter() {

        /** Show Data to Room Database */
        PersonDatabase database = Room.databaseBuilder(getApplicationContext(), PersonDatabase.class, "Person_Database")
                .allowMainThreadQueries().build();
        PersonDao personDao = database.personDao();

        List<PersonInfo> list = personDao.showData();
        adapter = new PersonAdapter(list,this);
        recyclerView.setAdapter(adapter);



        // Check recyclerView is empty
        if (adapter.getItemCount() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyLay.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLay.setVisibility(View.GONE);
        }

    }


    /** Show Add Person Popup */
    public void showAddPersonPopup(View view) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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


        // for set image corner
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            personImg.setClipToOutline(true);
        }

        // for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.birthday, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        saveDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int randomRequest = (int) (System.currentTimeMillis()/100000);

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
                        datePicker.getDayOfMonth(),datePicker.getMonth(),randomRequest);

                /** Save Data to Room Database */
                PersonDatabase database = Room.databaseBuilder(getApplicationContext(), PersonDatabase.class, "Person_Database")
                        .allowMainThreadQueries().build();
                PersonDao personDao = database.personDao();

                personDao.insertData(new PersonInfo(personName,whatDay,ImageViewToByte(personImg),
                        datePicker.getDayOfMonth(),datePicker.getMonth()+1,datePicker.getYear(),1,randomRequest));

                // add +1 because month count 0.1.2.... when add +1 than month count 1.2.3....

                Toast.makeText(getApplicationContext(), "Save Successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                // When new data add to database than Immediately update to recyclerView
                onResume();

                // recycle View visible, if recyclerView not empty
                recyclerView.setVisibility(View.VISIBLE);
                emptyLay.setVisibility(View.GONE);


                /** set Interstitial Ad  */
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(PersonActivity.this);
                }


            }


        });

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (EasyPermissions.hasPermissions(PersonActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){

                    // When Permission already granted
                    // For Image Picker

                    try {

                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,"Select Image"),1);

                    }catch (Exception e){
                        Log.e(TAG, "onClick: "+e);
                    }

                }

                else {
                    EasyPermissions.requestPermissions(PersonActivity.this,"Add needs to External Storage Permission",
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


    private void loadInterstitialAd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-3683066282343593/3940037368", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");



                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                loadInterstitialAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });



                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {
            if (requestCode == 1 && resultCode == RESULT_OK) {

                Uri imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMultiTouchEnabled(true)
                        .start(this);

            }


            // When crop Successfully
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {

                    Uri uri = result.getUri();// Crop Image Uri

                    // Use Picasso than add picasso dependence
                    Picasso.with(this)
                            .load(uri)
                            .resize(400,400)
                            .centerCrop()
                            .into(personImg);

                }

            }

        } catch (Exception e) {
            Log.e(TAG, "onActivityResult: OnActivityResult Error");
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
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }

    }




    public void addPersonBackBtn(View view) {
        onBackPressed();
    }


    private boolean valPersonYear(){

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int personYear = datePicker.getYear();

        if (currentYear < personYear) {
            Toast.makeText(getApplicationContext(), ""+spinner.getSelectedItem().toString()+" date can't in future", Toast.LENGTH_SHORT).show();
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



        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("name",personName);
        intent.putExtra("subject",whatDay);
        intent.putExtra("image",imageViewToByte);
        intent.putExtra("time",calendar.getTimeInMillis());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,requestCode,intent,PendingIntent.FLAG_IMMUTABLE);


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
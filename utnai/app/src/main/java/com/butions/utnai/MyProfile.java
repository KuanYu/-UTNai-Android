package com.butions.utnai;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Map;

/**
 * Created by Chalitta Khampachua on 19-Jul-17.
 */

public class MyProfile extends AppCompatActivity implements View.OnClickListener, MyProfileFirebase.ProfileCallbacks {

    private String TAG = "MyProfile";
    private ImageView imageProfile;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static String mUserID;
    private TextView btnName, btnBirthDay, btnCity, btnEmail, btnPhone, btnGender;
    private EditText btnEditPhone;
    private ImageView btnSave;
    private ImageView btnBack;
    private boolean isFrist;
    private int isYear, isMonth, isDay;
    private String birthDate;
    private MyProfileFirebase.ProfileListener profileListener;
    private Map<String, String > myProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        ActivityManagerName managerName = new ActivityManagerName();
        managerName.setProcessNameClass(this);
        managerName.setNameClassInProcessString("MyProfile");

        Translater.getInstance().setLanguages(this);

        isFrist = true;

        Bundle bundle = getIntent().getExtras();
        mUserID = bundle.getString("mUserID");

        //call profile
        profileListener = MyProfileFirebase.addProfileListener(mUserID, this, this);

        InitializeMyProfile();

    }

    @Override
    public void onProfileChange(ProfileValue profile) {
        myProfile = profile.getMyProfile();
        setDataProfile();
    }

    private void InitializeMyProfile() {
        TextView textProfile = (TextView) findViewById(R.id.textProfile);
        imageProfile = (ImageView) findViewById(R.id.imageProfile);
        btnName = (TextView) findViewById(R.id.btnName);
        btnBirthDay = (TextView) findViewById(R.id.btnBirthDay);
        btnCity = (TextView) findViewById(R.id.btnCity);
        btnEmail = (TextView) findViewById(R.id.btnEmail);
        btnPhone = (TextView) findViewById(R.id.btnPhone);
        btnGender = (TextView) findViewById(R.id.btnGender);
        btnSave = (ImageView) findViewById(R.id.btnSave);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnEditPhone = (EditText) findViewById(R.id.btnEditPhone);
        btnEditPhone.setVisibility(View.GONE);

        //setFont
        textProfile.setText(StringManager.getsInstance().getString("Profile"));
        textProfile.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        btnName.setTypeface(Config.getInstance().getDefaultFont(this));
        btnBirthDay.setTypeface(Config.getInstance().getDefaultFont(this));
        btnCity.setTypeface(Config.getInstance().getDefaultFont(this));
        btnEmail.setTypeface(Config.getInstance().getDefaultFont(this));
        btnPhone.setTypeface(Config.getInstance().getDefaultFont(this));
        btnGender.setTypeface(Config.getInstance().getDefaultFont(this));
        btnEditPhone.setTypeface(Config.getInstance().getDefaultFont(this));

        btnBack.setOnClickListener(this);
        btnPhone.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnBirthDay.setOnClickListener(this);
    }


    private void setDataProfile() {
        Picasso.with(this)
                .load(myProfile.get("Picture"))
                .centerCrop()
                .fit()
                .placeholder(R.drawable.bg_circle_white)
                .error(R.drawable.ic_account_circle)
                .transform(new CircleTransform())
                .into(imageProfile);

        btnName.setText(myProfile.get("Name"));
        if (myProfile.containsKey("Birthday") && !myProfile.get("Birthday").trim().isEmpty()) {
            convertDate(myProfile.get("Birthday"));
            btnBirthDay.setText(myProfile.get("Birthday"));
            btnBirthDay.setTextColor(getResources().getColor(R.color.black));
        } else {
            btnBirthDay.setText(StringManager.getsInstance().getString("HintBirthday"));
            btnBirthDay.setTextColor(getResources().getColor(R.color.grey));
        }
        btnCity.setText(myProfile.get("Address"));
        btnEmail.setText(myProfile.get("Email"));
        if(myProfile.containsKey("Phone") && !myProfile.get("Phone").trim().isEmpty()){
            btnEditPhone.setVisibility(View.GONE);
            btnPhone.setVisibility(View.VISIBLE);
            btnPhone.setText(myProfile.get("Phone"));
        }else {
            btnPhone.setVisibility(View.GONE);
            btnEditPhone.setVisibility(View.VISIBLE);
            btnEditPhone.setHint(StringManager.getsInstance().getString("HintPhone"));
        }
        btnGender.setText(myProfile.get("Gender"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "***onStop***");
        MyProfileFirebase.stop(profileListener);
    }

    @Override
    public void onClick(View view) {
        if(view == btnPhone){
            btnPhone.setVisibility(View.GONE);
            btnEditPhone.setVisibility(View.VISIBLE);
            btnEditPhone.clearFocus();
            btnEditPhone.setCursorVisible(true);
        }else if(view == btnSave){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnSave.setBackgroundResource(R.color.Transparent);
                }
            },2000);
            String dataPhone = btnEditPhone.getText().toString();
            DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID);
            if(!dataPhone.equals("")) {
                mUsersRef.child("Phone").setValue(dataPhone);
            }
            if(!birthDate.equals("")) {
                mUsersRef.child("Birthday").setValue(birthDate);
            }
            btnEditPhone.setVisibility(View.GONE);
            btnPhone.setVisibility(View.VISIBLE);
            btnPhone.setText(dataPhone);
        }else if(view == btnBack){
            finish();
        }else if(view == btnBirthDay){
            DialogdatePicker();
        }
    }

    private void DialogdatePicker(){
        if(isFrist){
            Log.d(TAG, "*** isMonth-1: " + (isMonth-1) + "***");
            isMonth = isMonth-1;
            isFrist = false;
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.Theme_DatePickerDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                showDate(year, monthOfYear+1, dayOfMonth);
                isMonth = monthOfYear;
                isYear = year;
                isDay = dayOfMonth;
            }
        },isYear,isMonth,isDay);
        datePickerDialog.show();
    }

    private void showDate(int year, int month, int day) {
        btnBirthDay.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));

        birthDate =  day + "/" + month + "/" + year;
        Log.d(TAG,"BirthDate : " + birthDate);
    }

    private boolean convertDate(String birthDate) {
        if(!birthDate.equals("null") && !birthDate.equals("")) {
            String part[] = birthDate.split("/");
            isDay = Integer.parseInt(part[0]);
            isMonth = Integer.parseInt(part[1]);
            isYear = Integer.parseInt(part[2]);
            showDate(isYear, isMonth, isDay);
            return true;
        }
        else{
            java.util.Calendar now = java.util.Calendar.getInstance();
            isYear = now.get(java.util.Calendar.YEAR);
            isMonth = now.get(java.util.Calendar.MONTH) + 1; // Note: zero based!
            isDay = now.get(java.util.Calendar.DAY_OF_MONTH);
            showDate(isYear, isMonth, isDay);
            return false;
        }
    }
}

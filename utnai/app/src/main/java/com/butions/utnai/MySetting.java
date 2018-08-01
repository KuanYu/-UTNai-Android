package com.butions.utnai;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.twitter.sdk.android.core.TwitterCore;

public class MySetting extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private String TAG = "MySetting";
    private TextView btnLogOut;
    private TextView btnProfile;
    private TextView btnAbout;
    private ImageView btnBack;
    private static String mUserID;
    private SwitchCompat switch_on_map;
    private DatabaseReference mRootRef;
    private String MyOnMap;
    private TextView btnPrivacyPolicy;
    private TextView btnNotification;
    private TextView btnLanguages;
    private TextView btnMyLocation;
    private TextView textSetting;
    private TextView btnBlocking;
    private TextView btnDevice;
    private String MyDeviceID;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_setting);
        ActivityManagerName managerName = new ActivityManagerName();
        managerName.setProcessNameClass(this);
        managerName.setNameClassInProcessString("MySetting");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Bundle bundle = getIntent().getExtras();
        mUserID = bundle.getString("mUserID");
        MyOnMap = bundle.getString("isMyOnMap");
        MyDeviceID = bundle.getString("mDeviceID");

        textSetting = (TextView) findViewById(R.id.textSetting);
        textSetting.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);

        btnProfile = (TextView) findViewById(R.id.btnProfile);
        btnProfile.setTypeface(Config.getInstance().getDefaultFont(this));
        btnProfile.setOnClickListener(this);

        btnLogOut = (TextView) findViewById(R.id.btnLogOut);
        btnLogOut.setTypeface(Config.getInstance().getDefaultFont(this));
        btnLogOut.setOnClickListener(this);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        btnAbout = (TextView) findViewById(R.id.btnAbout);
        btnAbout.setTypeface(Config.getInstance().getDefaultFont(this));
        btnAbout.setOnClickListener(this);

        btnPrivacyPolicy = (TextView) findViewById(R.id.btnPrivacyPolicy);
        btnPrivacyPolicy.setTypeface(Config.getInstance().getDefaultFont(this));
        btnPrivacyPolicy.setOnClickListener(this);

        btnNotification = (TextView) findViewById(R.id.btnNotification);
        btnNotification.setTypeface(Config.getInstance().getDefaultFont(this));
        btnNotification.setOnClickListener(this);

        btnBlocking = (TextView) findViewById(R.id.btnBlocking);
        btnBlocking.setTypeface(Config.getInstance().getDefaultFont(this));
        btnBlocking.setOnClickListener(this);

        btnLanguages = (TextView) findViewById(R.id.btnLanguages);
        btnLanguages.setTypeface(Config.getInstance().getDefaultFont(this));
        btnLanguages.setOnClickListener(this);

        btnDevice = (TextView) findViewById(R.id.btnDevice);
        btnDevice.setTypeface(Config.getInstance().getDefaultFont(this));
        btnDevice.setOnClickListener(this);

        btnMyLocation = (TextView) findViewById(R.id.btnMyLocation);
        btnMyLocation.setTypeface(Config.getInstance().getDefaultFont(this));

        switch_on_map = (SwitchCompat) findViewById(R.id.switch_on_map);
        switch_on_map.setTypeface(Config.getInstance().getDefaultFont(this));
        if(MyOnMap.equals("1")){
            switch_on_map.setChecked(true);
            switch_on_map.setShowText(true);
        }else{
            switch_on_map.setChecked(false);
            switch_on_map.setShowText(true);
        }
        switch_on_map.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch_on_map.setShowText(b);
                switch_on_map.setChecked(b);
                if(b){
                    DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID);
                    mUsersRef.child("MyOnMap").setValue(1);
                }else{
                    DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID);
                    mUsersRef.child("MyOnMap").setValue(0);
                }
            }
        });

    }

    private void setColor() {
        btnProfile.setBackgroundResource(R.color.white);
        btnLogOut.setBackgroundResource(R.drawable.bg_button_logout);
        btnAbout.setBackgroundResource(R.color.white);
        btnPrivacyPolicy.setBackgroundResource(R.color.white);
        btnNotification.setBackgroundResource(R.color.white);
        btnLanguages.setBackgroundResource(R.color.white);
        btnBlocking.setBackgroundResource(R.color.white);
        btnDevice.setBackgroundResource(R.color.white);
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogOut) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            view.setBackgroundResource(R.drawable.bg_button_ok);
            btnLogOut.setTextColor(getResources().getColor(R.color.grey_cc));
            btnLogOut.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_logout_grey,0,0,0);

            String time = CalendarTime.getInstance().getCurrentDate();
            long timeStamp = CalendarTime.getInstance().getCurrentTime();
            DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID).child("Devices").child(MyDeviceID);
            mUsersRef.child("Active").setValue(0);
            mUsersRef.child("LogOutTime").setValue(time);
            mUsersRef.child("Timestamp").setValue(timeStamp);
            String providerId = null;

            this.getSharedPreferences("LANGUAGECODE", Context.MODE_PRIVATE).edit().clear().apply();
            this.getSharedPreferences("MYLANGUAGE", Context.MODE_PRIVATE).edit().clear().apply();
            this.getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE).edit().clear().apply();
            this.getSharedPreferences("NOTIFICATION", Context.MODE_PRIVATE).edit().clear().apply();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            for (UserInfo profile : user.getProviderData()) {
                providerId = profile.getProviderId();
                Log.d(TAG, "providerId: " + providerId);
            }

            if(providerId.equals("facebook.com")){
                mAuth.signOut();
                LoginManager.getInstance().logOut();

            }else if(providerId.equals("google.com")){
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.d(TAG, "GoogleSignInApi:LogOut:Success");
                    }
                });

            }else if(providerId.equals("twitter.com")){
                mAuth.signOut();
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
            }else{
                mAuth.signOut();
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        }else if(view == btnProfile){
            view.setBackgroundResource(R.color.grey_208);
            Intent intent = new Intent(this, MyProfile.class);
            intent.putExtra("mUserID", mUserID);
            startActivity(intent);
        }else if(view == btnNotification){
            view.setBackgroundResource(R.color.grey_208);
            Intent intent = new Intent(this, MyNotification.class);
            startActivity(intent);

        }else if(view == btnLanguages){
            view.setBackgroundResource(R.color.grey_208);
            Intent intent = new Intent(this, MyLanguages.class);
            startActivity(intent);
        }
        else if(view == btnBlocking){
            view.setBackgroundResource(R.color.grey_208);
            Intent intent = new Intent(this, MyBlocking.class);
            intent.putExtra("mUserID", mUserID);
            startActivity(intent);
        }
        else if(view == btnDevice){
            view.setBackgroundResource(R.color.grey_208);
            Intent intent = new Intent(this, MyDevices.class);
            intent.putExtra("mUserID", mUserID);
            startActivity(intent);
        }
        else if(view == btnBack) {
            finish();
        }else if(view == btnAbout){
            view.setBackgroundResource(R.color.grey_208);
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }else if(view == btnPrivacyPolicy){
            view.setBackgroundResource(R.color.grey_208);
            Intent intent = new Intent(this, PrivacyPolicy.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "*** onResume ***");
        Translater.getInstance().setLanguages(this);
        setFonts();
        setColor();
    }

    private void setFonts() {
        btnProfile.setText(StringManager.getsInstance().getString("Profile"));
        btnLogOut.setText(StringManager.getsInstance().getString("LogOut"));
        btnAbout.setText(StringManager.getsInstance().getString("About"));
        btnPrivacyPolicy.setText(StringManager.getsInstance().getString("Privacy"));
        btnNotification.setText(StringManager.getsInstance().getString("Notification"));
        btnLanguages.setText(StringManager.getsInstance().getString("Languages"));
        btnBlocking.setText(StringManager.getsInstance().getString("Blocking"));
        btnMyLocation.setText(StringManager.getsInstance().getString("MyLocation"));
        textSetting.setText(StringManager.getsInstance().getString("Setting"));
        btnDevice.setText(StringManager.getsInstance().getString("Devices"));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleSignIn:onConnectionFailed");
    }
}

package com.butions.utnai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, MyLanguageFirebase.LanguageCallbacks, MyLocation.LocationCallbacks {

    private CallbackManager callbackManager;
    private String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRootRef;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private double getLatitude;
    private double getLongitude;
    private String deviceID;
    private String deviceName;
    private MyLoading objLoading;
    private LoginButton loginButtonFacebook;
    private String isCode;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton loginButtonGoogle;
    private TwitterLoginButton loginButtonTwitter;
    private Typeface ntailu_font;
    private String mUserName;
    private String mEmail;
    private Uri mPhotoUrl;
    private String mUserID;
    private TextView text_warning;
    private TextView text_other_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "*** onCreate ***");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Configure Facebook Sign In
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

        //Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        ntailu_font = Typeface.createFromAsset(getAssets(), "fonts/ntailu.ttf");

        Initialize();
        checkPermissionLocation();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mUserName = user.getDisplayName();
                    mEmail = user.getEmail();
                    mPhotoUrl = user.getPhotoUrl();
                    mUserID = user.getUid();
                    new MyLocation(MainActivity.this, MainActivity.this);
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(user);
            }
        };


    }

    @Override
    public void onLocationDone(MyLocationValue locationValue) {
        listenerCallbackLocation(locationValue);
    }

    @SuppressLint("HardwareIds")
    private void Initialize() {
        objLoading = new MyLoading(this);

        Typeface giorgio_font = Typeface.createFromAsset(getAssets(), "fonts/giorgio.ttf");
        TextView text_care = (TextView) findViewById(R.id.text_care);
        text_care.setTypeface(giorgio_font);

        ImageView icon_utnai = (ImageView) findViewById(R.id.icon_utnai);
        float m = Config.getInstance().getDisplayDensity(this);
        icon_utnai.getLayoutParams().height = (int) (500 * m);
        icon_utnai.getLayoutParams().width = (int) (500 * m);
        icon_utnai.requestLayout();

        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceName = Config.getInstance().getDeviceName();

        text_warning = (TextView) findViewById(R.id.text_warning);
        text_other_login = (TextView) findViewById(R.id.text_other_login);

        //Initialize google login
        loginButtonGoogle = (SignInButton) findViewById(R.id.login_button_google);
        loginButtonGoogle.setSize(SignInButton.SIZE_WIDE);
        loginButtonGoogle.setOnClickListener(this);
        setTextButtonGoogle(loginButtonGoogle, "Sing in with Google");

        //Initialize Twitter login
        loginButtonTwitter = (TwitterLoginButton) findViewById(R.id.login_button_twitter);
        loginButtonTwitter.setTypeface(ntailu_font);
        loginButtonTwitter.setText("Sing in with Twitter");
        loginButtonTwitter.setTextSize(16);
        loginButtonTwitter.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        loginButtonTwitter.setCompoundDrawablePadding((int) (10*Config.getInstance().getDisplayDensity(this)));
        loginButtonTwitter.setCallback(new TwitterCallbackListener());


        //Initialize facebook login
        callbackManager = CallbackManager.Factory.create();
        loginButtonFacebook = (LoginButton) findViewById(R.id.login_button_facebook);
        loginButtonFacebook.setVisibility(View.VISIBLE);
        loginButtonFacebook.setTypeface(ntailu_font);
        loginButtonFacebook.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends", "user_location", "user_posts", "user_about_me"));
        loginButtonFacebook.registerCallback(callbackManager, new FacebookCallbackListener());
    }

    private void setTextButtonGoogle(SignInButton loginButtonGoogle, String buttonText) {
        for (int i = 0; i < loginButtonGoogle.getChildCount(); i++) {
            View v = loginButtonGoogle.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                tv.setTypeface(ntailu_font);
                tv.setTextSize(16);
                tv.setCompoundDrawablePadding((int) (40*Config.getInstance().getDisplayDensity(this)));
                tv.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

                return;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "*** onStart ***");
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "*** onResume ***");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "*** onStop ***");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "*** onDestroy ***");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "*** onPause ***");
    }

    @Override
    public void onClick(View view) {
        if(view == loginButtonGoogle){
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
        }
    }

    //Callback Facebook and Callback Google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:requestCode " + resultCode);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Log.d(TAG, "onActivityResult:Sing in with Google");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                updateUI(null);
            }
        }
        else if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult:Sing in with Twitter");
            loginButtonTwitter.onActivityResult(requestCode, resultCode, data);
        }
        else if(FacebookSdk.isFacebookRequestCode(requestCode)){
            Log.d(TAG, "onActivityResult:Sing in with Facebook");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Callback Google Failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void checkPermissionLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Marshmallow API 23
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) +
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) +
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)+
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)+
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION ,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE ,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        text_warning.setVisibility(View.GONE);
        text_other_login.setVisibility(View.GONE);
        if (user != null) {
            loginButtonFacebook.setVisibility(View.GONE);
            loginButtonGoogle.setVisibility(View.GONE);
            loginButtonTwitter.setVisibility(View.GONE);
        } else {

            //facebook
            final Profile profile = Profile.getCurrentProfile();
            if(profile != null){
                LoginManager.getInstance().logOut();
            }

            //twitter
            TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
            if (session != null) {
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
            }

            loginButtonFacebook.setVisibility(View.VISIBLE);
            loginButtonGoogle.setVisibility(View.VISIBLE);
            loginButtonTwitter.setVisibility(View.VISIBLE);
        }
    }

    //Login complete Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        final String googlePulsID = acct.getId();

        setTranslate();
        setDefaultNotification();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.d(TAG, "firebaseAuthWithGoogle:onComplete_fail" + task.getException().getMessage());
                    text_warning.setVisibility(View.VISIBLE);
                    text_other_login.setVisibility(View.VISIBLE);
                    text_other_login.setText("* Please login with Facebook or Twitter");
                    objLoading.loading(false);

                } else {
                    Log.d(TAG, "firebaseAuthWithGoogle:onComplete_success");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        mUserName = user.getDisplayName();
                        mEmail = user.getEmail();
                        mPhotoUrl = user.getPhotoUrl();
                        mUserID = user.getUid();
                        handleGoogleSignIn(mUserName , mEmail, mPhotoUrl, mUserID,  googlePulsID);
                    }else{
                        objLoading.loading(false);
                    }

                    updateUI(user);
                }
            }
        });
    }

    //Callback class Twitter
    private class TwitterCallbackListener extends Callback<TwitterSession> {

        @Override
        public void success(Result<TwitterSession> result) {
            Log.d(TAG, "twitterLogin:success" + result);
            objLoading.loading(true);

            setTranslate();
            setDefaultNotification();

            final TwitterSession session = result.data;
            AuthCredential credential = TwitterAuthProvider.getCredential(session.getAuthToken().token, session.getAuthToken().secret);
            mAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        //An account already exists with the same email address but different sign-in credentials.
                        Log.d(TAG, "TwitterCallbackListener:onComplete_fail:" + task.getException().getMessage());
                        text_warning.setVisibility(View.VISIBLE);
                        text_other_login.setVisibility(View.VISIBLE);
                        text_other_login.setText("* Please login with Facebook or Google");
                        objLoading.loading(false);

                    }else{
                        Log.d(TAG, "TwitterCallbackListener:onComplete_success");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            mUserName = user.getDisplayName();
                            mEmail = user.getEmail();
                            mPhotoUrl = user.getPhotoUrl();
                            mUserID = user.getUid();
                            handleTwitterSignIn(mUserName , mEmail, mPhotoUrl, mUserID, session);
                        }else{
                            objLoading.loading(false);
                        }
                        updateUI(user);
                    }
                }
            });
        }

        @Override
        public void failure(TwitterException exception) {
            Log.w(TAG, "twitterLogin:failure", exception);
            objLoading.loading(false);
            updateUI(null);
        }
    }

    //Callback class Facebook
    private class FacebookCallbackListener implements FacebookCallback<LoginResult>{
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d(TAG, "FacebookLogin:success");
            objLoading.loading(true);

            setTranslate();
            setDefaultNotification();

            final AccessToken accessToken = loginResult.getAccessToken();
            AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
            mAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "FacebookCallbackListener:onComplete_fail" + task.getException().getMessage());
                        text_warning.setVisibility(View.VISIBLE);
                        text_other_login.setVisibility(View.VISIBLE);
                        text_other_login.setText("* Please login with Google or Twitter");
                        objLoading.loading(false);

                    } else {
                        Log.d(TAG, "FacebookCallbackListener:onComplete_success");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            mUserName = user.getDisplayName();
                            mEmail = user.getEmail();
                            mPhotoUrl = user.getPhotoUrl();
                            mUserID = user.getUid();
                            handleFacebookAccessToken(accessToken, mUserName , mEmail, mPhotoUrl, mUserID);
                        }else{
                            objLoading.loading(false);
                        }
                        updateUI(user);
                    }
                }
            });

        }

        @Override
        public void onCancel() {
            objLoading.loading(false);
            updateUI(null);
            Log.d(TAG, "*** facebook:onCancel ***");
        }

        @Override
        public void onError(FacebookException error) {
            objLoading.loading(false);
            updateUI(null);
            Log.d(TAG,"*** facebook:onError ***" + error.getMessage());
        }
    }

    //Google Get Data
    private void handleGoogleSignIn(String mUserName, String mEmail, Uri mPhotoUrl, String mUserID, String mGooglePulsID) {

        Person person  = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String mBirthday = person.getBirthday();
        String mGender;
        if(person.getGender() == 1){
            mGender = "female";
        }else{
            mGender = "male";
        }
        String mLink = "https://plus.google.com/"+mGooglePulsID;

        register(mUserName, mBirthday, mEmail, mGender, mLink, null, mPhotoUrl.toString(), mUserID, mGooglePulsID, "google");
    }

    //Twitter Get Data
    private void handleTwitterSignIn(final String mUserName, final String mEmail, final Uri mPhotoUrl, final String mUserID, TwitterSession mSeccess) {

        TwitterCore.getInstance().getApiClient(mSeccess).getAccountService().verifyCredentials(true,false,true).enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                User user = result.data;
                String mLink = "https://twitter.com/"+user.screenName;
                String mTwitterID = String.valueOf(user.id);
                Log.d(TAG, "screenName : "+  user.screenName);
                register(mUserName, null, mEmail, null, mLink, null, mPhotoUrl.toString(), mUserID, mTwitterID, "twitter");
            }

            @Override
            public void failure(TwitterException exception) {
                register(mUserName, null, mEmail, null, null, null, mPhotoUrl.toString(), mUserID, null, null);
            }
        });

    }

    //Facebook Get Data
    private void handleFacebookAccessToken(AccessToken accessToken, final String name , final String email, final Uri photoUrl, final String uid) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse response) {
                final String mBirthday;
                final String mGender;
                final String mLink;
                final String providerID;
                String mLocation;
                Log.d(TAG,"FacebookLogin:Response " + response);
                try {
                    JSONObject data = response.getJSONObject();
                    if (data.has("location")) {
                        mLocation = data.getJSONObject("location").getString("name");
                    }else{
                        mLocation = null;
                    }
                    providerID = user.optString("id");
                    mBirthday = user.optString("birthday");
                    mGender = user.optString("gender");
                    mLink = user.optString("link");


                    Log.d(TAG, "*** doing register ***");
                    Log.d(TAG, "UserID : " + uid);
                    Log.d(TAG, "mName : " + name);
                    Log.d(TAG, "mBirthday : " + mBirthday);
                    Log.d(TAG, "mEmail : " + email);
                    Log.d(TAG, "mGender : " + mGender);
                    Log.d(TAG, "mLink : " + mLink);
                    Log.d(TAG, "mPicture : " + photoUrl);
                    Log.d(TAG, "mLocation : " + mLocation);

                    register(name, mBirthday, email, mGender, mLink, mLocation, photoUrl.toString(), uid, providerID, "facebook");

                }catch (Exception e){
                    objLoading.loading(false);
                    LoginManager.getInstance().logOut();
                    e.getMessage();
                }
            }
        });

        //get fields from facebook
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,birthday,email,gender,link,locale,picture.type(large),friends,location{id, name}");
        request.setParameters(parameters);
        request.executeAsync();
    }


    private void register(String mName, String mBirthday, String mEmail, String mGender, String mLink, String mLocation, String mPhotoUrl, String mUserID, String mProvider, String mProviderName) {

        addProfile(mName, mBirthday, mEmail, mGender, mLink, mLocation, mPhotoUrl, mUserID, mProvider, mProviderName);

        //check generate code
        QueryHasCode(mUserID);
    }

    private void QueryHasCode(String mUserID) {
        DatabaseReference sRef = FirebaseDatabase.getInstance().getReference();
        Query query = sRef.child("Users").child(mUserID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0) {
                    Log.d(TAG, "QueryHasCode:dataSnapshot:" + dataSnapshot.getValue());
                    Map<String, Object> mapObj = (Map<String, Object>) dataSnapshot.getValue();
                    if (!mapObj.containsKey("Code")) {
                        GenerateCode();
                    } else {
                        isCode = mapObj.get("Code").toString();
                        Log.d(TAG, "onProfileChange : isCode : " + isCode);
                        final DatabaseReference mUsersRef2 = mRootRef.child("Codes").child(isCode);
                        if(getLatitude != 0.0 && getLongitude != 0.0) mUsersRef2.child("LatLng").setValue(getLatitude +"," +getLongitude);

                    }
                }else{
                    Log.d(TAG, "QueryHasCode:dataSnapshot:0");
                    GenerateCode();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addProfile(String mName, String mBirthday, String mEmail, String mGender, String mLink, String mLocation, String mPhotoUrl, String mUserID, String mProviderID, String mProviderName) {
        //Add and Update data basic
        final DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID);
        mUsersRef.child("UserID").setValue(mUserID);
        mUsersRef.child("Name").setValue(mName);
        if(mBirthday != null && !mBirthday.trim().isEmpty()) {
            mUsersRef.child("Birthday").setValue(mBirthday);
        }
        mUsersRef.child("Email").setValue(mEmail);
        mUsersRef.child("Picture").setValue(mPhotoUrl);
        if(mLocation!=null) mUsersRef.child("Address").setValue(mLocation);
        if(mGender!=null)mUsersRef.child("Gender").setValue(mGender);
        if(mLink!=null)mUsersRef.child("Link").setValue(mLink);
        if(mProviderName.equals("facebook")){
            mUsersRef.child("ProviderName").setValue("facebook");
            mUsersRef.child("ProviderID").setValue(mProviderID);
        }else if(mProviderName.equals("google")){
            mUsersRef.child("ProviderName").setValue("google");
            mUsersRef.child("ProviderID").setValue(mProviderID);
        }else if(mProviderName.equals("twitter")){
            mUsersRef.child("ProviderName").setValue("twitter");
            mUsersRef.child("ProviderID").setValue(mProviderID);
        }

        SharedPreferences sp = getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Birthday", mBirthday);
        editor.putString("Gender", mGender);
        editor.putString("Link", mLink);
        editor.putString("Address", mLocation);
        editor.putString("Name", mName);
        editor.putString("Picture", mPhotoUrl);
        editor.putString("Email", mEmail);
        editor.putString("UserID", mUserID);
        editor.apply();

    }

    private void setDefaultNotification() {
        SharedPreferences sp = getSharedPreferences("NOTIFICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("popup", 1);
        editor.putInt("push", 2);
        editor.apply();
    }

    private void setTranslate() {
        String localeLanguage = Locale.getDefault().getDisplayLanguage();
        String localeCode = Locale.getDefault().toString().toLowerCase().replace("_", "-");
        Log.d(TAG, "localeCode : " + localeCode);

        SharedPreferences sp_languages = getSharedPreferences("MYLANGUAGE", Context.MODE_PRIVATE);
        String stringObject = sp_languages.getString(localeCode, "-1");
        if(stringObject.equals("-1")) {
            MyLanguageFirebase.addLanguageListener(localeLanguage, localeCode, this, MainActivity.this);
        }else{
            SharedPreferences sp_language_code = getSharedPreferences("LANGUAGECODE", Context.MODE_PRIVATE);
            String outputLocal =  sp_language_code.getString("LanguageCode", "-1");
            if(outputLocal.equals("-1")){
                MyLanguageFirebase.addLanguageListener(localeLanguage, "en-us", this, MainActivity.this);
            }else {
                MyLanguageFirebase.addLanguageListener(localeLanguage, outputLocal, this, MainActivity.this);
            }

        }
    }

    @Override
    public void onLanguageChange() {
        Log.d(TAG, "MyLanguageFirebase:onLanguageChange:success");
    }

    private void GenerateCode() {
        Random RandomUtils = new Random();
        int length = 5;
        String ALLOWED_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final StringBuilder sb = new StringBuilder(length);
        for(int i=0;i<length;++i) {
            sb.append(ALLOWED_CHARACTERS.charAt(RandomUtils.nextInt(ALLOWED_CHARACTERS.length())));
        }
        isCode = sb.toString();
        Log.d(TAG, "random code : " + sb.toString());

        //add data to firebase database [User]
        final DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID);
        mUsersRef.child("Code").setValue(isCode);

        //[Code]
        SharedPreferences sp = getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Code", isCode);

        final DatabaseReference mUsersRef2 = mRootRef.child("Codes").child(isCode);
        mUsersRef2.child("ID").setValue(mUserID);
        if(getLatitude != 0.0 && getLongitude != 0.0) mUsersRef2.child("LatLng").setValue(getLatitude +"," +getLongitude);
    }

    private String getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String country = addresses.get(0).getCountryName();
            return country;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Callback class GetLocation
    public void listenerCallbackLocation(MyLocationValue locationValue){
        String tokenFirebase = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Firebase Token :" + tokenFirebase);
        getLatitude = locationValue.getLatitude();
        getLongitude = locationValue.getLongitude();
        Log.d(TAG, "latitude : " + getLatitude);
        Log.d(TAG, "longitude : " + getLongitude);

        if(getLatitude != 0.0 || getLongitude != 0.0) {
            String address = getAddress(getLatitude, getLongitude);

            //add Lat and Lng into Location (in devices)
            DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID).child("Devices").child(deviceID);
            mUsersRef.child("ID").setValue(deviceID);
            mUsersRef.child("DeviceName").setValue(deviceName);
            mUsersRef.child("DeviceType").setValue("Android");
            mUsersRef.child("Active").setValue(1);
            mUsersRef.child("LogOutTime").setValue(0);
            mUsersRef.child("Timestamp").setValue(9999999999999L);
            mUsersRef.child("LatLng").setValue(getLatitude +"," +getLongitude);
            mUsersRef.child("Token").setValue(tokenFirebase);

            //add current Lat and Lng (main)
            DatabaseReference mLocationRef = mRootRef.child("Users").child(mUserID);
            mLocationRef.child("DeviceName").setValue(deviceName);
            mLocationRef.child("DeviceID").setValue(deviceID);
            mLocationRef.child("LatLng").setValue(getLatitude +"," +getLongitude);
            mLocationRef.child("OnMap").setValue("1");
            mLocationRef.child("Token").setValue(tokenFirebase);

            //update address
            Log.d(TAG, "address : " + address);
            assert address != null;
            mLocationRef.child("Address").setValue(address);


            SharedPreferences sp = getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("DeviceID", deviceID);
            editor.putString("DeviceName", deviceName);
            editor.putString("Token", tokenFirebase);
            editor.putString("OnMap", "1");
            editor.putString("Latitude", String.valueOf(getLatitude));
            editor.putString("Longitude", String.valueOf(getLongitude));
            editor.apply();

            //[Code]
            if(isCode != null) {
                Log.d(TAG, "isCode : " + isCode);
                final DatabaseReference mUsersRef2 = mRootRef.child("Codes").child(isCode);
                mUsersRef2.child("LatLng").setValue(getLatitude +"," +getLongitude);
            }

            objLoading.loadingLogo(false);
            objLoading.loading(false);

            Log.d(TAG, "*** Go to Map ***");
            ActivityManagerName managerName = new ActivityManagerName();
            managerName.setCurrentPage("MyMap");
            Intent intent = new Intent(MainActivity.this, MyMap.class);
            intent.putExtra("UserID", mUserID);
            intent.putExtra("UserPhoto", mPhotoUrl.toString());
            intent.putExtra("Latitude", getLatitude);
            intent.putExtra("Longitude", getLongitude);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        }
    }

}

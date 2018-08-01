package com.butions.utnai;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransitMode;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MyMap extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, MyProfileFirebase.ProfileCallbacks, MyProfileFriendsFirebase.ProfileFriendsCallbacks, EmojiFirebase.EmojiCallbacks, CodeFirebase.CodesCallbacks {

    private String TAG = "MyMap";
    private String mUserID;
    private String myName;
    private String myPicture;
    private String mPostMessage;
    private String mPostID;
    private String mPostImage;
    private String mPostDate;
    private String mPostVideo;
    private String mPostCheckIn;
    private DatabaseReference mRootRef;
    private MyUpDateLocation locationListener;
    private LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private final static int DISTANCE_UPDATES = 1;
    private final static int TIME_UPDATES = 5000;
    private MapFragment mapFragment;
    private static GoogleMap mGoogleMap;
    private double Lat, Lng;
    private Marker marker;
    private Marker lastMarker = null;
    private ViewGroup infoWindow;
    private RelativeLayout rectangle_bubble;
    private TextView title;
    private TextView date;
    private static Marker my_marker;
    private static Bitmap my_icon_marker;
    private String deviceID;
    private String deviceName;
    private Bitmap map_mark_utani, map_mark_start, map_mark_end, map_mark_navigate;
    private String myOnMap;
    private LatLng my_position;
    private MapWrapperLayout mapWrapperLayout;
    private static final int DEFAULT_MARGIN_BOTTOM = 2;
    private MapOnInfoWindowElemTouchListener infoButtonListenerLoadMore , infoButtonListenerChat, infoButtonListenerPlay;
    private LinearLayout infoSpaceBtnChat;
    private List<Marker> ListMarker = new ArrayList<>();
    private String mPostFeeling;
    private ImageView image;
    private ImageButton play;
    private TextView phone;
    private TextView feel;
    private ImageView image_feeling;
    private TextView locat;
    private ImageView btnPost, btnHome, btnFeedFriend, btnFeed;
    public static ImageView btnFriend;
    private FragmentManager transaction;
    private RelativeLayout MapRelative;
    private TextView text_utnai;
    @SuppressLint("StaticFieldLeak")
    public static ImageView btnSavePost;
    private RelativeLayout toolbarMap;

    private Map<String, String> myProfile;
    private Map<String, String> myPosts;
    private Map<Integer, String> myNameFriends;
    private Map<Integer, String> myFacebookIDFriends;
    private Map<Integer, String> myPictureFriend;
    private Map<Integer, String> myOnMapFriend;
    private static MarkerOptions myMarkerOptions = null;

    @SuppressLint("StaticFieldLeak")
    private RelativeLayout space_frame;
    public static FloatingActionButton icon_fab;
    private ProgressBar title_loading;
    private ProgressBar image_loading;
    private ArrayList<Marker> ArrayMarker = new ArrayList<>();
    private ArrayList<MarkerOptions> ArrayMarkerOptions = new ArrayList<>();
    private ArrayList<Marker> ArrayMarkerForRemove = new ArrayList<>();
    private ArrayList<MarkerOptions> ArrayMarkerOptionsForRemove = new ArrayList<>();
    private Polyline polyline;
    private MyProfileFirebase.ProfileListener profileListener;
    private MyProfileFriendsFirebase.ProfileFriendsListener profileFriendsListener;
    private boolean no_posts;
    private boolean toolbarShow;
    private EmojiFirebase.EmojiListener emojiListener;
    private static ArrayList<String> EmojiImage;
    private static ArrayList<String> EmojiName;
    private static ArrayList<String> EmojiKey;
    private LinearLayout toolbarNavigation;
    private TextView myCode;
    private EditText searchCode;
    private ImageView btnNavigate;
    private TextView btnHideNavigate;
    private boolean showNavigate;
    private MyLoading objLoading;
    private CodeFirebase.CodesListener codeListener;
    private String isCode, strMyCode;
    private Marker marker_point_start, marker_point_end, marker_start_navigate;
    private RelativeLayout footer_navigate;
    private TextView duration;
    private TextView distance;
    private TextView start_place;
    private ImageView btnCancel;
    private ImageView btnStartNavigate;
    private LinearLayout footbarMap;
    private RelativeLayout.LayoutParams params;
    private RelativeLayout space_navigate;
    private String mUserPhoto;
    private ActivityManagerName managerName;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_map);
        managerName = new ActivityManagerName();
        managerName.setProcessNameClass(this);
        managerName.setNameClassInProcessString("MyMap");

        objLoading = new MyLoading(this);

        Translater.getInstance().setLanguages(this);

        toolbarShow = true;
        mRootRef = FirebaseDatabase.getInstance().getReference();

        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceName = Config.getInstance().getDeviceName();

        map_mark_utani = DrawableMarker.getInstance().resizeMapIcons(R.drawable.map_marker_utnai,this, 4);
        map_mark_start = DrawableMarker.getInstance().resizeMapIcons(R.drawable.point_start,this,2);
        map_mark_end = DrawableMarker.getInstance().resizeMapIcons(R.drawable.point_end,this,2);
        map_mark_navigate = DrawableMarker.getInstance().resizeMapIcons(R.drawable.ic_start_navigate,this,1);

        toolbarMap = (RelativeLayout) findViewById(R.id.toolbarMap);
        toolbarMap.setVisibility(View.VISIBLE);

        space_frame = (RelativeLayout) findViewById(R.id.space_frame);
        footbarMap = (LinearLayout) findViewById(R.id.footbarMap);
        icon_fab = (FloatingActionButton) findViewById(R.id.icon_fab);

        text_utnai = (TextView) findViewById(R.id.text_utnai);
        text_utnai.setText(StringManager.getsInstance().getString("Utnai"));
        text_utnai.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);

        btnSavePost = (ImageView) findViewById(R.id.btnSavePost);
        btnSavePost.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        Lat = bundle.getDouble("Latitude");
        Lng = bundle.getDouble("Longitude");
        mUserID = bundle.getString("UserID");
        mUserPhoto = bundle.getString("UserPhoto");
        int mNotification = bundle.getInt("NotificationID");

        MapRelative = (RelativeLayout) findViewById(R.id.Map);
        transaction = getSupportFragmentManager();

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_framelayout);
                if(fragment != null) transaction.beginTransaction().remove(fragment).commit();
                try {
                    mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.content_map);
                    mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
                    mapFragment.getMapAsync(MyMap.this);
                }catch (Exception e){
                    e.getMessage();
                }
            }
        });

        Initialize();
        if(mNotification != 0){
            Log.d(TAG, "NotificationID:"+ mNotification);
            checkPage();
        }


        SharedPreferences sp = getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
        String error = "-1";
        myName = sp.getString("Name", error);
        myPicture = sp.getString("Picture", error);
        strMyCode = sp.getString("Code", error);

        //call emoji
        emojiListener = EmojiFirebase.addEmojiListener(this, this);
        //call my profile and my posts
        profileListener = MyProfileFirebase.addProfileListener(mUserID, this, this);
        GetLocation();
    }

    private void checkPage() {
        Log.d(TAG, "checkPage:currentPage:"+managerName.getCurrentPage());
        if(managerName.getCurrentPage().equals("MyPost")){
            postsPage();
        }else if(managerName.getCurrentPage().equals("MyFriend")){
            friendPage();
        }else if(managerName.getCurrentPage().equals("MyFeed")){
            myFeedPage();
        }else{
            homePage();
        }
    }

    private void Initialize() {
        showNavigate = true;
        toolbarNavigation = (LinearLayout) findViewById(R.id.toolbarNavigation);
        myCode = (TextView) findViewById(R.id.myCode);
        searchCode = (EditText) findViewById(R.id.searchCode);
        btnNavigate = (ImageView) findViewById(R.id.btnNavigate);
        btnHideNavigate = (TextView) findViewById(R.id.btnHideNavigate);

        footer_navigate = (RelativeLayout) findViewById(R.id.footer_navigate);
        space_navigate = (RelativeLayout) findViewById(R.id.space_navigate);
        duration = (TextView) findViewById(R.id.duration);
        distance = (TextView) findViewById(R.id.distance);
        start_place = (TextView) findViewById(R.id.start_place);
        btnCancel = (ImageView) findViewById(R.id.btnCancel);
        btnStartNavigate = (ImageView) findViewById(R.id.btnStartNavigate);

        duration.setTypeface(Config.getInstance().getDefaultFont(this));
        distance.setTypeface(Config.getInstance().getDefaultFont(this));
        start_place.setTypeface(Config.getInstance().getDefaultFont(this));

        btnHideNavigate.setTypeface(Config.getInstance().getDefaultFont(this));
        searchCode.setTypeface(Config.getInstance().getDefaultFont(this));
        myCode.setTypeface(Config.getInstance().getDefaultFont(this));

        btnHideNavigate.setText(StringManager.getsInstance().getString("HideNavigate"));
        searchCode.setHint(StringManager.getsInstance().getString("FriendCode"));
        searchCode.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        btnStartNavigate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnHideNavigate.setOnClickListener(this);
        btnNavigate.setOnClickListener(this);
        myCode.setOnClickListener(this);

        btnHome = (ImageView) findViewById(R.id.btnHome);
        btnFriend = (ImageView) findViewById(R.id.btnFriend);
        btnPost = (ImageView) findViewById(R.id.btnPost);
        btnFeedFriend = (ImageView) findViewById(R.id.btnFeedFriend);
        btnFeed = (ImageView) findViewById(R.id.btnFeed);

        btnHome.setBackgroundResource(R.color.transparent);
        btnFriend.setBackgroundResource(R.color.transparent);
        btnPost.setBackgroundResource(R.color.transparent);
        btnFeedFriend.setBackgroundResource(R.color.transparent);
        btnFeed.setBackgroundResource(R.color.transparent);

        btnHome.setOnClickListener(this);
        btnPost.setOnClickListener(this);
        btnFriend.setOnClickListener(this);
        btnFeedFriend.setOnClickListener(this);
        btnFeed.setOnClickListener(this);
    }


    @Override
    public void onProfileChange(ProfileValue profileValue) {
        //my position change
        //profileListener
        if(profileValue != null) {
            myProfile = profileValue.getMyProfile();
            myPosts = profileValue.getMyPosts();
            strMyCode = myProfile.get("Code");
            myCode.setText(myProfile.get("Code"));
            if (myPosts != null) {
                no_posts = false;
                String message = myPosts.get("Message");
                mPostMessage = deviceName + "\n" + message;
                mPostFeeling = myPosts.get("Feeling");
                mPostImage = myPosts.get("Image");
                mPostVideo = myPosts.get("Video");
                mPostCheckIn = myPosts.get("CheckInName");
                mPostDate = myPosts.get("Created_time");
                mPostID = myPosts.get("ID");
            } else {
                no_posts = true;
            }
            onUpdateMyMarker();

            //my friends position change
            myNameFriends = profileValue.getMyNameFriends();
            myFacebookIDFriends = profileValue.getMyFacebookIDFriends();
            myPictureFriend = profileValue.getMyPictureFriend();
            myOnMapFriend = profileValue.getMyOnMapFriend();
            onMarkerChangeMyFriends();
        }

    }

    private void onUpdateMyMarker() {
        Lat = Double.parseDouble(myProfile.get("Latitude"));
        Lng = Double.parseDouble(myProfile.get("Longitude"));
        if(myMarkerOptions == null) {
            Log.d(TAG, "*** myMarkerOptions == null ***");
            //my marker
            String data = mUserID + "/&"
                    + Lat + "/&"
                    + Lng + "/&"
                    + mPostMessage + "/&"
                    + mPostDate + "/&"
                    + deviceID + "/&"
                    + mPostFeeling + "/&"
                    + mPostImage + "/&"
                    + mPostVideo + "/&"
                    + mPostCheckIn + "/&"
                    + mPostID + "/&"
                    + mUserPhoto;
            new BitmapFromUrl().execute(data);
        }else{
            Log.d(TAG, "*** myMarkerOptions != null ***");
            LatLng position = new LatLng(Lat, Lng);
            my_marker.remove();
            my_marker = mGoogleMap.addMarker(myMarkerOptions);
            String title = mPostMessage+ "/&" + CalendarTime.getInstance().getTimeAgo(mPostDate) + "/&" + mPostFeeling + "/&" + mPostImage  +"/&" + mPostVideo+"/&" + mPostCheckIn+ "/&" + mPostID;
            my_marker.setPosition(position);
            my_marker.setTitle(title);
        }
    }

    private void onMarkerChangeMyFriends() {
        for(int i=0; i<myFacebookIDFriends.size(); i++){
            String onMapFriends = myOnMapFriend.get(i);
            if (onMapFriends.equals("1")) {
                addMarkerFriend(myFacebookIDFriends.get(i));
            } else if (onMapFriends.equals("0")) {
                hideMarkerFriend(myFacebookIDFriends.get(i));
            }
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //Log.d(TAG, "*** onMapReady ***");
        final UiSettings settings = googleMap.getUiSettings();
        settings.setMapToolbarEnabled(false);      //navigation bar
        settings.setZoomControlsEnabled(false);   //button zoom

        my_position = new LatLng(Lat, Lng);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        boolean isFlag = MoveToFriesndLocation.getInstance().getFlag();
        if(isFlag){
            LatLng friend_position = new LatLng(MoveToFriesndLocation.getInstance().getLatitude(), MoveToFriesndLocation.getInstance().getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(friend_position, 18));
            MoveToFriesndLocation.getInstance().setFlag(false);
        }else {
            boolean isFlagNavigate = MoveToNavigateWithCode.getInstance().getFlagNavigate();
            if(isFlagNavigate){
                btnStartNavigate.setVisibility(View.VISIBLE);
                start_place.setVisibility(View.VISIBLE);
                params = (RelativeLayout.LayoutParams) space_navigate.getLayoutParams();
                params.height = 300;
                space_navigate.setLayoutParams(params);

                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchCode.getWindowToken(), 0);
                //set code to editText
                isCode = MoveToNavigateWithCode.getInstance().getCode();
                searchCode.setText(isCode);

                if(!isCode.isEmpty()){
                    objLoading.loading(true);
                    codeListener = CodeFirebase.addCodesListener(this , isCode);
                }
                MoveToNavigateWithCode.getInstance().setFlagNavigate(false);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my_position, 18));
            }else {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my_position, 13));
            }
        }

//      add marker me
        String data = mUserID + "/&"
                + Lat +"/&"
                + Lng + "/&"
                + mPostMessage+"/&"
                + mPostDate+"/&"
                + deviceID +"/&"
                + mPostFeeling +"/&"
                + mPostImage +"/&"
                + mPostVideo+"/&"
                + mPostCheckIn+ "/&"
                + mPostID + "/&"
                + mUserPhoto;

        BitmapDescriptor bmp = BitmapDescriptorFactory.fromBitmap(map_mark_utani);
        myMarkerOptions = new MarkerOptions()
                .position(my_position)
                .title(mPostMessage+ "/&" + CalendarTime.getInstance().getTimeAgo(mPostDate) + "/&" + mPostFeeling + "/&" + mPostImage  +"/&" + mPostVideo+"/&" + mPostCheckIn+ "/&" + mPostID + "/&" + mUserPhoto)
                .snippet(mUserID+"/&"+deviceID)
                .icon(bmp);
        my_marker = googleMap.addMarker(myMarkerOptions);

        new BitmapFromUrl().execute(data);

        marker = my_marker;

        this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_info_windows, null);
        this.rectangle_bubble = (RelativeLayout) infoWindow.findViewById(R.id.rectangle_bubble);
        this.title = (TextView) infoWindow.findViewById(R.id.title);
        this.title_loading = (ProgressBar) infoWindow.findViewById(R.id.title_loading);
        this.date = (TextView) infoWindow.findViewById(R.id.date);
        this.image = (ImageView) infoWindow.findViewById(R.id.image);
        this.image_loading = (ProgressBar) infoWindow.findViewById(R.id.image_loading);
        this.play = (ImageButton) infoWindow.findViewById(R.id.play);
        this.phone = (TextView) infoWindow.findViewById(R.id.phone);
        this.feel = (TextView) infoWindow.findViewById(R.id.feeling);
        this.image_feeling = (ImageView) infoWindow.findViewById(R.id.image_feeling);
        this.locat = (TextView) infoWindow.findViewById(R.id.checkIn);
        this.infoSpaceBtnChat = (LinearLayout) infoWindow.findViewById(R.id.btnChat);
        LinearLayout infoSpaceBtnPost = (LinearLayout) infoWindow.findViewById(R.id.btnPost);
        ImageButton infoButtonPost = (ImageButton) infoWindow.findViewById(R.id.imagePost);
        ImageButton infoButtonChat = (ImageButton) infoWindow.findViewById(R.id.imageChat);

        //setFont
        this.title.setTypeface(Config.getInstance().getDefaultFont(this));
        this.date.setTypeface(Config.getInstance().getDefaultFont(this));
        this.phone.setTypeface(Config.getInstance().getDefaultFont(this));
        this.feel.setTypeface(Config.getInstance().getDefaultFont(this));
        this.locat.setTypeface(Config.getInstance().getDefaultFont(this));

        this.infoButtonListenerLoadMore = new MapOnInfoWindowElemTouchListener(infoSpaceBtnPost,
                getResources().getDrawable(R.drawable.infowindows3),
                getResources().getDrawable(R.drawable.infowindows4)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                String[] mSnippet = marker.getSnippet().split("/&");
                String getSnippetFacebookId = mSnippet[0];
                String getSnippetDeviceId = mSnippet[1];
                if(!marker.getTitle().equals("null/&null/&null/&null/&null/&null/&null")) {
                    Intent intent = new Intent(MyMap.this, MyPostDetail.class);
                    intent.putExtra("mUserID", getSnippetFacebookId);
                    intent.putExtra("mPostMessage", marker.getTitle());
                    intent.putExtra("MyFacebookID", mUserID);
                    intent.putExtra("MyName", myName);
                    intent.putExtra("MyPicture", myPicture);
                    startActivity(intent);
                }else{
                    postsPage();
                }
            }
        };
        infoButtonPost.setOnTouchListener(infoButtonListenerLoadMore);

        this.infoButtonListenerChat = new MapOnInfoWindowElemTouchListener(infoSpaceBtnChat,
                getResources().getDrawable(R.drawable.infowindows3),
                getResources().getDrawable(R.drawable.infowindows4)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Log.d(TAG, "marker.getSnippet():" + marker.getSnippet());
                if(!marker.getSnippet().equals(null) || !marker.getSnippet().contains("null")) {
                    String isMyCode = myProfile.get("Code");
                    Intent intent = new Intent(MyMap.this, Chat.class);
                    intent.putExtra("isUserID", marker.getSnippet());   //Friend
                    intent.putExtra("MyUserID", mUserID);          //Me
                    intent.putExtra("MyPicture", myPicture);
                    intent.putExtra("MyCode", isMyCode);
                    startActivity(intent);
                }
            }
        };
        infoButtonChat.setOnTouchListener(infoButtonListenerChat);

        this.infoButtonListenerPlay = new MapOnInfoWindowElemTouchListener(play,
                getResources().getDrawable(R.drawable.infowindows3),
                getResources().getDrawable(R.drawable.infowindows4)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                //intent play video
                String[] arrayMessage = marker.getTitle().split("/&");
                String video = arrayMessage[4];
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video));
                intent.setDataAndType(Uri.parse(video), "video/mp4");
                startActivity(intent);
            }
        };
        this.play.setOnTouchListener(infoButtonListenerPlay);

        icon_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my_position, 13));
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker != null){
                    marker.showInfoWindow();
                }
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker != null) {
                    Log.d(TAG, "*** click marker :  " + marker.getSnippet() + "***");
                    Log.d(TAG, "*** Array marker : " + ArrayMarker + "***");
//                for(int i = 0; i<ArrayMarker.size(); i++){
//                    if(marker.getPosition().equals(ArrayMarker.get(i).getPosition())){
//                        Log.d(TAG, "*** marker duplicate : "+ArrayMarker.get(i).getSnippet()+"***");
//                        ArrayMarkerForRemove.add(ArrayMarker.get(i));
//                        ArrayMarkerOptionsForRemove.add(ArrayMarkerOptions.get(i));
//                    }
//                }
//
//                for(int i=0; i<ArrayMarkerForRemove.size(); i++){
//                    Marker isMarker = ArrayMarkerForRemove.get(i);
//                    isMarker.remove();
//                }
//
//                createPoint();
                }

                return false;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("MapsActivity", "*** Map Click ***");
//                for(int i=0; i<ArrayMarkerForRemove.size(); i++){
//                    Marker isMarker = ArrayMarkerForRemove.get(i);
//                    isMarker.remove();
//                }
//                ArrayMarkerForRemove.clear();
//                ArrayMarkerOptionsForRemove.clear();
//                if(polyline != null){
//                    polyline.remove();
//                }
                //addMarkerStart();
            }
        });

        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

    }

    private void createPoint() {
        double x = 0.000002;
        double y = 0.0000002;
        Log.d(TAG, "*** size ArrayMarkerForRemove : " +ArrayMarkerForRemove.size()+ "***");
        for(int i=0; i<ArrayMarkerForRemove.size(); i++){
            if(i == 0){
                Marker m  = mGoogleMap.addMarker(ArrayMarkerOptionsForRemove.get(i));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ArrayMarkerOptionsForRemove.get(i).getPosition(), 22));
            }else{
                double isLat = ArrayMarkerOptionsForRemove.get(i).getPosition().latitude;
                double isLng = ArrayMarkerOptionsForRemove.get(i).getPosition().longitude;
                double calLat = (isLat * x)+ isLat;
                double calLng = (isLng * y) + isLng;
                LatLng marker_position = new LatLng(calLat, calLng);
                Marker m  = mGoogleMap.addMarker(ArrayMarkerOptionsForRemove.get(i));
                m.setPosition(marker_position);
                NavigatorDirection(ArrayMarkerOptionsForRemove.get(i).getPosition(), m.getPosition(), mGoogleMap);
            }
        }
    }

    private void NavigatorDirection(final LatLng myLocation, LatLng toLocation, final GoogleMap googleMap){
        //Draw poly line form origin to destination
        ClearPolyline();
        PolylineOptions polylineOpt = new PolylineOptions()
                .add(myLocation)
                .add(toLocation)
                .width(3)
                .color(getResources().getColor(R.color.black))
                .geodesic(true);
        polyline = googleMap.addPolyline(polylineOpt);
    }

    private void ClearPolyline() {
        if(polyline != null) {
            polyline.remove();
        }
        if(marker_point_start != null){
            marker_point_start.remove();
            marker_point_start = null;
        }
        if(marker_point_end != null){
            marker_point_end.remove();
            marker_point_end = null;
        }
        if(marker_start_navigate != null){
            marker_start_navigate.remove();
            marker_point_end = null;
        }
    }

    private void ClearFooter(){
        footbarMap.setVisibility(View.VISIBLE);
        icon_fab.setVisibility(View.VISIBLE);
        footer_navigate.setVisibility(View.GONE);
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        CustomInfoWindowAdapter() {

        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
                //Log.d(TAG, "id marker : " + marker.getId());
            if(marker.getSnippet() != null) {
                if (lastMarker == null || !lastMarker.getId().equals(marker.getId())) {
                    lastMarker = marker;

                    //set Data Info
                    String[] mSnippet = marker.getSnippet().split("/&");
                    String getSnippetFacebookId = mSnippet[0];
                    String getSnippetDeviceId = mSnippet[1];
                    if (marker.getId().equals("m0") || getSnippetFacebookId.equals(mUserID) && getSnippetDeviceId.equals(deviceID)) {
                        rectangle_bubble.setBackgroundResource(R.drawable.infowindows2);  //Red
                        infoSpaceBtnChat.setVisibility(View.GONE);  //hide chat
                        String[] arrayMessage = marker.getTitle().split("/&");
                        //Log.d(TAG, "Title : " + marker.getTitle());
                        String message = arrayMessage[0];
                        String timeDate = arrayMessage[1];
                        String feeling = arrayMessage[2];
                        String picture = arrayMessage[3];
                        String video = arrayMessage[4];
                        String checkIn = arrayMessage[5];

                        if (!message.equals("null")) {
                            title_loading.setVisibility(View.GONE);
                            phone.setVisibility(View.VISIBLE);

                            String[] arrayMessage2 = message.split("\n");
                            if (!arrayMessage2[1].equals("null")) {
                                title.setText(arrayMessage2[1]);
                            } else {
                                title.setText(" ");
                            }
                            phone.setText(String.valueOf("- " + StringManager.getsInstance().getString("By") + " " + arrayMessage2[0]));
                        } else {
                            if (!no_posts) {
                                title_loading.setVisibility(View.VISIBLE);
                                title.setTextColor(getResources().getColor(R.color.red));
                                title.setText(StringManager.getsInstance().getString("Loading"));
                                phone.setVisibility(View.GONE);
                            } else {
                                title_loading.setVisibility(View.INVISIBLE);
                                title.setTextColor(getResources().getColor(R.color.grey_900));
                                title.setText(StringManager.getsInstance().getString("AddStatus"));
                            }
                        }

                        if (!feeling.equals("null")) {
                            int index_emoji = checkImageEmoji(feeling);
                            if (index_emoji != -1) {
                                image_feeling.setVisibility(View.VISIBLE);
                                feel.setVisibility(View.VISIBLE);
                                Picasso.with(MyMap.this)
                                        .load(EmojiImage.get(index_emoji))
                                        .centerCrop()
                                        .fit()
                                        .noFade()
                                        .placeholder(R.drawable.ic_emotion_mini)
                                        .error(R.drawable.ic_emotion_mini)
                                        .into(image_feeling);
                                feel.setText(String.valueOf("-" + StringManager.getsInstance().getString("FeelingFeed") + " " + EmojiName.get(index_emoji)));
                            } else {
                                image_feeling.setVisibility(View.VISIBLE);
                                image_feeling.setImageResource(R.drawable.ic_emotion_mini);
                                feel.setVisibility(View.VISIBLE);
                                feel.setText(String.valueOf("-" + StringManager.getsInstance().getString("FeelingFeed") + feeling));
                            }
                        } else {
                            image_feeling.setVisibility(View.GONE);
                            feel.setVisibility(View.GONE);
                        }

                        if (!timeDate.equals("null") && !timeDate.trim().isEmpty()) {
                            date.setVisibility(View.VISIBLE);
                            date.setText(timeDate);
                        } else {
                            date.setVisibility(View.GONE);
                        }

                        if (!checkIn.equals("null")) {
                            locat.setVisibility(View.VISIBLE);
                            String text = checkIn.replace("\n", " ");
                            locat.setText(text);
                        } else {
                            locat.setVisibility(View.GONE);
                        }

                        if (picture != null && !picture.equals("null")) {
                            image.setVisibility(View.VISIBLE);
                            image_loading.setVisibility(View.VISIBLE);
                            Picasso.with(MyMap.this)
                                    .load(picture)
                                    .centerCrop()
                                    .fit()
                                    .transform(new CircleTransform())
                                    .placeholder(R.drawable.bg_circle_white)
                                    .error(R.drawable.bg_circle_white)
                                    .into(image, new ImageLoadedCallback(image_loading, marker));
                            if (video != null && !video.equals("null")) {
                                play.setVisibility(View.VISIBLE);
                            } else {
                                play.setVisibility(View.GONE);
                            }
                        } else {
                            image.setVisibility(View.GONE);
                            image_loading.setVisibility(View.GONE);
                            play.setVisibility(View.GONE);
                        }

                    } else {
                        rectangle_bubble.setBackgroundResource(R.drawable.infowindows2);  //White
                        infoSpaceBtnChat.setVisibility(View.VISIBLE);  //show chat
                        String[] arrayMessage = marker.getTitle().split("/&");
                        String message = arrayMessage[0];
                        String timeDate = arrayMessage[1];
                        String feeling = arrayMessage[2];
                        String picture = arrayMessage[3];
                        String video = arrayMessage[4];
                        String checkIn = arrayMessage[5];

                        if (!message.equals("null")) {
                            title_loading.setVisibility(View.GONE);
                            phone.setVisibility(View.VISIBLE);

                            String[] arrayMessage2 = message.split("\n");
                            if (!arrayMessage2[1].equals("null")) {
                                title.setText(arrayMessage2[1]);
                            } else {
                                title.setText(" ");
                            }
                            phone.setText(String.valueOf("- " + StringManager.getsInstance().getString("By") + " " + arrayMessage2[0]));
                        } else {
                            title_loading.setVisibility(View.VISIBLE);
                            title.setText(StringManager.getsInstance().getString("Loading"));
                            phone.setVisibility(View.GONE);
                        }

                        if (!feeling.equals("null")) {
                            int index_emoji = checkImageEmoji(feeling);
                            if (index_emoji != -1) {
                                image_feeling.setVisibility(View.VISIBLE);
                                feel.setVisibility(View.VISIBLE);
                                Picasso.with(MyMap.this)
                                        .load(EmojiImage.get(index_emoji))
                                        .centerCrop()
                                        .fit()
                                        .noFade()
                                        .placeholder(R.color.white)
                                        .error(R.drawable.ic_emotion_mini)
                                        .into(image_feeling);
                                feel.setText(String.valueOf("-" + StringManager.getsInstance().getString("FeelingFeed") + " " + EmojiName.get(index_emoji)));
                            } else {
                                image_feeling.setVisibility(View.VISIBLE);
                                image_feeling.setImageResource(R.drawable.ic_emotion_mini);
                                feel.setVisibility(View.VISIBLE);
                                feel.setText(String.valueOf("-" + StringManager.getsInstance().getString("FeelingFeed") + feeling));
                            }
                        } else {
                            image_feeling.setVisibility(View.GONE);
                            feel.setVisibility(View.GONE);
                        }

                        if (!timeDate.equals("null") && !timeDate.trim().isEmpty()) {
                            date.setVisibility(View.VISIBLE);
                            date.setText(timeDate);
                        } else {
                            date.setVisibility(View.GONE);
                        }

                        if (!checkIn.equals("null")) {
                            locat.setVisibility(View.VISIBLE);
                            String text = checkIn.replace("\n", " ");
                            locat.setText(text);
                        } else {
                            locat.setVisibility(View.GONE);
                        }

                        if (picture != null && !picture.equals("null")) {
                            image.setVisibility(View.VISIBLE);
                            image_loading.setVisibility(View.VISIBLE);
                            Picasso.with(MyMap.this)
                                    .load(picture)
                                    .centerCrop()
                                    .fit()
                                    .noFade()
                                    .placeholder(R.drawable.bg_circle_white)
                                    .error(R.drawable.bg_circle_white)
                                    .transform(new CircleTransform())
                                    .into(image, new ImageLoadedCallback(image_loading, marker));
                            if (video != null && !video.equals("null")) {
                                play.setVisibility(View.VISIBLE);
                            } else {
                                play.setVisibility(View.GONE);
                            }
                        } else {
                            image.setVisibility(View.GONE);
                            image_loading.setVisibility(View.GONE);
                            play.setVisibility(View.GONE);
                        }
                    }
                }

                mapWrapperLayout.init(mGoogleMap, getPixelsFromDp(getApplicationContext(), DEFAULT_MARGIN_BOTTOM));
                infoButtonListenerLoadMore.setMarker(marker);
                infoButtonListenerChat.setMarker(marker);
                infoButtonListenerPlay.setMarker(marker);
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
            }else{
                rectangle_bubble.setVisibility(View.GONE);
            }
            return infoWindow;
        }
    }

    private int checkImageEmoji(String feeling) {
        int emojiIndex = -1;
        if(feeling != null && EmojiKey != null) {
            for (int i = 0; i < EmojiKey.size(); i++) {
                if (EmojiKey.get(i).contains(feeling)) {
                    emojiIndex = i;
                }
            }
        }
        return emojiIndex;
    }

    private class ImageLoadedCallback implements Callback{
        Marker marker = null;

        ImageLoadedCallback(ProgressBar progBar, Marker marker) {
            this.marker = marker;
            image_loading = progBar;
        }

        @Override
        public void onSuccess() {
            if(image_loading != null){
                image_loading.setVisibility(View.GONE);
            }

            if (marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }

        }

        @Override
        public void onError() {

        }
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 2.5f);
    }

    private void hideMarkerFriend(String facebookIdFriend) {
        //hide all marker, it had facebookID
        for(int i=0; i<ListMarker.size(); i++) {
            Marker isMarker = ListMarker.get(i);
            String[] mSnippet = isMarker.getSnippet().split("/&");
            String getSnippetFacebookId = mSnippet[0];
            if (getSnippetFacebookId.equals(facebookIdFriend)) {
                isMarker.remove();
            }
        }
    }

    private void addMarkerFriend(final String FacebookIdFriend) {
        profileFriendsListener = MyProfileFriendsFirebase.addProfileFriendsListener(FacebookIdFriend, this);
    }

    @Override
    public void onFriendsChange(ProfileFriendsValue profilefriendsValue) {
        //profileFriendsListener
        if(profilefriendsValue != null) {
            Map<String, String> myFriends = profilefriendsValue.getMyFriends();
            Map<String, String> myPostsFriends = profilefriendsValue.getMyPostsFriends();
            //add marker friend
            if(myFriends.get("Latitude") != null &&  myFriends.get("Longitude") != null) {
                String data = myFriends.get("UserID") + "/&" + myFriends.get("Latitude") + "/&" + myFriends.get("Longitude") + "/&" + myFriends.get("DeviceName") + "\n" + myPostsFriends.get("Message") + "/&" + myPostsFriends.get("Created_time") + "/&" + myFriends.get("DeviceID") + "/&" + myPostsFriends.get("Feeling") + "/&" + myPostsFriends.get("Image") + "/&" + myPostsFriends.get("Video") + "/&" + myPostsFriends.get("CheckInName") + "/&" + myPostsFriends.get("ID") + "/&" + myFriends.get("Picture");
                new BitmapFromUrl().execute(data);
            }
        }
    }

    private void GetLocation() {
        Log.d(TAG, "*** Initialize Get Location ***");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyUpDateLocation(this);
        RequestLocationUpdates();
    }

    private void RequestLocationUpdates(){
        mRootRef.child("Users").child(mUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> mapObj = (Map<String, Object>) dataSnapshot.getValue();
                    if (mapObj.containsKey("OnMap")) {
                        myOnMap = mapObj.get("OnMap").toString();
                        if (myOnMap.equals("1")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Marshmallow API 23
                                if (ActivityCompat.checkSelfPermission(MyMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MyMap.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MyMap.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                                    ActivityCompat.requestPermissions(MyMap.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
                                } else {
                                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_UPDATES, DISTANCE_UPDATES, locationListener);
                                }
                            } else {
                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_UPDATES, DISTANCE_UPDATES, locationListener);
                            }
                        } else {
                            if (ActivityCompat.checkSelfPermission(MyMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyMap.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MyMap.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                                ActivityCompat.requestPermissions(MyMap.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
                                return;
                            }
                            locationManager.removeUpdates(locationListener);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class MyUpDateLocation implements android.location.LocationListener {

        private final Context mContext;

        MyUpDateLocation(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG,"latitude :" + location.getLatitude());
            Log.d(TAG,"longitude :" + location.getLongitude());

            SharedPreferences sp = getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
            double sp_lat = Double.parseDouble(sp.getString("Latitude", "-1"));
            double sp_lng = Double.parseDouble(sp.getString("Longitude", "-1"));

            if(location.getLatitude() != sp_lat || location.getLongitude() != sp_lng){
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Latitude", String.valueOf(location.getLatitude()));
                editor.putString("Longitude", String.valueOf(location.getLongitude()));
                editor.apply();

                UpdateLocation(location.getLatitude(), location.getLongitude());
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            //Log.d(TAG, "*** on Provider Disabled ***");
        }
    }
    private void UpdateLocation(Double Latitude, Double Longitude) {
        //update marker google map
        LatLng position = new LatLng(Latitude, Longitude);
        my_position = position;
        if(my_marker != null) {
            my_marker.remove();
            my_marker = null;
        }

        if(my_marker == null) {
            try {
                if (my_icon_marker != null) {
                    BitmapDescriptor bmp = BitmapDescriptorFactory.fromBitmap(my_icon_marker);
                    my_marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(mPostMessage +"/&"+CalendarTime.getInstance().getTimeAgo(mPostDate)+"/&"+mPostFeeling+"/&"+mPostImage +"/&" + mPostVideo +"/&" + mPostCheckIn+ "/&" + mPostID)
                            .snippet(mUserID+"/&"+deviceID)
                            .icon(bmp));
                } else {
                    BitmapDescriptor bmp = BitmapDescriptorFactory.fromBitmap(map_mark_utani);
                    my_marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(mPostMessage +"/&"+CalendarTime.getInstance().getTimeAgo(mPostDate)+"/&"+mPostFeeling+"/&"+mPostImage +"/&" + mPostVideo +"/&" + mPostCheckIn+ "/&" + mPostID)
                            .snippet(mUserID+"/&"+deviceID)
                            .icon(bmp));
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }

        //update location into Devices
        DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID).child("Devices").child(deviceID);
        mUsersRef.child("ID").setValue(deviceID);
        mUsersRef.child("DeviceName").setValue(deviceName);
        mUsersRef.child("DeviceType").setValue("Android");
        mUsersRef.child("LatLng").setValue(Latitude +"," +Longitude);

        //update location into main
        DatabaseReference mLocationRef = mRootRef.child("Users").child(mUserID);
        mLocationRef.child("DeviceID").setValue(deviceID);
        mLocationRef.child("DeviceName").setValue(deviceName);
        mLocationRef.child("LatLng").setValue(Latitude +"," +Longitude);

        if(strMyCode != null && !strMyCode.equals("-1")) {
            final DatabaseReference mUsersRef2 = mRootRef.child("Codes").child(strMyCode);
            mUsersRef2.child("LatLng").setValue(Latitude +"," +Longitude);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class BitmapFromUrl extends AsyncTask<String, Bitmap, Bitmap> {
        Bitmap bitmap = null;
        InputStream input;
        HttpURLConnection connection = null;
        String isUserID;
        String isLatitude;
        String isLongitude;
        String isMessage;
        String isDeviceID;
        String isCreateTime;
        String isFeeling;
        String isPicture;
        String isVideo;
        String isCheckIn;
        String isPostID;
        String isPhotoProfile;
        @Override
        protected Bitmap doInBackground(String... data) {
            try {
                String isData = data[0];
                String[] getData = isData.split("/&");
                isUserID = getData[0];
                isLatitude = getData[1];
                isLongitude = getData[2];
                isMessage = getData[3];

                String date = getData[4];
                isCreateTime = CalendarTime.getInstance().getTimeAgo(date);

                isDeviceID = getData[5];
                isFeeling = getData[6];
                isPicture = getData[7];
                isVideo = getData[8];
                isCheckIn = getData[9];
                isPostID = getData[10];
                isPhotoProfile = getData[11];
                URL url = new URL(isPhotoProfile);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                input = new BufferedInputStream(connection.getInputStream());

                //read image from url
                bitmap = BitmapFactory.decodeStream(input);
                return DrawableMarker.getInstance().drawableFromUrl(bitmap, MyMap.this);

            }catch (Exception e){
                e.getMessage();
            }finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            BitmapDescriptor bmp = BitmapDescriptorFactory.fromBitmap(bitmap);
            double doulat = Double.parseDouble(isLatitude);
            double doulng = Double.parseDouble(isLongitude);
            LatLng isPosition = new LatLng(doulat , doulng);
            if(isUserID.equals(mUserID)){
                try {
                    my_icon_marker = bitmap;
                    my_marker.remove();
                    myMarkerOptions = new MarkerOptions()
                            .position(isPosition)
                            .title(isMessage + "/&" + isCreateTime + "/&" + isFeeling + "/&" + isPicture + "/&" + isVideo + "/&" + isCheckIn + "/&" + isPostID)
                            .snippet(isUserID + "/&" + isDeviceID)
                            .icon(bmp);
                    my_marker = mGoogleMap.addMarker(myMarkerOptions);
                    ArrayMarker.add(my_marker);
                    ArrayMarkerOptions.add(myMarkerOptions);
                }catch (Exception e){
                    Log.d(TAG, e.getMessage());
                }
            }else {
                //check device id before mark lat,lng (case: location change)
                try {
                    hideMarkerFriend(isUserID);
                    MarkerOptions marker_option = new MarkerOptions()
                            .position(isPosition)
                            .title(isMessage + "/&" + isCreateTime + "/&" + isFeeling + "/&" + isPicture + "/&" + isVideo + "/&" + isCheckIn + "/&" + isPostID)
                            .snippet(isUserID + "/&" + isDeviceID)
                            .icon(bmp);
                    marker = mGoogleMap.addMarker(marker_option);
                    ArrayMarkerOptions.add(marker_option);
                    ArrayMarker.add(marker);
                    ListMarker.add(marker);

                }catch (Exception e){
                    Log.d(TAG, "ERE : " + e.getMessage());
                }
            }
        }
    }

    private void postsPage(){
        managerName.setCurrentPage("MyPost");
        space_frame.setFocusableInTouchMode(true);
        space_frame.setClickable(true);

        MapRelative.setFocusableInTouchMode(false);
        MapRelative.setClickable(false);
        MapRelative.setVisibility(View.GONE);

        toolbarShow = true;
        toolbarMap.setVisibility(View.VISIBLE);
        text_utnai.setText(StringManager.getsInstance().getString("NewPost"));
        btnSavePost.setVisibility(View.VISIBLE);
        icon_fab.setVisibility(View.GONE);
        footer_navigate.setVisibility(View.GONE);

        btnPost.setImageResource(R.drawable.ic_add_utnai_full);
        clearClickIcon(btnPost);
        Bundle bundle = new Bundle();
        bundle.putString("MyUserID", mUserID);
        bundle.putString("MyName", myName);
        bundle.putString("MyPicture", myPicture);
        bundle.putDouble("MyLatitude", my_position.latitude);
        bundle.putDouble("MyLongitude", my_position.longitude);
        MyPost objMyPost = new MyPost();
        objMyPost.setArguments(bundle);
        transaction.beginTransaction().replace(R.id.content_framelayout, objMyPost).commit();
    }

    private void friendPage(){
        managerName.setCurrentPage("MyFriend");
        space_frame.setFocusableInTouchMode(true);
        space_frame.setClickable(true);

        toolbarNavigation.setVisibility(View.GONE);
        btnHideNavigate.setVisibility(View.GONE);

        MapRelative.setFocusableInTouchMode(false);
        MapRelative.setClickable(false);
        MapRelative.setVisibility(View.GONE);

        toolbarShow = true;
        toolbarMap.setVisibility(View.VISIBLE);
        text_utnai.setText(StringManager.getsInstance().getString("Tracking"));
        btnSavePost.setVisibility(View.GONE);
        icon_fab.setVisibility(View.GONE);
        footer_navigate.setVisibility(View.GONE);

        btnFriend.setImageResource(R.drawable.ic_friends_full);
        clearClickIcon(btnFriend);

        Bundle bundle = new Bundle();
        bundle.putString("MyUserID", mUserID);
        MyFriend objMyFriend = new MyFriend();
        objMyFriend.setArguments(bundle);
        transaction.beginTransaction().replace(R.id.content_framelayout, objMyFriend).commit();
    }

    private void homePage(){
        managerName.setCurrentPage("MyMap");
        space_frame.setFocusableInTouchMode(false);
        space_frame.setClickable(false);

        toolbarNavigation.setVisibility(View.VISIBLE);
        btnHideNavigate.setVisibility(View.VISIBLE);

        MapRelative.setFocusableInTouchMode(true);
        MapRelative.setClickable(true);
        MapRelative.setVisibility(View.VISIBLE);

        toolbarShow = true;
        toolbarMap.setVisibility(View.VISIBLE);
        text_utnai.setText(StringManager.getsInstance().getString("Utnai"));
        searchCode.setHint(StringManager.getsInstance().getString("FriendCode"));
        btnSavePost.setVisibility(View.GONE);
        icon_fab.setVisibility(View.VISIBLE);

        btnHome.setImageResource(R.drawable.ic_home_full);
        clearClickIcon(btnHome);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_framelayout);
        if(fragment != null) transaction.beginTransaction().remove(fragment).commit();
    }

    private void myFeedPage(){
        managerName.setCurrentPage("MyFeed");
        space_frame.setFocusableInTouchMode(true);
        space_frame.setClickable(true);

        MapRelative.setFocusableInTouchMode(false);
        MapRelative.setClickable(false);
        MapRelative.setVisibility(View.GONE);

        toolbarNavigation.setVisibility(View.GONE);
        btnHideNavigate.setVisibility(View.GONE);

        toolbarShow = false;
        toolbarMap.setVisibility(View.GONE);
        btnSavePost.setVisibility(View.GONE);
        icon_fab.setVisibility(View.GONE);
        footer_navigate.setVisibility(View.GONE);

        btnFeed.setImageResource(R.drawable.ic_person_full);
        clearClickIcon(btnFeed);

        Bundle bundle = new Bundle();
        bundle.putString("mUserID", mUserID);
        bundle.putString("MyName", myName);
        bundle.putString("MyPicture", myPicture);
        bundle.putString("MyOnMap",myOnMap);
        bundle.putDouble("MyLatitude", my_position.latitude);
        bundle.putDouble("MyLongitude", my_position.longitude);
        MyFeed objMyFeed = new MyFeed();
        objMyFeed.setArguments(bundle);
        transaction.beginTransaction().replace(R.id.content_framelayout, objMyFeed).commit();
    }

    private void newFeedFriendPage(){
        managerName.setCurrentPage("MyNewFeeds");
        space_frame.setFocusableInTouchMode(true);
        space_frame.setClickable(true);

        MapRelative.setFocusableInTouchMode(false);
        MapRelative.setClickable(false);
        MapRelative.setVisibility(View.GONE);

        toolbarNavigation.setVisibility(View.GONE);
        btnHideNavigate.setVisibility(View.GONE);

        toolbarShow = true;
        toolbarMap.setVisibility(View.VISIBLE);
        text_utnai.setText(StringManager.getsInstance().getString("NewFeeds"));
        btnSavePost.setVisibility(View.GONE);
        icon_fab.setVisibility(View.GONE);
        footer_navigate.setVisibility(View.GONE);

        btnFeedFriend.setImageResource(R.drawable.ic_feed_full);
        clearClickIcon(btnFeedFriend);

        ArrayList<String> listNameFriend = new ArrayList<>(myNameFriends.values());
        ArrayList<String> listFacebookIDFriend = new ArrayList<>(myFacebookIDFriends.values());
        ArrayList<String> listPictureFriend = new ArrayList<>(myPictureFriend.values());

        Bundle bundle = new Bundle();
        bundle.putString("MyFacebookID", mUserID);
        bundle.putString("MyName", myName);
        bundle.putString("MyPicture", myPicture);
        bundle.putString("MyOnMap",myOnMap);
        bundle.putDouble("MyLatitude", my_position.latitude);
        bundle.putDouble("MyLongitude", my_position.longitude);
        bundle.putStringArrayList("NameFriend", listNameFriend);
        bundle.putStringArrayList("FBIDFriend", listFacebookIDFriend);
        bundle.putStringArrayList("PictureFriend", listPictureFriend);
        MyNewFeeds objMyFeedFriend = new MyNewFeeds();
        objMyFeedFriend.setArguments(bundle);
        transaction.beginTransaction().replace(R.id.content_framelayout, objMyFeedFriend).commit();
    }

    @Override
    public void onClick(final View view) {
        if(view == btnPost){
            postsPage();

        }else if(view == btnFriend){
            friendPage();

        }else if(view == btnHome){
            homePage();

        }else if(view == btnFeed){
            myFeedPage();

        }else if(view == btnFeedFriend){
            newFeedFriendPage();

        }else if(view  == btnHideNavigate){
            if(showNavigate) {
                toolbarNavigation.setVisibility(View.GONE);
                btnHideNavigate.setText(StringManager.getsInstance().getString("ShowNavigate"));
                showNavigate = false;
            }else{
                toolbarNavigation.setVisibility(View.VISIBLE);
                btnHideNavigate.setText(StringManager.getsInstance().getString("HideNavigate"));
                showNavigate = true;
            }
        }else if(view == btnNavigate){

            btnStartNavigate.setVisibility(View.VISIBLE);
            start_place.setVisibility(View.VISIBLE);
            params = (RelativeLayout.LayoutParams) space_navigate.getLayoutParams();
            params.height = 300;
            space_navigate.setLayoutParams(params);

            //hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchCode.getWindowToken(), 0);
            //get code from editText
            isCode = searchCode.getText().toString().toUpperCase().trim();
            if(!isCode.isEmpty()){
                objLoading.loading(true);
                codeListener = CodeFirebase.addCodesListener(this, isCode);
            }

        }else if(view == myCode){
            String isMyCode = myProfile.get("Code");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.putExtra(Intent.EXTRA_TEXT, isMyCode);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share Code!"));
        }else if(view == btnCancel){
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my_position, 13));
            ClearFooter();
            ClearPolyline();
        }else if(view == btnStartNavigate){
            BitmapDescriptor bmp = BitmapDescriptorFactory.fromBitmap(map_mark_navigate);
            btnStartNavigate.setVisibility(View.GONE);
            start_place.setVisibility(View.GONE);
            params = (RelativeLayout.LayoutParams) space_navigate.getLayoutParams();
            params.height = 200;
            space_navigate.setLayoutParams(params);
            LatLng isPositionNavigation = marker_point_start.getPosition();
            marker_start_navigate = mGoogleMap.addMarker(new MarkerOptions()
                    .position(isPositionNavigation)
                    .icon(bmp));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(isPositionNavigation, 20));
        }
    }

    private void clearClickIcon(ImageView view) {
        if(view == btnHome){
            btnFriend.setImageResource(R.drawable.ic_friends);
            btnFeedFriend.setImageResource(R.drawable.ic_feed);
            btnFeed.setImageResource(R.drawable.ic_person);
            btnPost.setImageResource(R.drawable.ic_add_utnai);

            btnFriend.setBackgroundResource(R.color.transparent);
            btnFeedFriend.setBackgroundResource(R.color.transparent);
            btnFeed.setBackgroundResource(R.color.transparent);
            btnPost.setBackgroundResource(R.color.transparent);
        }else if(view == btnPost){
            btnFriend.setImageResource(R.drawable.ic_friends);
            btnFeedFriend.setImageResource(R.drawable.ic_feed);
            btnFeed.setImageResource(R.drawable.ic_person);
            btnHome.setImageResource(R.drawable.ic_home);

            btnFriend.setBackgroundResource(R.color.transparent);
            btnFeedFriend.setBackgroundResource(R.color.transparent);
            btnFeed.setBackgroundResource(R.color.transparent);
            btnHome.setBackgroundResource(R.color.transparent);
        }else if(view == btnFriend){
            btnPost.setImageResource(R.drawable.ic_add_utnai);
            btnFeedFriend.setImageResource(R.drawable.ic_feed);
            btnFeed.setImageResource(R.drawable.ic_person);
            btnHome.setImageResource(R.drawable.ic_home);

            btnPost.setBackgroundResource(R.color.transparent);
            btnFeedFriend.setBackgroundResource(R.color.transparent);
            btnFeed.setBackgroundResource(R.color.transparent);
            btnHome.setBackgroundResource(R.color.transparent);
        }else if(view == btnFeedFriend){
            btnPost.setImageResource(R.drawable.ic_add_utnai);
            btnFriend.setImageResource(R.drawable.ic_friends);
            btnFeed.setImageResource(R.drawable.ic_person);
            btnHome.setImageResource(R.drawable.ic_home);

            btnPost.setBackgroundResource(R.color.transparent);
            btnFriend.setBackgroundResource(R.color.transparent);
            btnFeed.setBackgroundResource(R.color.transparent);
            btnHome.setBackgroundResource(R.color.transparent);
        }else if(view == btnFeed){
            btnPost.setImageResource(R.drawable.ic_add_utnai);
            btnFriend.setImageResource(R.drawable.ic_friends);
            btnFeedFriend.setImageResource(R.drawable.ic_feed);
            btnHome.setImageResource(R.drawable.ic_home);

            btnPost.setBackgroundResource(R.color.transparent);
            btnFriend.setBackgroundResource(R.color.transparent);
            btnFeedFriend.setBackgroundResource(R.color.transparent);
            btnHome.setBackgroundResource(R.color.transparent);
        }
    }

    @Override
    public void onEmojiChange(EmojiValue emojiValue) {
        if(emojiValue != null) {
            EmojiImage = emojiValue.getEmojiImage();
            EmojiName = emojiValue.getEmojiName();
            EmojiKey = emojiValue.getEmojiKey();
        }
        EmojiFirebase.stop(emojiListener);
    }

    @Override
    public void onCodesChange(CodesValue codesValue) {
        Log.d(TAG, "isCode : " + isCode);
        CodeFirebase.stop(codeListener);
        if(codesValue != null){
            String  codesID = codesValue.getCodes();
            String codesLat = codesValue.getCodesLat();
            String codesLng = codesValue.getCodesLng();
            //navigate
            Log.d(TAG, "Navigate Founds!");

            footbarMap.setVisibility(View.GONE);
            icon_fab.setVisibility(View.GONE);
            footer_navigate.setVisibility(View.VISIBLE);

            Log.d(TAG, "id follow : " + codesID);
            LatLng toLocation = new LatLng(Double.parseDouble(codesLat), Double.parseDouble(codesLng));
            objLoading.loading(false);
            Navigator(my_position, toLocation, mGoogleMap);


        }else{
            //dialog show not founds
            objLoading.loading(false);
            Log.d(TAG, "Navigate Not Founds!");
            //show dialog not founds
            final Dialog dialog_waining = new Dialog(this);
            dialog_waining.getWindow();
            dialog_waining.setCanceledOnTouchOutside(false);
            dialog_waining.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_waining.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog_waining.setContentView(R.layout.dialog_data);

            TextView dialog_title = (TextView) dialog_waining.findViewById(R.id.dialog_title);
            dialog_title.setText(StringManager.getsInstance().getString("NoRouteFound"));

            TextView dialog_detail = (TextView) dialog_waining.findViewById(R.id.dialog_detail);
            dialog_detail.setText(StringManager.getsInstance().getString("CheckCode"));

            Button buttonOK = (Button) dialog_waining.findViewById(R.id.btnOk);
            buttonOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_waining.cancel();
                    searchCode.setText("");
                }
            });

            dialog_waining.show();

        }
    }


    private void Navigator(final LatLng myLocation, LatLng toLocation, final GoogleMap googleMap){
        Log.d(TAG, "myLocation : " + myLocation);
        Log.d(TAG, "toLocation : " + toLocation);
        ClearPolyline();
        String SERVER_KEY = getResources().getString(R.string.API_KEY_GOOGLE_MAP);
        GoogleDirectionConfiguration.getInstance().setLogEnabled(true);
        GoogleDirection.withServerKey(SERVER_KEY)  // use lib of <com.akexorcist:googledirectionlibrary:1.0.5>
                .from(myLocation)
                .to(toLocation)
                .alternativeRoute(true)
                .transportMode(TransportMode.DRIVING)
                .transitMode(TransitMode.BUS)
                .unit(Unit.METRIC)
                .language(com.akexorcist.googledirection.constant.Language.ENGLISH)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        String status = direction.getStatus();
                        Log.d(TAG, "rawBody : " + rawBody);
                        Log.d(TAG, "*** Request Result Router Direction : "+status+" ***");
                        if(status.equals(RequestResult.OK)){
                            try {

                                //transport mode TRANSIT
                                Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);
                                LatLng start = leg.getStartLocation().getCoordination();
                                LatLng end = leg.getEndLocation().getCoordination();

                                BitmapDescriptor bmp_start = BitmapDescriptorFactory.fromBitmap(map_mark_start);
                                BitmapDescriptor bmp_end = BitmapDescriptorFactory.fromBitmap(map_mark_end);
                                marker_point_start = googleMap.addMarker(new MarkerOptions()
                                        .position(start)
                                        .icon(bmp_start));

                                marker_point_end = googleMap.addMarker(new MarkerOptions()
                                        .position(end)
                                        .icon(bmp_end));

                                Log.d(TAG, "legs start_address  : " + leg.getStartAddress());
                                Log.d(TAG, "legs start_location  : " + start);
                                Log.d(TAG, "legs end_address  : " + leg.getEndAddress());
                                Log.d(TAG, "legs end_location  : " + end);
                                Log.d(TAG, "legs distance  : " + leg.getDistance().getText());
                                Log.d(TAG, "legs duration  : " + leg.getDuration().getText());
                                distance.setText(leg.getDistance().getText());
                                duration.setText(leg.getDuration().getText());
                                start_place.setText(leg.getStartAddress());

                                //transport mode TRANSIT
//                                List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
//                                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(MyMap.this, stepList, 10, Color.argb(255, 52, 154, 255) , 10, Color.argb(255, 52, 154, 255));
//                                for(PolylineOptions polylineOptions : polylineOptionList) {
//                                    Log.d(TAG, "*** create poly line ***");
//                                    polyline = googleMap.addPolyline(polylineOptions);
//                                }


                                //transport mode DRIVING
                                ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(MyMap.this, directionPositionList, 10, Color.argb(255, 52, 154, 255));
                                polyline = googleMap.addPolyline(polylineOptions);

                            }catch (Exception e){
                                Log.d(TAG, e.getMessage());
                            }
                        }else if(status.equals(RequestResult.NOT_FOUND)){
                            footer_navigate.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), status,Toast.LENGTH_SHORT).show();
                        }else{
                            footer_navigate.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), status,Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "***onResume***");
        //translate
        Translater.getInstance().setLanguages(this);

        //call emoji
        emojiListener = EmojiFirebase.addEmojiListener(this, this);

        checkCurrentPage();

        ActivityManagerName managerName = new ActivityManagerName();
        managerName.setProcessNameClass(this);
        managerName.setNameClassInProcessString("MyMap");
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.content_map);
        mapFragment.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_UPDATES, DISTANCE_UPDATES, locationListener);
        btnSavePost.setVisibility(View.GONE);
        RequestLocationUpdates();
    }

    private void checkCurrentPage() {
        if(toolbarShow){
            toolbarMap.setVisibility(View.VISIBLE);
        }else{
            toolbarMap.setVisibility(View.GONE);
        }

        if(managerName.getCurrentPage()!= null && managerName.getCurrentPage().equals("MyMap")){
            toolbarNavigation.setVisibility(View.VISIBLE);
            btnHideNavigate.setVisibility(View.VISIBLE);
            icon_fab.setVisibility(View.VISIBLE);
            footer_navigate.setVisibility(View.GONE);
            searchCode.setHint(StringManager.getsInstance().getString("FriendCode"));
            if(showNavigate) {
                btnHideNavigate.setText(StringManager.getsInstance().getString("ShowNavigate"));
            }else{
                btnHideNavigate.setText(StringManager.getsInstance().getString("HideNavigate"));
            }
        }else{
            icon_fab.setVisibility(View.GONE);
            toolbarNavigation.setVisibility(View.GONE);
            btnHideNavigate.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "***onPause***");
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.content_map);
        mapFragment.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ListMarker.clear();
        Log.d(TAG, "***onDestroy***");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }
        if(locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        MyProfileFirebase.stop(profileListener);
        if(profileFriendsListener != null) {
            MyProfileFriendsFirebase.stop(profileFriendsListener);
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.content_map);
        mapFragment.onLowMemory();
        super.onLowMemory();
    }
}

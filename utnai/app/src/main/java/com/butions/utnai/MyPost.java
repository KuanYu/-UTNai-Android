package com.butions.utnai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MyPost extends Fragment implements View.OnClickListener, EmojiFirebase.EmojiCallbacks {

    private ArrayList<String> images;
    private String TAG = "MyPost";
    private EditText text_comment;
    private RelativeLayout space_text;
    private TextView icon_camera;
    private TextView icon_image;
    private TextView icon_video;
    private TextView icon_emotion;
    private DatabaseReference mRootRef;
    private String mUserID;
    private LinearLayout space_all;
    private String mName;
    private String mPicture;
    private TextView text_name_city;
    private String mLocalName = "-1";
    private String deviceID;
    private String deviceName;
    private ExpandableHeightListView list_item_emoji;
    private ArrayList<String> EmojiName;
    private ArrayList<String> EmojiImage;
    private ArrayList<String> EmojiKey;
    private CustomAdapterEmoji adapter_emoji;
    private RelativeLayout space_select;
    private TextView text_emoji;
    private ImageView cancel_emoji;
    private LinearLayout space_emoji;
    private String select_emoji;
    private ExpandableHeightGridView grid_gallery;
    private ImageAdapter adapter_gallery;
    private ImageView image_comment;
    private int w, h;
    private LinearLayout space_image;
    private LinearLayout layout_image;
    private LinearLayout layout_camera;
    private LinearLayout layout_emotion;
    private LinearLayout layout_video;
    private String mPostDate;
    private String checkInName, checkInLat, checkInLng;
    private VideoView video_comment;
    private String PHOTO_PATH;
    private static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String PHOTO_PATH_FROM_VIDEO;
    private String URL_IMAGE_VIDEO;
    private Context context;
    private View v;
    private double mLat;
    private double mLng;
    private EmojiFirebase.EmojiListener emojiListener;
    private ImageView image_emoji;


    @SuppressLint("HardwareIds")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.my_post, container, false);
        context = getActivity();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        MyTakePhoto.getInstance().setIsOpenCamera(false);

        MyMap.btnSavePost.setVisibility(View.VISIBLE);
        MyMap.btnSavePost.setOnClickListener(this);

        mUserID = getArguments().getString("MyUserID");
        mName = getArguments().getString("MyName");
        mPicture = getArguments().getString("MyPicture");
        mLat = getArguments().getDouble("MyLatitude");
        mLng = getArguments().getDouble("MyLongitude");
        mPostDate = CalendarTime.getInstance().getCurrentDate();
        adapter_gallery = new ImageAdapter((Activity) context);

        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceName = Config.getInstance().getDeviceName();

        float m =  Config.getInstance().getDisplayDensity(context);
        w = (int) (210 * m);
        h = (int) (210 * m);

        //call emoji
        emojiListener = EmojiFirebase.addEmojiListener(this, context);

        InitializeMyPost();
        getAddress(mLat, mLng);

        return v;
    }

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            mLocalName = city + "," + country;
            Log.d(TAG, "Address : " + mLocalName);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void InitializeMyPost() {

        list_item_emoji = (ExpandableHeightListView) v.findViewById(R.id.list_item);
        adapter_emoji = new CustomAdapterEmoji(context);
        list_item_emoji.setExpanded(true);

        space_all = (LinearLayout) v.findViewById(R.id.space_all);
        ImageView imageProfile = (ImageView) v.findViewById(R.id.imageProfile);
        TextView text_name = (TextView) v.findViewById(R.id.text_name);
        text_name_city = (TextView) v.findViewById(R.id.text_name_city);
        TextView text_name_date = (TextView) v.findViewById(R.id.text_name_date);
        text_name.setTypeface(Config.getInstance().getDefaultFont(context), Typeface.BOLD);
        text_name_city.setTypeface(Config.getInstance().getDefaultFont(context));
        text_name_date.setTypeface(Config.getInstance().getDefaultFont(context));

        layout_image = (LinearLayout) v.findViewById(R.id.layout_image);
        layout_camera = (LinearLayout) v.findViewById(R.id.layout_camera);
        layout_emotion = (LinearLayout) v.findViewById(R.id.layout_emotion);
        layout_video = (LinearLayout) v.findViewById(R.id.layout_video);

        space_text = (RelativeLayout) v.findViewById(R.id.space_text);
        text_comment = (EditText) v.findViewById(R.id.text_comment);
        text_comment.setTypeface(Config.getInstance().getDefaultFont(context));
        text_comment.setHint(StringManager.getsInstance().getString("YourCaption"));
        String textDate = CalendarTime.getInstance().getCurrentOnlyDateText();
        text_name_date.setText(textDate);

        space_image = (LinearLayout) v.findViewById(R.id.space_image);
        video_comment = (VideoView) v.findViewById(R.id.video_comment);
        image_comment = (ImageView) v.findViewById(R.id.image_comment);
        ViewGroup.LayoutParams params = image_comment.getLayoutParams();
        params.width = w;
        params.height = h;
        image_comment.setLayoutParams(params);
        image_comment.setScaleType(ImageView.ScaleType.FIT_CENTER);
        video_comment.setLayoutParams(params);

        icon_camera = (TextView) v.findViewById(R.id.icon_camera);
        icon_image = (TextView) v.findViewById(R.id.icon_image);
        icon_video = (TextView) v.findViewById(R.id.icon_video);
        icon_emotion = (TextView) v.findViewById(R.id.icon_emotion);

        icon_camera.setTypeface(Config.getInstance().getDefaultFont(context));
        icon_image.setTypeface(Config.getInstance().getDefaultFont(context));
        icon_video.setTypeface(Config.getInstance().getDefaultFont(context));
        icon_emotion.setTypeface(Config.getInstance().getDefaultFont(context));

        icon_camera.setText(StringManager.getsInstance().getString("Camera"));
        icon_image.setText(StringManager.getsInstance().getString("Photo"));
        icon_video.setText(StringManager.getsInstance().getString("Video"));
        icon_emotion.setText(StringManager.getsInstance().getString("Feeling"));


        space_select = (RelativeLayout) v.findViewById(R.id.space_select);
        space_emoji = (LinearLayout) v.findViewById(R.id.space_emoji);
        text_emoji = (TextView) v.findViewById(R.id.text_emoji);
        image_emoji = (ImageView) v.findViewById(R.id.image_emoji);
        text_emoji.setTypeface(Config.getInstance().getDefaultFont(context));
        cancel_emoji = (ImageView) v.findViewById(R.id.cancel_emoji);
        cancel_emoji.setOnClickListener(this);

        list_item_emoji.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                space_emoji.setVisibility(View.VISIBLE);
                select_emoji = EmojiKey.get(i);
                Picasso.with(context)
                        .load(EmojiImage.get(i))
                        .centerCrop()
                        .fit()
                        .noFade()
                        .placeholder(R.color.white)
                        .error(R.drawable.ic_emotion_mini)
                        .into(image_emoji);
                text_emoji.setText(StringManager.getsInstance().getString("FeelingFeed") + " " + EmojiName.get(i));
            }
        });

        grid_gallery = (ExpandableHeightGridView) v.findViewById(R.id.gallery_gridView);
        grid_gallery.setExpanded(true);

        new LoadGallery().execute();

        grid_gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                space_image.setVisibility(View.VISIBLE);
                image_comment.setVisibility(View.VISIBLE);
                video_comment.setVisibility(View.GONE);
                PHOTO_PATH = Uri.parse(images.get(i)).toString();
                Log.d(TAG, "File Name Gallery: " + PHOTO_PATH);
                image_comment.setImageURI(Uri.parse(images.get(i)));
            }
        });
        text_name_city.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_location_on,0);
        text_name_city.setText(String.valueOf(StringManager.getsInstance().getString("AddLocation") + "..."));

        icon_camera.setOnClickListener(this);
        icon_image.setOnClickListener(this);
        icon_video.setOnClickListener(this);
        icon_emotion.setOnClickListener(this);

        layout_image.setOnClickListener(this);
        layout_camera.setOnClickListener(this);
        layout_emotion.setOnClickListener(this);
        layout_video.setOnClickListener(this);


        space_text.setOnClickListener(this);
        space_all.setOnClickListener(this);
        text_comment.setOnClickListener(this);
        text_name_city.setOnClickListener(this);

        Picasso.with(context)
                .load(mPicture)
                .centerCrop()
                .fit()
                .placeholder(R.drawable.bg_circle_white)
                .error(R.drawable.ic_account_circle)
                .transform(new CircleTransform())
                .into(imageProfile);

        text_name.setText(mName);
    }

    @Override
    public void onEmojiChange(EmojiValue emojiValue) {
        if(emojiValue != null) {
            EmojiImage = emojiValue.getEmojiImage();
            EmojiName = emojiValue.getEmojiName();
            EmojiKey = emojiValue.getEmojiKey();
        }
        adapter_emoji.notifyDataSetChanged();
        list_item_emoji.setAdapter(adapter_emoji);
    }

    @Override
    public void onClick(View view) {
        if(view == icon_camera || view == layout_camera){
            hideKeyBoard();
            space_select.setVisibility(View.GONE);

            Intent nextpage = new Intent(context, MyCamera.class);
            startActivity(nextpage);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }else if(view == icon_image || view == layout_image){
            MyTakePhoto.getInstance().setPathVideo(null);

            hideKeyBoard();

            space_select.setVisibility(View.VISIBLE);
            list_item_emoji.setVisibility(View.GONE);
            grid_gallery.setVisibility(View.VISIBLE);

        }else if(view == icon_video || view == layout_video){

            MyTakePhoto.getInstance().setPathImage(null);

            hideKeyBoard();
            space_select.setVisibility(View.GONE);

            Intent nextpage = new Intent(context, MyCameraVideo.class);
            startActivity(nextpage);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }else if(view == icon_emotion || view == layout_emotion){
            hideKeyBoard();

            space_select.setVisibility(View.VISIBLE);
            list_item_emoji.setVisibility(View.VISIBLE);
            grid_gallery.setVisibility(View.GONE);

        }else if(view == MyMap.btnSavePost){
            hideKeyBoard();
            String message = checkMessage(text_comment.getText().toString());
            if(message != null || select_emoji != null) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyMap.btnSavePost.setBackgroundResource(R.color.Transparent);
                    }
                }, 2000);
                if (PHOTO_PATH != null) {
                    uploadFromFile(PHOTO_PATH);
                } else {
                    addDataToFirebase(null,null);
                }
            }else{
                text_comment.setText("");
            }

        }else if(view == space_text || view == text_comment){
            space_select.setVisibility(View.GONE);

            text_comment.clearFocus();
            text_comment.requestFocus();
            text_comment.setCursorVisible(true);
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(text_comment, InputMethodManager.SHOW_IMPLICIT);

        }else if(view == space_all){
            hideKeyBoard();

        }else if(view == cancel_emoji){
            hideKeyBoard();
            text_emoji.setText("");
            select_emoji = "";
            space_emoji.setVisibility(View.GONE);

        }else if(view == text_name_city){
            hideKeyBoard();
            space_select.setVisibility(View.GONE);
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onPickButtonClick();
                }
            });
        }
    }


    public void onPickButtonClick() {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build((Activity) context);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "Error check in : " + e.getMessage());
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, context);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final LatLng latlng = place.getLatLng();

            text_name_city.setBackgroundResource(R.color.transparent);
            text_name_city.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_location,0);
            text_name_city.setText(name+"\n"+address);
            checkInName = name.toString()+"\n"+address.toString();
            checkInLat = String.valueOf(latlng.latitude);
            checkInLng = String.valueOf(latlng.longitude);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(text_comment.getWindowToken(), 0);
    }

    private void uploadFromFile(String path) {
        Log.d(TAG, "File Path : " + path);
        if(path.contains(".mp4")){
            uploadImageFromVideoFile(PHOTO_PATH_FROM_VIDEO);

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("video/mp4")
                    .setCustomMetadata("country", mLocalName)
                    .build();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference folderRef = storageRef.child("Posts").child(mUserID);
            //Helper.showDialog(this);
            Uri file = Uri.fromFile(new File(path));
            StorageReference imageRef = folderRef.child(file.getLastPathSegment());
            final UploadTask mUploadTask = imageRef.putFile(file , metadata);

            Helper.initProgressDialog(context);
            mUploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //Helper.dismissDialog();
                    Helper.dismissProgressDialog();
                    Log.d(TAG, String.format("Failure: %s", exception.getMessage()));
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Helper.dismissDialog();
                    Helper.dismissProgressDialog();
                    Toast.makeText(context,"Upload Video Success", Toast.LENGTH_SHORT).show();
                    //add data to database
                    addDataToFirebase(String.valueOf(taskSnapshot.getDownloadUrl()), URL_IMAGE_VIDEO);

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    Helper.setProgress(progress);
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "*** OnPausedListener ***");
                }
            });

        }else{
            uploadImageFromFile(path);
        }
    }

    private void uploadImageFromVideoFile(String path) {
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("country", mLocalName)
                .build();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference folderRef = storageRef.child("Posts").child(mUserID);
        //Helper.showDialog(this);
        Uri file = Uri.fromFile(new File(path));
        StorageReference imageRef = folderRef.child(file.getLastPathSegment());

        final UploadTask mUploadTask = imageRef.putFile(file , metadata);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, String.format("Failure: %s", exception.getMessage()));
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload Image From Video File Success");
                URL_IMAGE_VIDEO = String.valueOf(taskSnapshot.getDownloadUrl());

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "*** OnPausedListener ***");
            }
        });
    }

    private void uploadImageFromFile(String path) {
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("country", mLocalName)
                .build();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference folderRef = storageRef.child("Posts").child(mUserID);
        //Helper.showDialog(this);
        Uri file = Uri.fromFile(new File(path));
        StorageReference imageRef = folderRef.child(file.getLastPathSegment());


        final UploadTask mUploadTask = imageRef.putFile(file , metadata);

        Helper.initProgressDialog(context);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Helper.dismissDialog();
                Helper.dismissProgressDialog();
                Log.d(TAG, String.format("Failure: %s", exception.getMessage()));
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Helper.dismissDialog();
                Helper.dismissProgressDialog();
                Toast.makeText(context,"Upload Image Success", Toast.LENGTH_SHORT).show();
                //add data to database
                addDataToFirebase(null, String.valueOf(taskSnapshot.getDownloadUrl()));

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                Helper.setProgress(progress);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "*** OnPausedListener ***");
            }
        });
    }

    private void addDataToFirebase(String UrlVideo, String UrlImage) {
        //update name location
        if(!mLocalName.equals("-1")) {
            mRootRef.child("Users").child(mUserID).child("Address").setValue(mLocalName);
        }

        //add post
        String message = checkMessage(text_comment.getText().toString());
        if(message != null && !message.trim().isEmpty() || UrlImage != null || select_emoji != null) {
            //Node Posts
            Log.d(TAG, "### IS ADD POST FIREBASE");
            DatabaseReference mPostRef = mRootRef.child("Posts").child(mUserID).push();
            String key = mPostRef.getKey();
            mPostRef.child("Id").setValue(key);
            mPostRef.child("Created_time").setValue(mPostDate);
            if (message != null && !message.equals("")) mPostRef.child("Message").setValue(message);
            if (select_emoji != null) mPostRef.child("Feeling").setValue(select_emoji);
            if (UrlImage != null) mPostRef.child("Image").setValue(UrlImage);
            if (UrlVideo != null) mPostRef.child("Video").setValue(UrlVideo);
            if (checkInName != null) {
                mPostRef.child("CheckInName").setValue(checkInName);
                mPostRef.child("Latitude").setValue(checkInLat);
                mPostRef.child("Longitude").setValue(checkInLng);
            }

            //Node LastPost
            DatabaseReference mLastPostRef = mRootRef.child("Users").child(mUserID).child("LastPost");
            mLastPostRef.child("Id").setValue(key);
            mLastPostRef.child("Created_time").setValue(mPostDate);
            if (message != null && !message.equals("")) mLastPostRef.child("Message").setValue(message);
            if (select_emoji != null) mLastPostRef.child("Feeling").setValue(select_emoji);
            if (UrlImage != null) mLastPostRef.child("Image").setValue(UrlImage);
            if (UrlVideo != null) mLastPostRef.child("Video").setValue(UrlVideo);
            if (checkInName != null) {
                mLastPostRef.child("CheckInName").setValue(checkInName);
                mLastPostRef.child("Latitude").setValue(checkInLat);
                mLastPostRef.child("Longitude").setValue(checkInLng);
            }

            ActivityManagerName managerName = new ActivityManagerName();
            managerName.setCurrentPage("MyMap");
            Intent intent = new Intent(context, MyMap.class);
            intent.putExtra("Latitude", mLat);
            intent.putExtra("Longitude", mLng);
            intent.putExtra("UserID", mUserID);
            intent.putExtra("UserPhoto", mPicture);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private String checkMessage(String text) {
        if(!text.trim().isEmpty()){
            return text;
        }else{
            return null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "***OnStop***");
        EmojiFirebase.stop(emojiListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "*** onResume ***");
        MyMap.btnSavePost.setVisibility(View.VISIBLE);
        if(MyTakePhoto.getInstance().getIsOpenCamera()){
            layout_camera.setBackgroundResource(R.color.transparent);
            layout_video.setBackgroundResource(R.color.transparent);
            if(MyTakePhoto.getInstance().getPathImage() != null) {
                PHOTO_PATH = MyTakePhoto.getInstance().getPathImage();
                PHOTO_PATH_FROM_VIDEO = null;
                Log.d(TAG, "File Name Photo: " + PHOTO_PATH);
                space_image.setVisibility(View.VISIBLE);
                image_comment.setVisibility(View.VISIBLE);
                video_comment.setVisibility(View.GONE);
                image_comment.setImageURI(Uri.parse(MyTakePhoto.getInstance().getPathImage()));
            }else if(MyTakePhoto.getInstance().getPathVideo() != null){
                PHOTO_PATH = MyTakePhoto.getInstance().getPathVideoFile().toString();
                PHOTO_PATH_FROM_VIDEO = MyTakePhoto.getInstance().getPathImageFromVideoFile().toString();
                Log.d(TAG, "File Name Video: " + PHOTO_PATH);
                space_image.setVisibility(View.VISIBLE);
                image_comment.setVisibility(View.GONE);
                video_comment.setVisibility(View.VISIBLE);
                MediaController mediaController = new MediaController(context);
                mediaController.setAnchorView(video_comment);
                video_comment.setMediaController(mediaController);
                video_comment.setVideoURI(MyTakePhoto.getInstance().getPathVideo());
                video_comment.start();
            }
        }

        text_name_city.setBackgroundResource(R.color.transparent);
    }

    private class CustomAdapterEmoji extends BaseAdapter {

        public Context mContext;
        private LayoutInflater mLayoutInflater;
        CustomAdapterEmoji(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return EmojiName.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {

            //list on click select list view
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if(convertView == null){
                convertView =  mLayoutInflater.inflate(R.layout.listview_emoji, parent, false);
                holder = new ViewHolder();
                holder.image_emoji = (ImageView) convertView.findViewById(R.id.image_emoji);
                holder.text_emoji = (TextView) convertView.findViewById(R.id.text_emoji);

                holder.text_emoji.setTypeface(Config.getInstance().getDefaultFont(mContext));

                convertView.setTag(holder);

            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            Picasso.with(mContext)
                    .load(EmojiImage.get(position))
                    .centerCrop()
                    .fit()
                    .noFade()
                    .placeholder(R.color.white)
                    .error(R.drawable.ic_emotion_mini)
                    .into(holder.image_emoji);
            holder.text_emoji.setText(EmojiName.get(position));

            return convertView;
        }
    }
    private class ViewHolder{
        ImageView image_emoji;
        TextView text_emoji;
    }

    private class ImageAdapter extends BaseAdapter {

        private  LayoutInflater mLayoutInflater;
        private Activity context;
        ImageAdapter(Activity localContext) {
            context = localContext;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolderGridView holder;
            if (convertView == null) {
                convertView =  mLayoutInflater.inflate(R.layout.gridview, parent, false);
                holder = new ViewHolderGridView();

                holder.picturesView = (ImageView) convertView.findViewById(R.id.image);
                ViewGroup.LayoutParams params = holder.picturesView.getLayoutParams();
                params.width = w;
                params.height = h;
                holder.picturesView.setLayoutParams(params);
                holder.picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolderGridView) convertView.getTag();
            }

            Glide.with(context)
                    .load(images.get(position))
                    .placeholder(R.color.black)
                    .fitCenter()
                    .into(holder.picturesView);

            return convertView;
        }
    }
    private class ViewHolderGridView{
        ImageView picturesView;
    }

    @SuppressLint("Recycle")
    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[] {
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media._ID +" DESC");

        assert cursor != null;
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

    private class LoadGallery extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            images = getAllShownImagesPath((Activity) context);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            grid_gallery.setAdapter(adapter_gallery);
        }
    }
}

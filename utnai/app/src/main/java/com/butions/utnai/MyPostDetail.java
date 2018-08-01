package com.butions.utnai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//public class MyPostDetail extends YouTubeBaseActivity implements View.OnClickListener, YouTubePlayer.OnInitializedListener {
public class MyPostDetail extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "MyPostDetail";
    private String mFacebookIDFriend;
    private String mPostMessage;
    private ImageView imageProfile;
    private TextView text_name;
    private TextView text_caption;
    private DatabaseReference mRootRef;
    private ImageView btnBack;
    private String mNameProfile, mImageProfile;
    private ImageView image;
//    private YouTubePlayerView youTubeView;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private YouTubePlayer player;
    private String id_youtube_video;
    private ExpandableHeightListView list_item_comment;

    private ImageView image_comment;
    private TextView name_user_comment;
    private TextView text_comment_comment;
    private CustomAdapter adapter_comment;
    private ImageView btnSend;
    private LinearLayout rootView;

    private ArrayList<String> mUserComment = new ArrayList<String>();
    private ArrayList<String> mPhotoComment = new ArrayList<String>();
    private ArrayList<String> mTextComment = new ArrayList<String>();
    private ArrayList<String> mDateComment = new ArrayList<String>();
    private ArrayList<String> EmojiImage = new ArrayList<String>();
    private ArrayList<String> EmojiName = new ArrayList<String>();
    private ArrayList<String> mNameUserLike = new ArrayList<String>();
    private ArrayList<String> mPictureUserLike = new ArrayList<String>();
    private TextView text_name_city;
    private String isDeviceNameFriend;
    private TextView text_name_date;
    private String myFacebookID;
    private String myName;
    private String myPicture;
    private String message, timeDate, feeling, picture, video, checkIn, postId;
    private RelativeLayout space_image;
    private ImageView play;
    private VideoView videoView;
    private TextView text_emoji;
    private boolean isMyClickLike;
    private ImageView ic_like;
    private TextView count_like;
    private TextView count_comment;
    private ValueEventListener listenerListLike;
    private ValueEventListener listenerComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_post_detail);
        ActivityManagerName managerName = new ActivityManagerName();
        managerName.setProcessNameClass(this);
        managerName.setNameClassInProcessString("MyPostDetail");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        isMyClickLike = false;

        Bundle bundle = getIntent().getExtras();
        mFacebookIDFriend = bundle.getString("mUserID");
        mPostMessage = bundle.getString("mPostMessage");
        myFacebookID= bundle.getString("MyFacebookID");
        myName= bundle.getString("MyName");
        myPicture= bundle.getString("MyPicture");

        String[] arrayMessage = mPostMessage.split("/&");
        String isMessageAndDevice = arrayMessage[0];
        timeDate = arrayMessage[1];
        feeling = arrayMessage[2];
        picture = arrayMessage[3];
        video = arrayMessage[4];
        checkIn = arrayMessage[5];
        postId = arrayMessage[6];

        String[] arrayMessage2 = isMessageAndDevice.split("\n");
        message = arrayMessage2[1];
        isDeviceNameFriend = arrayMessage2[0];

        InitializeMyPostDetail();
        LoadEmoji();
        callProfileFirebase();
        callListLikes();
        hideKeyBorad(this);

    }

    private void callListLikes() {
        //get my post
        mRootRef.child("Posts").child(mFacebookIDFriend).child(postId).child("Likes").addValueEventListener( listenerListLike = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0) {
                    mNameUserLike.clear();
                    mPictureUserLike.clear();
                    for (DataSnapshot objDataSnapshotPost : dataSnapshot.getChildren()) {
                        Map<String, Object> mapObjLike = (Map<String, Object>) objDataSnapshotPost.getValue();
                        mNameUserLike.add(mapObjLike.get("Name").toString());
                        mPictureUserLike.add(mapObjLike.get("Picture").toString());
                        if (objDataSnapshotPost.getKey().equals(myFacebookID)) {
                            isMyClickLike = true;
                        }
                    }

                    count_like.setText(String.valueOf(mNameUserLike.size()));

                    if(isMyClickLike){
                        ic_like.setImageResource(R.drawable.ic_heart_full);
                    }else{
                        ic_like.setImageResource(R.drawable.ic_heart);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void callProfileFirebase() {
        mRootRef.child("Users").child(mFacebookIDFriend).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0) {
                    Map<String, Object> mapObj = (Map<String, Object>) dataSnapshot.getValue();
                    assert mapObj != null;
                    if (mapObj.containsKey("Name")) {
                        mNameProfile = mapObj.get("Name").toString();
                        mImageProfile = mapObj.get("Picture").toString();
                        if(checkIn != null && !checkIn.equals("null")){
                            text_name_city.setText(checkIn);
                        }else{
                            getLocation();
                        }
                        setData();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getLocation() {
        mRootRef.child("Users").child(mFacebookIDFriend).child("Location").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> mapObj = (Map<String, Object>) objDataSnapshot.getValue();
                        assert mapObj != null;
                        final String DeviceNameFriend = mapObj.get("DeviceName").toString();
                        if(DeviceNameFriend.equals(isDeviceNameFriend)) {
                            final Double Lat = Double.parseDouble(mapObj.get("Latitude").toString());
                            final Double Lng = Double.parseDouble(mapObj.get("Longitude").toString());
                            //getAddress(Lat, Lng);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setData() {
//        if(mPostLink != null){
//            if(mPostLink.contains("https://www.youtube.com")){
//                id_youtube_video = mPostLink.replace("https://www.youtube.com/watch?v=", "");
//                String url = "https://img.youtube.com/vi/"+id_youtube_video+"/0.jpg";
//                image.setVisibility(View.GONE);
//                youTubeView.setVisibility(View.VISIBLE);
//                youTubeView.initialize(Config.getInstance().YOUTUBE_API_KEY, this);
//                playerStateChangeListener = new MyPlayerStateChangeListener();
//                playbackEventListener = new MyPlaybackEventListener();
//
//            }else {
//                youTubeView.setVisibility(View.GONE);
//                if (mPostPicture != null) {
//                    image.setVisibility(View.VISIBLE);
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(mPostPicture, 0, mPostPicture.length);
//                    image.setImageBitmap(bitmap);
//                } else {
//                    image.setVisibility(View.GONE);
//                }
//            }
//        }

        if(checkIn != null && !checkIn.equals("null")){
            text_name_city.setVisibility(View.VISIBLE);
            text_name_city.setText(checkIn);
        }else{
            text_name_city.setVisibility(View.GONE);
        }

        if(feeling != null && !feeling.equals("null")){
            text_emoji.setVisibility(View.VISIBLE);
            String image_emoji = checkImageEmoji(feeling);
            if(!image_emoji.equals("-1")){
                text_emoji.setText(image_emoji + " I'm felling " +feeling);
            }else{
                text_emoji.setText("I'm felling " + feeling);
            }
        }else{
            text_emoji.setVisibility(View.GONE);
        }

        if(message != null && !message.equals("null")) {
            text_caption.setVisibility(View.VISIBLE);
            text_caption.setText(message);
        }else{
            text_caption.setVisibility(View.GONE);
        }

        if(timeDate != null && !timeDate.equals("null")){
            text_name_date.setText(CalendarTime.getInstance().getTimeAgo(timeDate));
        }else{
            text_name_date.setVisibility(View.GONE);
        }

        if(picture != null && !picture.equals("null")){
            space_image.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(picture)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.bg_circle_white)
                    .error(R.drawable.bg_circle_white)
                    .into(image);

            if(video != null && !video.equals("null")){
                play.setVisibility(View.VISIBLE);
            }else{
                play.setVisibility(View.GONE);
            }
        }else{
            image.setVisibility(View.GONE);
            space_image.setVisibility(View.GONE);
        }

        if(mNameProfile != null) {
            text_name.setText(mNameProfile);
            Picasso.with(this)
                    .load(mImageProfile)
                    .centerCrop()
                    .fit()
                    .noFade()
                    .placeholder(R.drawable.bg_circle_white)
                    .error(R.drawable.ic_account_circle)
                    .transform(new CircleTransform())
                    .into(imageProfile);
        }
        name_user_comment.setText(myName);
        Picasso.with(this)
                .load(myPicture)
                .centerCrop()
                .fit()
                .noFade()
                .placeholder(R.drawable.bg_circle_white)
                .error(R.drawable.ic_account_circle)
                .transform(new CircleTransform())
                .into(image_comment);

        adapter_comment = new CustomAdapter(MyPostDetail.this);
        list_item_comment.setExpanded(true);
        list_item_comment.setAdapter(adapter_comment);
        LoadComment();
    }

    private void InitializeMyPostDetail() {
        imageProfile = (ImageView) findViewById(R.id.imageProfile);
        text_name = (TextView) findViewById(R.id.text_name);
        text_caption = (TextView) findViewById(R.id.text_caption);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        text_name_city = (TextView) findViewById(R.id.text_name_city);
        text_name_date = (TextView) findViewById(R.id.text_name_date);
        text_emoji = (TextView) findViewById(R.id.text_emoji);

        ic_like = (ImageView) findViewById(R.id.ic_like);
        count_like = (TextView) findViewById(R.id.count_like);
        count_comment = (TextView) findViewById(R.id.count_comment);

        space_image = (RelativeLayout) findViewById(R.id.space_image);
        image = (ImageView) findViewById(R.id.image);
        play = (ImageView) findViewById(R.id.play);
        videoView = (VideoView) findViewById(R.id.videoView);

        list_item_comment = (ExpandableHeightListView) findViewById(R.id.list_item_comment);

        image_comment = (ImageView) findViewById(R.id.image_comment);
        name_user_comment = (TextView) findViewById(R.id.name_user_comment);
        text_comment_comment = (TextView) findViewById(R.id.text_comment_comment);
        btnSend = (ImageView) findViewById(R.id.btnSend);
        rootView = (LinearLayout) findViewById(R.id.rootView);

        play.setOnClickListener(this);

        btnSend.setOnClickListener(this);
        text_comment_comment.setOnClickListener(this);
        rootView.setOnClickListener(this);
        image.setOnClickListener(this);
        btnBack.setOnClickListener(this);

    }

    private void LoadEmoji() {
        mRootRef.child("Emotions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0){
                    EmojiImage.clear();
                    EmojiName.clear();
                    for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> mapObj = (Map<String, Object>) objDataSnapshot.getValue();
                        try {
                            EmojiImage.add(mapObj.get("Image").toString());
                            EmojiName.add(mapObj.get("Name").toString());
                        }catch (Exception e){
                            Log.d(TAG, "error load emoji : " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    private String checkImageEmoji(String name) {
        String emoji = "-1";
        for(int i=0; i<EmojiName.size(); i++){
            if(EmojiName.get(i).contains(name)){
                emoji = EmojiImage.get(i);
            }
        }
        return emoji;
    }

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String mLocalName = city + "," + country;
            Log.d(TAG, "Address : " + mLocalName);
            //text_name_city.setText(mLocalName);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void LoadComment() {
        mRootRef.child("Posts").child(mFacebookIDFriend).child(postId).child("Comments").addValueEventListener(listenerComment = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0){
                    Log.d(TAG, "have comment");
                    mUserComment.clear();
                    mPhotoComment.clear();
                    mTextComment.clear();
                    mDateComment.clear();
                    Log.d(TAG, "Count LoadComment: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "objDataSnapshot LoadComment: " + objDataSnapshot.getValue());
                        Map<String, Object> mapObj = (Map<String, Object>) objDataSnapshot.getValue();
                        Log.d(TAG, "key LoadComment: " +objDataSnapshot.getKey());
                        try {
                            mUserComment.add(mapObj.get("Name").toString());
                            mPhotoComment.add(mapObj.get("Photo").toString());
                            mTextComment.add(mapObj.get("Text").toString());
                            mDateComment.add(mapObj.get("Date").toString());
                        }catch (Exception e){
                            Log.d(TAG, "error loadComment : " + e.getMessage());
                        }
                    }

                    count_comment.setText(String.valueOf(mUserComment.size()));
                    adapter_comment.notifyDataSetChanged();
                }
                else{
                    Log.d(TAG, "no comment");
                    adapter_comment.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    public class CustomAdapter extends BaseAdapter {

        public Context mContext;
        public LayoutInflater mLayoutInflater;
        public CustomAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mUserComment.size();
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

            ViewHolder holder = null;
            if(convertView == null){
                convertView =  mLayoutInflater.inflate(R.layout.listview_comment, null);
                holder = new ViewHolder();
                holder.name_user = (TextView) convertView.findViewById(R.id.name_user_comment);
                holder.text_comment = (TextView) convertView.findViewById(R.id.text_comment_comment);
                holder.imageView = (ImageView) convertView.findViewById(R.id.image_comment);
                holder.date_comment = (TextView) convertView.findViewById(R.id.date_user_comment);

                convertView.setTag(holder);

            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            try {
                holder.name_user.setText(mUserComment.get(position));
                if(!mTextComment.get(position).equals(" ") || !mTextComment.get(position).equals("")){
                    holder.text_comment.setVisibility(View.VISIBLE);
                    holder.text_comment.setText(mTextComment.get(position));
                }
                else{
                    holder.text_comment.setVisibility(View.GONE);
                }

                String pathImage = mPhotoComment.get(position);
                Log.d(TAG, "PathImage:" + pathImage);
                Picasso.with(mContext)
                        .load(pathImage)
                        .fit()
                        .centerCrop()
                        .error(R.color.white)
                        .placeholder(R.color.white)
                        .transform(new CircleTransform())
                        .into(holder.imageView);

                holder.date_comment.setText(CalendarTime.getInstance().getTimeAgo(mDateComment.get(position)));

            }catch (Exception e){
                Log.d(TAG, "*** error ****" + e.getMessage());
            }


            return convertView;
        }
    }

    private class ViewHolder{
        TextView name_user;
        TextView text_comment;
        TextView date_comment;
        ImageView imageView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRootRef.child("Posts").child(mFacebookIDFriend).child(postId).child("Likes").removeEventListener(listenerListLike);
        mRootRef.child("Posts").child(mFacebookIDFriend).child(postId).child("Comments").removeEventListener(listenerComment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onClick(View view) {
        if(view == btnBack) {
            finish();
        }else if(view == btnSend){
            String date = CalendarTime.getInstance().getCurrentDate();
            DatabaseReference mUsersRef = mRootRef.child("Posts").child(mFacebookIDFriend).child(postId).child("Comments").push();
            mUsersRef.child("Name").setValue(myName);
            mUsersRef.child("Photo").setValue(myPicture);
            mUsersRef.child("ID").setValue(myFacebookID);
            if(!text_comment_comment.getText().equals(" ") || !text_comment_comment.getText().equals("")) {
                mUsersRef.child("Text").setValue(text_comment_comment.getText().toString());
                mUsersRef.child("Date").setValue(date);
                text_comment_comment.setText("");
            }
        }else if(view == text_comment_comment){
            text_comment_comment.clearFocus();
            text_comment_comment.requestFocus();
            text_comment_comment.setCursorVisible(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(text_comment_comment, InputMethodManager.SHOW_IMPLICIT);
        }else if(view == rootView){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }else if(view == play){
            image.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.requestFocus();
            videoView.setVideoURI(Uri.parse(video));
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    videoView.start();
                }
            });
        }else if(view == image){
            Intent intent = new Intent(this, ShowFullImage.class);
            intent.putExtra("mPicture", picture);
            startActivity(intent);
        }
    }

    private void hideKeyBorad(Activity activity){
        if (activity != null) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

//    @Override
//    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
//        this.player = youTubePlayer;
//        player.setPlayerStateChangeListener(playerStateChangeListener);
//        player.setPlaybackEventListener(playbackEventListener);
//
//        if (!wasRestored) {
//            player.cueVideo(id_youtube_video);
//        }
//    }
//
//    @Override
//    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//    }

    private void showMessage(String message) {
        Log.d("MyPostDetail", "message : " + message);
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            showMessage("Playing");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            showMessage("Paused");
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    }
}

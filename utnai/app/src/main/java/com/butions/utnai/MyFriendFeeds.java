package com.butions.utnai;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyFriendFeeds extends AppCompatActivity implements View.OnClickListener, EmojiFirebase.EmojiCallbacks, MyProfileFriendsFirebase.ProfileFriendsCallbacks, MyPostsFirebase.PostsCallbacks {

    private String TAG = "MyFriendFeeds";
    private String mFriendFacebookID;
    private String mName, tempName;
    private String mPicture, tempPicture;
    private Context context;
    private ImageView imageProfile;
    private TextView text_name_city;
    private TextView text_link;
    private RecyclerView listViewFeed;
    private RecyclerViewAdapterMyFriendPost adapter;
    private DatabaseReference mRootRef;
    private ProgressBar image_loading;
    private TextView text_no_posts;

    private ArrayList<String> postId;
    private Map<Integer, String> postMessage;
    private Map<Integer, String> postCheckIn;
    private Map<Integer, String> postFeeling;
    private Map<Integer, String> postImage;
    private Map<Integer, String> postVideo;
    private Map<Integer, String> postTime;
    private Map<Integer, Integer> postCountLike;
    private Map<Integer, String>  postLikes;
    private Map<Integer, Integer> postComment;

    private static ArrayList<String> EmojiImage;
    private static ArrayList<String> EmojiName;
    private static ArrayList<String> EmojiKey;

    Map<String, String> myFriends;
    Map<String, String> myPostsFriends;

    private ImageView header_background;
    private ImageView btnBack;
    private String myFacebookID;
    private EmojiFirebase.EmojiListener emojiListener;
    private MyProfileFriendsFirebase.ProfileFriendsListener profileFriendsListener;
    private MyPostsFirebase.PostsListener postsListener;
    private TextView btnFollow;
    private TextView btnUnfollow;
    private String mOnMap;
    private ImageView btnUnBlock, btnBlock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_feed);

        context = getApplicationContext();
        ActivityManagerName managerName = new ActivityManagerName();
        managerName.setProcessNameClass(this);
        managerName.setNameClassInProcessString("MyFriendFeeds");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        myFacebookID = bundle.getString("MyFacebookID");
        mFriendFacebookID = bundle.getString("mFriendFacebookID");
        mName = bundle.getString("mFriendName");
        tempName = bundle.getString("mName");
        tempPicture = bundle.getString("mPicture");
        mOnMap = bundle.getString("mOnMap");

        //call emoji
        emojiListener = EmojiFirebase.addEmojiListener(this, context);

        //call profile friend
        profileFriendsListener = MyProfileFriendsFirebase.addProfileFriendsListener(mFriendFacebookID, this);

        //call posts
        postsListener = MyPostsFirebase.addPostsListener(mFriendFacebookID, this);

        InitializeMyFeed();

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
    public void onPostsChange(PostsValue postsValue) {
        if(postsValue != null) {
            addPostsArrayListData(postsValue);
            text_no_posts.setVisibility(View.GONE);
            image_loading.setVisibility(View.GONE);

            adapter.notifyDataSetChanged();
            listViewFeed.setAdapter(adapter);

        }else{
            text_no_posts.setVisibility(View.VISIBLE);
            image_loading.setVisibility(View.GONE);
        }

    }

    @Override
    public void onFriendsChange(ProfileFriendsValue profilefriendsValue) {
        if(profilefriendsValue != null) {
            myFriends = profilefriendsValue.getMyFriends();
            myPostsFriends = profilefriendsValue.getMyPostsFriends();
            addProfileData();
            getAddress(Double.parseDouble(myFriends.get("Latitude")), Double.parseDouble(myFriends.get("Longitude")));
        }
    }

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String mLocalName = city + "," + country;
            text_name_city.setText(mLocalName);
            Log.d(TAG, "Address : " + mLocalName);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addProfileData() {
        if(myFriends.containsKey("Picture")){
            mPicture = myFriends.get("Picture");
        }else{
            mPicture = tempPicture;
        }

        Picasso.with(context)
                .load(mPicture)
                .centerCrop()
                .fit()
                .noFade()
                .transform(new CircleTransform())
                .placeholder(R.drawable.bg_circle_white)
                .error(R.drawable.ic_account_circle)
                .into(imageProfile);

        if(myFriends.containsKey("Cover")){
            String mCover = myFriends.get("Cover");
            Picasso.with(context)
                    .load(mCover)
                    .centerCrop()
                    .fit()
                    .placeholder(R.color.white)
                    .error(R.color.white)
                    .into(header_background);
        }else{
            header_background.setBackgroundColor(Color.WHITE);
        }
    }

    private void addPostsArrayListData(PostsValue posts) {
        postCheckIn = posts.getCheckIn();
        postTime = posts.getTime();
        postId = posts.getId();
        postMessage = posts.getMessage();
        postFeeling = posts.getFeeling();
        postImage = posts.getImage();
        postLikes = posts.getLikes();
        postCountLike = posts.getCountLike();
        postVideo = posts.getVideo();
        postComment = posts.getComment();
    }

    private void InitializeMyFeed() {
        RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolbar);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        ImageView btnSetting = (ImageView) findViewById(R.id.btnSetting);
        ImageView btnEdit = (ImageView) findViewById(R.id.btnEdit);
        btnUnBlock = (ImageView) findViewById(R.id.btnUnBlock);
        btnBlock = (ImageView) findViewById(R.id.btnBlock);
        btnFollow = (TextView) findViewById(R.id.btnFollow);
        btnUnfollow = (TextView) findViewById(R.id.btnUnfollow);
        imageProfile = (ImageView) findViewById(R.id.imageProfile);
        ImageView edit_profile = (ImageView) findViewById(R.id.edit_profile);
        TextView text_name = (TextView) findViewById(R.id.text_name);
        text_name_city = (TextView) findViewById(R.id.text_name_city);
        text_link = (TextView) findViewById(R.id.text_link);
        text_link.setPaintFlags(text_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        text_link.setTextColor(getResources().getColor(R.color.sky));
        header_background = (ImageView) findViewById(R.id.header_background);
        image_loading = (ProgressBar) findViewById(R.id.image_loading);
        text_no_posts = (TextView) findViewById(R.id.text_no_posts);
        listViewFeed = (RecyclerView) findViewById(R.id.listViewFeed);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        listViewFeed.setLayoutManager(mLayoutManager);
        adapter = new RecyclerViewAdapterMyFriendPost();


        toolbar.setVisibility(View.VISIBLE);
        btnSetting.setVisibility(View.INVISIBLE);
        btnEdit.setVisibility(View.GONE);
        edit_profile.setVisibility(View.GONE);
        text_no_posts.setVisibility(View.INVISIBLE);
        image_loading.setVisibility(View.VISIBLE);
        if(mOnMap.equals("1")) {
            btnUnBlock.setVisibility(View.VISIBLE);
            btnBlock.setVisibility(View.GONE);
            btnFollow.setVisibility(View.VISIBLE);
            btnUnfollow.setVisibility(View.GONE);
        }else if(mOnMap.equals("0")) {
            btnUnBlock.setVisibility(View.VISIBLE);
            btnBlock.setVisibility(View.GONE);
            btnUnfollow.setVisibility(View.VISIBLE);
            btnFollow.setVisibility(View.GONE);
        }else if(mOnMap.equals("null")) {
            btnUnBlock.setVisibility(View.VISIBLE);
            btnBlock.setVisibility(View.GONE);
            btnUnfollow.setVisibility(View.VISIBLE);
            btnFollow.setVisibility(View.GONE);
        }else if(mOnMap.equals("2")){
            btnBlock.setVisibility(View.VISIBLE);
            btnUnBlock.setVisibility(View.GONE);
            btnUnfollow.setVisibility(View.GONE);
            btnFollow.setVisibility(View.GONE);
        }

        listViewFeed.setFocusable(false);
//        listViewFeed.setOnScrollListener(new AbsListView.OnScrollListener() {
//            private int mLastFirstVisibleItem;
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if(mLastFirstVisibleItem<firstVisibleItem) {
//                    Log.i("SCROLLING DOWN","TRUE");
//                    ViewHolder holder = new ViewHolder();
//                    if(holder.video != null) {
//                        holder.video.stopPlayback();
//                    }
//                }
//                if(mLastFirstVisibleItem>firstVisibleItem) {
//                    Log.i("SCROLLING UP","TRUE");
//                    ViewHolder holder = new ViewHolder();
//                    if(holder.video != null) {
//                        holder.video.stopPlayback();
//                    }
//                }
//                mLastFirstVisibleItem = firstVisibleItem;
//
//            }
//        });

        text_name.setText(mName);
        text_link.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        imageProfile.setOnClickListener(this);
        btnBlock.setOnClickListener(this);
        btnUnBlock.setOnClickListener(this);
        btnFollow.setOnClickListener(this);
        btnUnfollow.setOnClickListener(this);
    }


    private class RecyclerViewAdapterMyFriendPost extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_post_layout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int i) {
            Picasso.with(context)
                    .load(mPicture)
                    .centerCrop()
                    .fit()
                    .placeholder(R.drawable.bg_circle_white)
                    .error(R.drawable.bg_circle_white)
                    .transform(new CircleTransform())
                    .into(holder.imageProfile);

            holder.name.setText(mName);

            holder.date.setText(CalendarTime.getInstance().getTimeAgo(postTime.get(i)));

            if(postMessage != null && postMessage.get(i) != null) {
                holder.text.setVisibility(View.VISIBLE);
                holder.text.setText(postMessage.get(i));
            }else{
                holder.text.setVisibility(View.GONE);
            }

            if(postFeeling != null && postFeeling.get(i) != null){
                holder.text_emoji.setVisibility(View.VISIBLE);
                int index_emoji = checkImageEmoji(postFeeling.get(i));
                if (index_emoji != -1) {
                    holder.image_emoji.setVisibility(View.VISIBLE);
                    Picasso.with(context)
                            .load(EmojiImage.get(index_emoji))
                            .centerCrop()
                            .fit()
                            .noFade()
                            .placeholder(R.color.white)
                            .error(R.drawable.ic_emotion_mini)
                            .into(holder.image_emoji);
                    holder.text_emoji.setText(String.valueOf(StringManager.getsInstance().getString("IFeeling") + " " + EmojiName.get(index_emoji)));
                } else {
                    holder.image_emoji.setVisibility(View.GONE);
                    holder.text_emoji.setText(String.valueOf(StringManager.getsInstance().getString("IFeeling") + " " + postFeeling.get(i)));
                }
            }else{
                holder.image_emoji.setVisibility(View.GONE);
                holder.text_emoji.setVisibility(View.GONE);
            }

            if(postImage != null && postImage.get(i) != null){
                holder.space_image.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(postImage.get(i))
                        .fit()
                        .centerInside()
                        .placeholder(R.color.white)
                        .error(R.color.white)
                        .into(holder.image);
            }else{
                holder.space_image.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.GONE);
            }

            if(postCheckIn != null && postCheckIn.get(i) != null) {
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText(postCheckIn.get(i));
            }else{
                holder.location.setVisibility(View.GONE);
            }

            if(postVideo != null && postVideo.get(i) != null){
                holder.iconPlay.setVisibility(View.VISIBLE);
            }else{
                holder.iconPlay.setVisibility(View.GONE);
                holder.video.setVisibility(View.GONE);
            }

            holder.icon_delect.setVisibility(View.GONE);

            final ViewHolder finalHolder = holder;
            holder.iconPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalHolder.iconPlay.setVisibility(View.GONE);
                    finalHolder.image.setVisibility(View.GONE);
                    finalHolder.video.setVisibility(View.VISIBLE);
                    finalHolder.video.setVideoPath(postVideo.get(i));
                    MediaController mediaController = new MediaController(context);
                    mediaController.setAnchorView(finalHolder.video);
                    finalHolder.video.setMediaController(mediaController);
                    finalHolder.video.start();
                }
            });

            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String deviceName = Config.getInstance().getDeviceName();
                    String isPostMessage = deviceName + "\n" + postMessage.get(i);
                    String message = isPostMessage + "/&" + postTime.get(i) + "/&" + postFeeling.get(i) + "/&" + postImage.get(i) + "/&" + postVideo.get(i) + "/&" + postCheckIn.get(i) + "/&" + postId.get(i);
                    Intent intent = new Intent(MyFriendFeeds.this, MyPostDetail.class);
                    intent.putExtra("mUserID", mFriendFacebookID);
                    intent.putExtra("mPostMessage", message);
                    intent.putExtra("MyFacebookID", myFacebookID);
                    intent.putExtra("MyName", tempName);
                    intent.putExtra("MyPicture", tempPicture);
                    startActivity(intent);
                }
            });

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ShowFullImage.class);
                    intent.putExtra("mPicture", postImage.get(i));
                    startActivity(intent);
                }
            });

            if(postCountLike != null && postCountLike.get(i) != null){
                if(postCountLike.get(i) > 0){
                    holder.count_like.setText(String.valueOf(postCountLike.get(i)));
                }else{
                    holder.count_like.setText("");
                }
            }else{
                holder.count_like.setText("");
            }


            if(postLikes != null && postLikes.get(i) != null){
                holder.like.setImageResource(R.drawable.ic_heart_full);
            }else{
                holder.like.setImageResource(R.drawable.ic_heart);
            }

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(postLikes.get(i) != null){
                        DatabaseReference mUsersRef = mRootRef.child("Posts").child(mFriendFacebookID).child(postId.get(i)).child("Likes");
                        mUsersRef.child(myFacebookID).setValue(null);
                    }else {
                        DatabaseReference mUsersRef = mRootRef.child("Posts").child(mFriendFacebookID).child(postId.get(i)).child("Likes").child(myFacebookID);
                        mUsersRef.child("ID").setValue(myFacebookID);
                        mUsersRef.child("Name").setValue(mName);
                        mUsersRef.child("Picture").setValue(mPicture);
                    }
                }
            });

            if(postComment.get(i) != null  && postComment.get(i) != null){
                if(postComment.get(i) > 0){
                    holder.count_comment.setText(String.valueOf(postComment.get(i)));
                }else{
                    holder.count_comment.setText("");
                }
            }else{
                holder.count_comment.setText("");
            }
        }

        @Override
        public int getItemCount() {
            return postId.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView text;
        TextView location;
        TextView text_emoji;
        ImageView image_emoji;
        ImageView icon_delect;
        ImageView iconPlay;
        VideoView video;
        ImageView imageProfile;
        TextView name;
        TextView date;
        ImageView comment;
        RelativeLayout space_image;
        ImageView like;
        TextView count_like;
        TextView count_comment;

        public ViewHolder(View itemView) {
            super(itemView);
            imageProfile = (ImageView) itemView.findViewById(R.id.imageProfile);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            image = (ImageView) itemView.findViewById(R.id.image);
            space_image = (RelativeLayout) itemView.findViewById(R.id.space_image);
            text = (TextView) itemView.findViewById(R.id.text);
            location = (TextView) itemView.findViewById(R.id.location);
            icon_delect = (ImageView) itemView.findViewById(R.id.icon_delect);
            text_emoji = (TextView) itemView.findViewById(R.id.text_emoji);
            image_emoji = (ImageView) itemView.findViewById(R.id.image_emoji);
            iconPlay = (ImageView) itemView.findViewById(R.id.iconPlay);
            video = (VideoView) itemView.findViewById(R.id.video);
            comment = (ImageView) itemView.findViewById(R.id.ic_comment);
            like = (ImageView) itemView.findViewById(R.id.ic_like);
            count_like = (TextView) itemView.findViewById(R.id.count_like);
            count_comment = (TextView) itemView.findViewById(R.id.count_comment);
        }
    }

    private int checkImageEmoji(String key) {
        int emojiIndex = -1;
        if(key != null && EmojiKey != null) {
            for (int i = 0; i < EmojiKey.size(); i++) {
                if (EmojiKey.get(i).toLowerCase().contains(key.toLowerCase())) {
                    emojiIndex = i;
                }
            }
        }
        return emojiIndex;
    }

    @Override
    public void onClick(View view) {
        if(view == btnBack){
            finish();
        }else if(view == imageProfile){
            Intent intent = new Intent(this, ShowFullImage.class);
            intent.putExtra("mPicture", mPicture);
            startActivity(intent);
        }else if(view == btnFollow){
            btnUnfollow.setVisibility(View.VISIBLE);
            btnFollow.setVisibility(View.GONE);
            DatabaseReference mFriendRef = mRootRef.child("Users").child(myFacebookID).child("Friends").child(mFriendFacebookID);
            mFriendRef.child("OnMap").setValue("0");
        }else if(view == btnUnfollow){
            btnFollow.setVisibility(View.VISIBLE);
            btnUnfollow.setVisibility(View.GONE);
            DatabaseReference mFriendRef = mRootRef.child("Users").child(myFacebookID).child("Friends").child(mFriendFacebookID);
            mFriendRef.child("OnMap").setValue("1");
        }else if(view == btnUnBlock){  //need block
            dialogNeedBlock();

        }else if(view == btnBlock){  //need unblock
            dialogNeedUnBlock();
        }else if(view == text_link){
            text_link.setTextColor(getResources().getColor(R.color.grey));
            String mLink = text_link.getText().toString();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(mLink));
            startActivity(i);
        }
    }

    private void dialogNeedUnBlock() {
        final Dialog dialog_unblock = new Dialog(this);
        dialog_unblock.setCanceledOnTouchOutside(false);
        dialog_unblock.getWindow();
        dialog_unblock.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_unblock.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog_unblock.getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        dialog_unblock.setContentView(R.layout.dialog_block);

        ImageView dialog_image = (ImageView) dialog_unblock.findViewById(R.id.dialog_image);
        ImageView dialog_title = (ImageView) dialog_unblock.findViewById(R.id.dialog_title);
        TextView dialog_detail = (TextView) dialog_unblock.findViewById(R.id.dialog_detail);
        TextView btnOk = (TextView) dialog_unblock.findViewById(R.id.btnOk);
        TextView btnCancle = (TextView) dialog_unblock.findViewById(R.id.btnCancel);

        Picasso.with(context)
                .load(mPicture)
                .centerCrop()
                .fit()
                .noFade()
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(dialog_image);

        dialog_title.setImageResource(R.drawable.ic_unblock_grey);
        dialog_detail.setTypeface(Config.getInstance().getDefaultFont(context));
        dialog_detail.setText(StringManager.getsInstance().getString("Unblock").replace("XXXXX", mName));
        btnOk.setTypeface(Config.getInstance().getDefaultFont(context));
        btnOk.setText(StringManager.getsInstance().getString("Yes"));
        btnCancle.setTypeface(Config.getInstance().getDefaultFont(context));
        btnCancle.setText(StringManager.getsInstance().getString("No"));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Need UnBlock
                btnBlock.setVisibility(View.GONE);
                btnUnBlock.setVisibility(View.VISIBLE);
                btnFollow.setVisibility(View.VISIBLE);
                btnUnfollow.setVisibility(View.GONE);

                // me block friend
                DatabaseReference mMeRef = mRootRef.child("Users").child(myFacebookID).child("Friends").child(mFriendFacebookID);
                mMeRef.child("OnMap").setValue("1");

                // friend block me
                DatabaseReference mFriendRef = mRootRef.child("Users").child(mFriendFacebookID).child("Friends").child(myFacebookID);
                mFriendRef.child("OnMap").setValue("1");

                dialog_unblock.cancel();
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_unblock.cancel();
            }
        });

        dialog_unblock.show();
    }

    private void dialogNeedBlock() {
        final Dialog dialog_block = new Dialog(this);
        dialog_block.setCanceledOnTouchOutside(false);
        dialog_block.getWindow();
        dialog_block.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_block.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog_block.getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        dialog_block.setContentView(R.layout.dialog_block);

        ImageView dialog_image = (ImageView) dialog_block.findViewById(R.id.dialog_image);
        ImageView dialog_title = (ImageView) dialog_block.findViewById(R.id.dialog_title);
        TextView dialog_detail = (TextView) dialog_block.findViewById(R.id.dialog_detail);
        TextView btnOk = (TextView) dialog_block.findViewById(R.id.btnOk);
        TextView btnCancle = (TextView) dialog_block.findViewById(R.id.btnCancel);

        Picasso.with(context)
                .load(mPicture)
                .centerCrop()
                .fit()
                .noFade()
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(dialog_image);

        dialog_title.setImageResource(R.drawable.ic_block_grey);
        dialog_detail.setTypeface(Config.getInstance().getDefaultFont(context));
        dialog_detail.setText(StringManager.getsInstance().getString("Block").replace("XXXXX", mName));
        btnOk.setTypeface(Config.getInstance().getDefaultFont(context));
        btnOk.setText(StringManager.getsInstance().getString("Yes"));
        btnCancle.setTypeface(Config.getInstance().getDefaultFont(context));
        btnCancle.setText(StringManager.getsInstance().getString("No"));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Need Block
                btnBlock.setVisibility(View.VISIBLE);
                btnUnBlock.setVisibility(View.GONE);
                btnFollow.setVisibility(View.GONE);
                btnUnfollow.setVisibility(View.GONE);

                // me block friend
                DatabaseReference mMeRef = mRootRef.child("Users").child(myFacebookID).child("Friends").child(mFriendFacebookID);
                mMeRef.child("OnMap").setValue("2");

                // friend block me
                DatabaseReference mFriendRef = mRootRef.child("Users").child(mFriendFacebookID).child("Friends").child(myFacebookID);
                mFriendRef.child("OnMap").setValue("3");

                dialog_block.cancel();
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_block.cancel();
            }
        });


        dialog_block.show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        text_link.setTextColor(getResources().getColor(R.color.sky));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "***OnStop***");
        MyPostsFirebase.stop(postsListener);
        MyProfileFriendsFirebase.stop(profileFriendsListener);
    }
}

package com.butions.utnai;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
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
import java.util.Map;

public class MyFeed extends Fragment implements View.OnClickListener, MyPostsFirebase.PostsCallbacks, EmojiFirebase.EmojiCallbacks, MyProfileFirebase.ProfileCallbacks {

    private String TAG = "MyFeed";
    private String mUserID;
    private String mName;
    private String mPicture, tempPicture;
    private View v;
    private ImageView btnSetting;
    private Context context;
    private String myOnMap;
    private ImageView imageProfile;
    private String mLocalName;
    private TextView text_name_city;
    private TextView text_link;
    private RecyclerView listViewFeed;
    private RecyclerViewAdapterMyPost adapter;
    private DatabaseReference mRootRef;
    private MyPostsFirebase.PostsListener postsListener;
    private EmojiFirebase.EmojiListener emojiListener;
    private MyProfileFirebase.ProfileListener profileListener;
    private ImageView btnEdit;
    private ImageView header_background;
    private int w, h;
    private ImageAdapter adapter_gallery;
    private ArrayList<String> images;
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
    private Map<String, String > myProfile;
    private static ArrayList<String> EmojiImage;
    private static ArrayList<String> EmojiName;
    private static ArrayList<String> EmojiKey;
    private TextView btnFollow;
    private TextView btnUnfollow;
    private ImageView btnBlock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "*** onCreateView ***");

        v = inflater.inflate(R.layout.my_feed, container, false);
        context = getActivity();

        Translater.getInstance().setLanguages(context);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUserID = getArguments().getString("mUserID");
        mName = getArguments().getString("MyName");
        tempPicture = getArguments().getString("MyPicture");
        myOnMap = getArguments().getString("MyOnMap");
        double mLat = getArguments().getDouble("MyLatitude");
        double mLng = getArguments().getDouble("MyLongitude");

        //call emoji
        emojiListener = EmojiFirebase.addEmojiListener(this, context);

        //call profile
        profileListener = MyProfileFirebase.addProfileListener(mUserID, this, context);

        //call posts
        postsListener = MyPostsFirebase.addPostsListener(mUserID, this);

        InitializeMyFeed();
        getAddress(mLat, mLng);

        return v;
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
    public void onProfileChange(ProfileValue profile) {
        myProfile = profile.getMyProfile();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addProfileData();
            }
        });
    }

    @Override
    public void onPostsChange(PostsValue postsValue) {
        if(postsValue != null) {
            addPostsArrayListData(postsValue);
            text_no_posts.setVisibility(View.GONE);
            image_loading.setVisibility(View.GONE);
            listViewFeed.setVisibility(View.VISIBLE);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            listViewFeed.setLayoutManager(mLayoutManager);
            listViewFeed.setAdapter(adapter);

        }else{
            listViewFeed.setVisibility(View.GONE);
            text_no_posts.setVisibility(View.VISIBLE);
            image_loading.setVisibility(View.GONE);
        }
    }

    private void addProfileData() {
        if(myProfile.containsKey("Picture")){
            mPicture = myProfile.get("Picture");
        }else{
            mPicture = tempPicture;
        }

        if(myProfile.containsKey("Link")){
            String mLink = myProfile.get("Link");
            if(mLink.contains("facebook.com")){
                text_link.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.com_facebook_button_icon_blue,0,0,0);
            }else if (mLink.contains("twitter.com")){
                text_link.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_twitter_logo,0,0,0);
            }else if(mLink.contains("google.com")){
                text_link.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.googleg_standard_color_18,0,0,0);
            }
            text_link.setText(mLink);
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

        if(myProfile.containsKey("Cover")){
            String mCover = myProfile.get("Cover");
            Picasso.with(context)
                    .load(mCover)
                    .centerCrop()
                    .fit()
                    .noFade()
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
        RelativeLayout toolbar = (RelativeLayout) v.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        //set Margin bottom 40 dp
        int px = (int) (80 * Config.getInstance().getDisplayDensity(context));
        LinearLayout layoutRootView = (LinearLayout) v.findViewById(R.id.layoutRootView);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutRootView.getLayoutParams();
        params.setMargins(0,0,0,px);
        layoutRootView.setLayoutParams(params);

        btnSetting = (ImageView) v.findViewById(R.id.btnSetting);
        btnEdit = (ImageView) v.findViewById(R.id.btnEdit);
        btnFollow = (TextView) v.findViewById(R.id.btnFollow);
        btnUnfollow = (TextView) v.findViewById(R.id.btnUnfollow);
        btnBlock = (ImageView) v.findViewById(R.id.btnBlock);
        imageProfile = (ImageView) v.findViewById(R.id.imageProfile);
        TextView text_name = (TextView) v.findViewById(R.id.text_name);
        text_name_city = (TextView) v.findViewById(R.id.text_name_city);
        text_link = (TextView) v.findViewById(R.id.text_link);
        text_link.setPaintFlags(text_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        text_link.setTextColor(getResources().getColor(R.color.sky));
        header_background = (ImageView) v.findViewById(R.id.header_background);
        image_loading = (ProgressBar) v.findViewById(R.id.image_loading);
        text_no_posts = (TextView) v.findViewById(R.id.text_no_posts);
        text_no_posts.setVisibility(View.INVISIBLE);
        image_loading.setVisibility(View.VISIBLE);
        btnFollow.setVisibility(View.GONE);
        btnUnfollow.setVisibility(View.GONE);
        btnBlock.setVisibility(View.GONE);
        listViewFeed = (RecyclerView) v.findViewById(R.id.listViewFeed);
        adapter = new RecyclerViewAdapterMyPost();
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
        btnEdit.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        imageProfile.setOnClickListener(this);
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
            text_name_city.setText(mLocalName);
            Log.d(TAG, "Address : " + mLocalName);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class RecyclerViewAdapterMyPost extends RecyclerView.Adapter<ViewHolder>{

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
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.bg_circle_white)
                    .error(R.drawable.bg_circle_white)
                    .into(holder.imageProfile);

            holder.name.setText(mName);

            holder.date.setText(CalendarTime.getInstance().getTimeAgo(postTime.get(i)));

            if(postMessage != null && postMessage.get(i) != null) {
                holder.text.setVisibility(View.VISIBLE);
                holder.text.setText(postMessage.get(i));
            }else{
                holder.text.setVisibility(View.GONE);
            }

            if(postFeeling != null && postFeeling.get(i) != null && EmojiKey != null){
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
                    if(EmojiName != null)
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
                Picasso.with(getContext())
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

            holder.icon_delect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog_delecte = new Dialog(context);
                    dialog_delecte.setCanceledOnTouchOutside(false);
                    dialog_delecte.getWindow();
                    dialog_delecte.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_delecte.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog_delecte.getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
                    dialog_delecte.setContentView(R.layout.dialog_delete);

                    TextView dialog_detail = (TextView) dialog_delecte.findViewById(R.id.dialog_detail);
                    TextView btnOk = (TextView) dialog_delecte.findViewById(R.id.btnOk);
                    TextView btnCancle = (TextView) dialog_delecte.findViewById(R.id.btnCancel);

                    dialog_detail.setTypeface(Config.getInstance().getDefaultFont(context));
                    dialog_detail.setText(StringManager.getsInstance().getString("Delete"));
                    btnOk.setTypeface(Config.getInstance().getDefaultFont(context));
                    btnOk.setText(StringManager.getsInstance().getString("Yes"));
                    btnCancle.setTypeface(Config.getInstance().getDefaultFont(context));
                    btnCancle.setText(StringManager.getsInstance().getString("No"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Delete Posts
                            DatabaseReference mPostRef = mRootRef.child("Posts").child(mUserID).child(postId.get(i));
                            mPostRef.setValue(null);
                            dialog_delecte.cancel();
                        }
                    });

                    btnCancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog_delecte.cancel();
                        }
                    });

                    dialog_delecte.show();
                }
            });

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
                    Intent intent = new Intent(getActivity(), MyPostDetail.class);
                    intent.putExtra("mUserID", mUserID);
                    intent.putExtra("mPostMessage", message);
                    intent.putExtra("MyFacebookID", mUserID);
                    intent.putExtra("MyName", mName);
                    intent.putExtra("MyPicture", mPicture);
                    startActivity(intent);
                }
            });

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ShowFullImage.class);
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


            if(postLikes.get(i) != null && postLikes.get(i) != null){
                holder.like.setImageResource(R.drawable.ic_heart_full);
            }else{
                holder.like.setImageResource(R.drawable.ic_heart);
            }

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(postLikes.get(i) != null){
                        DatabaseReference mUsersRef = mRootRef.child("Posts").child(mUserID).child(postId.get(i)).child("Likes");
                        mUsersRef.child(mUserID).setValue(null);
                    }else {
                        DatabaseReference mUsersRef = mRootRef.child("Posts").child(mUserID).child(postId.get(i)).child("Likes").child(mUserID);
                        mUsersRef.child("ID").setValue(mUserID);
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
        if(view == btnSetting){
            btnSetting.setImageResource(R.drawable.ic_settings_black_full);
            Intent intent = new Intent(context, MySetting.class);
            intent.putExtra("mUserID", mUserID);
            intent.putExtra("isMyOnMap", myOnMap);
            intent.putExtra("mDeviceID", myProfile.get("DeviceID"));
            startActivity(intent);
        }else if(view == imageProfile){
            //upload profile
            dialogSelectImage("Profiles");
        }else if(view == btnEdit){
            dialogSelectImage("Covers");
        }else if(view == text_link){
            text_link.setTextColor(getResources().getColor(R.color.grey));
            String mLink = text_link.getText().toString();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(mLink));
            startActivity(i);
        }
    }

    private void dialogSelectImage(final String select){
        final Dialog dialog_select = new Dialog(context);
        dialog_select.setCanceledOnTouchOutside(false);
        dialog_select.getWindow();
        dialog_select.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_select.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog_select.getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        dialog_select.setContentView(R.layout.dialog_select_image);
        dialog_select.getWindow().setLayout((int) (getScreenWidth(getActivity())*0.96), (int)(getScreenHeight(getActivity())*0.96));

        ExpandableHeightGridView grid_gallery = (ExpandableHeightGridView) dialog_select.findViewById(R.id.gallery_gridView);
        grid_gallery.setExpanded(true);
        grid_gallery.setAdapter(adapter_gallery);
        grid_gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog_select.cancel();
                String cover_path = Uri.parse(images.get(i)).toString();
                uploadImageFromFile(cover_path, select);
                //header_background.setImageURI(Uri.parse(images.get(i)));
            }
        });

        ImageView btnClose = (ImageView) dialog_select.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_select.cancel();
            }
        });


        dialog_select.show();
    }

    private void uploadImageFromFile(String path , final String isChild) {
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("country", mLocalName)
                .build();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference folderRef = storageRef.child(isChild).child(mUserID);
        //Helper.showDialog(this);
        Uri file = Uri.fromFile(new File(path));
        StorageReference imageRef = folderRef.child(file.getLastPathSegment());


        final UploadTask mUploadTask = imageRef.putFile(file , metadata);

        Helper.initProgressDialog(context);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Helper.dismissProgressDialog();
                Log.d(TAG, String.format("Failure: %s", exception.getMessage()));
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Helper.dismissProgressDialog();
                Toast.makeText(context,"Upload Image Success", Toast.LENGTH_SHORT).show();

                //add data to database
                DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID);
                if(isChild.equals("Covers")) {
                    mUsersRef.child("Cover").setValue(taskSnapshot.getDownloadUrl().toString());
                }else{
                    mUsersRef.child("Picture").setValue(taskSnapshot.getDownloadUrl().toString());
                }

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

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.y;
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
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

    private class ImageAdapter extends BaseAdapter {

        private  LayoutInflater mLayoutInflater;
        private Activity context;
        public ImageAdapter(Activity localContext) {
            context = localContext;
            mLayoutInflater = LayoutInflater.from(context);
            images = getAllShownImagesPath((Activity) context);
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

            ViewHolderGridView holder = null;
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
                    .centerCrop()
                    .into(holder.picturesView);

            return convertView;
        }
    }
    private class ViewHolderGridView{
        ImageView picturesView;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter_gallery = new ImageAdapter((Activity) context);
        float m =  Config.getInstance().getDisplayDensity(context);
        w = (int) (210 * m);
        h = (int) (210 * m);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "*** onResume ***");
        Translater.getInstance().setLanguages(context);

        btnSetting.setImageResource(R.drawable.ic_settings_black);
        btnSetting.setBackgroundResource(R.color.transparent);
        text_link.setTextColor(getResources().getColor(R.color.sky));
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "***OnStop***");
        MyPostsFirebase.stop(postsListener);
        MyProfileFirebase.stop(profileListener);
    }
}

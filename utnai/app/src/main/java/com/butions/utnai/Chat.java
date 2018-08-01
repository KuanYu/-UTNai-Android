package com.butions.utnai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

//import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
//import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by Chalitta Khampachua on 08-Nov-16.
 */
public class Chat extends AppCompatActivity implements View.OnClickListener , MessageFirebase.MessagesCallbacks, MyProfileFriendsFirebase.ProfileFriendsCallbacks, EmojiFirebase.EmojiCallbacks {

    private String TAG = "MessageChat";
    private ImageView btnBack;
    private EditText inputText;
    private ImageView btnSend;
    private ArrayList<Message> mMessages;
    private MessagesAdapter mAdapter;
    private ImageView btnAdd;
    private MessageFirebase.MessagesListener mListener;
    private ImageView btnCancel;
    private RelativeLayout space_photo;
    private GridView gallery;
    private ArrayList<String> images;
    private RelativeLayout.LayoutParams params;
    private ImageView imageSelect;
    private ImageView iconUploadImage;
    private ImageView btnBackCamera;
    private String mPathImageSelect;
    private Context mContext;
    private String newMessage;
    private int padding15, padding10, padding35, padding43;
    private LinearLayout footerSelect;
    private String friendFacebookId;
    private String myUserID;
    private String myPicture;
    private DatabaseReference mRootRef;
    private TextView textToolbar;
    private String friendPicture;
    private ValueEventListener mReadValueEventListener;
    private LinearLayout layout_camera, layout_video, layout_image, layout_emotion, layout_location;
    private TextView icon_camera, icon_video, icon_image, icon_emotion, icon_location;
    private ImageView icon_emotion_image;
    private ImageView iconPlay;
    private String mPathVideoSelect;
    private String URL_IMAGE_VIDEO;
    private boolean status_footer_tab;
    View rootView;
//    private EmojIconActions emojIcon;
    private String friendDeviceId;
    private String friendToken;
    private String friendName;
    private String friendChatKey;
    private String myDeviceID;
    private ActivityManagerName managerName;
    private MyProfileFriendsFirebase.ProfileFriendsListener profileFriendsListener;
    private String myCode;
    private GridView emoji;
    private EmojiFirebase.EmojiListener emojiListener;
    private static ArrayList<String> EmojiImage;
    private static ArrayList<String> EmojiName;
    private static ArrayList<String> EmojiKey;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messager_chat);
        managerName = new ActivityManagerName();
        managerName.setProcessNameClass(this);
        managerName.setNameClassInProcessString("Chat");

        Translater.getInstance().setLanguages(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mContext = getApplicationContext();

        //call emoji
        emojiListener = EmojiFirebase.addEmojiListener(this, this);

        //initialize
        status_footer_tab = true;
        rootView = findViewById(R.id.root_view);
        textToolbar = (TextView) findViewById(R.id.textToolbar);
        textToolbar.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        inputText = (EditText) findViewById(R.id.inputText);
        inputText.setTypeface(Config.getInstance().getDefaultFont(this));
        btnSend = (ImageView) findViewById(R.id.btnSend);
        btnAdd = (ImageView) findViewById(R.id.btnAdd);
        btnCancel = (ImageView) findViewById(R.id.btnCancel);
        imageSelect = (ImageView) findViewById(R.id.imageSelect);
        iconPlay = (ImageView) findViewById(R.id.iconPlay);
        iconUploadImage = (ImageView) findViewById(R.id.iconUploadImage);
        space_photo = (RelativeLayout) findViewById(R.id.space_photo);
        btnBackCamera = (ImageView) findViewById(R.id.btnBackCamera);
        iconPlay.setVisibility(View.GONE);
        imageSelect.setVisibility(View.INVISIBLE);
        iconUploadImage.setVisibility(View.INVISIBLE);
        btnBackCamera.setVisibility(View.INVISIBLE);

        footerSelect = (LinearLayout) findViewById(R.id.footerSelect);
        footerSelect.setVisibility(View.GONE);
        layout_camera = (LinearLayout) findViewById(R.id.layout_camera);
        layout_video = (LinearLayout) findViewById(R.id.layout_video);
        layout_image = (LinearLayout) findViewById(R.id.layout_image);
        layout_emotion = (LinearLayout) findViewById(R.id.layout_emotion);
        layout_location = (LinearLayout) findViewById(R.id.layout_location);

        icon_camera = (TextView) findViewById(R.id.icon_camera);
        icon_video = (TextView) findViewById(R.id.icon_video);
        icon_image = (TextView) findViewById(R.id.icon_image);
        icon_emotion = (TextView) findViewById(R.id.icon_emotion);
        icon_location = (TextView) findViewById(R.id.icon_location);
        icon_emotion_image = (ImageView) findViewById(R.id.icon_emotion_image);

        setFonts();

        params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
        params.height = 0;
        space_photo.setLayoutParams(params);
        inputText.setCursorVisible(false);
        hideKeyboardAtfrist(this);
        gallery = (GridView) findViewById(R.id.galleryGridView);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != images && !images.isEmpty()) {
                    setPathImageSelect(images.get(position));
                    imageSelect.setVisibility(View.VISIBLE);
                    iconPlay.setVisibility(View.GONE);
                    iconUploadImage.setVisibility(View.VISIBLE);
                    btnBackCamera.setVisibility(View.VISIBLE);
                    Glide.with(Chat.this)
                            .load(images.get(position))
                            .centerCrop()
                            .placeholder(R.color.black)
                            .into(imageSelect);
                }

            }
        });

        emoji = (GridView) findViewById(R.id.emojiGridView);
        emoji.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(EmojiImage.get(i) != null) {
                    String text = StringManager.getsInstance().getString("SendEmoji");
                    Message msg = new Message();
                    msg.setText(EmojiImage.get(i));
                    msg.setSender(myUserID);
                    msg.setType("Emoji");
                    msg.setRead("false");

                    if(friendToken != null) {
                        String time = CalendarTime.getInstance().getCurrentTimeText();
                        String data = friendToken + "/&" + friendName + "/&" + friendPicture + "/&" + friendFacebookId + "/&" + friendDeviceId
                                + "/&" + text + "/&" + "text" + "/&" + time + "/&" + myUserID + "/&" + myDeviceID;
                        new SendPushNotification().execute(data);
                    }

                    MessageFirebase.saveMessage(msg);
                }
            }
        });


        //onClick
        btnSend.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        iconPlay.setOnClickListener(this);
        iconUploadImage.setOnClickListener(this);
        btnBackCamera.setOnClickListener(this);
        inputText.setOnClickListener(this);

        layout_camera.setOnClickListener(this);
        layout_video.setOnClickListener(this);
        layout_image.setOnClickListener(this);
        layout_emotion.setOnClickListener(this);
        layout_location.setOnClickListener(this);

        final ListView listViewChat = (ListView) findViewById(R.id.listViewChat);
        mMessages = new ArrayList<Message>();
        mAdapter = new MessagesAdapter(mMessages);
        listViewChat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listViewChat.setAdapter(mAdapter);
        listViewChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard(Chat.this);
            }
        });
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listViewChat.setSelection(mAdapter.getCount()-1);
            }
        });
        mAdapter.notifyDataSetChanged();

        Bundle bundle = getIntent().getExtras();
        myCode = bundle.getString("MyCode");
        myUserID = bundle.getString("MyUserID");
        myPicture = bundle.getString("MyPicture");
        String data = bundle.getString("isUserID");
        String[] mSnippet = data.split("/&");
        friendFacebookId = mSnippet[0];
        friendDeviceId = mSnippet[1];
        myDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        managerName.setChatRoomName(friendFacebookId);
        Log.d(TAG, "friendFacebookId : " + friendFacebookId);
        Log.d(TAG, "myUserID : " + myUserID);

        //get profile friend
        profileFriendsListener = MyProfileFriendsFirebase.addProfileFriendsListener(friendFacebookId, this);

//        setEmotion();
        new LoadGallery().execute();
    }

    private void setFonts() {
        icon_camera.setTypeface(Config.getInstance().getDefaultFont(this));
        icon_video.setTypeface(Config.getInstance().getDefaultFont(this));
        icon_image.setTypeface(Config.getInstance().getDefaultFont(this));
        icon_emotion.setTypeface(Config.getInstance().getDefaultFont(this));
        icon_location.setTypeface(Config.getInstance().getDefaultFont(this));

        icon_camera.setText(StringManager.getsInstance().getString("Camera"));
        icon_image.setText(StringManager.getsInstance().getString("Photo"));
        icon_video.setText(StringManager.getsInstance().getString("Video"));
        icon_emotion.setText(StringManager.getsInstance().getString("Feeling"));
        icon_location.setText(StringManager.getsInstance().getString("Location"));
    }

    @Override
    public void onFriendsChange(ProfileFriendsValue profilefriendsValue) {
        MyProfileFriendsFirebase.stop(profileFriendsListener);
        Map<String, String> myFriends = profilefriendsValue.getMyFriends();
        Map<String, String> myChatsFriends = profilefriendsValue.getMyChatsFriends();
        friendName = myFriends.get("Name");
        textToolbar.setText(friendName);
        friendPicture = myFriends.get("Picture");
        friendToken = myFriends.get("Token");

        createRoomChats(myChatsFriends);
    }

    private void createRoomChats(Map<String, String> myChatsFriends) {
        if(myChatsFriends != null && myChatsFriends.get(myUserID) != null){
            friendChatKey = myChatsFriends.get(myUserID);
            Log.d(TAG, "friendChatKey Get DB Key : " + friendChatKey);
        }else {
            friendChatKey = mRootRef.push().getKey();
            Log.d(TAG, "friendChatKey Push Key : " + friendChatKey);

            //My Node
            DatabaseReference mUsersRef = mRootRef.child("Users").child(myUserID).child("Chats");
            mUsersRef.child(friendFacebookId).setValue(friendChatKey);

            //Friend Node
            DatabaseReference mFriendRef = mRootRef.child("Users").child(friendFacebookId).child("Chats");
            mFriendRef.child(myUserID).setValue(friendChatKey);
        }

        mListener = MessageFirebase.addMessagesListener(friendChatKey, this);
        setMyRead(friendChatKey);
    }

    private void setMyRead(final String ChatsKey) {
        mRootRef.child("Chats").child(ChatsKey).addValueEventListener(mReadValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() != 0){
                    for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> mapObj = (Map<String, Object>) objDataSnapshot.getValue();
                        Log.d(TAG, "map chat read : " + mapObj);
                        String key = objDataSnapshot.getKey();
                        assert mapObj != null;
                        if(mapObj.containsKey("SenderId")){
                            String sender = mapObj.get("SenderId").toString();
                            if(!sender.equals(myUserID)){
                                DatabaseReference mReadRef = mRootRef.child("Chats").child(ChatsKey).child(key);
                                mReadRef.child("Read").setValue("true");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onEmojiChange(EmojiValue emojiValue) {
        //emojiListener
        if(emojiValue != null) {
            EmojiImage = emojiValue.getEmojiImage();
            EmojiName = emojiValue.getEmojiName();
            EmojiKey = emojiValue.getEmojiKey();
            emoji.setAdapter(new EmojiAdapter(this));
        }
        EmojiFirebase.stop(emojiListener);
    }

    private class EmojiAdapter extends BaseAdapter {

        private Activity context;
        public EmojiAdapter(Activity localContext) {
            context = localContext;
        }

        public int getCount() {
            return EmojiImage.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                float m =  Config.getInstance().getDisplayDensity(context);
                int w = (int) (100 * m);
                int h = (int) (100 * m);

                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView.setLayoutParams(new GridView.LayoutParams(w, h));

            } else {
                picturesView = (ImageView) convertView;
            }

            Glide.with(context)
                    .load(EmojiImage.get(position))
                    .placeholder(R.color.white)
                    .centerCrop()
                    .into(picturesView);

            return picturesView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        float m = Config.getInstance().getDisplayDensity(mContext);
        padding15 = (int) (15 * m);
        padding10 = (int) (10 * m);
        padding35 = (int) (35 * m);
        padding43 = (int) (43 * m);
    }



    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        iconUploadImage.setBackgroundResource(R.drawable.bg_circle);
        iconUploadImage.setAlpha((float) 1);

        iconPlay.setBackgroundResource(R.drawable.bg_circle);
        iconPlay.setAlpha((float) 1);

        if(MyTakePhoto.getInstance().getPathImage() != null){
            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
            float display = Config.getInstance().getDisplayDensity(mContext);
            params.height = (int) (640 * display);
            space_photo.setLayoutParams(params);

            gallery.setVisibility(View.INVISIBLE);
            iconPlay.setVisibility(View.GONE);
            imageSelect.setVisibility(View.VISIBLE);
            iconUploadImage.setVisibility(View.VISIBLE);
            btnBackCamera.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            Glide.with(Chat.this)
                    .load(MyTakePhoto.getInstance().getPathImage())
                    .centerCrop()
                    .placeholder(R.color.black)
                    .into(imageSelect);

            setPathImageSelect(MyTakePhoto.getInstance().getPathImage());
        }else if(MyTakePhoto.getInstance().getPathVideo() != null){
            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
            float display = Config.getInstance().getDisplayDensity(mContext);
            params.height = (int) (640 * display);
            space_photo.setLayoutParams(params);

            gallery.setVisibility(View.INVISIBLE);
            iconPlay.setVisibility(View.VISIBLE);
            imageSelect.setVisibility(View.VISIBLE);
            iconUploadImage.setVisibility(View.VISIBLE);
            btnBackCamera.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);

            Glide.with(Chat.this)
                    .load(MyTakePhoto.getInstance().getPathImageFromVideoFile().toString())
                    .centerCrop()
                    .placeholder(R.color.black)
                    .into(imageSelect);

            setPathImageSelect(MyTakePhoto.getInstance().getPathImageFromVideoFile().toString());
            setPathVideoSelect(MyTakePhoto.getInstance().getPathVideoFile().toString());
        }else {
            hideKeyboard(this);
            layout_camera.setBackgroundResource(R.color.transparent);
            layout_image.setBackgroundResource(R.color.transparent);
            layout_emotion.setBackgroundResource(R.color.transparent);
            layout_video.setBackgroundResource(R.color.transparent);
            layout_location.setBackgroundResource(R.color.transparent);
            if(status_footer_tab){
                footerSelect.setVisibility(View.GONE);
                btnAdd.setVisibility(View.VISIBLE);
                btnBackCamera.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
            }else{
                footerSelect.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.INVISIBLE);
                btnBackCamera.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
            }

            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
            params.height = 0;
            space_photo.setLayoutParams(params);
        }
    }

    private void setPathImageSelect(String s) {
        mPathImageSelect = s;
    }

    private String getPathImageSelect(){
        return mPathImageSelect;
    }

    private void setPathVideoSelect(String s) {
        mPathVideoSelect = s;
    }

    private String getPathVideoSelect(){
        return mPathVideoSelect;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        managerName.setChatRoomName("null");
        finish();
    }



    @Override
    public void onClick(View v) {
        if(v == btnSend){
            newMessage = inputText.getText().toString();
            Log.d(TAG,"new Message : " + newMessage);
            Message msg = new Message();
            msg.setText(newMessage);
            msg.setSender(myUserID);
            msg.setType("text");
            msg.setRead("false");
            inputText.setText("");

            if(friendToken != null) {
                String time = CalendarTime.getInstance().getCurrentTimeText();
                String data = friendToken + "/&" + friendName + "/&" + friendPicture + "/&" + friendFacebookId + "/&" + friendDeviceId
                        + "/&" + newMessage + "/&" + "text" + "/&" + time + "/&" + myUserID + "/&" + myDeviceID;
                new SendPushNotification().execute(data);
            }

            MessageFirebase.saveMessage(msg);
        }
        else if(v == inputText){
            inputText.setCursorVisible(true);
            showKeyboard(this);
            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
            params.height = 0;
            space_photo.setLayoutParams(params);
            btnCancel.setVisibility(View.INVISIBLE);
            btnBackCamera.setVisibility(View.INVISIBLE);
            btnAdd.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.VISIBLE);
        }
        else if(v == btnBack){
            managerName.setChatRoomName("null");
            finish();
        }
        else if(v == btnCancel){
            inputText.setCursorVisible(false);
            hideKeyboard(this);
            footerSelect.setVisibility(View.GONE);
            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
            params.height = 0;
            space_photo.setLayoutParams(params);
            imageSelect.setVisibility(View.INVISIBLE);
            iconUploadImage.setVisibility(View.INVISIBLE);
            btnBackCamera.setVisibility(View.INVISIBLE);
            gallery.setVisibility(View.INVISIBLE);
            emoji.setVisibility(View.GONE);
            btnAdd.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.INVISIBLE);

            layout_camera.setBackgroundResource(R.color.transparent);
            layout_image.setBackgroundResource(R.color.transparent);
            layout_emotion.setBackgroundResource(R.color.transparent);
            layout_video.setBackgroundResource(R.color.transparent);
            layout_location.setBackgroundResource(R.color.transparent);

        }
        else if(v == btnAdd){
            status_footer_tab = true;
            inputText.setCursorVisible(false);
            hideKeyboard(this);
            footerSelect.setVisibility(View.VISIBLE);
            gallery.setVisibility(View.INVISIBLE);
            emoji.setVisibility(View.GONE);
            imageSelect.setVisibility(View.INVISIBLE);
            iconUploadImage.setVisibility(View.INVISIBLE);
            btnBackCamera.setVisibility(View.INVISIBLE);

            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
            params.height = 0;
            space_photo.setLayoutParams(params);

//            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
//            float display = Config.getInstance().getDisplayDensity(mContext);
//            int height_display = (int) (640 * display);
//            params.height = height_display;
//            space_photo.setLayoutParams(params);
            btnAdd.setVisibility(View.INVISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.VISIBLE);

        }
        else if(v == iconUploadImage){
            iconUploadImage.setAlpha((float) 0.5);
            hideKeyboard(this);
            layout_camera.setBackgroundResource(R.color.transparent);
            layout_image.setBackgroundResource(R.color.transparent);
            layout_emotion.setBackgroundResource(R.color.transparent);
            layout_video.setBackgroundResource(R.color.transparent);
            layout_location.setBackgroundResource(R.color.transparent);

            btnAdd.setVisibility(View.VISIBLE);
            btnBackCamera.setVisibility(View.INVISIBLE);

            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
            params.height = 0;
            space_photo.setLayoutParams(params);

            if (getPathVideoSelect() != null) {
                uploadFromFile(getPathVideoSelect());
            }else{
                uploadFromFile(getPathImageSelect());
            }
        }
        else if(v == btnBackCamera){
            status_footer_tab = false;
            inputText.setCursorVisible(false);
            hideKeyboard(this);
            imageSelect.setVisibility(View.INVISIBLE);
            iconUploadImage.setVisibility(View.INVISIBLE);
            btnBackCamera.setVisibility(View.INVISIBLE);
            btnCancel.setVisibility(View.VISIBLE);

            layout_camera.setBackgroundResource(R.color.transparent);
            layout_image.setBackgroundResource(R.color.transparent);
            layout_emotion.setBackgroundResource(R.color.transparent);
            layout_location.setBackgroundResource(R.color.transparent);
            layout_video.setBackgroundResource(R.color.transparent);

            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
            params.height = 0;
            space_photo.setLayoutParams(params);

        }else if(v == iconPlay){
            iconPlay.setAlpha((float) 0.5);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getPathVideoSelect()));
            intent.setDataAndType(Uri.parse(getPathVideoSelect()), "video/mp4");
            startActivity(intent);
        }
        else if(v == layout_camera){
            clearImage();
            hideKeyboard(this);
            layout_camera.setBackgroundResource(R.color.grey_208);
            layout_image.setBackgroundResource(R.color.transparent);
            layout_emotion.setBackgroundResource(R.color.transparent);
            layout_location.setBackgroundResource(R.color.transparent);
            layout_video.setBackgroundResource(R.color.transparent);

            btnBackCamera.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);

            Intent nextpage = new Intent(this, MyCamera.class);
            startActivity(nextpage);
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }else if(v == layout_image){
            clearImage();
            hideKeyboard(this);
            layout_camera.setBackgroundResource(R.color.transparent);
            layout_image.setBackgroundResource(R.color.grey_208);
            layout_emotion.setBackgroundResource(R.color.transparent);
            layout_location.setBackgroundResource(R.color.transparent);
            layout_video.setBackgroundResource(R.color.transparent);

            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
            float display = Config.getInstance().getDisplayDensity(mContext);
            params.height = (int) (640 * display);
            space_photo.setLayoutParams(params);

            emoji.setVisibility(View.GONE);
            gallery.setVisibility(View.VISIBLE);
            iconPlay.setVisibility(View.INVISIBLE);
            imageSelect.setVisibility(View.INVISIBLE);
            iconUploadImage.setVisibility(View.INVISIBLE);
            btnBackCamera.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);

        }else if(v == layout_video){
            clearImage();
            hideKeyboard(this);
            layout_camera.setBackgroundResource(R.color.transparent);
            layout_image.setBackgroundResource(R.color.transparent);
            layout_emotion.setBackgroundResource(R.color.transparent);
            layout_location.setBackgroundResource(R.color.transparent);
            layout_video.setBackgroundResource(R.color.grey_208);

            btnBackCamera.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);

            Intent nextpage = new Intent(this, MyCameraVideo.class);
            startActivity(nextpage);
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }else if(v == layout_location){
            layout_camera.setBackgroundResource(R.color.transparent);
            layout_image.setBackgroundResource(R.color.transparent);
            layout_emotion.setBackgroundResource(R.color.transparent);
            layout_video.setBackgroundResource(R.color.transparent);
            layout_location.setBackgroundResource(R.color.grey_208);

            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
            params.height = 0;
            space_photo.setLayoutParams(params);

            String text = StringManager.getsInstance().getString("ShareLocation") + myCode;
            Message msg = new Message();
            msg.setText(myCode);
            msg.setSender(myUserID);
            msg.setType("Location");
            msg.setRead("false");

            if(friendToken != null) {
                String time = CalendarTime.getInstance().getCurrentTimeText();
                String data = friendToken + "/&" + friendName + "/&" + friendPicture + "/&" + friendFacebookId + "/&" + friendDeviceId
                        + "/&" + text + "/&" + "text" + "/&" + time + "/&" + myUserID + "/&" + myDeviceID;
                new SendPushNotification().execute(data);
            }

            MessageFirebase.saveMessage(msg);

        }else if(v == layout_emotion){
            clearImage();
            hideKeyboard(this);
            layout_camera.setBackgroundResource(R.color.transparent);
            layout_image.setBackgroundResource(R.color.transparent);
            layout_emotion.setBackgroundResource(R.color.grey_208);
            layout_location.setBackgroundResource(R.color.transparent);
            layout_video.setBackgroundResource(R.color.transparent);

            params = (RelativeLayout.LayoutParams) space_photo.getLayoutParams();
            float display = Config.getInstance().getDisplayDensity(mContext);
            params.height = (int) (640 * display);
            space_photo.setLayoutParams(params);

            emoji.setVisibility(View.VISIBLE);
            gallery.setVisibility(View.GONE);
            iconPlay.setVisibility(View.GONE);
            imageSelect.setVisibility(View.GONE);
            iconUploadImage.setVisibility(View.GONE);
            btnBackCamera.setVisibility(View.GONE);
            btnAdd.setVisibility(View.GONE);
            btnCancel.setVisibility(View.VISIBLE);



        }
    }

    private void uploadFromFile(String path) {
        Log.d(TAG, "File Path : " + path);
        if (path.contains(".mp4")) {
            uploadImageFromVideoFile(getPathImageSelect());

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("video/mp4")
                    .setCustomMetadata("country", String.valueOf(Locale.getDefault().getDisplayCountry()))
                    .build();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference folderRef = storageRef.child("Chats").child(friendFacebookId);
            Uri file = Uri.fromFile(new File(path));
            StorageReference imageRef = folderRef.child(file.getLastPathSegment());
            final UploadTask mUploadTask = imageRef.putFile(file, metadata);

            Helper.initProgressDialog(this);
            mUploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Helper.dismissProgressDialog();
                    Log.d(TAG, String.format("Failure: %s", exception.getMessage()));
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Helper.dismissDialog();
                    Helper.dismissProgressDialog();
                    Toast.makeText(getApplicationContext(), "Upload Video Success", Toast.LENGTH_SHORT).show();
                    //add data to database
                    addDataToFirebase(taskSnapshot.getDownloadUrl().toString(), URL_IMAGE_VIDEO);

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

        } else {
            uploadImageFromFile(path);
        }
    }

    private void uploadImageFromVideoFile(String path) {
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("country", String.valueOf(Locale.getDefault().getDisplayCountry()))
                .build();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference folderRef = storageRef.child("Chats").child(friendFacebookId);
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
                URL_IMAGE_VIDEO = taskSnapshot.getDownloadUrl().toString();

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
                .setCustomMetadata("country", String.valueOf(Locale.getDefault().getDisplayCountry()))
                .build();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference folderRef = storageRef.child("Chats").child(friendFacebookId);
        Uri file = Uri.fromFile(new File(path));
        StorageReference imageRef = folderRef.child(file.getLastPathSegment());


        final UploadTask mUploadTask = imageRef.putFile(file , metadata);

        Helper.initProgressDialog(this);
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
                Toast.makeText(getApplicationContext(),"Upload Image Success", Toast.LENGTH_SHORT).show();
                //add data to database
                addDataToFirebase(null, taskSnapshot.getDownloadUrl().toString());

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
        //add chats node
        String text;
        Message msg = new Message();
        if(UrlVideo != null){
            text = StringManager.getsInstance().getString("SendVideo");
            msg.setText(UrlImage);
            msg.setSender(myUserID);
            msg.setType("Video");
            msg.setVideo(UrlVideo);
            msg.setRead("false");

            clearImage();

        }else{
            text = StringManager.getsInstance().getString("SendPhoto");
            msg.setText(UrlImage);
            msg.setSender(myUserID);
            msg.setType("Image");
            msg.setRead("false");

            clearImage();
        }

        if(friendToken != null) {
            String time = CalendarTime.getInstance().getCurrentTimeText();
            String data = friendToken + "/&" + friendName + "/&" + friendPicture + "/&" + friendFacebookId + "/&" + friendDeviceId
                    + "/&" + text + "/&" + "text" + "/&" + time + "/&" + myUserID + "/&" + myDeviceID;
            new SendPushNotification().execute(data);
        }

        MessageFirebase.saveMessage(msg);
    }

    private void clearImage() {
        MyTakePhoto.getInstance().setPathImage(null);
        MyTakePhoto.getInstance().setPathVideo(null);
        MyTakePhoto.getInstance().setPathVideoFile(null);
        MyTakePhoto.getInstance().setPathImageFromVideoFile(null);
    }


    private void showKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void hideKeyboardAtfrist(Activity activity){
        if (activity != null) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private class ImageAdapter extends BaseAdapter {

        private Activity context;
        public ImageAdapter(Activity localContext) {
            context = localContext;
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                float m =  Config.getInstance().getDisplayDensity(context);
                int w = (int) (230 * m);
                int h = (int) (230 * m);

                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView.setLayoutParams(new GridView.LayoutParams(w, h));

            } else {
                picturesView = (ImageView) convertView;
            }

            Glide.with(context)
                    .load(images.get(position))
                    .placeholder(R.color.black)
                    .centerCrop()
                    .into(picturesView);

            return picturesView;
        }
    }

    @Override
    public void onMessageAdded(Message message) {
//        Log.d(TAG,"onMessageAdded : " + message);
        //check on page open for update node read
        mMessages.add(message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearImage();
        Log.d(TAG, "*** onDestroy ***");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "*** onStop() ***");

        MessageFirebase.stop(mListener);
        mRootRef.child("Chats").child(friendChatKey).removeEventListener(mReadValueEventListener);
    }

    private class MessagesAdapter extends ArrayAdapter<Message> {

        MessagesAdapter(ArrayList<Message> messages){
            super(Chat.this,R.layout.listview_chat, messages);
        }

        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            final Message message = getItem(position);

            final ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_chat, parent, false);

                //left
                holder.list_chat_left = (RelativeLayout) convertView.findViewById(R.id.list_chat_left);
                holder.imageViewLeft = (ImageView) convertView.findViewById(R.id.imageViewLeft);
                holder.text_message_left = (TextView) convertView.findViewById(R.id.text_message_left);
                holder.space_image_left = (RelativeLayout) convertView.findViewById(R.id.space_image_left);
                holder.text_message_image_left = (ImageView) convertView.findViewById(R.id.text_message_image_left);
                holder.text_video_play_left = (ImageView) convertView.findViewById(R.id.text_video_play_left);
                holder.timeChat_left = (TextView) convertView.findViewById(R.id.timeChat_left);
                holder.progressBar_left = (ProgressBar) convertView.findViewById(R.id.progressBar_left);
                holder.readChat_left = (ImageView) convertView.findViewById(R.id.readChat_left);


                //right
                holder.list_chat_right = (RelativeLayout) convertView.findViewById(R.id.list_chat_right);
                holder.text_message_right = (TextView) convertView.findViewById(R.id.text_message_right);
                holder.space_image_right = (RelativeLayout) convertView.findViewById(R.id.space_image_right);
                holder.text_message_image_right = (ImageView) convertView.findViewById(R.id.text_message_image_right);
                holder.text_video_play_right = (ImageView) convertView.findViewById(R.id.text_video_play_right);
                holder.timeChat_right = (TextView) convertView.findViewById(R.id.timeChat_right);
                holder.progressBar_right = (ProgressBar) convertView.findViewById(R.id.progressBar_right);

                //setFont
                holder.text_message_left.setTypeface(Config.getInstance().getDefaultFont(mContext));
                holder.timeChat_left.setTypeface(Config.getInstance().getDefaultFont(mContext));
                holder.text_message_right.setTypeface(Config.getInstance().getDefaultFont(mContext));
                holder.timeChat_right.setTypeface(Config.getInstance().getDefaultFont(mContext));

                holder.imageViewLeft.post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(getContext())
                                .load(friendPicture)
                                .fit()
                                .centerCrop()
                                .error(R.drawable.ic_person)
                                .placeholder(R.drawable.bg_circle_white)
                                .transform(new CircleTransform())
                                .into(holder.imageViewLeft);
                    }
                });

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            assert message != null;
            Log.d(TAG, "message.getSender(): " + message.getSender());
            if (message.getSender().toLowerCase().equals(myUserID.toLowerCase())){//right
                if(message.getRead().equals("true")){
                    holder.readChat_left.setVisibility(View.VISIBLE);
                }else{
                    holder.readChat_left.setVisibility(View.INVISIBLE);
                }
                holder.list_chat_left.setVisibility(View.GONE);
                holder.list_chat_right.setVisibility(View.VISIBLE);
                holder.timeChat_right.setText(message.getDate());

                if(message.getType().equals("text")) {
                    holder.space_image_right.setVisibility(View.GONE);
                    holder.text_message_image_right.setVisibility(View.GONE);
                    holder.text_video_play_right.setVisibility(View.GONE);
                    holder.text_message_right.setVisibility(View.VISIBLE);
                    holder.text_message_right.setBackgroundResource(R.drawable.bubble_right);
                    holder.text_message_right.setPadding(padding35, padding10, padding43, padding10);
                    holder.text_message_right.setTextColor(Color.WHITE);
                    holder.text_message_right.setGravity(Gravity.START);
                    holder.text_message_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    holder.text_message_right.setText(message.getText());
                    holder.text_message_right.setClickable(false);
                }
                else if(message.getType().equals("Image")){
                    holder.text_message_image_right.getLayoutParams().height = 440;
                    holder.text_message_image_right.getLayoutParams().width = 440;
                    holder.space_image_right.getLayoutParams().height = 440;
                    holder.space_image_right.getLayoutParams().width = 440;
                    holder.text_video_play_right.setVisibility(View.GONE);
                    holder.text_message_right.setVisibility(View.GONE);
                    holder.space_image_right.setVisibility(View.VISIBLE);
                    holder.text_message_image_right.setVisibility(View.VISIBLE);
                    holder.progressBar_right.setVisibility(View.VISIBLE);
                    Picasso.with(getContext())
                            .load(message.getText())
                            .resize(440, 440)
                            .centerCrop()
                            .error(R.color.white)
                            .placeholder(R.color.black)
                            .transform(new RoundedTransform())
                            .into(holder.text_message_image_right, new ImageLoadedCallbackRight(holder.progressBar_right));

                    holder.text_message_image_right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent i = new Intent(Chat.this, ShowFullImage.class);
                                i.putExtra("mPicture", message.getText());
                                startActivity(i);
                        }
                    });


                }
                else if(message.getType().equals("Emoji")){
                    holder.text_message_image_right.getLayoutParams().height = 180;
                    holder.text_message_image_right.getLayoutParams().width = 180;
                    holder.space_image_right.getLayoutParams().height = 180;
                    holder.space_image_right.getLayoutParams().width = 180;
                    holder.text_video_play_right.setVisibility(View.GONE);
                    holder.text_message_right.setVisibility(View.GONE);
                    holder.space_image_right.setVisibility(View.VISIBLE);
                    holder.text_message_image_right.setVisibility(View.VISIBLE);
                    holder.progressBar_right.setVisibility(View.VISIBLE);
                    Picasso.with(getContext())
                            .load(message.getText())
                            .resize(180, 180)
                            .centerCrop()
                            .error(R.color.white)
                            .placeholder(R.color.black)
                            .transform(new RoundedTransform())
                            .into(holder.text_message_image_right, new ImageLoadedCallbackRight(holder.progressBar_right));
                }
                else if(message.getType().equals("Video")){
                    holder.text_message_right.setVisibility(View.GONE);
                    holder.space_image_right.setVisibility(View.VISIBLE);
                    holder.text_message_image_right.setVisibility(View.VISIBLE);
                    holder.text_video_play_right.setVisibility(View.VISIBLE);
                    holder.text_video_play_right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getVideo()));
                            intent.setDataAndType(Uri.parse(message.getVideo()), "video/mp4");
                            startActivity(intent);
                        }
                    });
                    holder.progressBar_right.setVisibility(View.VISIBLE);
                    Picasso.with(getContext())
                            .load(message.getText())
                            .resize(440, 440)
                            .centerCrop()
                            .error(R.color.white)
                            .placeholder(R.color.black)
                            .transform(new RoundedTransform())
                            .into(holder.text_message_image_right, new ImageLoadedCallbackRight(holder.progressBar_right));
                }
                else if(message.getType().equals("Location")){
                    holder.space_image_right.setVisibility(View.GONE);
                    holder.text_message_image_right.setVisibility(View.GONE);
                    holder.text_video_play_right.setVisibility(View.GONE);
                    holder.text_message_right.setVisibility(View.VISIBLE);
                    holder.text_message_right.setBackgroundResource(R.drawable.bubble_right);
                    holder.text_message_right.setPadding(padding35, padding10, padding43, padding10);
                    holder.text_message_right.setTextColor(Color.WHITE);
                    holder.text_message_right.setGravity(Gravity.CENTER);
                    holder.text_message_right.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_flag, 0, 0, 0);
                    holder.text_message_right.setText(String.valueOf(StringManager.getsInstance().getString("ShareLocation") + message.getText()));
                    holder.text_message_right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences sp = getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
                            String mLat = sp.getString("Latitude", "-1");
                            String mLng = sp.getString("Longitude", "-1");

                            MoveToNavigateWithCode.getInstance().setFlagNavigate(true);
                            MoveToNavigateWithCode.getInstance().setCode(message.getText());
                            ActivityManagerName managerName = new ActivityManagerName();
                            managerName.setCurrentPage("MyMap");
                            Intent intent = new Intent(Chat.this, MyMap.class);
                            intent.putExtra("Latitude", Double.parseDouble(mLat));
                            intent.putExtra("Longitude", Double.parseDouble(mLng));
                            intent.putExtra("UserID", myUserID);
                            intent.putExtra("UserPhoto", myPicture);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

            }else { //left
                holder.list_chat_left.setVisibility(View.VISIBLE);
                holder.list_chat_right.setVisibility(View.GONE);
                holder.timeChat_left.setText(message.getDate());

                if (message.getType().equals("text")) {
                    holder.text_message_left.setVisibility(View.VISIBLE);
                    holder.text_message_left.setPadding(padding43, padding10, padding35, padding10);
                    holder.text_message_left.setTextColor(Color.BLACK);
                    holder.text_message_left.setGravity(Gravity.START);
                    holder.text_message_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    holder.text_message_left.setBackgroundResource(R.drawable.bubble_left);
                    holder.text_message_left.setClickable(false);
                    holder.text_message_left.setText(message.getText());
                    holder.space_image_left.setVisibility(View.GONE);
                    holder.text_message_image_left.setVisibility(View.GONE);

                }
                else if (message.getType().equals("Image")) {
                    holder.text_message_image_left.getLayoutParams().height = 440;
                    holder.text_message_image_left.getLayoutParams().width = 440;
                    holder.space_image_left.getLayoutParams().height = 440;
                    holder.space_image_left.getLayoutParams().width = 440;
                    holder.text_video_play_left.setVisibility(View.GONE);
                    holder.text_message_left.setVisibility(View.GONE);
                    holder.space_image_left.setVisibility(View.VISIBLE);
                    holder.text_message_image_left.setVisibility(View.VISIBLE);
                    holder.progressBar_left.setVisibility(View.VISIBLE);
                    Picasso.with(getContext())
                            .load(message.getText())
                            .resize(440, 440)
                            .centerCrop()
                            .error(R.color.white)
                            .placeholder(R.color.black)
                            .transform(new RoundedTransform())
                            .into(holder.text_message_image_left, new ImageLoadedCallbackLeft(holder.progressBar_left));

                    holder.text_message_image_left.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Chat.this, ShowFullImage.class);
                            i.putExtra("mPicture", message.getText());
                            startActivity(i);
                        }
                    });
                }
                else if (message.getType().equals("Emoji")) {
                    holder.text_message_image_left.getLayoutParams().height = 180;
                    holder.text_message_image_left.getLayoutParams().width = 180;
                    holder.space_image_left.getLayoutParams().height = 180;
                    holder.space_image_left.getLayoutParams().width = 180;
                    holder.text_video_play_left.setVisibility(View.GONE);
                    holder.text_message_left.setVisibility(View.GONE);
                    holder.space_image_left.setVisibility(View.VISIBLE);
                    holder.text_message_image_left.setVisibility(View.VISIBLE);
                    holder.progressBar_left.setVisibility(View.VISIBLE);
                    Picasso.with(getContext())
                            .load(message.getText())
                            .resize(180, 180)
                            .centerCrop()
                            .error(R.color.white)
                            .placeholder(R.color.black)
                            .transform(new RoundedTransform())
                            .into(holder.text_message_image_left, new ImageLoadedCallbackLeft(holder.progressBar_left));
                }
                else if(message.getType().equals("Video")){
                    holder.text_message_left.setVisibility(View.GONE);
                    holder.space_image_left.setVisibility(View.VISIBLE);
                    holder.text_message_image_left.setVisibility(View.VISIBLE);
                    holder.text_video_play_left.setVisibility(View.VISIBLE);
                    holder.text_video_play_left.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getVideo()));
                            intent.setDataAndType(Uri.parse(message.getVideo()), "video/mp4");
                            startActivity(intent);
                        }
                    });
                    holder.progressBar_left.setVisibility(View.VISIBLE);
                    Picasso.with(getContext())
                            .load(message.getText())
                            .resize(440, 440)
                            .centerCrop()
                            .error(R.color.white)
                            .placeholder(R.color.black)
                            .transform(new RoundedTransform())
                            .into(holder.text_message_image_left, new ImageLoadedCallbackLeft(holder.progressBar_left));
                }
                else if(message.getType().equals("Location")){
                    holder.space_image_left.setVisibility(View.GONE);
                    holder.text_message_image_left.setVisibility(View.GONE);
                    holder.text_video_play_left.setVisibility(View.GONE);
                    holder.text_message_left.setVisibility(View.VISIBLE);
                    holder.text_message_left.setBackgroundResource(R.drawable.bubble_right);
                    holder.text_message_left.setPadding(padding35, padding10, padding43, padding10);
                    holder.text_message_left.setTextColor(Color.WHITE);
                    holder.text_message_left.setGravity(Gravity.CENTER);
                    holder.text_message_left.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_flag, 0, 0, 0);
                    holder.text_message_left.setText(String.valueOf(StringManager.getsInstance().getString("ShareLocation") + message.getText()));
                    holder.text_message_left.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences sp = getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
                            String mLat = sp.getString("Latitude", "-1");
                            String mLng = sp.getString("Longitude", "-1");

                            MoveToNavigateWithCode.getInstance().setFlagNavigate(true);
                            MoveToNavigateWithCode.getInstance().setCode(message.getText());
                            ActivityManagerName managerName = new ActivityManagerName();
                            managerName.setCurrentPage("MyMap");
                            Intent intent = new Intent(Chat.this, MyMap.class);
                            intent.putExtra("Latitude", Double.parseDouble(mLat));
                            intent.putExtra("Longitude", Double.parseDouble(mLng));
                            intent.putExtra("UserID", myUserID);
                            intent.putExtra("UserPhoto", myPicture);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }

            return convertView;
        }

        private class ViewHolder{
            RelativeLayout list_chat_right;
            RelativeLayout list_chat_left;

            ImageView imageViewLeft;
            TextView text_message_left;
            RelativeLayout space_image_left;
            ImageView text_message_image_left;
            ImageView text_video_play_left;
            TextView timeChat_left;
            ProgressBar progressBar_left;
            ImageView readChat_left;

            TextView text_message_right;
            RelativeLayout space_image_right;
            ImageView text_message_image_right;
            ImageView text_video_play_right;
            TextView timeChat_right;
            ProgressBar progressBar_right;
        }

        private class ImageLoadedCallbackLeft implements Callback{

            final ViewHolder holder;

            public ImageLoadedCallbackLeft(ProgressBar progBar) {
                holder = new ViewHolder();
                holder.progressBar_left = progBar;
            }

            @Override
            public void onSuccess() {
                if(holder.progressBar_left != null){
                    holder.progressBar_left.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError() {

            }
        }

        private class ImageLoadedCallbackRight implements Callback{

            final ViewHolder holder;

            public ImageLoadedCallbackRight(ProgressBar progBar) {
                holder = new ViewHolder();
                holder.progressBar_right = progBar;
            }

            @Override
            public void onSuccess() {
                if(holder.progressBar_right != null){
                    holder.progressBar_right.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError() {

            }
        }
    }

    private class LoadGallery extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            images = getAllShownImagesPath(Chat.this);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            gallery.setAdapter(new ImageAdapter(Chat.this));
        }
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

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
}

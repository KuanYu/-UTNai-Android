package com.butions.utnai;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class AddFriends extends AppCompatActivity implements View.OnClickListener, CodeFirebase.CodesCallbacks {

    private String TAG = "AddFriends";
    private ImageView btnBack;
    private EditText searchBox;
    private ImageView searchEngine;
    private MyLoading objLoading;
    private TextView textNotFond;
    private LinearLayout layoutFindFriends;
    private ImageView imageUser;
    private TextView nameUser;
    private TextView textAddFriends;
    private TextView btnAddFriend;
    private String codeId;
    private String mUserID;
    private String mNameUser;
    private String codeName;
    private String mPicture;
    private String codePicture;
    private String codeToken;
    private String mDeviceID;
    private String codeDeviceID;
    private String newMessage;
    private CodeFirebase.CodesListener codeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friends);

        objLoading = new MyLoading(this);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        textAddFriends = (TextView) findViewById(R.id.textAddFriends);

        searchBox = (EditText) findViewById(R.id.text_search);
        searchBox.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        searchEngine = (ImageView) findViewById(R.id.icon_search);
        searchEngine.setOnClickListener(this);

        textNotFond = (TextView) findViewById(R.id.text_no_fond);
        textNotFond.setVisibility(View.GONE);

        layoutFindFriends = (LinearLayout) findViewById(R.id.layout_find_friends);
        imageUser = (ImageView) findViewById(R.id.image_user);
        nameUser = (TextView) findViewById(R.id.name_user);
        btnAddFriend = (TextView) findViewById(R.id.btnAddFriend);
        btnAddFriend.setOnClickListener(this);

        SharedPreferences sp = getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
        mUserID = sp.getString("UserID", "-1");
        mNameUser = sp.getString("Name", "-1");
        mPicture = sp.getString("Picture", "-1");
        mDeviceID = sp.getString("DeviceID", "-1");
        Log.d(TAG, "mUserID:" + mUserID);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "*** onResume ***");
        Translater.getInstance().setLanguages(this);

        newMessage = StringManager.getsInstance().getString("FriendRequests");

        textAddFriends.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        textAddFriends.setText(StringManager.getsInstance().getString("AddFriends"));

        btnAddFriend.setTypeface(Config.getInstance().getDefaultFont(this));

        searchBox.setTypeface(Config.getInstance().getDefaultFont(this));
        searchBox.setHint(StringManager.getsInstance().getString("FindFriends"));

        textNotFond.setText(StringManager.getsInstance().getString("UserNotFound"));
        textNotFond.setTypeface(Config.getInstance().getDefaultFont(this));
    }

    @Override
    public void onClick(View view) {
        if(view == searchEngine){
            String text = searchBox.getText().toString().trim();
            if(!text.isEmpty()){
                objLoading.loading(true);
                codeListener = CodeFirebase.addCodesListener(this, text);
            }
        }else if(view == btnBack){
            finish();
        }else if(view == btnAddFriend){
            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
            if(!mUserID.equals("-1") && codeId != null){
                //Me
                DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID).child("Friends").child(codeId);
                mUsersRef.child("ID").setValue(codeId);
                mUsersRef.child("Name").setValue(codeName);
                mUsersRef.child("Picture").setValue(codePicture);
                mUsersRef.child("Requests").setValue("wait");
                mUsersRef.child("OnMap").setValue("0");

                //Friends
                DatabaseReference mUsersFriendRef = mRootRef.child("Users").child(codeId).child("Friends").child(mUserID);
                mUsersFriendRef.child("ID").setValue(mUserID);
                mUsersFriendRef.child("Name").setValue(mNameUser);
                mUsersFriendRef.child("Picture").setValue(mPicture);
                mUsersFriendRef.child("Requests").setValue("requests");
                mUsersFriendRef.child("OnMap").setValue("0");

                if(codeToken != null) {
                    Log.d(TAG, "SendNotification:Token:" + codeToken);
                    String time = CalendarTime.getInstance().getCurrentTimeText();
                    String data = codeToken + "/&" + codeName + "/&" + codePicture + "/&" + codeId + "/&" + codeDeviceID
                            + "/&" + newMessage + "/&" + "add_friend" + "/&" + time + "/&" + mUserID + "/&" + mDeviceID;
                    new SendPushNotification().execute(data);
                }

                btnAddFriend.setClickable(false);
                btnAddFriend.setText(StringManager.getsInstance().getString("Requested"));
                btnAddFriend.setTextColor(getResources().getColor(R.color.grey));
                btnAddFriend.setBackgroundResource(R.drawable.bg_button_ok);
            }
        }
    }

    @Override
    public void onCodesChange(CodesValue codesValue) {
        CodeFirebase.stop(codeListener);
        if(codesValue != null){
            Log.d(TAG, "onCodesChange: Fond User : " + codesValue.getCodes());
            codeId = codesValue.getCodes();
            QueryHasMyFriend(codeId);
            QueryHasUser(codeId);
        }else{
            Log.d(TAG, "onCodesChange:Not Fond User");
            objLoading.loading(false);
            textNotFond.setVisibility(View.VISIBLE);
            layoutFindFriends.setVisibility(View.GONE);
        }
    }

    private void QueryHasMyFriend(final String codeId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").child(mUserID).child("Friends").orderByKey().equalTo(codeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> mapObjKey = (Map<String, Object>) dataSnapshot.getValue();
                    Map<String, Object> mapObjData = (Map<String, Object>)  mapObjKey.get(codeId);
                    if(mapObjData.containsKey("Requests")){
                        String requests = mapObjData.get("Requests").toString();
                        if(requests.equals("accept")){
                            btnAddFriend.setClickable(false);
                            btnAddFriend.setText(StringManager.getsInstance().getString("Friends"));
                            btnAddFriend.setTextColor(getResources().getColor(R.color.grey));
                            btnAddFriend.setBackgroundResource(R.drawable.bg_button_ok);
                        }else if(requests.equals("wait")){
                            btnAddFriend.setClickable(false);
                            btnAddFriend.setText(StringManager.getsInstance().getString("Requested"));
                            btnAddFriend.setTextColor(getResources().getColor(R.color.grey));
                            btnAddFriend.setBackgroundResource(R.drawable.bg_button_ok);
                        }else if(requests.equals("reject")){
                            btnAddFriend.setClickable(true);
                            btnAddFriend.setText(StringManager.getsInstance().getString("AddFriends"));
                            btnAddFriend.setTextColor(getResources().getColor(R.color.green));
                            btnAddFriend.setBackgroundResource(R.drawable.bg_button_green);
                        }

                    }else{
                        btnAddFriend.setClickable(true);
                        btnAddFriend.setText(StringManager.getsInstance().getString("AddFriends"));
                        btnAddFriend.setTextColor(getResources().getColor(R.color.green));
                        btnAddFriend.setBackgroundResource(R.drawable.bg_button_green);
                    }
                }else{
                    btnAddFriend.setClickable(true);
                    btnAddFriend.setText(StringManager.getsInstance().getString("AddFriends"));
                    btnAddFriend.setTextColor(getResources().getColor(R.color.green));
                    btnAddFriend.setBackgroundResource(R.drawable.bg_button_green);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void QueryHasUser(final String userID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").orderByKey().equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0) {
                    objLoading.loading(false);
                    textNotFond.setVisibility(View.GONE);
                    layoutFindFriends.setVisibility(View.VISIBLE);
                    Map<String, Object> mapObjKey = (Map<String, Object>) dataSnapshot.getValue();
                    Map<String, Object> mapObjData = (Map<String, Object>)  mapObjKey.get(userID);
                    Log.d(TAG, "QueryHasUser:onDataChange:mapObjData:" + mapObjData);
                    String urlImage = mapObjData.get("Picture").toString();
                    String name = mapObjData.get("Name").toString();
                    String token = mapObjData.get("Token").toString();
                    String deviceID = mapObjData.get("DeviceID").toString();

                    Picasso.with(AddFriends.this)
                            .load(urlImage)
                            .centerCrop()
                            .fit()
                            .placeholder(R.drawable.bg_circle_white)
                            .error(R.drawable.ic_account_circle)
                            .transform(new CircleTransform())
                            .into(imageUser);

                    nameUser.setText(name);
                    codePicture = urlImage;
                    codeName = name;
                    codeDeviceID = deviceID;
                    codeToken = token;
                }else{
                    objLoading.loading(false);
                    textNotFond.setVisibility(View.VISIBLE);
                    layoutFindFriends.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

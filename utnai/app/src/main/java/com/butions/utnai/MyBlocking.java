package com.butions.utnai;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyBlocking extends AppCompatActivity implements MyProfileFirebase.ProfileCallbacks, View.OnClickListener {

    private String TAG = "MyBlocking";
    private ListView my_list_friends;
    private CustomListViewFriend adapter;
    private String mUserID;

    private List<String> myNameFriends;
    private List<String> myFacebookIDFriends;
    private List<String> myPictureFriend;
    private List<String> myOnMapFriend;
    private Map<String, String> myProfile;

    //temp search
    private Map<Integer, String> itemNameFriends;
    private Map<Integer, String> itemFacebookIDFriends;
    private Map<Integer, String> itemPictureFriend;
    private Map<Integer, String> itemOnMapFriend;

    private MyProfileFirebase.ProfileListener profileListener;
    private DatabaseReference mRootRef;
    private MyLoading objMyLoading;
    private TextView textToolbarBlocking;
    private ImageView btnBack;
    private EditText searchBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_blocking);

        Translater.getInstance().setLanguages(this);

        objMyLoading = new MyLoading(this);
        mRootRef = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        mUserID = bundle.getString("mUserID");

        //call profile
        profileListener = MyProfileFirebase.addProfileListener(mUserID, this, this);

        adapter = new CustomListViewFriend(this);
        my_list_friends = (ListView) findViewById(R.id.my_list_friends);
        textToolbarBlocking = (TextView) findViewById(R.id.textToolbarBlocking);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        searchBox = (EditText) findViewById(R.id.text_search);
        searchBox.setTypeface(Config.getInstance().getDefaultFont(this));
        searchBox.setHint(StringManager.getsInstance().getString("Search"));
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "String Search : " + s.toString());
                if(s.toString().trim().isEmpty()){
                    initializeList();
                }
                else{
                    String searchString = searchBox.getText().toString();
                    int textLength = searchString.length();
                    searchItem(searchString, textLength);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textToolbarBlocking.setTypeface(Config.getInstance().getDefaultFont(this));
        btnBack.setOnClickListener(this);
    }

    private void initializeList(){
        myNameFriends = new ArrayList<String>(itemNameFriends.values());
        myFacebookIDFriends = new ArrayList<String>(itemFacebookIDFriends.values());
        myPictureFriend = new ArrayList<String>(itemPictureFriend.values());
        myOnMapFriend = new ArrayList<String>(itemOnMapFriend.values());

        adapter.notifyDataSetChanged();
        my_list_friends.setAdapter(adapter);
    }


    @Override
    public void onProfileChange(ProfileValue profileValue) {
        myProfile = profileValue.getMyProfile();

        itemNameFriends = profileValue.getMyNameFriends();
        itemFacebookIDFriends = profileValue.getMyFacebookIDFriends();
        itemPictureFriend = profileValue.getMyPictureFriend();
        itemOnMapFriend = profileValue.getMyOnMapFriend();

        myNameFriends = new ArrayList<String>(itemNameFriends.values());
        myFacebookIDFriends = new ArrayList<String>(itemFacebookIDFriends.values());
        myPictureFriend = new ArrayList<String>(itemPictureFriend.values());
        myOnMapFriend = new ArrayList<String>(itemOnMapFriend.values());

        searchBox.setText("");
        initializeList();
    }

    private void searchItem(String textToSearch, int textLength) {
        try {
            myNameFriends.clear();
            myFacebookIDFriends.clear();
            myPictureFriend.clear();
            myOnMapFriend.clear();
            for (int i=0; i<itemNameFriends.size(); i++) {
                String item = itemNameFriends.get(i);
                if(textLength <= item.length()){
                    if(textToSearch.equalsIgnoreCase(item.substring(0,textLength))){
                        myNameFriends.add(itemNameFriends.get(i));
                        myFacebookIDFriends.add(itemFacebookIDFriends.get(i));
                        myPictureFriend.add(itemPictureFriend.get(i));
                        myOnMapFriend.add(itemOnMapFriend.get(i));
                    }
                }

            }
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "*** onResume ***");
        adapter.notifyDataSetChanged();

        Translater.getInstance().setLanguages(this);
        setFonts();
    }

    private void setFonts() {
        textToolbarBlocking.setText(StringManager.getsInstance().getString("Blocking"));
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "*** onStop ***");
        MyProfileFirebase.stop(profileListener);
    }

    @Override
    public void onClick(View view) {
        if(view == btnBack) {
            finish();
        }
    }

    private class CustomListViewFriend extends BaseAdapter {

        public Context mContext;
        public LayoutInflater mInflater;
        public CustomListViewFriend(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return myFacebookIDFriends.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.listview_block_layout, null);
                holder = new ViewHolder();
                holder.header_name = (LinearLayout) convertView.findViewById(R.id.header_name);
                holder.btnUnBlock = (TextView) convertView.findViewById(R.id.btnUnBlock);
                holder.text_name = (TextView) convertView.findViewById(R.id.name);
                holder.imageProfile = (ImageView) convertView.findViewById(R.id.imageProfile);
                holder.space_imageProfile = (LinearLayout) convertView.findViewById(R.id.space_imageProfile);

                holder.btnUnBlock.setTypeface(Config.getInstance().getDefaultFont(mContext));
                holder.btnUnBlock.setText(StringManager.getsInstance().getString("BtnUnblock"));
                holder.text_name.setTypeface(Config.getInstance().getDefaultFont(mContext), Typeface.BOLD);

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text_name.setTextColor(getResources().getColor(R.color.red));
            holder.text_name.setText(myNameFriends.get(i));
            if(myOnMapFriend.get(i).equals("2")) {        //block
                holder.header_name.setVisibility(View.VISIBLE);
            }else{
                holder.header_name.setVisibility(View.GONE);
            }

            Picasso.with(mContext)
                    .load(myPictureFriend.get(i))
                    .centerCrop()
                    .fit()
                    .placeholder(R.drawable.bg_circle_white)
                    .error(R.drawable.ic_account_circle)
                    .transform(new CircleTransform())
                    .into(holder.imageProfile);

            final ViewHolder finalHolder = holder;
            holder.btnUnBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogNeedUnBlock(myFacebookIDFriends.get(i), myPictureFriend.get(i) , myNameFriends.get(i));
                }
            });

            final ViewHolder finalHolder1 = holder;
            holder.text_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //intent to profile my friend
                    finalHolder1.text_name.setTextColor(getResources().getColor(R.color.grey));
                    Intent intent = new Intent(MyBlocking.this, MyFriendFeeds.class);
                    intent.putExtra("mUserID", mUserID);
                    intent.putExtra("mFriendFacebookID", myFacebookIDFriends.get(i));
                    intent.putExtra("mFriendName", myNameFriends.get(i));
                    intent.putExtra("mName", myProfile.get("Name"));
                    intent.putExtra("mPicture", myProfile.get("Picture"));
                    intent.putExtra("mOnMap", myOnMapFriend.get(i));
                    startActivity(intent);
                }
            });


            return convertView;
        }
    }

    private class ViewHolder{
        TextView btnUnBlock;
        TextView text_name;
        ImageView imageProfile;
        LinearLayout space_imageProfile;
        LinearLayout header_name;
    }

    private void dialogNeedUnBlock(final String mFriendFacebookID ,String mPictureFriend, String mNameFriend) {
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

        Picasso.with(this)
                .load(mPictureFriend)
                .centerCrop()
                .fit()
                .noFade()
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(dialog_image);

        dialog_title.setImageResource(R.drawable.ic_unblock_grey);
        dialog_detail.setTypeface(Config.getInstance().getDefaultFont(this));
        dialog_detail.setText(StringManager.getsInstance().getString("Unblock").replace("XXXXX", mNameFriend));
        btnOk.setTypeface(Config.getInstance().getDefaultFont(this));
        btnOk.setText(StringManager.getsInstance().getString("Yes"));
        btnCancle.setTypeface(Config.getInstance().getDefaultFont(this));
        btnCancle.setText(StringManager.getsInstance().getString("No"));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Need UnBlock

                // me block friend
                DatabaseReference mMeRef = mRootRef.child("Users").child(mUserID).child("Friends").child(mFriendFacebookID);
                mMeRef.child("OnMap").setValue("1");

                // friend block me
                DatabaseReference mFriendRef = mRootRef.child("Users").child(mFriendFacebookID).child("Friends").child(mUserID);
                mFriendRef.child("OnMap").setValue("1");

                adapter.notifyDataSetChanged();
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

}

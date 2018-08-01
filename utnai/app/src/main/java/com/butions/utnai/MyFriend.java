package com.butions.utnai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyFriend extends Fragment implements MyProfileFirebase.ProfileCallbacks, MyProfileFriendsFirebase.ProfileFriendsCallbacks, View.OnClickListener {

    private String TAG = "MyFriend";
    private ListView my_list_friends;
    private CustomListViewFriend adapter;
    private String mUserID;

    private List<String> myNameFriends;
    private List<String> myFacebookIDFriends;
    private List<String> myPictureFriend;
    private List<String> myOnMapFriend;
    private List<String> myRequestsFriend;
    private Map<String, String> myProfile;

    //temp search
    private Map<Integer, String> itemNameFriends;
    private Map<Integer, String> itemFacebookIDFriends;
    private Map<Integer, String> itemPictureFriend;
    private Map<Integer, String> itemOnMapFriend;
    private Map<Integer, String> itemRequestsFriend;

    private Context context;
    private MyProfileFirebase.ProfileListener profileListener;
    private DatabaseReference mRootRef;
    private String mLat, mLng;
    private MyProfileFriendsFirebase.ProfileFriendsListener profileFriendsListener;
    private MyLoading objMyLoading;
    private EditText searchBox;
    private TextWatcher listenerTextWatcher;
    private FloatingActionButton btnAddFriends;
    private TextView tabFriends, tabRequests;
    private RelativeLayout layout_tap_list_friends,layout_tap_friends_requests;
    private CustomListViewFriendRequests adapter_requests;
    private ListView my_list_friends_requests;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "*** onCreateView ***");
        View v = inflater.inflate(R.layout.my_friend, container, false);
        ActivityManagerName managerName = new ActivityManagerName();
        managerName.setProcessNameClass(context);
        managerName.setNameClassInProcessString("MyFriend");

        Translater.getInstance().setLanguages(context);

        objMyLoading = new MyLoading(context);
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUserID = getArguments().getString("MyUserID");
        SharedPreferences sp = context.getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
        mLat = sp.getString("Latitude", "-1");
        mLng = sp.getString("Longitude", "-1");

        adapter = new CustomListViewFriend(context);
        adapter_requests = new CustomListViewFriendRequests(context);
        my_list_friends = (ListView) v.findViewById(R.id.my_list_friends);
        my_list_friends_requests = (ListView) v.findViewById(R.id.my_list_friends_requests);
        btnAddFriends = (FloatingActionButton) v.findViewById(R.id.fab_add_friends);
        tabFriends = (TextView) v.findViewById(R.id.tab_friends);
        tabRequests = (TextView) v.findViewById(R.id.tab_requests);
        layout_tap_list_friends =  (RelativeLayout) v.findViewById(R.id.layout_tap_list_friends);
        layout_tap_friends_requests = (RelativeLayout) v.findViewById(R.id.layout_tap_friends_requests);
        searchBox = (EditText) v.findViewById(R.id.text_search);
        searchBox.addTextChangedListener(listenerTextWatcher = new TextWatcher() {
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

        //call profile
        profileListener = MyProfileFirebase.addProfileListener(mUserID, this, context);

        //set fonts
        searchBox.setTypeface(Config.getInstance().getDefaultFont(context));
        tabFriends.setTypeface(Config.getInstance().getDefaultFont(context));
        tabRequests.setTypeface(Config.getInstance().getDefaultFont(context));

        searchBox.setHint(StringManager.getsInstance().getString("Search"));
        tabFriends.setText(StringManager.getsInstance().getString("MyFriends"));
        tabRequests.setText(StringManager.getsInstance().getString("Requests"));

        btnAddFriends.setOnClickListener(this);
        tabFriends.setOnClickListener(this);
        tabRequests.setOnClickListener(this);

        return v;
    }

    @Override
    public void onProfileChange(ProfileValue profileValue) {
        myProfile = profileValue.getMyProfile();

        itemNameFriends = profileValue.getMyNameFriends();
        itemFacebookIDFriends = profileValue.getMyFacebookIDFriends();
        itemPictureFriend = profileValue.getMyPictureFriend();
        itemOnMapFriend = profileValue.getMyOnMapFriend();
        itemRequestsFriend = profileValue.getMyRequestsFriend();

        myNameFriends = new ArrayList<String>(itemNameFriends.values());
        myFacebookIDFriends = new ArrayList<String>(itemFacebookIDFriends.values());
        myPictureFriend = new ArrayList<String>(itemPictureFriend.values());
        myOnMapFriend = new ArrayList<String>(itemOnMapFriend.values());
        myRequestsFriend = new ArrayList<String>(itemRequestsFriend.values());

        searchBox.setText("");
        initializeList();
    }

    private void initializeList(){
        myNameFriends = new ArrayList<String>(itemNameFriends.values());
        myFacebookIDFriends = new ArrayList<String>(itemFacebookIDFriends.values());
        myPictureFriend = new ArrayList<String>(itemPictureFriend.values());
        myOnMapFriend = new ArrayList<String>(itemOnMapFriend.values());
        myRequestsFriend = new ArrayList<String>(itemRequestsFriend.values());

        adapter.notifyDataSetChanged();
        my_list_friends.setAdapter(adapter);

        adapter_requests.notifyDataSetChanged();
        my_list_friends_requests.setAdapter(adapter_requests);
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

        SharedPreferences sp = context.getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
        mLat = sp.getString("Latitude", "-1");
        mLng = sp.getString("Longitude", "-1");

        //call profile
//        profileListener = MyProfileFirebase.addProfileListener(mUserID, this, context);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "*** onStop ***");
        MyProfileFirebase.stop(profileListener);
        searchBox.removeTextChangedListener(listenerTextWatcher);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "*** onDestroyView ***");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "*** onPause ***");
    }

    @Override
    public void onClick(View view) {
        if(view == btnAddFriends){
            Intent intent = new Intent(context, AddFriends.class);
            startActivity(intent);
        }else if(view == tabFriends){
            layout_tap_friends_requests.setVisibility(View.GONE);
            layout_tap_list_friends.setVisibility(View.VISIBLE);
            tabFriends.setBackgroundResource(R.drawable.tab_pink);
            tabRequests.setBackgroundResource(R.drawable.tab_grey);
        }else if(view == tabRequests){
            layout_tap_friends_requests.setVisibility(View.VISIBLE);
            layout_tap_list_friends.setVisibility(View.GONE);
            tabFriends.setBackgroundResource(R.drawable.tab_grey);
            tabRequests.setBackgroundResource(R.drawable.tab_pink);
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
                convertView = mInflater.inflate(R.layout.listview_friend_layout, null);
                holder = new ViewHolder();
                holder.header_name = (LinearLayout) convertView.findViewById(R.id.header_name);
                holder.switch_status = (SwitchCompat) convertView.findViewById(R.id.switch_status);
                holder.text_name = (TextView) convertView.findViewById(R.id.name);
                holder.imageProfile = (ImageView) convertView.findViewById(R.id.imageProfile);
                holder.space_imageProfile = (LinearLayout) convertView.findViewById(R.id.space_imageProfile);


                holder.text_name.setTypeface(Config.getInstance().getDefaultFont(mContext), Typeface.BOLD);

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            if(myRequestsFriend.get(i).equals("accept") && myOnMapFriend != null && myOnMapFriend.get(i) != null) {
                if (myOnMapFriend.get(i).equals("0")) {        //off
                    holder.header_name.setVisibility(View.VISIBLE);
                    holder.switch_status.setShowText(true);
                    holder.switch_status.setChecked(false);
                } else if (myOnMapFriend.get(i).equals("1")) {  //on
                    holder.header_name.setVisibility(View.VISIBLE);
                    holder.switch_status.setShowText(true);
                    holder.switch_status.setChecked(true);
                } else if (myOnMapFriend.get(i).equals("null")) {
                    holder.header_name.setVisibility(View.VISIBLE);
                    holder.switch_status.setShowText(true);
                    holder.switch_status.setChecked(false);
                } else {
                    holder.header_name.setVisibility(View.GONE);
                }
            }else{
                holder.header_name.setVisibility(View.GONE);
            }

            holder.text_name.setTextColor(getResources().getColor(R.color.red));
            holder.text_name.setText(myNameFriends.get(i));

            Picasso.with(mContext)
                    .load(myPictureFriend.get(i))
                    .centerCrop()
                    .fit()
                    .placeholder(R.drawable.bg_circle_white)
                    .error(R.drawable.ic_account_circle)
                    .transform(new CircleTransform())
                    .into(holder.imageProfile);

            final ViewHolder finalHolder = holder;
            holder.switch_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    finalHolder.switch_status.setChecked(b);
                    finalHolder.switch_status.setShowText(b);
                    if(b){
                        DatabaseReference mFriendRef = mRootRef.child("Users").child(mUserID).child("Friends").child(myFacebookIDFriends.get(i));
                        mFriendRef.child("OnMap").setValue("1");
                    }else{
                        DatabaseReference mFriendRef = mRootRef.child("Users").child(mUserID).child("Friends").child(myFacebookIDFriends.get(i));
                        mFriendRef.child("OnMap").setValue("0");
                    }
                }
            });

            holder.imageProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //to move position on map
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            objMyLoading.loading(true);
                            callLatLngFriends(myFacebookIDFriends.get(i));
                        }
                    });
                }
            });

            final ViewHolder finalHolder1 = holder;
            holder.text_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //intent to profile my friend
                    finalHolder1.text_name.setTextColor(getResources().getColor(R.color.grey));
                    Intent intent = new Intent(getActivity(), MyFriendFeeds.class);
                    intent.putExtra("MyFacebookID", mUserID);
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
        SwitchCompat switch_status;
        TextView text_name;
        ImageView imageProfile;
        LinearLayout space_imageProfile;
        LinearLayout header_name;
    }


    private class CustomListViewFriendRequests extends BaseAdapter {

        public Context mContext;
        public LayoutInflater mInflater;
        public CustomListViewFriendRequests(Context context) {
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
            ViewHolderRequests holder = null;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.listview_request_friend_layout, null);
                holder = new ViewHolderRequests();
                holder.header_name = (LinearLayout) convertView.findViewById(R.id.header_name);
                holder.text_name = (TextView) convertView.findViewById(R.id.name);
                holder.imageProfile = (ImageView) convertView.findViewById(R.id.imageProfile);
                holder.btnAccept = (TextView) convertView.findViewById(R.id.btnAccept);
                holder.btnReject = (TextView) convertView.findViewById(R.id.btnReject);

                holder.text_name.setTypeface(Config.getInstance().getDefaultFont(mContext), Typeface.BOLD);
                holder.btnAccept.setTypeface(Config.getInstance().getDefaultFont(mContext));
                holder.btnReject.setTypeface(Config.getInstance().getDefaultFont(mContext));

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolderRequests) convertView.getTag();
            }

            if(myRequestsFriend.get(i).equals("requests")) {
                holder.text_name.setTextColor(getResources().getColor(R.color.red));
                holder.text_name.setText(myNameFriends.get(i));

                Picasso.with(mContext)
                        .load(myPictureFriend.get(i))
                        .centerCrop()
                        .fit()
                        .placeholder(R.drawable.bg_circle_white)
                        .error(R.drawable.ic_account_circle)
                        .transform(new CircleTransform())
                        .into(holder.imageProfile);
            }else{
                holder.header_name.setVisibility(View.GONE);
            }


            final ViewHolderRequests finalHolder1 = holder;
            holder.text_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //intent to profile my friend
                    finalHolder1.text_name.setTextColor(getResources().getColor(R.color.grey));
                    Intent intent = new Intent(getActivity(), MyFriendFeeds.class);
                    intent.putExtra("MyFacebookID", mUserID);
                    intent.putExtra("mFriendFacebookID", myFacebookIDFriends.get(i));
                    intent.putExtra("mFriendName", myNameFriends.get(i));
                    intent.putExtra("mName", myProfile.get("Name"));
                    intent.putExtra("mPicture", myProfile.get("Picture"));
                    intent.putExtra("mOnMap", myOnMapFriend.get(i));
                    startActivity(intent);
                }
            });

            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Me
                    DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID).child("Friends").child(myFacebookIDFriends.get(i));
                    mUsersRef.child("Requests").setValue("accept");

                    //Friends
                    DatabaseReference mUsersFriendRef = mRootRef.child("Users").child(myFacebookIDFriends.get(i)).child("Friends").child(mUserID);
                    mUsersFriendRef.child("Requests").setValue("accept");

                }
            });

            holder.btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Me
                    DatabaseReference mUsersRef = mRootRef.child("Users").child(mUserID).child("Friends").child(myFacebookIDFriends.get(i));
                    mUsersRef.child("Requests").setValue("reject");

                    //Friends
                    DatabaseReference mUsersFriendRef = mRootRef.child("Users").child(myFacebookIDFriends.get(i)).child("Friends").child(mUserID);
                    mUsersFriendRef.child("Requests").setValue("reject");

                }
            });

            return convertView;
        }
    }

    private class ViewHolderRequests{
        TextView text_name;
        ImageView imageProfile;
        LinearLayout header_name;
        TextView btnAccept;
        TextView btnReject;
    }

    private void callLatLngFriends(String facebookIDFriend) {
        profileFriendsListener = MyProfileFriendsFirebase.addProfileFriendsListener(facebookIDFriend, this);
    }

    @Override
    public void onFriendsChange(final ProfileFriendsValue profilefriendsValue) {
        objMyLoading.loading(false);
        try {
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyProfileFriendsFirebase.stop(profileFriendsListener);
                    if(profilefriendsValue != null) {
                        Map<String, String> myFriends = profilefriendsValue.getMyFriends();
                        double lat = Double.parseDouble(myFriends.get("Latitude"));
                        double lng = Double.parseDouble(myFriends.get("Longitude"));

                        MoveToFriesndLocation.getInstance().setFlag(true);
                        MoveToFriesndLocation.getInstance().setLatitude(lat);
                        MoveToFriesndLocation.getInstance().setLongitude(lng);

                        Log.d(TAG, "lat : " + Double.parseDouble(mLat));
                        Log.d(TAG, "lng : " + Double.parseDouble(mLng));
                        Log.d(TAG, "UserID : " + mUserID);

                        ActivityManagerName managerName = new ActivityManagerName();
                        managerName.setCurrentPage("MyMap");
                        Intent intent = new Intent(context, MyMap.class);
                        intent.putExtra("Latitude", Double.parseDouble(mLat));
                        intent.putExtra("Longitude", Double.parseDouble(mLng));
                        intent.putExtra("UserID", mUserID);
                        intent.putExtra("UserPhoto", myProfile.get("Picture"));
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });

        }catch (Exception e){
            e.getMessage();
        }
    }

}

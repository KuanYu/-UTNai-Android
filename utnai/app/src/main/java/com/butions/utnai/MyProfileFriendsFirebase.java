package com.butions.utnai;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Chalitta Khampachua on 09-Nov-16.
 */
public class MyProfileFriendsFirebase {
    private static final DatabaseReference sRef = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "MyFriendsFirebase";
    private static String My_FRIENDS;

    public static ProfileFriendsListener addProfileFriendsListener(String facebookID, final ProfileFriendsCallbacks callbacks){
        ProfileFriendsListener listener = new ProfileFriendsListener(callbacks);
        My_FRIENDS = "Users/" + facebookID;
        sRef.child(My_FRIENDS).addValueEventListener(listener);
        return listener;
    }

    public static void stop(ProfileFriendsListener listener){
        sRef.child(My_FRIENDS).removeEventListener(listener);
    }


    public static class ProfileFriendsListener implements ValueEventListener {
        private ProfileFriendsCallbacks callbacks;
        private Map<String, String> myFriends = new HashMap<>();
        private Map<String, String> myPostsFriends = new HashMap<>();
        private Map<String, String> myChatsFriends = new HashMap<>();

        ProfileFriendsListener(ProfileFriendsCallbacks callbacks){
            this.callbacks = callbacks;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount() > 0) {
                clearAll();
                Map<String, Object> mapObj = (Map<String, Object>) dataSnapshot.getValue();
                assert mapObj != null;

                if (mapObj.containsKey("Link")) {
                    myFriends.put("Link", mapObj.get("Link").toString());
                }

                if (mapObj.containsKey("Address")) {
                    myFriends.put("Address", mapObj.get("Address").toString());
                }

                if (mapObj.containsKey("Name")) {
                    myFriends.put("Name", mapObj.get("Name").toString());
                }

                if (mapObj.containsKey("Picture")) {
                    myFriends.put("Picture", mapObj.get("Picture").toString());
                }

                if (mapObj.containsKey("Cover")) {
                    myFriends.put("Cover", mapObj.get("Cover").toString());
                }

                if (mapObj.containsKey("Email")) {
                    myFriends.put("Email", mapObj.get("Email").toString());
                }

                if (mapObj.containsKey("UserID")) {
                    myFriends.put("UserID", mapObj.get("UserID").toString());
                }

                if(mapObj.containsKey("DeviceID")){
                    myFriends.put("DeviceID", mapObj.get("DeviceID").toString());
                }

                if(mapObj.containsKey("DeviceName")){
                    myFriends.put("DeviceName", mapObj.get("DeviceName").toString());
                }

                if(mapObj.containsKey("Token")){
                    myFriends.put("Token", mapObj.get("Token").toString());
                }

                if(mapObj.containsKey("OnMap")){
                    myFriends.put("OnMap", mapObj.get("OnMap").toString());
                }

                if(mapObj.containsKey("LatLng")){
                    String latlng = mapObj.get("LatLng").toString();
                    String[] location = latlng.split(",");
                    myFriends.put("Latitude", location[0]);
                    myFriends.put("Longitude", location[1]);
                }

                //get my friend latest post
                if(mapObj.containsKey("LastPost")) {
                    Map<String, Object> mapObjPost = (Map<String, Object>) mapObj.get("LastPost");
                    assert mapObjPost != null;
                    if (mapObjPost.containsKey("Message")) {
                        myPostsFriends.put("Message", mapObjPost.get("Message").toString());
                    }else{
                        myPostsFriends.put("Message", "null");
                    }

                    if (mapObjPost.containsKey("Feeling")) {
                        myPostsFriends.put("Feeling",mapObjPost.get("Feeling").toString());
                    }else{
                        myPostsFriends.put("Feeling", "null");
                    }

                    if(mapObjPost.containsKey("Image")){
                        myPostsFriends.put("Image",mapObjPost.get("Image").toString());
                    }else{
                        myPostsFriends.put("Image", "null");
                    }

                    if(mapObjPost.containsKey("Video")){
                        myPostsFriends.put("Video",mapObjPost.get("Video").toString());
                    }else{
                        myPostsFriends.put("Video", "null");
                    }

                    if(mapObjPost.containsKey("CheckInName")){
                        myPostsFriends.put("CheckInName",mapObjPost.get("CheckInName").toString());
                    }else{
                        myPostsFriends.put("CheckInName", "null");
                    }

                    if (mapObjPost.containsKey("Created_time")) {
                        myPostsFriends.put("Created_time", mapObjPost.get("Created_time").toString());
                    }else{
                        myPostsFriends.put("Created_time", "null");
                    }
                }

                //get chat key
                if(mapObj.containsKey("Chats")) {
                    try {
                        Map<String, Object> mapObjChats = (Map<String, Object>) mapObj.get("Chats");
                        SortedSet<String> keys = new TreeSet<String>(mapObjChats.keySet());
                        for (String key : keys) {
                            myChatsFriends.put(key, mapObjChats.get(key).toString());
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }
                }else{
                    myChatsFriends = null;
                }

                ProfileFriendsValue friends = new ProfileFriendsValue();
                friends.setMyFriends(myFriends);
                friends.setMyPostsFriends(myPostsFriends);
                friends.setMyChatsFriends(myChatsFriends);

                if(callbacks != null){
                    callbacks.onFriendsChange(friends);
                }

            }else{
                //is not value at database
                if(callbacks != null){
                    callbacks.onFriendsChange(null);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

        private void clearAll() {
            myFriends.clear();
        }
    }

    private static String getPattenDate(String[] arrayDate) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.ENGLISH);
        int monthNum = Integer.parseInt(arrayDate[1]) - 1;
        calendar.set(Calendar.MONTH, monthNum);
        String monthName = month_date.format(calendar.getTime());

        return monthName + " " + arrayDate[2] + ", " + arrayDate[0];
    }

    public interface ProfileFriendsCallbacks{
        public void onFriendsChange(ProfileFriendsValue profilefriendsValue);
    }

}

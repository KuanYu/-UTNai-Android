package com.butions.utnai;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 09-Nov-16.
 */
public class MyProfileFirebase {
    private static final DatabaseReference sRef = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "MyProfileFirebase";
    private static Context mContext;
    private static String My_PROFILE;

    public static ProfileListener addProfileListener(String facebookID, final ProfileCallbacks callbacks, Context context){
        mContext = context;
        ProfileListener listener = new ProfileListener(callbacks);
        My_PROFILE = "Users/" + facebookID;
        sRef.child(My_PROFILE).addValueEventListener(listener);
        return listener;
    }

    public static void stop(ProfileListener listener){
        sRef.child(My_PROFILE).removeEventListener(listener);
    }


    public static class ProfileListener implements ValueEventListener {
        private ProfileCallbacks callbacks;
        private Map<String, String> myProfile = new HashMap<>();
        private Map<String, String> myPosts = new HashMap<>();
        private Map<Integer, String> myNameFriends = new HashMap<> ();
        private Map<Integer, String> myFacebookIDFriends = new HashMap<> ();
        private Map<Integer, String> myPictureFriend = new HashMap<> ();
        private Map<Integer, String> myOnMapFriend = new HashMap<>();
        private Map<Integer, String> myRequestsFriend = new HashMap<>();


        ProfileListener(ProfileCallbacks callbacks){
            this.callbacks = callbacks;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount() > 0) {
                clearAll();
                Map<String, Object> mapObj = (Map<String, Object>) dataSnapshot.getValue();
                assert mapObj != null;
                if (mapObj.containsKey("Birthday")) {
                    myProfile.put("Birthday", mapObj.get("Birthday").toString());
                }

                if (mapObj.containsKey("Gender")) {
                    myProfile.put("Gender", mapObj.get("Gender").toString());
                }

                if (mapObj.containsKey("Link")) {
                    myProfile.put("Link", mapObj.get("Link").toString());
                }

                if (mapObj.containsKey("Address")) {
                    myProfile.put("Address", mapObj.get("Address").toString());
                }

                if (mapObj.containsKey("Name")) {
                    myProfile.put("Name", mapObj.get("Name").toString());
                }

                if (mapObj.containsKey("Phone")) {
                    myProfile.put("Phone", mapObj.get("Phone").toString());
                }

                if (mapObj.containsKey("Picture")) {
                    myProfile.put("Picture", mapObj.get("Picture").toString());
                }

                if (mapObj.containsKey("Cover")) {
                    myProfile.put("Cover", mapObj.get("Cover").toString());
                }

                if (mapObj.containsKey("Email")) {
                    myProfile.put("Email", mapObj.get("Email").toString());
                }

                if (mapObj.containsKey("UserID")) {
                    myProfile.put("UserID", mapObj.get("UserID").toString());
                }

                if(mapObj.containsKey("DeviceID")){
                    myProfile.put("DeviceID", mapObj.get("DeviceID").toString());
                }

                if(mapObj.containsKey("DeviceName")){
                    myProfile.put("DeviceName", mapObj.get("DeviceName").toString());
                }

                if(mapObj.containsKey("Token")){
                    myProfile.put("Token", mapObj.get("Token").toString());
                }

                if(mapObj.containsKey("OnMap")){
                    myProfile.put("OnMap", mapObj.get("OnMap").toString());
                }

                if(mapObj.containsKey("LatLng")){
                    String latlng = mapObj.get("LatLng").toString();
                    String[] location = latlng.split(",");
                    myProfile.put("Latitude", location[0]);
                    myProfile.put("Longitude", location[1]);
                }

                if(mapObj.containsKey("Code")){
                    myProfile.put("Code", mapObj.get("Code").toString());
                }

                //get my latest post
                if(mapObj.containsKey("LastPost")) {
                    try {
                        Map<String, Object> mapObjPost = (Map<String, Object>) mapObj.get("LastPost");
                        if (mapObjPost.containsKey("Message")) {
                            myPosts.put("Message", mapObjPost.get("Message").toString());
                        } else {
                            myPosts.put("Message", "null");
                        }

                        if (mapObjPost.containsKey("Feeling")) {
                            myPosts.put("Feeling", mapObjPost.get("Feeling").toString());
                        } else {
                            myPosts.put("Feeling", "null");
                        }

                        if (mapObjPost.containsKey("Image")) {
                            myPosts.put("Image", mapObjPost.get("Image").toString());
                        } else {
                            myPosts.put("Image", "null");
                        }

                        if (mapObjPost.containsKey("Video")) {
                            myPosts.put("Video", mapObjPost.get("Video").toString());
                        } else {
                            myPosts.put("Video", "null");
                        }

                        if (mapObjPost.containsKey("CheckInName")) {
                            myPosts.put("CheckInName", mapObjPost.get("CheckInName").toString());
                        } else {
                            myPosts.put("CheckInName", "null");
                        }

                        if (mapObjPost.containsKey("Created_time")) {
                            myPosts.put("Created_time", mapObjPost.get("Created_time").toString());
                        } else {
                            myPosts.put("Created_time", "null");
                        }

                        if (mapObjPost.containsKey("ID")) {
                            myPosts.put("ID", mapObjPost.get("ID").toString());
                        } else {
                            myPosts.put("ID", "null");
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }
                    }else{
                        myPosts = null;
                    }


                //get my friends
                if(mapObj.containsKey("Friends")){
                    myNameFriends.clear();
                    myFacebookIDFriends.clear();
                    myPictureFriend.clear();
                    myOnMapFriend.clear();

                    Map<String, Object> objFriends = (Map<String, Object>) mapObj.get("Friends");
                    int count = -1;
                    for(String key : objFriends.keySet()){
                        count++;
                        Map<String, Object> objFriends2 = (Map<String, Object>) objFriends.get(key);
                        if(objFriends2.containsKey("Name")) {
                            myNameFriends.put(count, objFriends2.get("Name").toString());
                        }
                        if(objFriends2.containsKey("ID")) {
                            myFacebookIDFriends.put(count, objFriends2.get("ID").toString());
                        }
                        if(objFriends2.containsKey("Picture")) {
                            myPictureFriend.put(count, objFriends2.get("Picture").toString());
                        }
                        if(objFriends2.containsKey("OnMap")) {
                            myOnMapFriend.put(count, objFriends2.get("OnMap").toString());
                        }else{
                            myOnMapFriend.put(count, "null");
                        }

                        if(objFriends2.containsKey("Requests")) {
                            myRequestsFriend.put(count, objFriends2.get("Requests").toString());
                        }else{
                            myRequestsFriend.put(count, "null");
                        }
                    }
                }

                ProfileValue profile = new ProfileValue();
                profile.setMyProfile(myProfile, mContext);
                profile.setMyPosts(myPosts);
                profile.setMyNameFriends(myNameFriends);
                profile.setMyFacebookIDFriends(myFacebookIDFriends);
                profile.setMyPictureFriend(myPictureFriend);
                profile.setMyOnMapFriend(myOnMapFriend);
                profile.setMyRequestsFriend(myRequestsFriend);

                if(callbacks != null){
                    callbacks.onProfileChange(profile);
                }

            }else{
                //is not value at database
                if(callbacks != null){
                    callbacks.onProfileChange(null);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

        private void clearAll() {
            myProfile.clear();
        }
    }

    public interface ProfileCallbacks{
        public void onProfileChange(ProfileValue profileValue);
    }

}

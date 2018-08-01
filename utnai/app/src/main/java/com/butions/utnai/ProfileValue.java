package com.butions.utnai;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 03-Oct-17.
 */

public class ProfileValue {

    private String TAG = "ProfileValue";
    private Map<String, String> myProfile = new HashMap<>();
    private Map<String, String> myPosts = new HashMap<>();
    private Map<Integer, String> myNameFriends = new HashMap<> ();
    private Map<Integer, String> myFacebookIDFriends = new HashMap<> ();
    private Map<Integer, String> myPictureFriend = new HashMap<> ();
    private Map<Integer, String> myOnMapFriend = new HashMap<>();
    private Map<Integer, String> myRequestsFriend = new HashMap<>();

    public Map<String, String> getMyProfile() {
        return myProfile;
    }

    public void setMyProfile(Map<String, String> myProfile , Context context) {
        this.myProfile = myProfile;

        SharedPreferences sp = context.getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Birthday", myProfile.get("Birthday"));
        editor.putString("Gender", myProfile.get("Gender"));
        editor.putString("Link", myProfile.get("Link"));
        editor.putString("Address", myProfile.get("Address"));
        editor.putString("Name", myProfile.get("Name"));
        editor.putString("Phone", myProfile.get("Phone"));
        editor.putString("Picture", myProfile.get("Picture"));
        editor.putString("Cover", myProfile.get("Cover"));
        editor.putString("Email", myProfile.get("Email"));
        editor.putString("FacebookID", myProfile.get("FacebookID"));
        editor.putString("DeviceID", myProfile.get("DeviceID"));
        editor.putString("DeviceName", myProfile.get("DeviceName"));
        editor.putString("Token", myProfile.get("Token"));
        editor.putString("OnMap", myProfile.get("OnMap"));
        editor.putString("Latitude", myProfile.get("Latitude"));
        editor.putString("Longitude", myProfile.get("Longitude"));
        editor.putString("Code", myProfile.get("Code"));
        editor.apply();
    }

    public Map<String, String> getMyPosts() {
        return myPosts;
    }

    public void setMyPosts(Map<String, String> myPosts) {
        this.myPosts = myPosts;
    }

    public Map<Integer, String> getMyNameFriends() {
        return myNameFriends;
    }

    public void setMyNameFriends(Map<Integer, String> myNameFriends) {
        this.myNameFriends = myNameFriends;
    }

    public Map<Integer, String> getMyFacebookIDFriends() {
        return myFacebookIDFriends;
    }

    public void setMyFacebookIDFriends(Map<Integer, String> myFacebookIDFriends) {
        this.myFacebookIDFriends = myFacebookIDFriends;
    }

    public Map<Integer, String> getMyPictureFriend() {
        return myPictureFriend;
    }

    public void setMyPictureFriend(Map<Integer, String> myPictureFriend) {
        this.myPictureFriend = myPictureFriend;
    }

    public Map<Integer, String> getMyOnMapFriend() {
        return myOnMapFriend;
    }

    public void setMyOnMapFriend(Map<Integer, String> myOnMapFriend) {
        this.myOnMapFriend = myOnMapFriend;
    }

    public Map<Integer, String> getMyRequestsFriend() {
        return myRequestsFriend;
    }

    public void setMyRequestsFriend(Map<Integer, String> myRequestsFriend) {
        this.myRequestsFriend = myRequestsFriend;
    }
}

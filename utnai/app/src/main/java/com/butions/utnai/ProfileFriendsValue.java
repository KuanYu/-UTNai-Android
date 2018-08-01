package com.butions.utnai;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 03-Oct-17.
 */

public class ProfileFriendsValue {

    private String TAG = "FriendsValue";
    private Map<String, String> myFriends = new HashMap<>();
    private Map<String, String> myPostsFriends = new HashMap<>();
    private Map<String, String> myChatsFriends = new HashMap<>();

    public Map<String, String> getMyFriends() {
        return myFriends;
    }

    public void setMyFriends(Map<String, String> myFriends) {
        this.myFriends = myFriends;
    }

    public Map<String, String> getMyPostsFriends() {
        return myPostsFriends;
    }

    public void setMyPostsFriends(Map<String, String> myPostsFriends) {
        this.myPostsFriends = myPostsFriends;
    }

    public Map<String, String> getMyChatsFriends() {
        return myChatsFriends;
    }

    public void setMyChatsFriends(Map<String, String> myChatsFriends) {
        this.myChatsFriends = myChatsFriends;
    }
}

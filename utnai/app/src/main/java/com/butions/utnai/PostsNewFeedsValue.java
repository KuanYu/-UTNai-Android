package com.butions.utnai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 29-Sep-17.
 */

public class PostsNewFeedsValue {
    private String TAG = "PostsNewFeedsValue";
    private ArrayList<String> Id = new ArrayList<>();
    private Map<String, ArrayList<String>> Message = new HashMap<>();
    private Map<String, ArrayList<String>> Feeling = new HashMap<>();
    private Map<String, ArrayList<String>> Image = new HashMap<>();
    private Map<String, ArrayList<String>> Video = new HashMap<>();
    private Map<String, ArrayList<String>> CheckIn = new HashMap<>();
    private Map<String, ArrayList<String>> Longitude = new HashMap<>();
    private Map<String, ArrayList<String>> Latitude = new HashMap<>();
    private Map<String, ArrayList<String>> Time = new HashMap<>();
    private Map<String, ArrayList<Integer>> CountLike = new HashMap<>();
    private Map<String, ArrayList<String>> Likes = new HashMap<>();
    private Map<String, ArrayList<Integer>> CountComment = new HashMap<>();


    public ArrayList<String> getId() {
        return Id;
    }

    public void setId(ArrayList<String> id) {
        Id = id;
    }

    public Map<String, ArrayList<String>> getMessage() {
        return Message;
    }

    public void setMessage(Map<String, ArrayList<String>> message) {
        Message = message;
    }

    public Map<String, ArrayList<String>> getFeeling() {
        return Feeling;
    }

    public void setFeeling(Map<String, ArrayList<String>> feeling) {
        Feeling = feeling;
    }

    public Map<String, ArrayList<String>> getImage() {
        return Image;
    }

    public void setImage(Map<String, ArrayList<String>> image) {
        Image = image;
    }

    public Map<String, ArrayList<String>> getVideo() {
        return Video;
    }

    public void setVideo(Map<String, ArrayList<String>> video) {
        Video = video;
    }

    public Map<String, ArrayList<String>> getCheckIn() {
        return CheckIn;
    }

    public void setCheckIn(Map<String, ArrayList<String>> checkIn) {
        CheckIn = checkIn;
    }

    public Map<String, ArrayList<String>> getLongitude() {
        return Longitude;
    }

    public void setLongitude(Map<String, ArrayList<String>> longitude) {
        Longitude = longitude;
    }

    public Map<String, ArrayList<String>> getLatitude() {
        return Latitude;
    }

    public void setLatitude(Map<String, ArrayList<String>> latitude) {
        Latitude = latitude;
    }

    public Map<String, ArrayList<String>> getTime() {
        return Time;
    }

    public void setTime(Map<String, ArrayList<String>> time) {
        Time = time;
    }

    public Map<String, ArrayList<Integer>> getCountLike() {
        return CountLike;
    }

    public void setCountLike(Map<String, ArrayList<Integer>> countLike) {
        CountLike = countLike;
    }

    public Map<String, ArrayList<String>> getLikes() {
        return Likes;
    }

    public void setLikes(Map<String, ArrayList<String>> likes) {
        Likes = likes;
    }

    public Map<String, ArrayList<Integer>> getCountComment() {
        return CountComment;
    }

    public void setCountComment(Map<String, ArrayList<Integer>> countComment) {
        CountComment = countComment;
    }
}

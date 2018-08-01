package com.butions.utnai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 29-Sep-17.
 */

public class PostsValue {
    private String TAG = "PostsValue";
    private ArrayList<String> Id = new ArrayList<String>();
    private Map<Integer, String> Message = new HashMap<Integer, String>();
    private Map<Integer, String> CheckIn = new HashMap<Integer, String>();
    private Map<Integer, String> Latitude = new HashMap<Integer, String>();
    private Map<Integer, String> Longitude = new HashMap<Integer, String>();
    private Map<Integer, String> Feeling = new HashMap<Integer, String>();
    private Map<Integer, String> Image = new HashMap<Integer, String>();
    private Map<Integer, String> Video = new HashMap<Integer, String>();
    private Map<Integer, String> Time = new HashMap<Integer, String>();
    private Map<Integer, Integer> CountLike = new HashMap<Integer, Integer>();
    private Map<Integer, String>  Likes = new HashMap<Integer, String>();
    private Map<Integer, Integer> Comment = new HashMap<Integer, Integer>();
    public static List<Object> ArrayIndex = new ArrayList<Object>();

    public void setCheckIn(Map<Integer, String> checkIn){
        CheckIn = checkIn;
    }
    public Map<Integer, String> getCheckIn(){
        return CheckIn;
    }



    public void setComment(Map<Integer, Integer> comment) {
        Comment = comment;
    }
    public Map<Integer, Integer> getComment() {
        return Comment;
    }


    public void setCountLike( Map<Integer, Integer> countLike) {
        CountLike = countLike;

    }
    public  Map<Integer, Integer> getCountLike() {
        return CountLike;
    }



    public void setLikes(Map<Integer, String> likes) {
        Likes = likes;

    }
    public Map<Integer, String> getLikes() {
        return Likes;
    }


    public void setTime(Map<Integer, String> time) {
        Time = time;
    }
    public Map<Integer, String> getTime() {
        return Time;
    }



    public void setId(ArrayList<String> id) {
        Id = id;
    }
    public ArrayList<String> getId() {
        return Id;
    }



    public void setLatitude(Map<Integer, String> latitude) {
        Latitude = latitude;
    }
    public Map<Integer, String> getLatitude() {
        return Latitude;
    }



    public void setLongitude(Map<Integer, String> longitude) {
        Longitude = longitude;

    }
    public Map<Integer, String> getLongitude() {
        return Longitude;
    }




    public void setMessage(Map<Integer, String> message) {
        Message = message;
    }
    public Map<Integer, String> getMessage() {
        return Message;
    }




    public void setFeeling(Map<Integer, String> feeling) {
            Feeling = feeling;
    }
    public Map<Integer, String> getFeeling() {
        return Feeling;
    }




    public void setVideo(Map<Integer, String> video) {
         Video = video;
    }
    public Map<Integer, String> getVideo() {
        return Video;
    }




    public void setImage(Map<Integer, String> image) {
        Image = image;
    }
    public Map<Integer, String> getImage() {
        return Image;
    }




    public void setArrayIndex(List<Object> arrayIndex){
        ArrayIndex = arrayIndex;
    }
    public List<Object> getArrayIndex(){
        return ArrayIndex;
    }

}

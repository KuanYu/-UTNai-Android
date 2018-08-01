package com.butions.utnai;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Chalitta Khampachua on 09-Nov-16.
 */
public class Message {
    private String TAG = "Message";
    private String mText;
    private String mSender;
    private String mDate;
    private String mRead;
    private String mReadFriend;
    private String mType;
    private String mImage;
    private String mVideo;

    public void setImageProfile(String image){
        mImage = image;
    }
    public String getImageProfile(){
        return mImage;
    }



    public void setType(String type){
        mType = type;
    }
    public String getType(){
        return mType;
    }



    public void setRead(String read){
        mRead = read;
        Log.d(TAG, "mRead : " + mRead);
    }
    public String getRead(){
        return mRead;
    }


    public String getDate() {
        return mDate;
    }
    public void setDate(String date) {
        long timestamp = Long.valueOf(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+'SSS", Locale.ENGLISH);
        String pattern = dateFormat.format(timestamp);
        mDate = CalendarTime.getInstance().getTimeAgo(pattern);
    }//convert time gmt to local time



    public String getVideo() {
        return mVideo;
    }
    public void setVideo(String video) {
        mVideo = video;
    }



    public String getText() {
        return mText;
    }
    public void setText(String text) {
        mText = text;
    }



    public String getSender() {
        return mSender;
    }
    public void setSender(String sender) {
        mSender = sender;
    }
}


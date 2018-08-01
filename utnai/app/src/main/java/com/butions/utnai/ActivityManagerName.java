package com.butions.utnai;

import android.content.Context;
import android.util.Log;

/**
 * Created by Chalitta Khampachua on 10-Feb-17.
 * This is get class name is current process foreground for send notificaton popup
 * GoSpotting Class use setProcessNameClass method such as com.totoit.gospotting.Home@5846b86
 * So that getProcessNameClass method was applied in MyFcmListenerService class
 */
public class ActivityManagerName {
    private String TAG = "ActivityManagerName";
    public static Context isRunningForeground;
    public static String isNameClass;
    public static String isChatRoom;
    private static String currentPage;

    public void setProcessNameClass(Context nameClass){
        isRunningForeground = nameClass;
        //Log.d(TAG, "isRunningForeground :" + isRunningForeground);
    }

    public Context getProcessNameClass(){
        //Log.d(TAG, "isRunningForeground :" + isRunningForeground);
        return isRunningForeground;
    }

    public void setNameClassInProcessString(String nameClassInProcessString){
        isNameClass = nameClassInProcessString;
        //Log.d(TAG, "isNameClass :" + isNameClass);
    }

    public String getNameClassInProcessString(){
        //Log.d(TAG, "isNameClass :" + isNameClass);
        return isNameClass;
    }


    public void setChatRoomName(String chatRoomName){
        isChatRoom = chatRoomName;
    }


    public String getChatRoomName(){
        Log.d(TAG, "isChatName : " + isChatRoom);
        return isChatRoom;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String isCurrentPage) {
        currentPage = isCurrentPage;
    }
}

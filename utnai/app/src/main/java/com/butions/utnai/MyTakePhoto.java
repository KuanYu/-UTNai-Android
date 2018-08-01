package com.butions.utnai;

import android.net.Uri;

import java.io.File;

/**
 * Created by Chalitta Khampachua on 15-Aug-17.
 */

public class MyTakePhoto {

    private static String pathImage;
    private static Uri pathVideo;
    private static File pathVideoFile;
    private static File pathImageFromVideoFile;
    private static boolean openCamera;
    private String TAG = "MyTakePhoto";
    private static MyTakePhoto mInstance = null;

    protected MyTakePhoto(){
    }
    public static synchronized MyTakePhoto getInstance(){
        if(null == mInstance){
            mInstance = new MyTakePhoto();
        }
        return mInstance;
    }

    public void setPathImage(String paths){
        pathImage = paths;
    }

    public String getPathImage(){
        return pathImage;
    }

    public void setIsOpenCamera(boolean isOpenCamera){
        openCamera = isOpenCamera;
    }

    public boolean getIsOpenCamera(){
        return openCamera;
    }


    public void setPathVideo(Uri paths){
        pathVideo = paths;
    }

    public Uri getPathVideo(){
        return pathVideo;
    }

    public void setPathVideoFile(File pathsFile){
        pathVideoFile = pathsFile;
    }

    public File getPathVideoFile(){
        return pathVideoFile;
    }

    public void setPathImageFromVideoFile(File pathImage){
        pathImageFromVideoFile = pathImage;
    }

    public File getPathImageFromVideoFile(){
        return pathImageFromVideoFile;
    }

}

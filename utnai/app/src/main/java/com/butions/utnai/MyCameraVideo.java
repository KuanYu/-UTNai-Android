package com.butions.utnai;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Chalitta Khampachua on 13-Mar-17.
 */
public class MyCameraVideo extends AppCompatActivity {

    private String TAG = "MyCameraVideo";
    private Context mContext;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final String URLVIDEO_STORAGE_KEY = "viewvideo";
    private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
    private File videoFile;
    private Uri videoUri;
    private String mCurrentPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        ActivityManagerName managerName = new ActivityManagerName();
        managerName.setProcessNameClass(this);
        managerName.setNameClassInProcessString("MyCameraVideo");

        MyTakePhoto.getInstance().setIsOpenCamera(true);

        try {
            dispatchTakeVideoIntent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "*** onSaveInstanceState ***");
        outState.putParcelable(URLVIDEO_STORAGE_KEY, videoUri);
        outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (videoUri != null) );
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "*** onRestoreInstanceState ***");
        videoUri = savedInstanceState.getParcelable(URLVIDEO_STORAGE_KEY);
    }

    private void dispatchTakeVideoIntent() throws IOException {

        Uri videoURI;
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.CAMERA , android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_VIDEO_CAPTURE);
            }
            else{

                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

                    videoFile = createVideoFile(mContext);
                    Log.d(TAG,"videoFile : " + videoFile);
                    MyTakePhoto.getInstance().setPathVideoFile(videoFile);
                    // Continue only if the File was successfully created
                    if (videoFile != null) {
                        videoURI = FileProvider.getUriForFile(MyCameraVideo.this, "com.butions.utnai.fileprovider", videoFile);
                        Log.d(TAG,"videoURI : " + videoURI);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                        MyCameraVideo.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
            }
        }
        else{
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                videoFile = createVideoFile(mContext);
                Log.d(TAG,"videoFile : " + videoFile);
                MyTakePhoto.getInstance().setPathVideoFile(videoFile);
                // Continue only if the File was successfully created
                if (videoFile != null) {
                    videoURI = Uri.fromFile(videoFile);
                    Log.d(TAG,"videoURI : " + videoURI);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                    MyCameraVideo.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "*** onActivityResult ***");
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = intent.getData();
            MyTakePhoto.getInstance().setPathVideo(videoUri);

            //get shot frame from video file
            String path = videoFile.toString();
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
            try {
                File fileImageFromVideo = createImageFromVideoFile(mContext);
                FileOutputStream fileOutput = new FileOutputStream(fileImageFromVideo);
                thumb.compress(Bitmap.CompressFormat.JPEG, 100, fileOutput);
                fileOutput.flush();
                fileOutput.close();
                MyTakePhoto.getInstance().setPathImageFromVideoFile(fileImageFromVideo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();

        }else{
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public File createVideoFile(Context mContext) throws IOException {
        // Create an image file name
        if(externalStorageAvailable()) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
            String imageFileName = "VDO_" + timeStamp + "_";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "*** sdk > M***");
                File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File videoFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".mp4",         /* suffix */
                        storageDir      /* directory */
                );
                // Save a file: path for use with ACTION_VIEW intents
                mCurrentPath = "file:" + videoFile.getAbsolutePath();
                return videoFile;
            }else{
                Log.d(TAG, "*** sdk < M***");
                File storageDir = Environment.getExternalStorageDirectory();
                File videoFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".mp4",         /* suffix */
                        storageDir      /* directory */
                );
                // Save a file: path for use with ACTION_VIEW intents
                mCurrentPath = "file:" + videoFile.getAbsolutePath();
                return videoFile;
            }
        }else{
            Log.d(TAG, "*** externalStorageAvailable() false ***");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
            String imageFileName = "VDO_" + timeStamp + "_";
            File storageDir = Environment.getExternalStorageDirectory();
            File videoFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".mp4",         /* suffix */
                    storageDir      /* directory */
            );
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPath = "file:" + videoFile.getAbsolutePath();
            return videoFile;
        }
    }

    private boolean externalStorageAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public File createImageFromVideoFile(Context mContext) throws IOException {
        // Create an image file name
        if(externalStorageAvailable()) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
            String imageFileName = "JPG_" + timeStamp + "_";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "*** sdk > M***");
                File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File imageFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
                return imageFile;

            }else{
                Log.d(TAG, "*** sdk < M***");
                File storageDir = Environment.getExternalStorageDirectory();
                File imageFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
                return imageFile;
            }
        }else{
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
            String imageFileName = "JPG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStorageDirectory();
            File imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            return imageFile;
        }
    }
}


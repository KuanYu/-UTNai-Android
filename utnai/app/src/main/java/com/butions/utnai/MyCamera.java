package com.butions.utnai;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyCamera extends AppCompatActivity {

    private String TAG = "MyCamera";
    private static final int ACTION_TAKE_PHOTO_S = 1;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private Bitmap mImageBitmap;
    private Bitmap bmp;
    private File photoFile;
    private String mCurrentPath;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        ActivityManagerName managerName = new ActivityManagerName();
        managerName.setProcessNameClass(this);
        managerName.setNameClassInProcessString("MyCamera");

        MyTakePhoto.getInstance().setIsOpenCamera(true);

        mImageBitmap = null;
        try {
            dispatchTakePictureIntent(ACTION_TAKE_PHOTO_S);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void dispatchTakePictureIntent(int actionCode) throws IOException {

        Uri photoURI;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE}, actionCode);
            }
            else{
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    photoFile = createImageFile(mContext);
                    Log.d(TAG,"photoFile : " + photoFile);
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(MyCamera.this, "com.butions.utnai.fileprovider", photoFile);
                        Log.d(TAG,"photoURI : " + photoURI);
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.ORIENTATION,90);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, actionCode);
                        MyCamera.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
            }
        }
        else{
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                photoFile = createImageFile(mContext);
                Log.d(TAG,"photoFile : " + photoFile);
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    photoURI = Uri.fromFile(photoFile);
                    Log.d(TAG,"photoURI : " + photoURI);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.ORIENTATION,90);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, actionCode);
                    MyCamera.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ACTION_TAKE_PHOTO_S && resultCode == RESULT_OK){

            if(mCurrentPath != null) {
                String mImgaeGetPath = photoFile.getPath();
                Log.d(TAG,"mImgaeGetPath : " + mImgaeGetPath);

                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeFile(mImgaeGetPath, bmpFactoryOptions);

                if(bmp == null){
                    Log.d(TAG, "-**bmp == null ***");
                }else{
                    Log.d(TAG, "-**bmp != null ***");
                    new ResizeImage().execute();
                }

            }
        }else{
            finish();
        }
    }

    private class ResizeImage extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String filePath = Uri.parse(new File(photoFile.getPath()).toString()).getPath();
            String fileName = photoFile.getName();
            try {
                String pathName = MyResizeImage.getInstance().OrientationAndResize(mContext, filePath, fileName, bmp);
                return pathName;

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(final String pathName) {
            super.onPostExecute(pathName);
            MyTakePhoto.getInstance().setPathImage(pathName);
            finish();
        }
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
    }


    public File createImageFile(Context mContext) throws IOException {
        Log.d(TAG, "*** createImageFile() ***");
        // Create an image file name
        if(externalStorageAvailable()) {
            Log.d(TAG, "*** externalStorageAvailable() true ***");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
            String imageFileName = "JPG_" + timeStamp + "_";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "*** sdk > M***");
                File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
                // Save a file: path for use with ACTION_VIEW intents
                mCurrentPath = "file:" + image.getAbsolutePath();
                return image;

            }else{
                Log.d(TAG, "*** sdk < M***");
                File storageDir = Environment.getExternalStorageDirectory();
                File image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
                // Save a file: path for use with ACTION_VIEW intents
                mCurrentPath = "file:" + image.getAbsolutePath();
                return image;
            }
        }else{
            Log.d(TAG, "*** externalStorageAvailable() false ***");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
            String imageFileName = "JPG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStorageDirectory();
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPath = "file:" + image.getAbsolutePath();
            return image;
        }
    }

    private boolean externalStorageAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

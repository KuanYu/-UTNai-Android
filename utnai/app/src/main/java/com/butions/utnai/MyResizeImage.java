package com.butions.utnai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Chalitta Khampachua on 06-Jun-17.
 */
public class MyResizeImage {

    private String TAG = "ResizeImage";
    private static MyResizeImage mInstance = null;

    protected MyResizeImage(){
    }
    public static synchronized MyResizeImage getInstance(){
        if(null == mInstance){
            mInstance = new MyResizeImage();
        }
        return mInstance;
    }

    public String OrientationAndResize(Context mContext, String filePath, String fileName, Bitmap thumbnail){
        try {
            final int MAX_IMAGE_SIZE = 640 * 1024; // max final file size in kilobytes
            Log.d(TAG, "*** file exists ***");
            // First decode with inJustDecodeBounds=true to check dimensions of image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            options.inSampleSize = calculateInSampleSize(options, 640, 640);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inPreferredConfig= Bitmap.Config.ARGB_8888;
            Bitmap bm = BitmapFactory.decodeFile(filePath, options);

            if (bm == null) {
                Log.d(TAG, "*** BitmapFactory  bm == null ***");
                if(thumbnail == null){
                    Log.d(TAG, "Return Old File Path");
                    return filePath;
                }else {
                    bm = thumbnail;
                }

            } else {
                Log.d(TAG, "Height bm :" + String.valueOf(bm.getHeight()));
                Log.d(TAG, "Width bm :" + String.valueOf(bm.getWidth()));
            }

            ExifInterface exif = null;
            try {
                Log.d(TAG, "*** ExifInterface ***");
                exif = new ExifInterface(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            Log.d(TAG, "*** bm is getWidth and getHeight ***");
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            Bitmap newBitmap = Bitmap.createBitmap(bm, 0, 0, options.outWidth, options.outHeight, matrix, true);

            Log.d(TAG, "*** resize ***");
            int compressQuality = 100; // quality decreasing by 5 every loop.
            int streamLength;
            do{
                ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
                Log.d("compressBitmap", "Quality: " + compressQuality);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
                byte[] bmpPicByteArray = bmpStream.toByteArray();
                streamLength = bmpPicByteArray.length;
                compressQuality -= 5;
                Log.d("compressBitmap", "Size: " + streamLength/1024+" kb");
            }while (streamLength >= MAX_IMAGE_SIZE);

            try {
                //save the resized and compressed file to disk cache
                Log.d("compressBitmap","cacheDir: "+mContext.getCacheDir());
                FileOutputStream bmpFile = new FileOutputStream(mContext.getCacheDir()+fileName);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
                bmpFile.flush();
                bmpFile.close();
            } catch (Exception e) {
                Log.e("compressBitmap", "Error on saving file");
            }
            //return the path of resized and compressed file
            return  mContext.getCacheDir()+fileName;

        } catch (Exception e) {
            Log.d(TAG, "Return Old File Path " + e.getMessage());
            return filePath;
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(TAG,"image height: "+height+ " , image width: "+ width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(TAG,"inSampleSize: "+inSampleSize);
        return inSampleSize;
    }
}

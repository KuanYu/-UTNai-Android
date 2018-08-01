package com.butions.utnai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

/**
 * Created by Chalitta Khampachua on 01-Aug-17.
 */

public class DrawableMarker {

    private static DrawableMarker mInstance = null;
    private Context context;
    private String FacebookID;

    protected DrawableMarker(){

    }

    public static synchronized DrawableMarker getInstance(){
        if(null == mInstance){
            mInstance = new DrawableMarker();
        }
        return mInstance;
    }

    public Bitmap drawableFromUrl (Bitmap bitmap, Context context){
        Canvas canvas;
        int radius = (int)(80 * Config.getInstance().getDisplayDensity(context));
        int stroke = 6;
        Bitmap bmp = null;

        try {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            bmp = Bitmap.createBitmap((int) radius, (int) radius + 25, conf);
            canvas = new Canvas(bmp);

            // creates a centered bitmap of the desired size
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, (int) radius - stroke, (int) radius - stroke, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
//            paint.setColor(0xFFAB2933);  //red
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(0, 0, 0, 90, Color.argb(255,249,50,163), Color.argb(255,196,101,173), Shader.TileMode.MIRROR));

            // the triangle laid under the circle
            int pointedness = 20;
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(radius / 2, radius + 15);
            path.lineTo(radius / 2 + pointedness, radius - 10);
            path.lineTo(radius / 2 - pointedness, radius - 10);
            canvas.drawPath(path, paint);

            // color circle background
            RectF rect = new RectF(0, 0, radius, radius);
            canvas.drawRoundRect(rect, radius / 2, radius / 2, paint);

            // circle photo
            paint.setShader(shader);
            rect = new RectF(stroke, stroke, radius - stroke, radius - stroke);
            canvas.drawRoundRect(rect, (radius - stroke) / 2, (radius - stroke) / 2, paint);

//                //image circle
//                RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
//                roundDrawable.setCircular(true);
//                drawable = roundDrawable;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public Bitmap resizeMapIcons(int iconRes, Context context, int size){
        int w,h;
        float density = 0;
        Bitmap resizedBitmap = null;
        try {
            density = context.getResources().getDisplayMetrics().densityDpi;
            Bitmap imageBitmap = getBitmap(iconRes, context);
            float m = 1;
            if(density == 2.0){   //default => 2 , density/2;  //320dpi
                m = 1;
            }else if(density ==  1.0){  //160dpi
                m = (float) 0.5;
            }
            else if(density ==  0.75){  //120dpi
                m = (float) 0.375;
            }
            else if(density ==  1.5){   //240dpi
                m = (float) 0.75;
            }
            else if(density ==  3.0){   //480dpi
                m = (float) 1.5;
            }
            else if(density ==  4.0){   //640dpi
                m = (float) 2;
            }
            w = (int) (imageBitmap.getWidth() *m)/size;
            h = (int) (imageBitmap.getHeight() *m)/size;

            resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, w, h, false);
        }catch (Exception e){
            e.getMessage();

        }
        return resizedBitmap;
    }

    private Bitmap getBitmap(int drawableRes, Context context) {
        Drawable drawable = context.getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}

package com.butions.utnai;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import java.lang.reflect.Field;

/**
 * Created by Chalitta Khampachua on 21-Jul-17.
 */

public class Config {

    private static Config mInstance = null;

    protected Config(){

    }

    public static synchronized Config getInstance(){
        if(null == mInstance){
            mInstance = new Config();
        }
        return mInstance;
    }

    public static final String YOUTUBE_API_KEY = "AIzaSyAv5ir-HHRShCWousULjD-kRy838Uer7JY";

    public  void setDefaultFont(Context context){
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/ntailu.ttf");
        replaceFont("MONOSPACE", font);
    }

    protected void replaceFont(String typeFieldName, Typeface typeface){
        try {
            final Field staticField = Typeface.class.getDeclaredField(typeFieldName);
            staticField.setAccessible(true);

            staticField.set(null, typeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public float getDisplayDensity(Context context){
        float density = context.getResources().getDisplayMetrics().density;
        //Log.d(TAG, "size image density : " + density);
        float m = 1;
        if(density == 2.0){   //default => 2 , density/2; //320dpi
            m = 1;
            return m;
        }else if(density ==  1.0){  //160dpi
            m = (float) 0.5;
            return m;
        }
        else if(density ==  0.75){  //120dpi
            m = (float) 0.375;
            return m;
        }
        else if(density ==  1.5){   //240dpi
            m = (float) 0.75;
            return m;
        }
        else if(density ==  3.0){   //480dpi
            m = (float) 1.5;
            return m;
        }
        else if(density ==  4.0){   //640dpi
            m = (float) 2;
            return m;
        }
        return m;
    }

    public Typeface getDefaultFont(Context context){
        Typeface ntailu_font = Typeface.createFromAsset(context.getAssets(), "fonts/ntailu.ttf");
        return ntailu_font;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}

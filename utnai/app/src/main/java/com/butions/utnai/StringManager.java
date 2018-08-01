package com.butions.utnai;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 10-Oct-17.
 */

public class StringManager {
    private String TAG = "StringManager";
    private static StringManager mInstance = null;
    private static String mLanguageCode = "en-us";

    private static Map<String, Map<String, Object>> mLanguageMap = new HashMap<>();

    public StringManager() {
    }

    public static StringManager getsInstance(){
        if(mInstance == null){
            synchronized (StringManager.class){
                if(mInstance == null){
                    mInstance = new StringManager();
                }
            }
        }
        return mInstance;
    }

    public void addLanguage(String languageCode, Map<String, Object> json){
        mLanguageCode = languageCode;
        mLanguageMap.put(languageCode, json);
    }

    public String getString(String key){
        String getKey = null;
        Map<String, Object> jsonObject;
        try {
            jsonObject = mLanguageMap.get(mLanguageCode);
            getKey = jsonObject.get(key).toString();

        } catch (Exception e) {
            Log.d(TAG, "error get String : " + e.getMessage());
            e.printStackTrace();
        }
        return getKey;
    }
}

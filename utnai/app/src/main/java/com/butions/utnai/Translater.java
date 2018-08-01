package com.butions.utnai;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 10-Oct-17.
 */

public class Translater {

    private String TAG = "MyLanguage";
    private static Translater mInstance = null;
    private  Map<String, Object> mapObjLang = new HashMap<>();

    protected Translater(){

    }

    public static synchronized Translater getInstance(){
        if(null == mInstance){
            mInstance = new Translater();
        }
        return mInstance;
    }

    public void setLanguages(Context context){
        String local;
        SharedPreferences sp_language_code = context.getSharedPreferences("LANGUAGECODE", Context.MODE_PRIVATE);
        String languageCode =  sp_language_code.getString("LanguageCode", "-1");
        Log.d(TAG,"output Local : " + languageCode);
        if(languageCode.equals("-1")){
            local = "en-us";
        }else{
            local = languageCode;
        }

        SharedPreferences sp = context.getSharedPreferences("MYLANGUAGE", Context.MODE_PRIVATE);
        String stringObject = sp.getString(local, "-1");
        Log.d(TAG, "stringObject : " + stringObject);

        if(!stringObject.equals("-1")) {
            String str = stringObject.substring(1, stringObject.length() - 1);
            String[] pairValue = str.split(",");

            for (String pair : pairValue) {
                String[] entry = pair.split("=");
                mapObjLang.put(entry[0].trim(), entry[1]);
            }

            StringManager.getsInstance().addLanguage(local, mapObjLang);
        }
    }
}

package com.butions.utnai;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Chalitta Khampachua on 09-Nov-16.
 */
public class MyLanguageFirebase {
    private static final DatabaseReference sRef = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "MyLanguageFirebase";
    private static Context mContext;
    private static String code_language;
    private static String MY_LANGUAGE;

    public static LanguageListener addLanguageListener(String languages ,String code, LanguageCallbacks callbacks , Context context){
        mContext = context;
        code_language = code;
        String languages_replace = languages.replace("ภาษาไทย", "ไทย");
        LanguageListener listener = new LanguageListener(callbacks);
        MY_LANGUAGE = "Languages/" + languages_replace;
        sRef.child(MY_LANGUAGE).addListenerForSingleValueEvent(listener);
        return listener;
    }

    public static void stop(LanguageListener listener){
        sRef.child(MY_LANGUAGE).removeEventListener(listener);
    }

    public static class LanguageListener implements ValueEventListener {
        private LanguageCallbacks callbacks;

        LanguageListener(LanguageCallbacks callbacks){
            this.callbacks = callbacks;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Map<String, Object> mapObjLang = (Map<String, Object>) dataSnapshot.getValue();

            SharedPreferences sp = mContext.getSharedPreferences("MYLANGUAGE", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(code_language, mapObjLang.toString());
            editor.apply();

            SharedPreferences sp_language_code = mContext.getSharedPreferences("LANGUAGECODE", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor_language_code = sp_language_code.edit();
            editor_language_code.putString("LanguageCode", code_language);
            editor_language_code.apply();

            if(callbacks != null){
                callbacks.onLanguageChange();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public interface LanguageCallbacks{
        void onLanguageChange();
    }

}

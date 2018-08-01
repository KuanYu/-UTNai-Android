package com.butions.utnai;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 09-Nov-16.
 */
public class EmojiFirebase {
    private static final DatabaseReference sRef = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "EmojiFirebase";
    private static String NAME = "en-us";
    private static String EMOTIONS ="Emotions";

    public static EmojiListener addEmojiListener(final EmojiCallbacks callbacks, Context context) {

        SharedPreferences sp_language_code = context.getSharedPreferences("LANGUAGECODE", Context.MODE_PRIVATE);
        String languageCode =  sp_language_code.getString("LanguageCode", "-1");
        Log.d(TAG, "local name : " + languageCode);
        if(languageCode.equals("-1")){
            NAME = "en-us";
        }else{
            NAME = languageCode;
        }


        EmojiListener listener = new EmojiListener(callbacks);
        sRef.child(EMOTIONS).addValueEventListener(listener);
        return listener;
    }


    public static void stop(EmojiListener listener) {
        sRef.child(EMOTIONS).removeEventListener(listener);
    }

    public static class EmojiListener implements ValueEventListener {
        private EmojiCallbacks callbacks;
        private ArrayList<String> EmojiImage = new ArrayList<String>();
        private ArrayList<String> EmojiName = new ArrayList<String>();
        private ArrayList<String> EmojiKey = new ArrayList<String>();

        EmojiListener(EmojiCallbacks callbacks) {
            this.callbacks = callbacks;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getChildrenCount() != 0) {
                clearAll();
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Map<String, Object> mapObj = (Map<String, Object>) objDataSnapshot.getValue();
                        EmojiKey.add(objDataSnapshot.getKey());
                        EmojiImage.add(mapObj.get("Image").toString());
                        EmojiName.add(mapObj.get(NAME).toString());
                    } catch (Exception e) {
                        Log.d(TAG, "error load emoji : " + e.getMessage());
                        if (callbacks != null) {
                            callbacks.onEmojiChange(null);
                        }
                    }
                }
            }

            EmojiValue emoji = new EmojiValue();
            emoji.setEmojiImage(EmojiImage);
            emoji.setEmojiName(EmojiName);
            emoji.setEmojiKey(EmojiKey);

            if (callbacks != null) {
                callbacks.onEmojiChange(emoji);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

        private void clearAll() {
            EmojiImage.clear();
            EmojiName.clear();
        }
    }

    public interface EmojiCallbacks {
        public void onEmojiChange(EmojiValue emojiValue);
    }

}

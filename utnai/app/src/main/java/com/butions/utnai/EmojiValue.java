package com.butions.utnai;

import java.util.ArrayList;

/**
 * Created by Chalitta Khampachua on 02-Oct-17.
 */

public class EmojiValue {

    private  ArrayList<String> EmojiImage = new ArrayList<String>();
    private  ArrayList<String> EmojiName = new ArrayList<String>();
    private  ArrayList<String> EmojiKey = new ArrayList<String>();

    public void setEmojiImage(ArrayList<String> emojiImage) {
        EmojiImage = emojiImage;
    }

    public ArrayList<String> getEmojiImage() {
        return EmojiImage;
    }


    public void setEmojiName(ArrayList<String> emojiName) {
        EmojiName = emojiName;
    }

    public ArrayList<String> getEmojiName() {
        return EmojiName;
    }

    public ArrayList<String> getEmojiKey() {
        return EmojiKey;
    }

    public void setEmojiKey(ArrayList<String> emojiKey) {
        EmojiKey = emojiKey;
    }
}

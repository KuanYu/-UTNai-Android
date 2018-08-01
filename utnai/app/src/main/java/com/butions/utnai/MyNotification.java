package com.butions.utnai;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyNotification extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "MyNotification";
    private ImageView btnBack;
    private LinearLayout popup_notificaion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_notification);

        TextView btnPopup = (TextView) findViewById(R.id.btnPopup);
        btnPopup.setTypeface(Config.getInstance().getDefaultFont(this));

        TextView btnPush = (TextView) findViewById(R.id.btnPush);
        btnPush.setTypeface(Config.getInstance().getDefaultFont(this));

        popup_notificaion = (LinearLayout) findViewById(R.id.popup_notificaion);

        TextView textNotification = (TextView) findViewById(R.id.textNotification);
        textNotification.setTypeface(Config.getInstance().getDefaultFont(this));

        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        final SwitchCompat switch_popup = (SwitchCompat) findViewById(R.id.switch_popup);
        switch_popup.setTypeface(Config.getInstance().getDefaultFont(this));

        final SwitchCompat switch_push = (SwitchCompat) findViewById(R.id.switch_push);
        switch_push.setTypeface(Config.getInstance().getDefaultFont(this));

        SharedPreferences sp = getSharedPreferences("NOTIFICATION", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        int status_popup = sp.getInt("popup", -1);
        int status_push = sp.getInt("push", -2);
        Log.d(TAG, "status_push : " + status_push);
        Log.d(TAG, "status_popup : " + status_popup);

        if(status_popup == 1){
            switch_popup.setChecked(true);
            switch_popup.setShowText(true);
        }else if(status_popup == 0 || status_popup == -1){
            switch_popup.setChecked(false);
            switch_popup.setShowText(true);
        }else if(status_push == 2){
            switch_push.setChecked(true);
            switch_push.setShowText(true);
        }else if(status_push == 3 || status_push == -2){
            switch_push.setChecked(false);
            switch_push.setShowText(true);
        }

        switch_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch_push.setShowText(true);
                switch_push.setChecked(b);

                if(b){
                    popup_notificaion.setVisibility(View.VISIBLE);
                    switch_popup.setClickable(true);
                    editor.putInt("push", 2);
                    editor.apply();
                }else{
                    switch_popup.setClickable(false);
                    switch_popup.setChecked(false);
                    popup_notificaion.setVisibility(View.GONE);
                    editor.putInt("popup", 0);  //switch_popup
                    editor.apply();

                    editor.putInt("push", 3);  //switch_push
                    editor.apply();
                }
            }
        });

        switch_popup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch_popup.setShowText(true);
                switch_popup.setChecked(b);

                if(b){
                    editor.putInt("popup", 1);
                    editor.apply();
                }else{
                    editor.putInt("popup", 0);
                    editor.apply();
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        if(view == btnBack){
            finish();
        }
    }
}

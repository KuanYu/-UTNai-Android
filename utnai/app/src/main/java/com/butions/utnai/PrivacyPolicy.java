package com.butions.utnai;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PrivacyPolicy extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy);
        ActivityManagerName managerName = new ActivityManagerName();
        managerName.setProcessNameClass(this);
        managerName.setNameClassInProcessString("PrivacyPolicy");

        TextView toolbar = (TextView) findViewById(R.id.toolbar);
        toolbar.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);

        TextView title1 = (TextView) findViewById(R.id.title1);
        TextView title2 = (TextView) findViewById(R.id.title2);
        TextView title3 = (TextView) findViewById(R.id.title3);
        TextView title4 = (TextView) findViewById(R.id.title4);
        TextView title5 = (TextView) findViewById(R.id.title5);
        TextView title6 = (TextView) findViewById(R.id.title6);
        TextView title7 = (TextView) findViewById(R.id.title7);
        TextView title8 = (TextView) findViewById(R.id.title8);
        TextView title9 = (TextView) findViewById(R.id.title9);
        TextView title10 = (TextView) findViewById(R.id.title10);
        TextView title11 = (TextView) findViewById(R.id.title11);
        TextView title12 = (TextView) findViewById(R.id.title12);
        TextView title13 = (TextView) findViewById(R.id.title13);
        TextView title14 = (TextView) findViewById(R.id.title14);
        TextView title15 = (TextView) findViewById(R.id.title15);
        TextView title16 = (TextView) findViewById(R.id.title16);
        TextView title17 = (TextView) findViewById(R.id.title17);
        TextView title18 = (TextView) findViewById(R.id.title18);
        TextView title19 = (TextView) findViewById(R.id.title19);
        TextView title20 = (TextView) findViewById(R.id.title20);
        TextView title21 = (TextView) findViewById(R.id.title21);
        TextView title22 = (TextView) findViewById(R.id.title22);
        TextView title23 = (TextView) findViewById(R.id.title23);
        TextView title24 = (TextView) findViewById(R.id.title24);


        TextView text1 = (TextView) findViewById(R.id.text1);
        TextView text2 = (TextView) findViewById(R.id.text2);
        TextView text3 = (TextView) findViewById(R.id.text3);
        TextView text4 = (TextView) findViewById(R.id.text4);
        TextView text5 = (TextView) findViewById(R.id.text5);
        TextView text6 = (TextView) findViewById(R.id.text6);
        TextView text7 = (TextView) findViewById(R.id.text7);
        TextView text8 = (TextView) findViewById(R.id.text8);
        TextView text9 = (TextView) findViewById(R.id.text9);
        TextView text10 = (TextView) findViewById(R.id.text10);
        TextView text11 = (TextView) findViewById(R.id.text11);
        TextView text12 = (TextView) findViewById(R.id.text12);
        TextView text13 = (TextView) findViewById(R.id.text13);
        TextView text14 = (TextView) findViewById(R.id.text14);
        TextView text15 = (TextView) findViewById(R.id.text15);
        TextView text16 = (TextView) findViewById(R.id.text16);
        TextView text17 = (TextView) findViewById(R.id.text17);
        TextView text18 = (TextView) findViewById(R.id.text18);
        TextView text19 = (TextView) findViewById(R.id.text19);
        TextView text20 = (TextView) findViewById(R.id.text20);
        TextView text21 = (TextView) findViewById(R.id.text21);
        TextView text22 = (TextView) findViewById(R.id.text22);
        TextView text23 = (TextView) findViewById(R.id.text23);
        TextView text24 = (TextView) findViewById(R.id.text24);


        title1.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title2.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title3.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title4.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title5.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title6.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title7.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title8.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title9.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title10.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title11.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title12.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title13.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title14.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title15.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title16.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title17.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title18.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title19.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title20.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title21.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title22.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title23.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);
        title24.setTypeface(Config.getInstance().getDefaultFont(this), Typeface.BOLD);


        text1.setTypeface(Config.getInstance().getDefaultFont(this));
        text2.setTypeface(Config.getInstance().getDefaultFont(this));
        text3.setTypeface(Config.getInstance().getDefaultFont(this));
        text4.setTypeface(Config.getInstance().getDefaultFont(this));
        text5.setTypeface(Config.getInstance().getDefaultFont(this));
        text6.setTypeface(Config.getInstance().getDefaultFont(this));
        text7.setTypeface(Config.getInstance().getDefaultFont(this));
        text8.setTypeface(Config.getInstance().getDefaultFont(this));
        text9.setTypeface(Config.getInstance().getDefaultFont(this));
        text10.setTypeface(Config.getInstance().getDefaultFont(this));
        text11.setTypeface(Config.getInstance().getDefaultFont(this));
        text12.setTypeface(Config.getInstance().getDefaultFont(this));
        text13.setTypeface(Config.getInstance().getDefaultFont(this));
        text14.setTypeface(Config.getInstance().getDefaultFont(this));
        text15.setTypeface(Config.getInstance().getDefaultFont(this));
        text16.setTypeface(Config.getInstance().getDefaultFont(this));
        text17.setTypeface(Config.getInstance().getDefaultFont(this));
        text18.setTypeface(Config.getInstance().getDefaultFont(this));
        text19.setTypeface(Config.getInstance().getDefaultFont(this));
        text20.setTypeface(Config.getInstance().getDefaultFont(this));
        text21.setTypeface(Config.getInstance().getDefaultFont(this));
        text22.setTypeface(Config.getInstance().getDefaultFont(this));
        text23.setTypeface(Config.getInstance().getDefaultFont(this));
        text24.setTypeface(Config.getInstance().getDefaultFont(this));


        ImageView btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        finish();
    }
}

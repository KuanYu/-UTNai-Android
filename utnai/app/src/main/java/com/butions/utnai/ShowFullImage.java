package com.butions.utnai;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ShowFullImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerName managerName = new ActivityManagerName();
        managerName.setProcessNameClass(this);
        managerName.setNameClassInProcessString("ShowFullImage");

        TouchImageView touchImageView = new TouchImageView(this);

        Bundle bundle = getIntent().getExtras();
        String urlImage = bundle.getString("mPicture");
        Picasso.with(this)
                .load(urlImage)
                .fit()
                .centerInside()
                .placeholder(R.color.black)
                .error(R.color.white)
                .into(touchImageView);

        touchImageView.setMaxZoom(4f);
        setContentView(touchImageView);

    }
}

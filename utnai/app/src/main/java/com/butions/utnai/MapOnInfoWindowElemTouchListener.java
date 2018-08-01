package com.butions.utnai;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Chalitta Khampachua on 22-Feb-17.
 */
public abstract class MapOnInfoWindowElemTouchListener implements View.OnTouchListener {
    private final View view;
    private final Drawable bgDrawableNormal;
    private final Drawable bgDrawablePressed;
    private final Handler handler = new Handler();

    private Marker marker;
    private boolean pressed = false;

    public MapOnInfoWindowElemTouchListener(View view, Drawable bgDrawableNormal, Drawable bgDrawablePressed) {
        this.view = view;
        this.bgDrawableNormal = bgDrawableNormal;
        this.bgDrawablePressed = bgDrawablePressed;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public boolean onTouch(View vv, MotionEvent event) {
//        if (0 <= event.getX() && event.getX() <= view.getWidth() &&
//                0 <= event.getY() && event.getY() <= view.getHeight())
//        {
//            Log.d("MapOnInfo", " MotionEvent :" + event.getActionMasked());
//            switch (event.getActionMasked()) {
//                case MotionEvent.ACTION_DOWN:
//                    Log.d("MapOnInfo", " MotionEvent ACTION_DOWN :" + event.getActionMasked());
//                    startPress();
//                    break;
//
//                // We need to delay releasing of the view a little so it shows the pressed state on the screen
//                case MotionEvent.ACTION_UP:
//                    Log.d("MapOnInfo", " MotionEvent ACTION_UP :" + event.getActionMasked());
//                    handler.postDelayed(confirmClickRunnable, 50);
//                    break;
//
//                case MotionEvent.ACTION_CANCEL:
//                    Log.d("MapOnInfo", " MotionEvent ACTION_CANCEL :" + event.getActionMasked());
//                    endPress();
//                    break;
//
//                default:
//                    break;
//            }
//        }
//        else {
//            // If the touch goes outside of the view's area
//            // (like when moving finger out of the pressed button)
//            // just release the press
//            endPress();
//        }

        Log.d("MapOnInfo", " MotionEvent :" + event.getActionMasked());
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("MapOnInfo", " MotionEvent ACTION_DOWN :" + event.getActionMasked());
                startPress();
                break;

            // We need to delay releasing of the view a little so it shows the pressed state on the screen
            case MotionEvent.ACTION_UP:
                Log.d("MapOnInfo", " MotionEvent ACTION_UP :" + event.getActionMasked());
                handler.postDelayed(confirmClickRunnable, 50);
                break;

            case MotionEvent.ACTION_CANCEL:
                Log.d("MapOnInfo", " MotionEvent ACTION_CANCEL :" + event.getActionMasked());
                endPress();
                break;

            default:
                break;
        }

        return false;
    }

    private void startPress() {
        if (!pressed) {
            pressed = true;
            handler.removeCallbacks(confirmClickRunnable);
            view.setBackground(bgDrawablePressed);
            if (marker != null)
                marker.showInfoWindow();
        }
    }

    private boolean endPress() {
        if (pressed) {
            this.pressed = false;
            handler.removeCallbacks(confirmClickRunnable);
            view.setBackground(bgDrawableNormal);
            if (marker != null)
                marker.showInfoWindow();
            return true;
        }
        else
            return false;
    }

    private final Runnable confirmClickRunnable = new Runnable() {
        public void run() {
            if (endPress()) {
                onClickConfirmed(view, marker);
            }
        }
    };

    /**
     * This is called after a successful click
     */
    protected abstract void onClickConfirmed(View v, Marker marker);
}

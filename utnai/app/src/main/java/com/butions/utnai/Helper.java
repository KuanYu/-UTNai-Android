package com.butions.utnai;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Chalitta Khampachua on 16-Aug-17.
 */

public class Helper {
    public static Dialog mProgressDialog;
    private static ProgressBar progress_bar;
    private static TextView process_text;

    public static void initProgressDialog(final Context context) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog == null) {
                    mProgressDialog = new Dialog(context);
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.getWindow();
                    mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    mProgressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
                    mProgressDialog.setContentView(R.layout.progressbar_layout);

                    progress_bar = (ProgressBar) mProgressDialog.findViewById(R.id.progress_bar);
                    process_text = (TextView) mProgressDialog.findViewById(R.id.process_text);


                    mProgressDialog.show();
                } else {
                    if(!((Activity) context).isFinishing()) {
                        mProgressDialog.show();
                    }
                }
            }
        });
    }

    public static void setProgress(int i) {
        progress_bar.setProgress(i);
        process_text.setText(String.valueOf(i));
    }

    public static void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}

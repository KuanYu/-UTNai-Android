package com.butions.utnai;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.Vector;

/**
 * Created by Chalitta Khampachua on 17-Jul-17.
 */

public class MyLoading {

    private String TAG = "MyLoading";
    private Context mContext;
    private static Vector<Dialog> vector_dialogs_loading = new Vector<Dialog>();

    public MyLoading(Context context) {
        this.mContext = context;
    }

    public void loading(final boolean status) {
        if (status) {
            Dialog dialog_loading = new Dialog(mContext);
            dialog_loading.setCanceledOnTouchOutside(false);
            dialog_loading.getWindow();
            dialog_loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog_loading.getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
            dialog_loading.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            dialog_loading.setContentView(R.layout.dialog_loading);

            vector_dialogs_loading.add(dialog_loading);
            dialog_loading.show();

        } else {
            closeDialog();
        }
    }

    public void loadingLogo(final boolean status){
        if (status) {
            Dialog dialog_logo = new Dialog(mContext, android.R.style.Theme_Light);
            dialog_logo.setCanceledOnTouchOutside(false);
            dialog_logo.getWindow();
            dialog_logo.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_logo.getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
            dialog_logo.setContentView(R.layout.dialog_loading_logo);
            dialog_logo.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);

            vector_dialogs_loading.add(dialog_logo);
            dialog_logo.show();

        } else {
            closeDialog();
        }
    }

    private void closeDialog(){
        if(!vector_dialogs_loading.isEmpty()) {
            Log.d(TAG, "*** dialog vector : " + vector_dialogs_loading.toString() + "***");
            for (Dialog dialog : vector_dialogs_loading) {
                if (dialog.isShowing()) {
                    if (dialog != null) {
                        try {
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }
            }

            vector_dialogs_loading.clear();
        }
    }
}

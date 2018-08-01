package com.butions.utnai;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.Vector;

/**
 * Created by Chalitta Khampachua on 10-Feb-17.
 * This is Class for popup message chat
 * Input data from MyFcmListenerService class by case notification
 */
public class AlertMessages {

    Context context;
    private static Vector<Dialog> vector_dialogs = new Vector<Dialog>();
    private Dialog dialog_chat;
    private String TAG = "AlertMessages";

    public AlertMessages(Context con) {
        this.context = con;
    }

    public void alertChatMessage(final Map<String, String> data) {
        closeDialog();
        Log.d(TAG, "*** Dialog Chat Message ***");
        Log.d(TAG, "Data : " + data);

        dialog_chat = new Dialog(context);
        dialog_chat.getWindow();
        dialog_chat.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_chat.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog_chat.setContentView(R.layout.dialog_chat);
        dialog_chat.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);

        ImageView dialog_image = (ImageView) dialog_chat.findViewById(R.id.dialog_image);
        TextView dialog_name = (TextView) dialog_chat.findViewById(R.id.dialog_name);
        TextView dialog_title = (TextView) dialog_chat.findViewById(R.id.dialog_title);
        TextView btnClose = (TextView) dialog_chat.findViewById(R.id.btnClose);
        TextView btnShow = (TextView) dialog_chat.findViewById(R.id.btnShow);

        Picasso.with(context)
                .load(data.get("image"))
                .centerCrop()
                .fit()
                .placeholder(R.drawable.bg_circle_white)
                .error(R.drawable.bg_circle_white)
                .transform(new CircleTransform())
                .into(dialog_image);

        dialog_name.setText(data.get("title"));
        dialog_title.setText(data.get("message"));

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_chat.cancel();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(Integer.parseInt(data.get("id")));
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_chat.cancel();
                Log.d(TAG, "Friend Facebook ID : " + data.get("friendFacebookId"));
                Log.d(TAG, "My Facebook ID : " + data.get("myFacebookID"));
                String myFacebookID = data.get("myFacebookID");
                String myDeviceID = data.get("myDeviceId");
                String friendFacebookId = data.get("friendFacebookId");
                String friendImage = data.get("image");
                String isSnippet = myFacebookID + "/&" + myDeviceID;
                if(myFacebookID != null) {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                    Intent intent = new Intent(context, Chat.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("isFacebookID", isSnippet);
                    intent.putExtra("MyFacebookID", friendFacebookId);
                    intent.putExtra("MyPicture", friendImage);
                    context.startActivity(intent);
                }
            }
        });


        vector_dialogs.add(dialog_chat);
        dialog_chat.show();
    }

    public void closeDialog(){
        Log.d(TAG, "*** dialog vector : " + vector_dialogs.toString() + "***");
        if(!vector_dialogs.isEmpty()) {
            for (Dialog dialog : vector_dialogs) {
                if (dialog.isShowing()) {
                    Log.d(TAG, "*** Dialog Dismiss ***");
                    if (dialog != null) {
                        try {
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }
            }

            vector_dialogs.clear();
        }
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.y;
    }


}

package com.butions.utnai;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 27-Dec-16.
 * This is class for input push notification from fcm
 */
public class MyFcmListenerService extends FirebaseMessagingService {

    private String TAG = "MyFcmListenerService";
    private Map<String, String> data;
    private Context mContext;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "*** " + TAG + " ***");
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message received ["+remoteMessage.getData()+"]");

        boolean isActivityFound = isRunningInForeground();
        if (isActivityFound) {
            Log.d(TAG, "*** foreground ***");
            data = remoteMessage.getData();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String urlFriendPicture = data.get("image");
                    new ReadImageUrlToBitmap().execute(urlFriendPicture);
                }
            });
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "**** onCreate() ****");
        mContext = getApplication();
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);

        data = new HashMap<String, String>();
        data.put("id", String.valueOf(intent.getExtras().get("id")));
        data.put("title", String.valueOf(intent.getExtras().get("title")));
        data.put("message", String.valueOf(intent.getExtras().get("message")));
        data.put("image", String.valueOf(intent.getExtras().get("image")));
        data.put("friendUserID", String.valueOf(intent.getExtras().get("friendUserID")));
        data.put("friendDeviceID" , String.valueOf(intent.getExtras().get("friendDeviceID")));
        data.put("time", String.valueOf(intent.getExtras().get("time")));
        data.put("type", String.valueOf(intent.getExtras().get("type")));
        data.put("myUserID", String.valueOf(intent.getExtras().get("myUserID")));
        data.put("myDeviceID", String.valueOf(intent.getExtras().get("myDeviceID")));

        boolean isActivityFound = isRunningInForeground();
        if (!isActivityFound) {
            Log.d(TAG, "*** background ***");
            Log.d(TAG, " Handle Intent data : " + data);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String urlFriendPicture = data.get("image");
                    new ReadImageUrlToBitmap().execute(urlFriendPicture);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "**** onDestroy() ****");
    }

    private void sendNotificationAddFriends(Bitmap friendPictureBitmap, String onRunning){

        int NotificationID = Integer.parseInt(data.get("id"));
        //image circle
        Bitmap bitmapImage = getCircleBitmap(friendPictureBitmap);

        RemoteViews contentView;
        Log.d(TAG, "Android Version : "+ Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT >= 24) {  //Android version more than Nougat (7.0)
            contentView = new RemoteViews(getPackageName(), R.layout.custom_push);
        }else{
            contentView = new RemoteViews(getPackageName(), R.layout.custom_push_low);
        }
        contentView.setImageViewBitmap(R.id.image, bitmapImage);
        contentView.setTextViewText(R.id.title_name, data.get("title"));
        contentView.setTextViewText(R.id.text_message, data.get("message"));
        contentView.setTextViewText(R.id.time, data.get("time"));

        SharedPreferences sp = getSharedPreferences("MYPROFILE", Context.MODE_PRIVATE);
        double lat = Double.parseDouble(sp.getString("Latitude", "-1"));
        double lng = Double.parseDouble(sp.getString("Longitude", "-1"));
        String mUserID = sp.getString("UserID", "-1");
        String mPicture = sp.getString("Picture", "-1");

        ActivityManagerName managerName = new ActivityManagerName();
        managerName.setCurrentPage("MyFriend");
        if(onRunning.equals("background")) {
            Intent intent = new Intent(this, MyMap.class);
            intent.putExtra("Latitude", lat);
            intent.putExtra("Longitude", lng);
            intent.putExtra("UserID", mUserID);
            intent.putExtra("UserPhoto", mPicture);
            intent.putExtra("NotificationID", NotificationID);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, NotificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            long[] vibrate = {0, 100, 200, 300};
            Uri sound = Uri.parse("android.resource://" + getBaseContext().getPackageName() + "/" + R.raw.sound1);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContent(contentView)
                    .setAutoCancel(true)
                    .setVibrate(vibrate)
                    .setSound(sound)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NotificationID, notificationBuilder.build());

        }else {
            if (!managerName.getNameClassInProcessString().equals("MyFriend")) {
                long[] vibrate = {0, 100, 200, 300};
                Uri sound = Uri.parse("android.resource://" + getBaseContext().getPackageName() + "/" + R.raw.sound1);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContent(contentView)
                        .setAutoCancel(true)
                        .setVibrate(vibrate)
                        .setSound(sound)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NotificationID, notificationBuilder.build());

                AlertCount.getInstance().setAlert(true);

            }else{
                long[] vibrate = {0, 100, 200, 300};
                Uri sound = Uri.parse("android.resource://" + getBaseContext().getPackageName() + "/" + R.raw.sound1);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContent(contentView)
                        .setAutoCancel(true)
                        .setVibrate(vibrate)
                        .setSound(sound)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NotificationID, notificationBuilder.build());
            }
        }

    }

    private void sendNotification(Bitmap friendPictureBitmap , String priority){

        int NotificationID = Integer.parseInt(data.get("id"));
        //image circle
        Bitmap bitmapImage = getCircleBitmap(friendPictureBitmap);

        RemoteViews contentView;
        if(Build.VERSION.SDK_INT >= 7.0) {
            contentView = new RemoteViews(getPackageName(), R.layout.custom_push);
        }else{
            contentView = new RemoteViews(getPackageName(), R.layout.custom_push_low);
        }
        contentView.setImageViewBitmap(R.id.image, bitmapImage);
        contentView.setTextViewText(R.id.title_name, data.get("title"));
        contentView.setTextViewText(R.id.text_message, data.get("message"));
        contentView.setTextViewText(R.id.time, data.get("time"));

        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("PushNotificationLogID", String.valueOf(NotificationID));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MyFcmListenerService.this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NotificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] vibrate = {0, 100, 200, 300};
        Uri sound = Uri.parse("android.resource://" + getBaseContext().getPackageName() + "/" + R.raw.sound1);
        if(priority.equals("High")) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContent(contentView)
                    .setAutoCancel(true)
                    .setVibrate(vibrate)
                    .setSound(sound)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NotificationID, notificationBuilder.build());

        }else{
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContent(contentView)
                    .setAutoCancel(true)
                    .setVibrate(vibrate)
                    .setSound(sound)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NotificationID, notificationBuilder.build());
        }

    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        Canvas canvas;
        int radius = (int) (181 * Config.getInstance().getDisplayDensity(mContext));
        int stroke = 1;
        Bitmap bmp;
        if(bitmap != null) {
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            bmp = Bitmap.createBitmap((int) radius, (int) radius + 25, conf);
            canvas = new Canvas(bmp);

            // creates a centered bitmap of the desired size
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, (int) radius - stroke, (int) radius - stroke, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);

            // color circle background
            RectF rect = new RectF(0, 0, radius, radius);
            canvas.drawRoundRect(rect, radius / 2, radius / 2, paint);

            // circle photo
            paint.setShader(shader);
            rect = new RectF(stroke, stroke, radius - stroke, radius - stroke);
            canvas.drawRoundRect(rect, (radius - stroke) / 2, (radius - stroke) / 2, paint);
        }else{
            bmp = DrawableMarker.getInstance().resizeMapIcons(R.drawable.map_marker_utnai,this, 4);
        }

        return bmp;
    }

    private class ReadImageUrlToBitmap extends AsyncTask<String, Bitmap, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            try {
                Log.d(TAG, "ReadImageUrlToBitmap:UrlImage:"+ Arrays.toString(strings));
                URL url;
                if(strings[0] != null) {
                    url = new URL(strings[0]);
                }else{
                    url = new URL(data.get("image"));
                }
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(connection.getInputStream());

                //read image from url
                bitmap = BitmapFactory.decodeStream(input);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(final Bitmap friendPictureBitmap) {
            super.onPostExecute(friendPictureBitmap);
            SharedPreferences sp = getSharedPreferences("NOTIFICATION", Context.MODE_PRIVATE);
            final int status_popup = sp.getInt("popup", -1);
            final int status_push = sp.getInt("push", -2);
            Log.d(TAG, "status_popup : " + status_popup);

            boolean isActivityFound = isRunningInForeground();
            if (isActivityFound) {
                Log.d(TAG, "*** foreground ***");
                ActivityManagerName managerName = new ActivityManagerName();
                if(status_push == 2) {
                    String type = data.get("type");
                    if(type.equals("text")) {
                        if (!managerName.getNameClassInProcessString().equals("Chat")) {
                            if (status_popup == 1) {
                                sendNotification(friendPictureBitmap, "Low");
                                AlertMessages msg = new AlertMessages(mContext);
                                msg.alertChatMessage(data);
                            } else {
                                sendNotification(friendPictureBitmap, "High");
                            }
                        } else {
                            if (!managerName.getChatRoomName().equals(data.get("myUserID"))) {
                                sendNotification(friendPictureBitmap, "High");
                            }
                        }
                    }else if(type.equals("add_friend")){
                        sendNotificationAddFriends(friendPictureBitmap, "foreground");
                    }
                }

            }else {
                Log.d(TAG, "*** background ***");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //GET CURRENT PROCESS ACTIVITY
                        if(status_push == 2) {
                            String type = data.get("type");
                            if(type.equals("text")) {
                                if (status_popup == 1) {
                                    sendNotification(friendPictureBitmap, "Low");
                                    AlertMessages msg = new AlertMessages(mContext);
                                    msg.alertChatMessage(data);
                                } else {
                                    sendNotification(friendPictureBitmap, "High");
                                }
                            }else if(type.equals("add_friend")){
                                sendNotificationAddFriends(friendPictureBitmap , "background");
                            }
                        }
                    }
                });
            }

        }
    }

    protected boolean isRunningInForeground(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        if(tasks.get(0).topActivity.getPackageName().equalsIgnoreCase(mContext.getPackageName())){
            Log.d(TAG, "run on foreground !");
            return true;
        }else {
            Log.d(TAG, "run on background !");
            return false;
        }
    }

}

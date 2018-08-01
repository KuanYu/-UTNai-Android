package com.butions.utnai;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Chalitta Khampachua on 13-Sep-17.
 */

public class SendPushNotification extends AsyncTask<String, Void, String> {

    private String TAG = "sendPushNotification";

    @Override
    protected String doInBackground(String... strings) {
        /*
         * ----set data receive-----
         * friendToken = data[0]
         * friendName = data[1]
         * friendPicture = data[2]
         * friendFacebookId = data[3]
         * friendDeviceId = data[4]
         * newMessage = data[5]
         * type = data[6]
         * time = data[7]
         * myFacebookID = data[8]
         * myDeviceID = data[9]
         */
        HttpURLConnection con = null;
        try{
            String[] data = strings[0].split("/&");

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);

            // HTTP request header
            con.setRequestProperty("Authorization", "key=AIzaSyC-BvQz_zY9BsSbDm0tJmQXSl9CVhvNfgU");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");
            con.connect();

            Map<String, String> notification_body = new HashMap<String, String>();
            notification_body.put("body", data[5]);
            notification_body.put("title", data[1]);
            notification_body.put("sound", "sound1");
            notification_body.put("priority", "high");

            Random RandomUtils = new Random();
            int randomID = RandomUtils.nextInt(1000000000);
            Map<String, String> data_body = new HashMap<String, String>();
            data_body.put("id", String.valueOf(randomID));
            data_body.put("title", data[1]);
            data_body.put("message", data[5]);
            data_body.put("image", data[2]);
            data_body.put("friendUserID",data[3]);
            data_body.put("friendDeviceID" , data[4]);
            data_body.put("type", data[6]);
            data_body.put("time", data[7]);
            data_body.put("myUserID", data[8]);
            data_body.put("myDeviceID", data[9]);

            // HTTP request
            JSONObject jsonData = new JSONObject();
           // jsonData.put("notification", new JSONArray(Arrays.asList(notification_body)));  //disable notification on system
            jsonData.put("data", new JSONArray(Arrays.asList(data_body)));  // can create the utnai notification
            jsonData.put("to", data[0]);
            jsonData.put("priority", "high");

            Log.d(TAG, "Data : " + jsonData);
            String StringData = jsonData.toString();
            String json = StringData.replace("[","").replace("]","");
            Log.d(TAG, "Json data :" + json);

            OutputStream os = con.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();

            int severResponseCode = con.getResponseCode();
            String serverResponseMessage = con.getResponseMessage();
            Log.i("LOG", "Server Response is: " + serverResponseMessage + ": " + severResponseCode);

            return serverResponseMessage;
        }catch (Exception e){
            e.getMessage();
        }finally {
            if(con != null){
                con.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.d(TAG, "result : " + s);
    }
}

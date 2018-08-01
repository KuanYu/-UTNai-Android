package com.butions.utnai;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 09-Nov-16.
 */
public class MyDeviceFirebase {
    private static final DatabaseReference sRef = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "MyDeviceFirebase";
    private static String My_DEVICES;

    public static DevicesListener addDeviceListener(String facebookID, final DevicesCallbacks callbacks){
        DevicesListener listener = new DevicesListener(callbacks);
        My_DEVICES = "Users/" + facebookID + "/Devices";
        long time_now = CalendarTime.getInstance().getCurrentTime();
        sRef.child(My_DEVICES).orderByChild("Timestamp").limitToFirst(25).addValueEventListener(listener);  // less to more

        return listener;
    }

    public static void stop(DevicesListener listener){
        sRef.child(My_DEVICES).removeEventListener(listener);
    }


    public static class DevicesListener implements ValueEventListener {
        private DevicesCallbacks callbacks;
        private Map<Integer, String> mDeviceName = new HashMap<>();
        private Map<Integer, String> mDeviceID = new HashMap<>();
        private Map<Integer, String> mDeviceType = new HashMap<>();
        private Map<Integer, String> mDeviceLng = new HashMap<>();
        private Map<Integer, String> mDeviceLat = new HashMap<>();
        private Map<Integer, String> mDeviceDate = new HashMap<>();
        private Map<Integer, String> mDeviceActive = new HashMap<>();
        private Map<Integer, String> mDeviceToken = new HashMap<>();


        DevicesListener(DevicesCallbacks callbacks){
            this.callbacks = callbacks;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.getChildrenCount() > 0) {
                clearAll();
                int count = -1;
                for (DataSnapshot objDataSnapshotDevices : dataSnapshot.getChildren()) {
                    count++;
                    Map<String, Object> mapObjDevice = (Map<String, Object>) objDataSnapshotDevices.getValue();
                    Log.d(TAG, "Timestamp : " + mapObjDevice.get("Timestamp"));
                    if (mapObjDevice.containsKey("DeviceName")) {
                        mDeviceName.put(count, mapObjDevice.get("DeviceName").toString());
                    }

                    if (mapObjDevice.containsKey("ID")) {
                        mDeviceID.put(count, mapObjDevice.get("ID").toString());
                    }

                    if (mapObjDevice.containsKey("DeviceType")) {
                        mDeviceType.put(count, mapObjDevice.get("DeviceType").toString());
                    }

                    if (mapObjDevice.containsKey("LogOutTime")) {
                        mDeviceDate.put(count, mapObjDevice.get("LogOutTime").toString());
                    }

                    if (mapObjDevice.containsKey("Active")) {
                        mDeviceActive.put(count, mapObjDevice.get("Active").toString());
                    }

                    if (mapObjDevice.containsKey("Token")) {
                        mDeviceToken.put(count, mapObjDevice.get("Token").toString());
                    }

                    if(mapObjDevice.containsKey("LatLng")){
                        String latlng = mapObjDevice.get("LatLng").toString();
                        String[] location = latlng.split(",");
                        mDeviceLat.put(count, location[0]);
                        mDeviceLng.put(count, location[1]);
                    }

                    if(mapObjDevice.containsKey("Timestamp")){

                    }
                }

                DevicesValue devices = new DevicesValue();
                devices.setDeviceName(mDeviceName);
                devices.setDeviceActive(mDeviceActive);
                devices.setDeviceDate(mDeviceDate);
                devices.setDeviceID(mDeviceID);
                devices.setDeviceLat(mDeviceLat);
                devices.setDeviceLng(mDeviceLng);
                devices.setDeviceType(mDeviceType);
                devices.setDeviceToken(mDeviceToken);

                if(callbacks != null){
                    callbacks.onDevicesChange(devices);
                }

            }else{
                //is not value at database
                if(callbacks != null){
                    callbacks.onDevicesChange(null);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

        private void clearAll() {
            mDeviceName.clear();
            mDeviceID.clear();
            mDeviceType.clear();
            mDeviceLng.clear();
            mDeviceLat.clear();
            mDeviceDate.clear();
            mDeviceToken.clear();
        }
    }

    public interface DevicesCallbacks{
        public void onDevicesChange(DevicesValue devicesValue);
    }

}

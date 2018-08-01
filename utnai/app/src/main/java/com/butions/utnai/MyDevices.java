package com.butions.utnai;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyDevices extends AppCompatActivity implements View.OnClickListener, MyDeviceFirebase.DevicesCallbacks {

    private String TAG = "MyDevices";
    private TextView textDevices;
    private ImageView btnBack;
    private RecyclerView my_list_devices;
    private RecyclerViewAdapter adapter;
    private MyDeviceFirebase.DevicesListener devicesListener;
    private Map<Integer, String> deviceName;
    private Map<Integer, String> deviceID;
    private Map<Integer, String> deviceType;
    private Map<Integer, String> deviceLng;
    private Map<Integer, String> deviceLat;
    private Map<Integer, String> deviceDate;
    private Map<Integer, String> deviceActive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_devices);

        Bundle bundle = getIntent().getExtras();
        String mUserID = bundle.getString("mUserID");

        //call device
        devicesListener = MyDeviceFirebase.addDeviceListener(mUserID, this);

        textDevices = (TextView) findViewById(R.id.textDevices);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        adapter = new RecyclerViewAdapter();
        my_list_devices = (RecyclerView) findViewById(R.id.my_list_devices);
    }

    @Override
    public void onDevicesChange(DevicesValue devicesValue) {
        if(devicesValue != null) {
            deviceName = devicesValue.getDeviceName();
            deviceID = devicesValue.getDeviceID();
            deviceType = devicesValue.getDeviceType();
            deviceLng = devicesValue.getDeviceLng();
            deviceLat = devicesValue.getDeviceLat();
            deviceDate = devicesValue.getDeviceDate();
            deviceActive = devicesValue.getDeviceActive();

            Log.d(TAG, "device id : " + deviceID);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            my_list_devices.setLayoutManager(mLayoutManager);
            my_list_devices.setAdapter(adapter);

        }else{
            Log.d(TAG, "devicesValue : null");
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder>{


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_devices_layout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //set font
            holder.name_devices.setTypeface(Config.getInstance().getDefaultFont(MyDevices.this));
            holder.time_devices.setTypeface(Config.getInstance().getDefaultFont(MyDevices.this));
            holder.location_device.setTypeface(Config.getInstance().getDefaultFont(MyDevices.this));

            holder.name_devices.setText(deviceName.get(position));
            if(deviceActive.get(position).equals("1")){
                holder.time_devices.setTextColor(getResources().getColor(R.color.green));
                holder.time_devices.setText(StringManager.getsInstance().getString("ActiveNow"));
            }else{
                holder.time_devices.setTextColor(getResources().getColor(R.color.grey));
                String timeAgo = CalendarTime.getInstance().getTimeAgo(deviceDate.get(position));
                holder.time_devices.setText(timeAgo);
            }

            String locationName = getAddress(Double.parseDouble(deviceLat.get(position)), Double.parseDouble(deviceLng.get(position)));
            if(locationName != null){
                holder.location_device.setVisibility(View.VISIBLE);
                holder.location_device.setText(locationName);
            }else{
                holder.location_device.setVisibility(View.GONE);
            }

            if(deviceType.get(position).toLowerCase().equals("android")){
                holder.image_devices.setImageResource(R.drawable.ic_deivce_android);
            }else if(deviceType.get(position).toLowerCase().equals("iphone")){
                holder.image_devices.setImageResource(R.drawable.ic_deivce_iphone);
            }else if(deviceType.get(position).toLowerCase().equals("ipad")){
                holder.image_devices.setImageResource(R.drawable.ic_deivce_ipad);
            }
        }

        @Override
        public int getItemCount() {
            return deviceID.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        TextView name_devices;
        TextView time_devices;
        ImageView image_devices;
        TextView location_device;

        public ViewHolder(View itemView) {
            super(itemView);

            name_devices = (TextView) itemView.findViewById(R.id.name_devices);
            time_devices = (TextView) itemView.findViewById(R.id.time_deivces);
            image_devices = (ImageView) itemView.findViewById(R.id.image_devices);
            location_device = (TextView) itemView.findViewById(R.id.location_device);
        }
    }

    private String getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String mLocalName = city + "," + country;

            Log.d(TAG, "Address : " + mLocalName);
            return mLocalName;


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Translater.getInstance().setLanguages(this);
        setFonts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyDeviceFirebase.stop(devicesListener);
    }

    private void setFonts() {
        textDevices.setText(StringManager.getsInstance().getString("Devices"));
    }

    @Override
    public void onClick(View view) {
        if(view == btnBack) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

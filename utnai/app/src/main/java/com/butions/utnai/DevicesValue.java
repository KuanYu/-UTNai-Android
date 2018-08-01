package com.butions.utnai;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chalitta Khampachua on 19-Oct-17.
 */

public class DevicesValue {
    private Map<Integer, String> deviceName = new HashMap<>();
    private Map<Integer, String> deviceID = new HashMap<>();
    private Map<Integer, String> deviceType = new HashMap<>();
    private Map<Integer, String> deviceLng = new HashMap<>();
    private Map<Integer, String> deviceLat = new HashMap<>();
    private Map<Integer, String> deviceDate = new HashMap<>();
    private Map<Integer, String> deviceActive = new HashMap<>();
    private Map<Integer, String> deviceToken = new HashMap<>();


    public Map<Integer, String> getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(Map<Integer, String> deviceName) {
        this.deviceName = deviceName;
    }

    public Map<Integer, String> getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(Map<Integer, String> deviceID) {
        this.deviceID = deviceID;
    }

    public Map<Integer, String> getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Map<Integer, String> deviceType) {
        this.deviceType = deviceType;
    }

    public Map<Integer, String> getDeviceLng() {
        return deviceLng;
    }

    public void setDeviceLng(Map<Integer, String> deviceLng) {
        this.deviceLng = deviceLng;
    }

    public Map<Integer, String> getDeviceLat() {
        return deviceLat;
    }

    public void setDeviceLat(Map<Integer, String> deviceLat) {
        this.deviceLat = deviceLat;
    }

    public Map<Integer, String> getDeviceDate() {
        return deviceDate;
    }

    public void setDeviceDate(Map<Integer, String> deviceDate) {
        this.deviceDate = deviceDate;
    }

    public Map<Integer, String> getDeviceActive() {
        return deviceActive;
    }

    public void setDeviceActive(Map<Integer, String> deviceActive) {
        this.deviceActive = deviceActive;
    }

    public Map<Integer, String> getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(Map<Integer, String> deviceToken) {
        this.deviceToken = deviceToken;
    }
}

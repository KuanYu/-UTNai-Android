package com.butions.utnai;

/**
 * Created by Chalitta Khampachua on 13-Oct-17.
 */

public class CodesValue {
    private String TAG = "CodesValue";
    private String CodesID;
    private String CodesLat;
    private String CodesLng;

    public String getCodes() {
        return CodesID;
    }

    public void setCodes(String isCodes) {
        this.CodesID = isCodes;
    }

    public String getCodesLat() {
        return CodesLat;
    }

    public void setCodesLat(String codesLat) {
        CodesLat = codesLat;
    }

    public String getCodesLng() {
        return CodesLng;
    }

    public void setCodesLng(String  codesLng) {
        CodesLng = codesLng;
    }
}

package com.butions.utnai;

/**
 * Created by Chalitta Khampachua on 25-Oct-17.
 */

public class MoveToNavigateWithCode {
    private String TAG = "MoveToFriesndLocation";
    private static MoveToNavigateWithCode mInstance = null;
    private boolean flagNavigate = false;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    protected MoveToNavigateWithCode(){

    }

    public static synchronized MoveToNavigateWithCode getInstance(){
        if(null == mInstance){
            mInstance = new MoveToNavigateWithCode();
        }
        return mInstance;
    }

    public boolean getFlagNavigate() {
        return flagNavigate;
    }

    public void setFlagNavigate(boolean flagNavigate) {
        this.flagNavigate = flagNavigate;
    }

}

package com.butions.utnai;

/**
 * Created by Chalitta Khampachua on 03-Nov-17.
 */

public class AlertCount {

    private boolean alert;
    private static AlertCount mInstance = null;

    protected AlertCount(){

    }

    public static synchronized AlertCount getInstance(){
        if(null == mInstance){
            mInstance = new AlertCount();
        }
        return mInstance;
    }

    public boolean getAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }
}

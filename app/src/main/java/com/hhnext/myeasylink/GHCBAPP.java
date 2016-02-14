package com.hhnext.myeasylink;

import android.app.Application;

import org.xutils.x;

public class GHCBAPP extends Application {

    public static final int MSG_VALUE_CHANGED = 0;

    public static final int ALL_CHANGED = 1;
    public static final int HUMIDITY_CHANGED = 2;
    public static final int TEMPERATURE_CHANGED = 3;
    public static final int LAMP_CHANGED = 4;
    public static final int PUMP_CHANGED = 5;
    public static final int HASIMAGE_CHANGED = 6;
    public static final int IPADDRESS_CHANGED = 7;

    public static GHCB CURRENTGHCB;

    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}
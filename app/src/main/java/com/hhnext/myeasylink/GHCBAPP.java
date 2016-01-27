package com.hhnext.myeasylink;

import android.app.Application;

import org.xutils.x;

public class GHCBAPP extends Application {
    public static final int ALL_CHANGED = 5;
    public static final int HUMIDITY_CHANGED = 2;
    public static final int LAMP_CHANGED = 3;
    public static final int MSG_VALUE_CHANGED = 0;
    public static final int PUMP_CHANGED = 4;
    public static final int TEMPERATURE_CHANGED = 1;

    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}
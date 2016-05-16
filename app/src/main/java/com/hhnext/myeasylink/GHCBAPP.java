package com.hhnext.myeasylink;

import android.app.Application;

import org.xutils.x;

public class GHCBAPP extends Application {

    public static final int MSG_VALUE_CHANGED = 30;
    public static final int HASIMAGE_CHANGED = 40;
    public static final int IPADDRESS_CHANGED = 50;

    public static final int ALL_CHANGED = 20;

//    public static final int PORT0_CHANGED = 2;
//    public static final int PORT1_CHANGED = 3;
//    public static final int PORT2_CHANGED = 4;
//    public static final int PORT3_CHANGED = 5;
//    public static final int PORT4_CHANGED = 6;
//    public static final int PORT5_CHANGED = 7;
//    public static final int PORT6_CHANGED = 8;
//    public static final int PORT7_CHANGED = 9;
//    public static final int PORT8_CHANGED = 10;
//    public static final int PORT9_CHANGED = 11;



    public static GHCB CURRENTGHCB;

    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}
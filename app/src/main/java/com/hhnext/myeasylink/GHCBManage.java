package com.hhnext.myeasylink;

import android.content.Context;

import java.util.concurrent.ConcurrentHashMap;

public class GHCBManage {
    public static final ConcurrentHashMap<String, GHCB> GHCBs = new ConcurrentHashMap();

    public static final String productID = "2f10b621";
    public static final String productSecertKey = "9a740523-d5f3-40f3-afb4-29b6d7c2726a";

    public static final String bindURL = "http://api.easylink.io/v1/key/authorize";
    public static final String devicesURL = "http://easylink.io/v1/device/devices";
    public static final String modifyURL = "http://easylink.io/v1/device/modify";
    public static final String authorizeURL = "http://easylink.io/v1/key/user/authorize";


    public static ConnManage connManage;
    private Context ctx;

    public GHCBManage(Context paramContext) {
        this.ctx = paramContext;
        connManage = new ConnManage(paramContext);
    }

    public static String getActiveToken(String paramString) {
        return MyUtil.MD5Encode(paramString + productSecertKey);
    }

    public static String getActiveURL(String paramString) {
        return "http://" + paramString + ":8001/dev-activate";
    }

    public void addGHCB(GHCB paramGHCB) {
        GHCBs.put(paramGHCB.getDevID(), paramGHCB);
    }
}
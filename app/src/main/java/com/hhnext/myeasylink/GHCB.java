package com.hhnext.myeasylink;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mxchip.mqttservice2.MqttServiceAPI;

public class GHCB {
    public static final String password = "123456";
    public static final String userName = "admin";
    private String IPAddress;
    private int ListViewindex;
    private String MAC;
    private String ROMVersion;
    private String createTime;
    private String description;
    private String devID;
    private String devToken;
    private Handler handler;
    private String humidity = "0";
    private boolean lamp;
    private MqttServiceAPI mapi;
    private String offlineTime;
    private String onlineTime;
    private String owner;
    private String publicIPAddress;
    private boolean pump = true;
    private String serialNo;
    private String ssid;
    private GHCBStatus status = GHCBStatus.offline;
    private String temperature = "0";

    private void PublishCommand(String paramString) {
        if (this.mapi != null)
            this.mapi.publishCommand(getInTopic(), paramString, 0, false);
        Log.i("orinoco", getInTopic() + ";" + paramString);
    }

    private void sendMsgToWindows(int paramInt) {
        if (this.handler != null) {
            Message localMessage = this.handler.obtainMessage(0);
            localMessage.arg1 = paramInt;
            localMessage.obj = this;
            this.handler.sendMessage(localMessage);
        }
    }

    public String getActiveURL() {
        return "http://" + this.IPAddress + ":8001/dev-activate";
    }

    public String getClientID() {
        return "v1-app-" + this.MAC;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String paramString) {
        this.createTime = paramString;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String paramString) {
        this.description = paramString;
    }

    public String getDevID() {
        return this.devID;
    }

    public void setDevID(String paramString) {
        this.devID = paramString;
    }

    public String getDevToken() {
        return this.devToken;
    }

    public void setDevToken(String paramString) {
        this.devToken = paramString;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public void setHandler(Handler paramHandler) {
        this.handler = paramHandler;
    }

    public String getHumidity() {
        return this.humidity;
    }

    public void setHumidity(String paramString) {
        if (this.humidity != paramString) {
            this.humidity = paramString;
            sendMsgToWindows(2);
        }
    }

    public String getIPAddress() {
        return this.IPAddress;
    }

    public void setIPAddress(String paramString) {
        this.IPAddress = paramString;
    }

    public String getInTopic() {
        return getDevID() + "/in";
    }

    public int getListViewindex() {
        return this.ListViewindex;
    }

    public void setListViewindex(int paramInt) {
        this.ListViewindex = paramInt;
    }

    public String getMAC() {
        return this.MAC;
    }

    public void setMAC(String paramString) {
        this.MAC = paramString;
    }

    public MqttServiceAPI getMapi() {
        return this.mapi;
    }

    public void setMapi(MqttServiceAPI paramMqttServiceAPI) {
        this.mapi = paramMqttServiceAPI;
    }

    public String getOfflineTime() {
        return this.offlineTime;
    }

    public void setOfflineTime(String paramString) {
        this.offlineTime = paramString;
    }

    public String getOnlineTime() {
        return this.onlineTime;
    }

    public void setOnlineTime(String paramString) {
        this.onlineTime = paramString;
    }

    public String getOutTopic() {
        return this.devID + "/out/#";
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String paramString) {
        this.owner = paramString;
    }

    public String getPublicIPAddress() {
        return this.publicIPAddress;
    }

    public void setPublicIPAddress(String paramString) {
        this.publicIPAddress = paramString;
    }

    public String getROMVersion() {
        return this.ROMVersion;
    }

    public void setROMVersion(String paramString) {
        this.ROMVersion = paramString;
    }

    public String getSerialNo() {
        return this.serialNo;
    }

    public void setSerialNo(String paramString) {
        this.serialNo = paramString;
    }

    public String getSsid() {
        return this.ssid;
    }

    public void setSsid(String paramString) {
        this.ssid = paramString;
    }

    public GHCBStatus getStatus() {
        return this.status;
    }

    public void setStatus(GHCBStatus paramGHCBStatus) {
        ConnManage localConnManage;
        if (this.status != paramGHCBStatus) {
            this.status = paramGHCBStatus;
            localConnManage = GHCBManage.connManage;
            if (paramGHCBStatus == GHCBStatus.online)
                localConnManage.startNewConnToGHCB(this);
        } else {
            return;
        }
        localConnManage.stopConnToGHCB(this);
    }

    public String getTemperature() {
        return this.temperature;
    }

    public void setTemperature(String paramString) {
        if (this.temperature != paramString) {
            this.temperature = paramString;
            sendMsgToWindows(1);
        }
    }

    public boolean isLamp() {
        return this.lamp;
    }

    public void setLamp(boolean paramBoolean) {
        if (this.lamp != paramBoolean) {
            this.lamp = paramBoolean;
            sendMsgToWindows(3);
        }
    }

    public boolean isPump() {
        return this.pump;
    }

    public void setPump(boolean paramBoolean) {
        if (this.pump != paramBoolean) {
            this.pump = paramBoolean;
            sendMsgToWindows(4);
        }
    }

    public void lampOFF() {
        PublishCommand("{\"rgbled_switch\":false}");
    }

    public void lampON() {
        PublishCommand("{\"rgbled_switch\":true}");
    }

    public void pumpOFF() {
        PublishCommand("{\"motor_switch\":false}");
    }

    public void pumpON() {
        PublishCommand("{\"motor_switch\":true}");
    }

    public static enum GHCBStatus {online, offline, activated, unactivated}

}
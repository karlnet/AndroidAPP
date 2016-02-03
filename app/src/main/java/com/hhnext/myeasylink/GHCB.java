package com.hhnext.myeasylink;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.JsonObject;
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
    private boolean hasImage = false;

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean value) {

        if (this.hasImage != value) {
            this.hasImage = value;
            if (hasImage)
                sendMsgToWindows(GHCBAPP.HASIMAGE_CHANGED);
        }
    }

    private void PublishCommand(String command) {
        if (this.mapi != null)
            this.mapi.publishCommand(getInTopic(), command, 0, false);
        Log.i("orinoco", getInTopic() + ";" + command);
    }

    private void sendMsgToWindows(int msgID) {
        if (this.handler != null) {
            Message msg = this.handler.obtainMessage(0);
            msg.arg1 = msgID;
            msg.obj = this;
            this.handler.sendMessage(msg);
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

    public void setHumidity(String value) {
        if (this.humidity != value) {
            this.humidity = value;
            sendMsgToWindows(GHCBAPP.HUMIDITY_CHANGED);
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

    public void setSerialNo(String value) {
        this.serialNo = value;
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

    public void setStatus(GHCBStatus value) {
        ConnManage conn = GHCBManage.connManage;
        if (this.status != value) {
            this.status = value;
            if (value == GHCBStatus.online) {
                conn.startNewConnToGHCB(this);
            } else if (value == GHCBStatus.offline) {
                conn.stopConnToGHCB(this);
            }
        }
    }

    public String getTemperature() {
        return this.temperature;
    }

    public void setTemperature(String value) {
        if (this.temperature != value) {
            this.temperature = value;
            sendMsgToWindows(GHCBAPP.TEMPERATURE_CHANGED);
        }
    }

    public boolean isLamp() {
        return this.lamp;
    }

    public void setLamp(boolean value) {
        if (this.lamp != value) {
            this.lamp = value;
            sendMsgToWindows(GHCBAPP.LAMP_CHANGED);
        }
    }

    public boolean isPump() {
        return this.pump;
    }

    public void setPump(boolean value) {
        if (this.pump != value) {
            this.pump = value;
            sendMsgToWindows(GHCBAPP.PUMP_CHANGED);
        }
    }

    public void lampOFF() {
        lampSwitch(false);
    }

    public void lampON() {
        lampSwitch(true);
    }

    public void pumpOFF() {
        pumpSwitch(false);
    }

    public void pumpON() {
        pumpSwitch(true);
    }

    private void lampSwitch(boolean flag) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rgbled_switch", flag);
        PublishCommand(jsonObject.toString());

    }

    private void pumpSwitch(boolean flag) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("motor_switch", flag);
        PublishCommand(jsonObject.toString());

    }

    public void takePhoto(String commamd) {
        PublishCommand(commamd);
    }

    public static enum GHCBStatus {online, offline, activated, unactivated}

}
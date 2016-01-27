package com.hhnext.myeasylink;

import android.content.Context;

import com.mxchip.mqttservice2.MqttServiceAPI;
import com.mxchip.mqttservice2.MqttServiceListener;

public class ConnManage {
    private static final String host = "api.easylink.io";
    private static final String port = "1883";
    private static ProtocolParser protocolParser = new ProtocolParser();
    private Context ctx;

    public ConnManage(Context paramContext) {
        this.ctx = paramContext;
    }

    public void startNewConnToGHCB(final GHCB theGHCB) {
        MqttServiceAPI localMqttServiceAPI = new MqttServiceAPI(this.ctx);
        theGHCB.setMapi(localMqttServiceAPI);
        localMqttServiceAPI.startMqttService("api.easylink.io", "1883", APPUser.userName, APPUser.userPassword, theGHCB.getClientID(), theGHCB.getOutTopic(), new MqttServiceListener() {
            public void onMqttReceiver(String paramAnonymousString1, String paramAnonymousString2) {
                if (paramAnonymousString1.equals("payload"))
                    ConnManage.protocolParser.Parse(theGHCB, paramAnonymousString2);
            }
        });
    }

    public void stopConnToGHCB(GHCB paramGHCB) {
        paramGHCB.getMapi().stopMqttService();
    }
}
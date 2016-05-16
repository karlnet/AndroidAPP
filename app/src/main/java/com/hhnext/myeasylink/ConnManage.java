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

        localMqttServiceAPI.startMqttService("api.easylink.io", "1883", APPUser.UserName, APPUser.UserPassword, theGHCB.getClientID(), theGHCB.getOutTopic(), new MqttServiceListener() {

            @Override
            public void onMqttReceiver(String s, String s1) {
                if (s.equals("payload"))
                    ConnManage.protocolParser.Parse(theGHCB, s1);
//                else if(s.equals("status")) {
//                    Log.i("orinoco", "status :" + s+" context: "+s1);
//                    if (s1.equals("Connected "))
//                        theGHCB.getGHCBStatus();
//                }
            }

        });

        theGHCB.setMapi(localMqttServiceAPI);
    }

//    public void stopConnToGHCB(GHCB paramGHCB) {
//        paramGHCB.getMapi().stopMqttService();
//    }
}
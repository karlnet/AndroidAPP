package com.hhnext.myeasylink;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.mxchip.mqttservice2.MqttServiceAPI;
import com.mxchip.mqttservice2.MqttServiceListener;

public class ConnManage {
    private static final String host = "api.easylink.io";
    private static final String port = "1883";
    private static ProtocolParser protocolParser = new ProtocolParser();
    private static Context ctx;

    public ConnManage(Context paramContext) {
        this.ctx = paramContext;
    }

    public void startNewConnToGHCB(final GHCB theGHCB) {
        new Thread(new MyTask(ctx, theGHCB)).start();
    }

    public void stopConnToGHCB(GHCB theGHCB) {
        theGHCB.getMapi().stopMqttService();
//        this.ctx.unbindService(theGHCB.getMapi());
    }


    public class MyTask implements Runnable {


        private Context ctx;
        private GHCB theGHCB;

        public MyTask(Context ctx, GHCB theGHCB) {
            this.theGHCB = theGHCB;
            this.ctx = ctx;
        }

        @Override
        public void run() {

            Looper.prepare();

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

            Handler cmdHander = new Handler() {


                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);


                }
            };

            theGHCB.setMapi(localMqttServiceAPI);
            theGHCB.setMQThreadHandler(cmdHander);

            Looper.loop();

        }
    }

}
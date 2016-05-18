package com.hhnext.myeasylink;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mxchip.mqttservice2.MqttServiceAPI;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;

public class GHCB implements RefreshData, SetViewHolderData {
    public final static int inBoardPort = 10;
    public final static int defaultRly1 = 0;
    public final static int defaultRly2 = 1;
    public static final String password = "123456";
    public static final String userName = "admin";
    public static ListView deviceListView;
    private static Gson gson = new Gson();
    public int rly1 = -1;
    public int rly2 = -1;
    private GHCBManage gHCBManage;
    private RefreshActivity refreshActivity;
    private String IPAddress = "0.0.0.0";
    private int ListViewindex;
    private String MAC;
    private String ROMVersion;
    private String createTime;
    private String powerOnTime;
    private String description;
    private String devID;
    private String devAlias;
    private String devToken;
    private Handler handler;
    private Handler MQThreadHandler;
    //    private String humidity = "0";
    private boolean lamp;
    private MqttServiceAPI mapi;
    private String offlineTime;
    private String onlineTime;
    private String owner;
    private String publicIPAddress = "0.0.0.0";
    private boolean pump = true;
    private String serialNo;
    private String ssid;
    private GHCBStatus status = GHCBStatus.offline;
    //    private String temperature = "0";
    private boolean hasImage = false;
    private boolean hasRlyInit = false;
    private boolean connect = false;

    private HashMap<String, Port> ports = new HashMap<>();
    private HashMap<String, List<String>> portTypeNums = new HashMap<>();

    public GHCB() {

        TempPort t = new TempPort(this);
        t.setPort(10);
        t.setPortModel("temp");
        t.setPortName("系统");

        AddPort(t);

//        this.AddPort(t);

//        currentTemp = 0;
//        currentRLY1 = 2;
//        currentRLY2 = 3;
    }

    public GHCBManage getGHCBManage() {
        return gHCBManage;
    }

    public void setGHCBManage(GHCBManage m) {
        this.gHCBManage = m;
    }

    public RefreshActivity getRefreshActivity() {
        return refreshActivity;
    }

    public void setRefreshActivity(RefreshActivity refreshActivity) {
        this.refreshActivity = refreshActivity;
    }

    public void AddOrDelToCloud(final Port port, final boolean create) {
        try {

            String url;

            if (create) {
                url = APPUser.MyBoardPortCreateURL;
            } else {
                url = APPUser.MyBoardPortRemoveURL;
            }


            RequestParams request = new RequestParams(url);
            APPUser.setMyRequestParamsHeader(request);
            request.setBodyContent(gson.toJson(port.getPortBindingModel()));
            x.http().post(request, new Callback.CommonCallback<String>() {
                public void onCancelled(CancelledException cex) {
//                    Log.i("orinoco", "cancel");
                }

                public void onError(Throwable ex, boolean b) {
//                    p.countDownLatch.countDown();
//                    Log.i("orinoco", "error");
                }

                public void onFinished() {
                    /*Log.i("orinoco", "finish");*/
//                    p.countDownLatch.countDown();

                }

                public void onSuccess(String jsonStr) {

                    if (create) {

                        AddPort(port);

                    } else {

                        int no = port.getPort();
                        DelPort(no);

                    }

                    refreshActivity.refreshRlyTempListView();

                }
            });

        } catch (Exception e2) {

        }
    }

    public void getPortsFromCloud() {
        try {

            RequestParams request = new RequestParams(APPUser.MyBoardPortsURL);
            APPUser.setMyRequestParamsHeader(request);
            String str = "{\"mac\":\"" + getMAC() + "\"}";
//                Log.i("orinoco", "s= " + str);
            request.setBodyContent(str);
            x.http().post(request, new Callback.CommonCallback<String>() {
                public void onCancelled(CancelledException cex) {
                }

                public void onError(Throwable ex, boolean b) {

                }

                public void onFinished() {
                }

                public void onSuccess(String jsonStr) {
                    if (!jsonStr.equals("[]")) {
                        try {
                            List<BoardPortsModel> pdList = gson.fromJson(jsonStr, new TypeToken<List<BoardPortsModel>>() {
                            }.getType());

                            for (BoardPortsModel pd : pdList
                                    ) {

                                Port p = (Port) Class.forName(pd.getClassType()).newInstance();
                                p.setGHCB(GHCB.this);
                                p.setFromPortBindingModel(pd);
                                AddPort(p);
                            }


                        } catch (Exception e) {

                        }
                    }
                    refreshActivity.refreshRlyTempListView();
                }
            });

        } catch (Exception e2) {

        }
    }

    public void getPortDescriptionFromCloud() {
        try {


            Log.i("orinoco", "in new thread");
            RequestParams request = new RequestParams(APPUser.MyBoardPortDescriptionURL);
            APPUser.setMyRequestParamsHeader(request);
//            request.setBodyContent(str);
            x.http().get(request, new Callback.CommonCallback<String>() {
                public void onCancelled(CancelledException cex) {
                }

                public void onError(Throwable ex, boolean b) {

                }

                public void onFinished() {

                }

                public void onSuccess(String jsonStr) {

                    try {
                        List<PortDescription> pdList = gson.fromJson(jsonStr, new TypeToken<List<PortDescription>>() {
                        }.getType());

                        for (PortDescription pd : pdList
                                ) {
//                            PortDescription p = new PortDescription(
////                            "rly", "继电器", "com.hhnext.myeasylink.RlyPort", Arrays.asList("pa1a-5v", "pa1a-12v")
//                    );
                            pd.setMap();
                            GHCBManage.PortDescriptions.put(pd.getPortModel(), pd);
                        }

                        int num = GHCBManage.PortDescriptions.size();
                        if (num != 0) {

                            refreshActivity.refreshPortDescriptionListView();

//                            portModel.performItemClick(portModel, 0, 0);

                        }

                    } catch (Exception e) {

                    }
                }
            });

        } catch (Exception e2) {

        }
    }

    public void BoardRename(final String newName) {
        try {
            RequestParams requestParams = new RequestParams(APPUser.modifyURL);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("device_id", getDevID());
            jsonObject.addProperty("alias", newName);
            requestParams.setBodyContent(jsonObject.toString());
            MyUtil.setRequestParamsHeader(requestParams);
            x.http().post(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    setDevAlias(newName);
                    refreshActivity.reName();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ex.printStackTrace();
                }

                @Override
                public void onCancelled(Callback.CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        } catch (Exception e) {

        }
    }

    public void setFromModel(GHCBModel gm) {

        devID = gm.id;
        serialNo = gm.serial;
        MAC = gm.bssid;
        createTime = gm.created;
        devAlias = gm.alias;

        powerOnTime = gm.power_time;
        IPAddress = gm.ip;
        ssid = gm.ssid;
        onlineTime = gm.online_time;
        offlineTime = gm.offline_time;

    }

//    private int currentTemp = 9;

    public void setViewHolder(MyBaseAdapter.ViewHolder ViewHolder) {

        DeviceAdapter.DeviceViewHolder viewHolder = (DeviceAdapter.DeviceViewHolder) ViewHolder;


        if (inBoardPort != -1) {
            TempPort p = (TempPort) getPort(String.valueOf(inBoardPort));
            viewHolder.temperature.setText(String.valueOf(p.getTemperature()));
            viewHolder.humidity.setText(String.valueOf(p.getHumidity()));
        }

        if (getRly1() != -1) {

            RlyPort t = (RlyPort) getPort(String.valueOf(getRly1()));
            viewHolder.rly1.setText(MyUtil.togglgText(t.isRlyState()));
        }
        if (getRly2() != -1) {
            RlyPort t = (RlyPort) getPort(String.valueOf(getRly2()));
            viewHolder.rly2.setText(MyUtil.togglgText(t.isRlyState()));
        }


    }

//    private int currentRLY1 = 0;

    public void refresh() {

        RefreshDataBase.refreshListViewData(deviceListView, this, ListViewindex);
    }

//    private int currentRLY2 = 1;

    public void AttachToMqTT(boolean connect) {
        ConnManage conn = GHCBManage.getConnManage();
        this.connect = connect;

        if (connect) {
            conn.startNewConnToGHCB(this);
        } else if (mapi != null) {
//            this.mapi.stopMqttService();
            conn.stopConnToGHCB(this);
        }
    }

    public int getInBoardTempPort() {
        return inBoardPort;
    }

//    private boolean addOrDelResult = false;

    public int getRly1() {
        return rly1;
    }

    public int getRly2() {
        return rly2;
    }

    public Port getPort(String no) {
        return ports.get(no);
    }

    public void DelPort(int no) {

        String noStr = String.valueOf(no);

        Port port = ports.get(noStr);

        if (port != null) {

//            p.countDownLatch = new CountDownLatch(1);

            port.delPortNo(noStr);
            ports.remove(noStr);

            if (defaultRly1 == no)
                rly1 = -1;

            if (defaultRly2 == no)
                rly2 = -1;

//            try {
//                if (p.countDownLatch.await(10, TimeUnit.SECONDS) && addOrDelResult) {
//                    p.delPortNo(s);
//                    ports.remove(s);
//
//                    if (defaultRly1 == no)
//                        rly1 = -1;
//
//                    if (defaultRly2 == no)
//                        rly2 = -1;
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void AddPort(Port port) {

//        int no = p.getPort();
//        String noStr = String.valueOf(no);

//        p.countDownLatch = new CountDownLatch(1);
        int no = port.getPort();

        String noStr = String.valueOf(no);

        ports.put(noStr, port);
        port.addPortNo(noStr);

        if (defaultRly1 == no)
            rly1 = defaultRly1;

        if (defaultRly2 == no)
            rly2 = defaultRly2;

//        try {
//            if (p.countDownLatch.await(10, TimeUnit.SECONDS)&&addOrDelResult) {

//        ports.put(noStr, p);
//        p.addPortNo(noStr);
//
//        if (defaultRly1 == no)
//            rly1 = defaultRly1;
//
//        if (defaultRly2 == no)
//            rly2 = defaultRly2;
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public HashMap<String, Port> getPorts() {
        return ports;
    }

    public HashMap<String, List<String>> getPortTypeNums() {
        return portTypeNums;
    }

    public void setJsonData(UpdateJsonData updateJsonData) {
        for (UpdateJsonData.Data d : updateJsonData.payload
                ) {
            Port p = ports.get(String.valueOf(d.port));
            if (p != null)
                p.setUploadData(d);
        }


    }


    public void PublishCommand(String command) {
        Log.i("orinoco", getInTopic() + ";" + command);
//        if (this.mapi != null) {
        final String c = command;
        final MqttServiceAPI m = this.mapi;

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
        if (m != null)
            m.publishCommand(getInTopic(), c, 0, false);
//                }
//            }).start();


//        }

    }

    public void sendMsgToWindows(int msgID) {
        if (this.handler != null) {
            Message msg = this.handler.obtainMessage(0);
            msg.arg1 = msgID;
            msg.obj = this;
            this.handler.sendMessage(msg);
        }
    }

    public String getDevAlias() {
        return devAlias;
    }

    public void setDevAlias(String devAlias) {
        this.devAlias = devAlias;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean value) {

        this.hasImage = value;
        if (hasImage)
            sendMsgToWindows(GHCBAPP.HASIMAGE_CHANGED);

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

    public Handler getMQThreadHandler() {
        return MQThreadHandler;
    }

    public void setMQThreadHandler(Handler MQThreadHandler) {
        this.MQThreadHandler = MQThreadHandler;
    }

    public String getIPAddress() {

        return this.IPAddress;
    }

    public void setIPAddress(String value) {
        if (this.IPAddress != value) {
            this.IPAddress = value;
            sendMsgToWindows(GHCBAPP.IPADDRESS_CHANGED);
        }

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

    public String getPowerOnTime() {
        return powerOnTime;
    }

//    public boolean isRlyInit() {
//
//        return hasRlyInit;
//    }

    public void setPowerOnTime(String powerOnTime) {
        this.powerOnTime = powerOnTime;
    }

    //    public void RlyInit(boolean hasRlyInit) {
//
//        this.hasRlyInit = hasRlyInit;
//    }
    public boolean isConnect() {
        return connect;
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
        this.status = value;
//        if (value == GHCBStatus.online) {
//            AttachToMqTT(true);
//        }
    }

    public void getGHCBStatus() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("system_status", "true");
        PublishCommand(jsonObject.toString());

    }

    public void takePhoto(String commamd) {
        PublishCommand(commamd);
    }

    public static enum GHCBStatus {online, offline, activated, unactivated}


}
package com.hhnext.myeasylink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class DevicesActivity extends AppCompatActivity {
    protected Handler handler;
    protected ListView lv;
    protected GHCB[] mGHCBList;
    protected GHCBManage mGHCBManage;
    protected MyAdapter myAdapter;

    private void initComponent() {
        this.handler = new myHandler();
        this.lv = ((ListView) findViewById(R.id.devicesListView));
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong) {
                Intent localIntent = new Intent(DevicesActivity.this, DeviceActivity.class);
                localIntent.putExtra("com.hhnext.myeasylink.DevID", DevicesActivity.this.mGHCBList[paramAnonymousInt].getDevID());
                DevicesActivity.this.startActivity(localIntent);
            }
        });
        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
            localActionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void updateListView(Message msg) {
        GHCB localGHCB = (GHCB) msg.obj;
        int msgID = msg.arg1;
        int start = this.lv.getFirstVisiblePosition();
        int last = this.lv.getLastVisiblePosition();
        int current = localGHCB.getListViewindex();
        ViewHolder viewHolder;
        Log.i("orinoco", "device msg " + localGHCB.toString());
        if ((current <= last) && (current >= start)) {
            viewHolder = (ViewHolder) this.lv.getChildAt(current - start).getTag();
            switch (msgID) {
                case GHCBAPP.TEMPERATURE_CHANGED:
                    viewHolder.DeviceTemperature.setText(localGHCB.getTemperature());
                    break;
                case GHCBAPP.HUMIDITY_CHANGED:
                    viewHolder.DeviceHumidity.setText(localGHCB.getHumidity());
                    break;
                case GHCBAPP.LAMP_CHANGED:
                    viewHolder.DeviceLamp.setText(MyUtil.togglgText(localGHCB.isLamp()));
                    break;
                case GHCBAPP.PUMP_CHANGED:
                    viewHolder.DevicePump.setText(MyUtil.togglgText(localGHCB.isPump()));
                    break;
                case GHCBAPP.ALL_CHANGED:
                    if (this.myAdapter == null) {
                        this.myAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_devices);
        initComponent();
        this.mGHCBManage = new GHCBManage(this);
        scanDevices();

    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        getMenuInflater().inflate(R.menu.devices_menu, paramMenu);
        return super.onCreateOptionsMenu(paramMenu);
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        switch (paramMenuItem.getItemId()) {
            case android.R.id.home:
                System.exit(0);
            case R.id.action_easylink:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_setting:
                return true;
            default:
                return super.onOptionsItemSelected(paramMenuItem);
        }
    }

    protected void onPause() {
        super.onPause();
        for (GHCB localGHCB : GHCBManage.GHCBs.values()) {
            if (localGHCB.getStatus() == GHCB.GHCBStatus.online)
                localGHCB.setHandler(null);
        }

    }

    protected void onResume() {
        super.onResume();
        for (GHCB localGHCB : GHCBManage.GHCBs.values()) {
//            Log.i("orinoco","devices handler:"+handler+","+localGHCB.toString());
            if (localGHCB.getStatus() == GHCB.GHCBStatus.online)
                localGHCB.setHandler(this.handler);
        }
        if (this.myAdapter != null)
            this.myAdapter.notifyDataSetChanged();
    }

    public void scanDevices() {
//        Log.i("orinoco", "start scan");
        RequestParams localRequestParams = new RequestParams("http://easylink.io/v1/device/devices");
        localRequestParams.addHeader("content-type", "application/json");
        MyUtil.setRequestParamsHeader(localRequestParams);
        x.http().post(localRequestParams, new CommonCallback<String>() {
            public void onCancelled(CancelledException paramAnonymousCancelledException) {
            }

            public void onError(Throwable paramAnonymousThrowable, boolean paramAnonymousBoolean) {
                Log.i("orinoco", "devices reponse:" + paramAnonymousThrowable.toString());
            }

            public void onFinished() {
            }

            public void onSuccess(String jsonString) {
                JsonArray jsonArray = (JsonArray) new JsonParser().parse(jsonString);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObject = (JsonObject) jsonArray.get(i);
//                    Log.i("orinoco","device is :"+jsonObject);
                    GHCB theGHCB = new GHCB();
                    theGHCB.setOwner(APPUser.userName);
                    theGHCB.setMAC(jsonObject.get("bssid").getAsString());
                    theGHCB.setDevID(jsonObject.get("id").getAsString());
                    theGHCB.setCreateTime(jsonObject.get("created").getAsString());
                    theGHCB.setOfflineTime(jsonObject.get("offline_time").getAsString());
                    theGHCB.setOnlineTime(jsonObject.get("online_time").getAsString());
                    theGHCB.setPublicIPAddress(jsonObject.get("ip").getAsString());
                    if (jsonObject.get("online").getAsString().equals("1")) {
                        theGHCB.setStatus(GHCB.GHCBStatus.online);
                        theGHCB.setHandler(DevicesActivity.this.handler);
                    } else {
                        theGHCB.setStatus(GHCB.GHCBStatus.offline);
                    }
                    DevicesActivity.this.mGHCBManage.addGHCB(theGHCB);
                }
                DevicesActivity.this.myAdapter = new DevicesActivity.MyAdapter(DevicesActivity.this);
                DevicesActivity.this.lv.setAdapter(DevicesActivity.this.myAdapter);
            }
        });
    }

    protected class MyAdapter extends BaseAdapter {
        private Context context;

        public MyAdapter(Context ctx) {
            this.context = ctx;
            DevicesActivity.this.mGHCBList = (GHCB[]) GHCBManage.GHCBs.values().toArray(new GHCB[0]);
        }

        public int getCount() {
            return DevicesActivity.this.mGHCBList.length;
        }

        public Object getItem(int position) {
            return DevicesActivity.this.mGHCBList[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            DevicesActivity.this.mGHCBList[position].setListViewindex(position);
            DevicesActivity.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.devices, null);
                viewHolder = new DevicesActivity.ViewHolder();
                viewHolder.GHCBIndex = position;
                viewHolder.DeviceLinerLayout = ((LinearLayout) convertView.findViewById(R.id.devicesLinerLayout));
                viewHolder.DeviceDescription = ((TextView) convertView.findViewById(R.id.deviceDescription));
                viewHolder.DeviceHumidity = ((TextView) convertView.findViewById(R.id.deviceHumidity));
                viewHolder.DeviceTemperature = ((TextView) convertView.findViewById(R.id.deviceTemperature));
                viewHolder.DevicePump = ((TextView) convertView.findViewById(R.id.devicePump));
                viewHolder.DeviceLamp = ((TextView) convertView.findViewById(R.id.deviceLamp));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (DevicesActivity.ViewHolder) convertView.getTag();
            }

            viewHolder.DeviceDescription.setText("大棚：" + DevicesActivity.this.mGHCBList[position].getMAC());
            viewHolder.DeviceTemperature.setText(DevicesActivity.this.mGHCBList[position].getTemperature());
            viewHolder.DeviceHumidity.setText(DevicesActivity.this.mGHCBList[position].getHumidity());
            viewHolder.DevicePump.setText(MyUtil.togglgText(DevicesActivity.this.mGHCBList[position].isPump()));
            viewHolder.DeviceLamp.setText(MyUtil.togglgText(DevicesActivity.this.mGHCBList[position].isLamp()));


            return convertView;
        }
    }

    private class ViewHolder {
        public TextView DeviceDescription;
        public TextView DeviceHumidity;
        public TextView DeviceLamp;
        public LinearLayout DeviceLinerLayout;
        public TextView DevicePump;
        public TextView DeviceTemperature;
        public int GHCBIndex;

        private ViewHolder() {
        }
    }

    public class myHandler extends Handler {
        public myHandler() {
        }

        public void handleMessage(Message msg) {
//            Log.i("orinoco","devices get msg ");
            super.handleMessage(msg);

            if (msg.what == 0)
                updateListView(msg);

        }
    }
}
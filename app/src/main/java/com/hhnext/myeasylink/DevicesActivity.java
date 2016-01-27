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

    private void updateListView(Message paramMessage) {
        GHCB localGHCB = (GHCB) paramMessage.obj;
        int itemID = paramMessage.arg1;
        int start = this.lv.getFirstVisiblePosition();
        int last = this.lv.getLastVisiblePosition();
        int current = localGHCB.getListViewindex();
        ViewHolder viewHolder;
        if ((current <= last) && (current >= start)) {
            viewHolder = (ViewHolder) this.lv.getChildAt(current - start).getTag();
            switch (itemID) {
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
            default:
                return super.onOptionsItemSelected(paramMenuItem);
            case R.id.action_easylink:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_setting:
                return true;
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
            if (localGHCB.getStatus() == GHCB.GHCBStatus.online)
                localGHCB.setHandler(this.handler);
        }
        if (this.myAdapter != null)
            this.myAdapter.notifyDataSetChanged();
    }

    public void scanDevices() {
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

            public void onSuccess(String paramAnonymousString) {
                JsonArray localJsonArray = (JsonArray) new JsonParser().parse(paramAnonymousString);
                for (int i = 0; i < localJsonArray.size(); i++) {
                    JsonObject localJsonObject = (JsonObject) localJsonArray.get(i);
                    GHCB localGHCB = new GHCB();
                    localGHCB.setOwner(APPUser.userName);
                    localGHCB.setMAC(localJsonObject.get("bssid").getAsString());
                    localGHCB.setDevID(localJsonObject.get("id").getAsString());
                    localGHCB.setCreateTime(localJsonObject.get("created").getAsString());
                    localGHCB.setOfflineTime(localJsonObject.get("offline_time").getAsString());
                    localGHCB.setOnlineTime(localJsonObject.get("online_time").getAsString());
                    localGHCB.setPublicIPAddress(localJsonObject.get("ip").getAsString());
                    if (localJsonObject.get("online").getAsString().equals("1")) {
                        localGHCB.setStatus(GHCB.GHCBStatus.online);
                        localGHCB.setHandler(DevicesActivity.this.handler);
                    } else {
                        localGHCB.setStatus(GHCB.GHCBStatus.offline);
                    }
                    DevicesActivity.this.mGHCBManage.addGHCB(localGHCB);
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
            DevicesActivity.this.mGHCBList = (GHCB[]) GHCBManage.GHCBs.values().toArray();
        }

        public int getCount() {
            return DevicesActivity.this.mGHCBList.length;
        }

        public Object getItem(int paramInt) {
            return DevicesActivity.this.mGHCBList[paramInt];
        }

        public long getItemId(int paramInt) {
            return paramInt;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
            DevicesActivity.this.mGHCBList[paramInt].setListViewindex(paramInt);
            DevicesActivity.ViewHolder viewHolder;
            if (paramView == null) {

                paramView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.devices, null);
                viewHolder = new DevicesActivity.ViewHolder();
                viewHolder.GHCBIndex = paramInt;
                viewHolder.DeviceLinerLayout = ((LinearLayout) paramView.findViewById(R.id.devicesLinerLayout));
                viewHolder.DeviceDescription = ((TextView) paramView.findViewById(R.id.deviceDescription));
                viewHolder.DeviceHumidity = ((TextView) paramView.findViewById(R.id.deviceHumidity));
                viewHolder.DeviceTemperature = ((TextView) paramView.findViewById(R.id.deviceTemperature));
                viewHolder.DevicePump = ((TextView) paramView.findViewById(R.id.devicePump));
                viewHolder.DeviceLamp = ((TextView) paramView.findViewById(R.id.deviceLamp));
                paramView.setTag(viewHolder);
            } else {
                viewHolder = (DevicesActivity.ViewHolder) paramView.getTag();
                viewHolder.DeviceDescription.setText("大棚：" + DevicesActivity.this.mGHCBList[paramInt].getMAC());
                viewHolder.DeviceTemperature.setText(DevicesActivity.this.mGHCBList[paramInt].getTemperature());
                viewHolder.DeviceHumidity.setText(DevicesActivity.this.mGHCBList[paramInt].getHumidity());
                viewHolder.DevicePump.setText(MyUtil.togglgText(DevicesActivity.this.mGHCBList[paramInt].isPump()));
                viewHolder.DeviceLamp.setText(MyUtil.togglgText(DevicesActivity.this.mGHCBList[paramInt].isLamp()));
            }

            return paramView;
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

        public void handleMessage(Message paramMessage) {
            super.handleMessage(paramMessage);
            if (paramMessage.what == 0)
                DevicesActivity.this.updateListView(paramMessage);
        }
    }
}
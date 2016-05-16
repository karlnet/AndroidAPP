package com.hhnext.myeasylink;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class DevicesActivity extends AppCompatActivity {
    protected Handler handler;
    protected ListView lv;
    protected GHCB[] mGHCBList;
    protected GHCBManage mGHCBManage;
    protected DeviceAdapter myAdapter;
    private Gson gson = new Gson();

    private void initComponent() {
        this.handler = new myHandler();

        this.lv = ((ListView) findViewById(R.id.devicesListView));
        GHCB.deviceListView = lv;
//        mGHCBList = (GHCB[]) GHCBManage.GHCBs.values().toArray(new GHCB[0]);
//        myAdapter = new DeviceAdapter(DevicesActivity.this, mGHCBList);
//        lv.setAdapter(myAdapter);

        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceAdapter.DeviceViewHolder dv = (DeviceAdapter.DeviceViewHolder) view.getTag();
                Intent intent = new Intent(DevicesActivity.this, DeviceActivity.class);
                GHCBAPP.CURRENTGHCB = GHCBManage.GHCBs.get(dv.devId);
//                 intent.putExtra("com.hhnext.myeasylink.DevID", DevicesActivity.this.mGHCBList[position].getDevID());
                DevicesActivity.this.startActivity(intent);
            }

        });


        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
            localActionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void updateListView(Message msg) {
        GHCB m = (GHCB) msg.obj;
        int msgID = msg.arg1;
        if (msgID != m.getRly1() && msgID != m.getRly2() && msgID != m.getInBoardTempPort())
            return;

        m.refreshListView();

        if (msgID == GHCBAPP.ALL_CHANGED) {
            this.myAdapter.notifyDataSetChanged();

        }
//        int t=localGHCB.getInBoardTempPort();
//        int t2=localGHCB.getRly1();
//        int t4=localGHCB.getRly2();
//        if (t!=msgID) return;
//        if (msgID != localGHCB.getInBoardTempPort() && msgID != localGHCB.getRly1() && msgID != localGHCB.getRly2())
//            return;
//        int start = this.lv.getFirstVisiblePosition();
//        int last = this.lv.getLastVisiblePosition();
//        int current = localGHCB.getListViewindex();
//        ViewHolder viewHolder;
//        Log.i("orinoco", "device msg " + localGHCB.toString());
//        if ((current <= last) && (current >= start)) {
//            viewHolder = (ViewHolder) this.lv.getChildAt(current - start).getTag();
//
//            String res = localGHCB.getPorts().get(String.valueOf(msgID)).getUploadData();
////            TempPort t = (TempPort)localGHCB.getPorts().get(String.valueOf(msgID));
//
//            if (msgID == localGHCB.getInBoardTempPort()) {
//                String[] s = res.split(":");
//                viewHolder.DeviceTemperature.setText(String.valueOf(s[0]));
//                viewHolder.DeviceHumidity.setText(String.valueOf(s[1]));
//            }
//
//            if (msgID == localGHCB.getRly1()) {
//                Boolean b = Boolean.valueOf(res);
//                viewHolder.DeviceLamp.setText(MyUtil.togglgText(b));
//            }
//            if (msgID == localGHCB.getRly2()) {
//                Boolean b = Boolean.valueOf(res);
//                viewHolder.DeviceLamp.setText(MyUtil.togglgText(b));
//            }
//            if (msgID == GHCBAPP.ALL_CHANGED) {
//                if (this.myAdapter == null) {
//                    this.myAdapter.notifyDataSetChanged();
//                }
//            }
//
//        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.devices_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                System.exit(0);
            case R.id.action_easylink:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.action_setting:
                break;
            case R.id.action_binding:
                startActivity(new Intent(this, BindingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(menuItem);

    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_devices);
        initComponent();
        GHCBManage.setCtx(DevicesActivity.this);
        if (GHCBManage.GHCBs.size() == 0)
            scanDevices();

    }

    protected void onPause() {
        super.onPause();

        for (GHCB localGHCB : GHCBManage.GHCBs.values()) {
            if (localGHCB.getStatus() == GHCB.GHCBStatus.online) {
                localGHCB.setHandler(null);
//                localGHCB.AttachToMqTT(false);
            }
        }

    }

    protected void onResume() {
        super.onResume();
//        GHCBManage.setCtx( DevicesActivity.this);
        for (GHCB localGHCB : GHCBManage.GHCBs.values()) {
//            Log.i("orinoco","devices handler:"+handler+","+localGHCB.toString());
            if (localGHCB.getStatus() == GHCB.GHCBStatus.online) {
                localGHCB.setHandler(this.handler);
                localGHCB.AttachToMqTT(true);
            }
        }
        if (myAdapter == null) {
            myAdapter = new DeviceAdapter(DevicesActivity.this/*, mGHCBList*/);
            lv.setAdapter(myAdapter);
        }
    }


    public void scanDevices() {
//        Log.i("orinoco", "start scan");
        RequestParams requestParams = new RequestParams(APPUser.devicesURL);

        MyUtil.setRequestParamsHeader(requestParams);
        x.http().post(requestParams, new CommonCallback<String>() {
            public void onCancelled(CancelledException paramAnonymousCancelledException) {
            }

            public void onError(Throwable paramAnonymousThrowable, boolean paramAnonymousBoolean) {
//                Log.i("orinoco", "devices reponse:" + paramAnonymousThrowable.toString());
            }

            public void onFinished() {
            }

            public void onSuccess(String jsonString) {
//                Log.i("orinoco", "jsonString :" + jsonString);
                List<GHCBModel> GHCBModelsList = gson.fromJson(jsonString, new TypeToken<List<GHCBModel>>() {
                }.getType());

                for (GHCBModel m : GHCBModelsList
                        ) {
                    GHCB theGHCB = new GHCB();
                    theGHCB.setFromModel(m);
//                    theGHCB.setGHCBManage(DevicesActivity.this.mGHCBManage);
//                    theGHCB.setOwner(APPUser.UserName);
                    if (m.online.equals("1")) {
                        theGHCB.setHandler(DevicesActivity.this.handler);
                        theGHCB.setStatus(GHCB.GHCBStatus.online);
                    } else
                        theGHCB.setStatus(GHCB.GHCBStatus.offline);

                    GHCBManage.addGHCB(theGHCB);
                }

//                mGHCBList = (GHCB[]) GHCBManage.GHCBs.values().toArray(new GHCB[0]);
                myAdapter = new DeviceAdapter(DevicesActivity.this/*, mGHCBList*/);
                lv.setAdapter(myAdapter);
//                myAdapter.notifyDataSetChanged();


//                for (GHCB m:mGHCBList
//                        ) {
//                    m.getGHCBStatus();
//                }

            }
        });
    }

//    protected class DeviceAdapter extends BaseAdapter {
//        private Context context;
//
//        public MyAdapter(Context ctx) {
//            this.context = ctx;
//            DevicesActivity.this.mGHCBList = (GHCB[]) GHCBManage.GHCBs.values().toArray(new GHCB[0]);
//        }
//
//        public int getCount() {
//            return DevicesActivity.this.mGHCBList.length;
//        }
//
//        public Object getItem(int position) {
//            return DevicesActivity.this.mGHCBList[position];
//        }
//
//        public long getItemId(int position) {
//            return position;
//        }
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            GHCB mm = DevicesActivity.this.mGHCBList[position];
//            mm.setListViewindex(position);
//            DevicesActivity.ViewHolder viewHolder;
//            if (convertView == null) {
//                convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.devices, null);
//                viewHolder = new DevicesActivity.ViewHolder();
//                viewHolder.GHCBIndex = position;
//                viewHolder.DeviceLinerLayout = ((LinearLayout) convertView.findViewById(R.id.devicesLinerLayout));
//                viewHolder.DeviceDescription = ((TextView) convertView.findViewById(R.id.deviceDescription));
//                viewHolder.Rly1Description = ((TextView) convertView.findViewById(R.id.rly1Description));
//                viewHolder.Rly2Description = ((TextView) convertView.findViewById(R.id.rly2Description));
//                viewHolder.DeviceHumidity = ((TextView) convertView.findViewById(R.id.deviceHumidity));
//                viewHolder.DeviceTemperature = ((TextView) convertView.findViewById(R.id.deviceTemperature));
//                viewHolder.DevicePump = ((TextView) convertView.findViewById(R.id.devicePump));
//                viewHolder.DeviceLamp = ((TextView) convertView.findViewById(R.id.deviceLamp));
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (DevicesActivity.ViewHolder) convertView.getTag();
//            }
//
//            viewHolder.DeviceDescription.setText(mGHCBList[position].getDevAlias());
//
//            int no = mm.getInBoardTempPort();
//            if (no != -1) {
//                Port p = mm.getPorts().get(String.valueOf(no));
//                String res = p.getUploadData();
//                String[] s = res.split(":");
//                viewHolder.DeviceTemperature.setText(s[0]);
//                viewHolder.DeviceHumidity.setText(s[1]);
//            }
//
//            no = mm.getRly1();
//            if (no != -1) {
//                RlyPort t = (RlyPort) mm.getPorts().get(String.valueOf(no));
//                viewHolder.DevicePump.setText(MyUtil.togglgText(t.isRlyState()));
//                viewHolder.Rly1Description.setText(t.getPortName() + "：");
//            }else{
//                viewHolder.DevicePump.setText("  ");
//                viewHolder.Rly1Description.setText("  ");
//
//            }
//
//            no = mm.getRly2();
//            if (no != -1) {
//                RlyPort t = (RlyPort) mm.getPorts().get(String.valueOf(no));
//                viewHolder.DeviceLamp.setText(MyUtil.togglgText(t.isRlyState()));
//                viewHolder.Rly2Description.setText(t.getPortName() + "：");
//            }else{
//                viewHolder.DeviceLamp.setText("  ");
//                viewHolder.Rly2Description.setText("  ");
//
//            }
//
//            return convertView;
//        }
//
//    }
//
//    private class ViewHolder {
//        public TextView DeviceDescription;
//        public TextView Rly1Description;
//        public TextView Rly2Description;
//        public TextView DeviceHumidity;
//        public TextView DeviceLamp;
//        public LinearLayout DeviceLinerLayout;
//        public TextView DevicePump;
//        public TextView DeviceTemperature;
//        public int GHCBIndex;
//
//        private ViewHolder() {
//        }
//    }

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
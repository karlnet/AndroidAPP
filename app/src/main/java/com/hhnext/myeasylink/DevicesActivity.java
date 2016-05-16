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

public class DevicesActivity extends AppCompatActivity implements RefreshActivity {
    protected Handler handler;
    protected ListView listView;
    protected GHCB[] mGHCBList;
    //    protected GHCBManage mGHCBManage;
    protected DeviceAdapter myAdapter;


    private void initComponent() {
        this.handler = new myHandler();

        this.listView = ((ListView) findViewById(R.id.devicesListView));
//        GHCB.deviceListView = lv;
//        mGHCBList = (GHCB[]) GHCBManage.GHCBs.values().toArray(new GHCB[0]);
//        myAdapter = new DeviceAdapter(DevicesActivity.this, mGHCBList);
//        lv.setAdapter(myAdapter);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        m.refresh(listView);

        if (msgID == GHCBAPP.ALL_CHANGED) {
            this.myAdapter.notifyDataSetChanged();

        }

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.devices_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent localIntent = new Intent(this, LoginActivity.class);
                localIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(localIntent);
                return true;
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

//        mGHCBManage=new GHCBManage();
        GHCBManage.setContext(this);
        GHCBManage.setRefreshActivity(this);

        if (GHCBManage.GHCBs.size() == 0) {
            GHCBManage.getDevicesFromCloud();
        } else {
            refreshRlyTempListView();
        }

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
        for (GHCB g : GHCBManage.GHCBs.values()) {
//            Log.i("orinoco","devices handler:"+handler+","+localGHCB.toString());
            if (g.getStatus() == GHCB.GHCBStatus.online) {
                g.setHandler(this.handler);
                g.AttachToMqTT(true);
            }
        }

    }

    public void refreshRlyTempListView() {
        if (myAdapter == null) {
            if (GHCBManage.GHCBs.size() > 0) {
                myAdapter = new DeviceAdapter(this);
                listView.setAdapter(myAdapter);
            }
        } else {
            myAdapter.notifyDataSetChanged();
        }
    }

    public void refreshPortDescriptionListView() {
    }

    public void reName() {
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
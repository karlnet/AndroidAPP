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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.xutils.HttpManager;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class DevicesActivity extends AppCompatActivity
{
    protected Handler handler;
    protected ListView lv;
    protected GHCB[] mGHCBList;
    protected GHCBManage mGHCBManage;
    protected MyAdapter myAdapter;

    private void initComponent()
    {
        this.handler = new myHandler();
        this.lv = ((ListView)findViewById(2131492980));
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
            {
                Intent localIntent = new Intent(DevicesActivity.this, DeviceActivity.class);
                localIntent.putExtra("com.hhnext.myeasylink.DevID", DevicesActivity.this.mGHCBList[paramAnonymousInt].getDevID());
                DevicesActivity.this.startActivity(localIntent);
            }
        });
        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
            localActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void updateListView(Message paramMessage)
    {
        GHCB localGHCB = (GHCB)paramMessage.obj;
        int i = paramMessage.arg1;
        int j = this.lv.getFirstVisiblePosition();
        int k = this.lv.getLastVisiblePosition();
        int m = localGHCB.getListViewindex();
        ViewHolder localViewHolder;
        if ((m <= k) && (m >= j))
        {
            localViewHolder = (ViewHolder)this.lv.getChildAt(m - j).getTag();
            switch (i)
            {
                default:
                case 2:
                case 1:
                case 3:
                case 4:
                case 5:
            }
        }
        do
        {
            return;
            localViewHolder.DeviceHumidity.setText(localGHCB.getHumidity());
            return;
            localViewHolder.DeviceTemperature.setText(localGHCB.getTemperature());
            return;
            localViewHolder.DeviceLamp.setText(MyUtil.togglgText(localGHCB.isLamp()));
            return;
            localViewHolder.DevicePump.setText(MyUtil.togglgText(localGHCB.isPump()));
            return;
        }
        while (this.myAdapter == null);
        this.myAdapter.notifyDataSetChanged();
    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.);
        initComponent();
        this.mGHCBManage = new GHCBManage(this);
        scanDevices();
    }

    public boolean onCreateOptionsMenu(Menu paramMenu)
    {
        getMenuInflater().inflate(2131558400, paramMenu);
        return super.onCreateOptionsMenu(paramMenu);
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem)
    {
        switch (paramMenuItem.getItemId())
        {
            default:
            case 2131493035:
            case 16908332:
        }
        while (true)
        {
            return super.onOptionsItemSelected(paramMenuItem);
            startActivity(new Intent(this, MainActivity.class));
            continue;
            System.exit(0);
        }
    }

    protected void onPause()
    {
        super.onPause();
        Iterator localIterator = GHCBManage.GHCBs.values().iterator();
        while (localIterator.hasNext())
        {
            GHCB localGHCB = (GHCB)localIterator.next();
            if (localGHCB.getStatus() == GHCB.GHCBStatus.online)
                localGHCB.setHandler(null);
        }
    }

    protected void onResume()
    {
        super.onResume();
        Iterator localIterator = GHCBManage.GHCBs.values().iterator();
        while (localIterator.hasNext())
        {
            GHCB localGHCB = (GHCB)localIterator.next();
            if (localGHCB.getStatus() == GHCB.GHCBStatus.online)
                localGHCB.setHandler(this.handler);
        }
        if (this.myAdapter != null)
            this.myAdapter.notifyDataSetChanged();
    }

    public void scanDevices()
    {
        RequestParams localRequestParams = new RequestParams("http://easylink.io/v1/device/devices");
        localRequestParams.addHeader("content-type", "application/json");
        MyUtil.setRequestParamsHeader(localRequestParams);
        x.http().post(localRequestParams, new Callback.CommonCallback()
        {
            public void onCancelled(Callback.CancelledException paramAnonymousCancelledException)
            {
            }

            public void onError(Throwable paramAnonymousThrowable, boolean paramAnonymousBoolean)
            {
                Log.i("orinoco", "devices reponse:" + paramAnonymousThrowable.toString());
            }

            public void onFinished()
            {
            }

            public void onSuccess(String paramAnonymousString)
            {
                JsonArray localJsonArray = (JsonArray)new JsonParser().parse(paramAnonymousString);
                int i = 0;
                if (i < localJsonArray.size())
                {
                    JsonObject localJsonObject = (JsonObject)localJsonArray.get(i);
                    GHCB localGHCB = new GHCB();
                    localGHCB.setOwner(APPUser.userName);
                    localGHCB.setMAC(localJsonObject.get("bssid").getAsString());
                    localGHCB.setDevID(localJsonObject.get("id").getAsString());
                    localGHCB.setCreateTime(localJsonObject.get("created").getAsString());
                    localGHCB.setOfflineTime(localJsonObject.get("offline_time").getAsString());
                    localGHCB.setOnlineTime(localJsonObject.get("online_time").getAsString());
                    localGHCB.setPublicIPAddress(localJsonObject.get("ip").getAsString());
                    if (localJsonObject.get("online").getAsString().equals("1"))
                    {
                        localGHCB.setStatus(GHCB.GHCBStatus.online);
                        localGHCB.setHandler(DevicesActivity.this.handler);
                    }
                    while (true)
                    {
                        DevicesActivity.this.mGHCBManage.addGHCB(localGHCB);
                        i++;
                        break;
                        localGHCB.setStatus(GHCB.GHCBStatus.offline);
                    }
                }
                DevicesActivity.this.myAdapter = new DevicesActivity.MyAdapter(DevicesActivity.this, DevicesActivity.this);
                DevicesActivity.this.lv.setAdapter(DevicesActivity.this.myAdapter);
            }
        });
    }

    protected class MyAdapter extends BaseAdapter
    {
        private Context context;

        public MyAdapter(Context arg2)
        {
            Object localObject;
            this.context = localObject;
            DevicesActivity.this.mGHCBList = ((GHCB[])GHCBManage.GHCBs.values().toArray(new GHCB[0]));
        }

        public int getCount()
        {
            return DevicesActivity.this.mGHCBList.length;
        }

        public Object getItem(int paramInt)
        {
            return DevicesActivity.this.mGHCBList[paramInt];
        }

        public long getItemId(int paramInt)
        {
            return paramInt;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
        {
            DevicesActivity.this.mGHCBList[paramInt].setListViewindex(paramInt);
            DevicesActivity.ViewHolder localViewHolder;
            if (paramView == null)
            {
                paramView = ((LayoutInflater)this.context.getSystemService("layout_inflater")).inflate(2130968617, null);
                localViewHolder = new DevicesActivity.ViewHolder(DevicesActivity.this, null);
                localViewHolder.DeviceLinerLayout = ((LinearLayout)paramView.findViewById(2131493014));
                localViewHolder.DeviceDescription = ((TextView)paramView.findViewById(2131493016));
                localViewHolder.DeviceHumidity = ((TextView)paramView.findViewById(2131493018));
                localViewHolder.DeviceTemperature = ((TextView)paramView.findViewById(2131493017));
                localViewHolder.DevicePump = ((TextView)paramView.findViewById(2131493020));
                localViewHolder.DeviceLamp = ((TextView)paramView.findViewById(2131493019));
                paramView.setTag(localViewHolder);
            }
            while (true)
            {
                localViewHolder.DeviceDescription.setText("大棚：" + DevicesActivity.this.mGHCBList[paramInt].getMAC());
                localViewHolder.DeviceTemperature.setText(DevicesActivity.this.mGHCBList[paramInt].getTemperature());
                localViewHolder.DeviceHumidity.setText(DevicesActivity.this.mGHCBList[paramInt].getHumidity());
                localViewHolder.DevicePump.setText(MyUtil.togglgText(DevicesActivity.this.mGHCBList[paramInt].isPump()));
                localViewHolder.DeviceLamp.setText(MyUtil.togglgText(DevicesActivity.this.mGHCBList[paramInt].isLamp()));
                return paramView;
                localViewHolder = (DevicesActivity.ViewHolder)paramView.getTag();
            }
        }
    }

    private class ViewHolder
    {
        public TextView DeviceDescription;
        public TextView DeviceHumidity;
        public TextView DeviceLamp;
        public LinearLayout DeviceLinerLayout;
        public TextView DevicePump;
        public TextView DeviceTemperature;

        private ViewHolder()
        {
        }
    }

    public class myHandler extends Handler
    {
        public myHandler()
        {
        }

        public void handleMessage(Message paramMessage)
        {
            super.handleMessage(paramMessage);
            if (paramMessage.what == 0)
                DevicesActivity.this.updateListView(paramMessage);
        }
    }
}
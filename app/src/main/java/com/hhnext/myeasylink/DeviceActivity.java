package com.hhnext.myeasylink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.concurrent.ConcurrentHashMap;
import org.xutils.ImageManager;
import org.xutils.image.ImageOptions;
import org.xutils.image.ImageOptions.Builder;
import org.xutils.x;

public class DeviceActivity extends AppCompatActivity
{
    private TextView deviceDetails;
    private ImageView deviceImage;
    protected myHandler handler;
    private TextView humidity;
    private TextView lamp;
    private ListView listView;
    protected GHCB mGHCB;
    private TextView pump;
    private SpeechListAdapter speechListAdapter;
    private ImageButton startGetImageButton;
    private TextView temperature;
    private ToggleButton toggleLamp;
    private ToggleButton togglepump;

    private void initComponent()
    {
        this.handler = new myHandler();
        this.temperature = ((TextView)findViewById(R.id.));
        this.humidity = ((TextView)findViewById(2131492974));
        this.lamp = ((TextView)findViewById(2131492975));
        this.pump = ((TextView)findViewById(2131492976));
        this.deviceImage = ((ImageView)findViewById(2131492971));
        this.startGetImageButton = ((ImageButton)findViewById(2131492972));
        this.startGetImageButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                ImageOptions localImageOptions = new ImageOptions.Builder().setSize(640, 480).setPlaceholderScaleType(ImageView.ScaleType.MATRIX).setImageScaleType(ImageView.ScaleType.CENTER).build();
                x.image().bind(DeviceActivity.this.deviceImage, "http://7xq5wl.com1.z0.glb.clouddn.com/car.jpg", localImageOptions);
            }
        });
        this.toggleLamp = ((ToggleButton)findViewById(2131492977));
        this.toggleLamp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
            {
                if (!paramAnonymousBoolean)
                {
                    DeviceActivity.this.mGHCB.lampON();
                    return;
                }
                DeviceActivity.this.mGHCB.lampOFF();
            }
        });
        this.togglepump = ((ToggleButton)findViewById(2131492978));
        this.togglepump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
            {
                if (!paramAnonymousBoolean)
                {
                    DeviceActivity.this.mGHCB.pumpON();
                    return;
                }
                DeviceActivity.this.mGHCB.pumpOFF();
            }
        });
        this.listView = ((ListView)findViewById(2131492979));
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
            {
                ((DeviceActivity.SpeechListAdapter)DeviceActivity.this.listView.getAdapter()).toggle(paramAnonymousInt);
            }
        });
        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
            localActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void refresUI()
    {
        boolean bool1 = true;
        this.humidity.setText(this.mGHCB.getHumidity());
        this.temperature.setText(this.mGHCB.getTemperature());
        this.lamp.setText(MyUtil.togglgText(this.mGHCB.isLamp()));
        this.pump.setText(MyUtil.togglgText(this.mGHCB.isPump()));
        ToggleButton localToggleButton1 = this.toggleLamp;
        boolean bool2;
        ToggleButton localToggleButton2;
        if (!this.mGHCB.isLamp())
        {
            bool2 = bool1;
            localToggleButton1.setChecked(bool2);
            localToggleButton2 = this.togglepump;
            if (this.mGHCB.isPump())
                break label128;
        }
        while (true)
        {
            localToggleButton2.setChecked(bool1);
            if (this.speechListAdapter != null)
                this.speechListAdapter.notifyDataSetChanged();
            return;
            bool2 = false;
            break;
            label128: bool1 = false;
        }
    }

    private void updateView(Message paramMessage)
    {
        switch (paramMessage.arg1)
        {
            default:
                return;
            case 2:
                this.humidity.setText(this.mGHCB.getHumidity());
                return;
            case 1:
                this.temperature.setText(this.mGHCB.getTemperature());
                return;
            case 3:
                this.lamp.setText(MyUtil.togglgText(this.mGHCB.isLamp()));
                return;
            case 4:
                this.pump.setText(MyUtil.togglgText(this.mGHCB.isPump()));
                return;
            case 5:
        }
        refresUI();
    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(2130968601);
        initComponent();
        this.mGHCB = ((GHCB)GHCBManage.GHCBs.get(getIntent().getStringExtra("com.hhnext.myeasylink.DevID")));
        this.speechListAdapter = new SpeechListAdapter(this);
        this.listView.setAdapter(this.speechListAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem)
    {
        switch (paramMenuItem.getItemId())
        {
            default:
            case 16908332:
        }
        while (true)
        {
            return super.onOptionsItemSelected(paramMenuItem);
            Intent localIntent = new Intent(this, DevicesActivity.class);
            localIntent.setFlags(67108864);
            startActivity(localIntent);
        }
    }

    protected void onPause()
    {
        super.onPause();
        this.mGHCB.setHandler(null);
    }

    protected void onResume()
    {
        super.onResume();
        refresUI();
        this.mGHCB.setHandler(this.handler);
    }

    private class SpeechListAdapter extends BaseAdapter
    {
        private Context mContext;
        private String[] mDialogue = new String[1];
        private boolean[] mExpanded = { false };
        private String[] mTitles = { "设备详细信息：" };

        public SpeechListAdapter(Context arg2)
        {
            Object localObject;
            this.mContext = localObject;
            this.mDialogue[0] = ("设备MAC:" + DeviceActivity.this.mGHCB.getMAC() + "\r\n设备编号:" + DeviceActivity.this.mGHCB.getDevID() + "\r\n设备创建时间:" + DeviceActivity.this.mGHCB.getCreateTime() + "\r\n设备离线时间:" + DeviceActivity.this.mGHCB.getOfflineTime() + "\r\n设备上线时间:" + DeviceActivity.this.mGHCB.getOnlineTime());
        }

        public int getCount()
        {
            return this.mTitles.length;
        }

        public Object getItem(int paramInt)
        {
            return Integer.valueOf(paramInt);
        }

        public long getItemId(int paramInt)
        {
            return paramInt;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
        {
            if (paramView == null)
                return new DeviceActivity.SpeechView(DeviceActivity.this, this.mContext, this.mTitles[paramInt], this.mDialogue[paramInt], this.mExpanded[paramInt]);
            DeviceActivity.SpeechView localSpeechView = (DeviceActivity.SpeechView)paramView;
            localSpeechView.setTitle(this.mTitles[paramInt]);
            localSpeechView.setDialogue(this.mDialogue[paramInt]);
            localSpeechView.setExpanded(this.mExpanded[paramInt]);
            return localSpeechView;
        }

        public void toggle(int paramInt)
        {
            boolean[] arrayOfBoolean = this.mExpanded;
            if (this.mExpanded[paramInt] == 0);
            for (int i = 1; ; i = 0)
            {
                arrayOfBoolean[paramInt] = i;
                notifyDataSetChanged();
                return;
            }
        }
    }

    private class SpeechView extends LinearLayout
    {
        private TextView mDialogue;
        private TextView mTitle;

        public SpeechView(Context paramString1, String paramString2, String paramBoolean, boolean arg5)
        {
            super();
            setOrientation(1);
            this.mTitle = new TextView(paramString1);
            this.mTitle.setText(paramString2);
            addView(this.mTitle, new LinearLayout.LayoutParams(-1, -2));
            this.mTitle.setTextSize(20.0F);
            this.mDialogue = new TextView(paramString1);
            this.mDialogue.setText(paramBoolean);
            this.mDialogue.setTextSize(18.0F);
            this.mDialogue.setLineSpacing(2.0F, 1.2F);
            LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, -2);
            localLayoutParams.topMargin = 10;
            addView(this.mDialogue, localLayoutParams);
            TextView localTextView = this.mDialogue;
            int i;
            if (i != 0);
            for (int j = 0; ; j = 8)
            {
                localTextView.setVisibility(j);
                return;
            }
        }

        public void setDialogue(String paramString)
        {
            this.mDialogue.setText(paramString);
        }

        public void setExpanded(boolean paramBoolean)
        {
            TextView localTextView = this.mDialogue;
            if (paramBoolean);
            for (int i = 0; ; i = 8)
            {
                localTextView.setVisibility(i);
                return;
            }
        }

        public void setTitle(String paramString)
        {
            this.mTitle.setText(paramString);
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
                DeviceActivity.this.updateView(paramMessage);
        }
    }
}
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.xutils.image.ImageOptions;
import org.xutils.x;

public class DeviceActivity extends AppCompatActivity {
    protected myHandler handler;
    protected GHCB mGHCB;

    private ImageView cameraImage;
    private TextView temperature, humidity, lamp, pump;
    private ListView listView;
    private SpeechListAdapter speechListAdapter;
    private ImageButton startCameraButton;
    private ToggleButton toggleLamp, togglepump;


    private void initComponent() {
        this.handler = new myHandler();
        this.temperature = ((TextView) findViewById(R.id.temperature));
        this.humidity = ((TextView) findViewById(R.id.humidity));
        this.lamp = ((TextView) findViewById(R.id.lamp));
        this.pump = ((TextView) findViewById(R.id.pump));
        this.cameraImage = ((ImageView) findViewById(R.id.cameraImage));
        this.startCameraButton = ((ImageButton) findViewById(R.id.cameraButton));
        this.startCameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                ImageOptions localImageOptions = new ImageOptions.Builder().setSize(640, 480).setPlaceholderScaleType(ImageView.ScaleType.MATRIX).setImageScaleType(ImageView.ScaleType.CENTER).build();
                x.image().bind(DeviceActivity.this.cameraImage, "http://7xq5wl.com1.z0.glb.clouddn.com/car.jpg", localImageOptions);
            }
        });
        this.toggleLamp = ((ToggleButton) findViewById(R.id.toggleLamp));
        this.toggleLamp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean) {
                if (!paramAnonymousBoolean) {
                    DeviceActivity.this.mGHCB.lampON();
                    return;
                }
                DeviceActivity.this.mGHCB.lampOFF();
            }
        });
        this.togglepump = ((ToggleButton) findViewById(R.id.togglePump));
        this.togglepump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean paramAnonymousBoolean) {
                if (!paramAnonymousBoolean) {
                    DeviceActivity.this.mGHCB.pumpON();
                    return;
                }
                DeviceActivity.this.mGHCB.pumpOFF();
            }
        });
        this.listView = ((ListView) findViewById(R.id.descriptionListView));
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View l, int position, long id) {
                ((SpeechListAdapter) listView.getAdapter()).toggle(position);
            }
        });
        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
            localActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void refresUI() {

        this.humidity.setText(this.mGHCB.getHumidity());
        this.temperature.setText(this.mGHCB.getTemperature());
        this.lamp.setText(MyUtil.togglgText(this.mGHCB.isLamp()));
        this.pump.setText(MyUtil.togglgText(this.mGHCB.isPump()));

        this.toggleLamp.setChecked(mGHCB.isLamp());
        this.togglepump.setChecked(mGHCB.isPump());

        if (this.speechListAdapter != null)
            this.speechListAdapter.notifyDataSetChanged();
    }

    private void updateView(Message msg) {
        switch (msg.arg1) {
            case GHCBAPP.HUMIDITY_CHANGED:
                this.humidity.setText(this.mGHCB.getHumidity());
                return;
            case GHCBAPP.TEMPERATURE_CHANGED:
                this.temperature.setText(this.mGHCB.getTemperature());
                return;
            case GHCBAPP.LAMP_CHANGED:
                this.lamp.setText(MyUtil.togglgText(this.mGHCB.isLamp()));
                return;
            case GHCBAPP.PUMP_CHANGED:
                this.pump.setText(MyUtil.togglgText(this.mGHCB.isPump()));
                return;
            case GHCBAPP.ALL_CHANGED:
                refresUI();
                return;
            default:
                return;
        }
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_devices);
        initComponent();
        this.mGHCB = GHCBManage.GHCBs.get(getIntent().getStringExtra("com.hhnext.myeasylink.DevID"));
        this.speechListAdapter = new SpeechListAdapter(this);
        this.listView.setAdapter(this.speechListAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        switch (paramMenuItem.getItemId()) {
            default:
                return super.onOptionsItemSelected(paramMenuItem);
            case android.R.id.home:
                Intent localIntent = new Intent(this, DevicesActivity.class);
                localIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(localIntent);
                return true;
        }
    }

    protected void onPause() {
        super.onPause();
        this.mGHCB.setHandler(null);
    }

    protected void onResume() {
        super.onResume();
        refresUI();
        this.mGHCB.setHandler(this.handler);
    }

    private class SpeechListAdapter extends BaseAdapter {
        private Context mContext;
        private String[] mDialogue = new String[1];
        private boolean[] mExpanded = {false};
        private String[] mTitles = {"设备详细信息："};

        public SpeechListAdapter(Context ctx) {
            this.mContext = ctx;
            this.mDialogue[0] = ("设备MAC:" + DeviceActivity.this.mGHCB.getMAC() + "\r\n设备编号:" + DeviceActivity.this.mGHCB.getDevID() + "\r\n设备创建时间:" + DeviceActivity.this.mGHCB.getCreateTime() + "\r\n设备离线时间:" + DeviceActivity.this.mGHCB.getOfflineTime() + "\r\n设备上线时间:" + DeviceActivity.this.mGHCB.getOnlineTime());
        }

        public int getCount() {
            return this.mTitles.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                return new DeviceActivity.SpeechView(this.mContext, this.mTitles[position], this.mDialogue[position], this.mExpanded[position]);
            DeviceActivity.SpeechView localSpeechView = (DeviceActivity.SpeechView) convertView;
            localSpeechView.setTitle(this.mTitles[position]);
            localSpeechView.setDialogue(this.mDialogue[position]);
            localSpeechView.setExpanded(this.mExpanded[position]);
            return localSpeechView;
        }

        public void toggle(int position) {
            mExpanded[position] = !mExpanded[position];
            notifyDataSetChanged();
        }
    }

    private class SpeechView extends LinearLayout {
        private TextView mDialogue;
        private TextView mTitle;

        public SpeechView(Context ctx, String title, String dialogue, boolean expanded) {
            super(ctx);
            setOrientation(VERTICAL);
            this.mTitle = new TextView(ctx);
            this.mTitle.setText(title);
            this.mTitle.setTextSize(20.0F);
            addView(this.mTitle, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            this.mDialogue = new TextView(ctx);
            this.mDialogue.setText(dialogue);
            this.mDialogue.setTextSize(18.0F);
            this.mDialogue.setLineSpacing(2.0F, 1.2F);
            LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            localLayoutParams.topMargin = 10;
            addView(this.mDialogue, localLayoutParams);

            mDialogue.setVisibility(expanded ? VISIBLE : GONE);
        }


        public void setExpanded(boolean expanded) {
            mDialogue.setVisibility(expanded ? VISIBLE : GONE);
        }

        public void setDialogue(String words) {
            this.mDialogue.setText(words);
        }

        public void setTitle(String title) {
            this.mTitle.setText(title);
        }
    }

    public class myHandler extends Handler {
        public myHandler() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0)
                DeviceActivity.this.updateView(msg);
        }
    }
}
package com.hhnext.myeasylink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import com.google.gson.JsonObject;
import com.qiniu.android.storage.UploadManager;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DeviceActivity extends AppCompatActivity {
    protected myHandler handler;
    protected GHCB mGHCB;

    UploadManager uploadManager = new UploadManager();
    String key, token;

    private TextView temperature, humidity, lamp, pump;
    private ImageView cameraImage;
    private ImageButton cameraButton;
    private ToggleButton toggleLamp, togglepump;
    private ListView listView;
    private SpeechListAdapter speechListAdapter;


    private void initComponent() {
        this.handler = new myHandler();
        this.temperature = ((TextView) findViewById(R.id.temperature));
        this.humidity = ((TextView) findViewById(R.id.humidity));
        this.lamp = ((TextView) findViewById(R.id.lamp));
        this.pump = ((TextView) findViewById(R.id.pump));
        this.cameraImage = ((ImageView) findViewById(R.id.cameraImage));
        this.cameraButton = ((ImageButton) findViewById(R.id.cameraButton));
        this.cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {

                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//                String key = "//"+mGHCB.getMAC()+"//"+APPUser.userName+"//"+df.format(new Date())+".jpg";
                key = df.format(new Date()) + ".jpg";
                token = APPUser.QiniuAuth.uploadToken(APPUser.bucket, key);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("key", key);
                jsonObject.addProperty("token", token);
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.add("take_photo", jsonObject);
                mGHCB.takePhoto(jsonObject1.toString());
                Log.i("orinoco", jsonObject1.toString());

               /* AssetManager a = getAssets();
                byte[] data = null;
                try {
                    InputStream is = a.open("yunan001.jpg");
                    data = new byte[is.available()];
                    is.read(data);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                uploadManager.put(data, key, token, new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        Log.i("orinoco", response.toString());
                    }
                }, null);*/
//                ImageOptions localImageOptions = new ImageOptions.Builder().setSize(640, 480).setPlaceholderScaleType(ImageView.ScaleType.MATRIX).setImageScaleType(ImageView.ScaleType.CENTER).build();
//                x.image().bind(DeviceActivity.this.cameraImage, "http://7xq5wl.com1.z0.glb.clouddn.com/images/yunan001.jpg", localImageOptions);
            }
        });
        this.toggleLamp = ((ToggleButton) findViewById(R.id.toggleLamp));
        this.toggleLamp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean) {
                if (!paramAnonymousBoolean) {
                    mGHCB.lampON();
                    return;
                }
                mGHCB.lampOFF();
            }
        });
        this.togglepump = ((ToggleButton) findViewById(R.id.togglePump));
        this.togglepump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean paramAnonymousBoolean) {
                if (!paramAnonymousBoolean) {
                    mGHCB.pumpON();
                    return;
                }
                mGHCB.pumpOFF();
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

        humidity.setText(mGHCB.getHumidity());
        temperature.setText(mGHCB.getTemperature());
        lamp.setText(MyUtil.togglgText(mGHCB.isLamp()));
        pump.setText(MyUtil.togglgText(mGHCB.isPump()));

        toggleLamp.setChecked(!mGHCB.isLamp());
        togglepump.setChecked(!mGHCB.isPump());

        if (speechListAdapter != null)
            speechListAdapter.notifyDataSetChanged();
    }

    private void updateView(Message msg) {
        int msgID = msg.arg1;
        switch (msgID) {
            case GHCBAPP.HUMIDITY_CHANGED:
                this.humidity.setText(mGHCB.getHumidity());
                return;
            case GHCBAPP.TEMPERATURE_CHANGED:
                temperature.setText(mGHCB.getTemperature());
                return;
            case GHCBAPP.LAMP_CHANGED:
                lamp.setText(MyUtil.togglgText(mGHCB.isLamp()));
                return;
            case GHCBAPP.PUMP_CHANGED:
                pump.setText(MyUtil.togglgText(mGHCB.isPump()));
                return;
            case GHCBAPP.HASIMAGE_CHANGED:
                ImageOptions localImageOptions = new ImageOptions.Builder().setPlaceholderScaleType(ImageView.ScaleType.MATRIX).setImageScaleType(ImageView.ScaleType.CENTER).build();
//                x.image().bind(DeviceActivity.this.cameraImage, "http://7xq5wl.com2.z0.glb.qiniucdn.com/"+key+"?imageMogr2/thumbnail/480x320!", localImageOptions);
                x.image().bind(DeviceActivity.this.cameraImage, "http://7xq5wl.com2.z0.glb.qiniucdn.com/MyTest.jpg?imageMogr2/thumbnail/480x320!", localImageOptions);
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
        setContentView(R.layout.activity_device);
        initComponent();
        mGHCB = GHCBManage.GHCBs.get(getIntent().getStringExtra("com.hhnext.myeasylink.DevID"));
        speechListAdapter = new SpeechListAdapter(this);
        listView.setAdapter(speechListAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default:
                return super.onOptionsItemSelected(menuItem);
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
            this.mDialogue[0] = ("设备MAC: " + DeviceActivity.this.mGHCB.getMAC() + "\r\n设备编号: " + DeviceActivity.this.mGHCB.getDevID() + "\r\n设备创建时间: " + DeviceActivity.this.mGHCB.getCreateTime() + "\r\n设备离线时间: " + DeviceActivity.this.mGHCB.getOfflineTime() + "\r\n设备上线时间: " + DeviceActivity.this.mGHCB.getOnlineTime());
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
            localLayoutParams.topMargin = 15;
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
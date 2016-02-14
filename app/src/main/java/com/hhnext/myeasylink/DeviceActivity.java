package com.hhnext.myeasylink;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.JsonObject;
import com.qiniu.android.storage.UploadManager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DeviceActivity extends AppCompatActivity {
    protected myHandler handler;
    protected GHCB mGHCB;

    UploadManager uploadManager = new UploadManager();
    String key, token;
    ActionBar actionBar;
    AlertDialog ad;
    private EditText newName;
    private TextView temperature, humidity, lamp, pump;
    private ImageView cameraImage;
    private ImageButton cameraButton;
    private ToggleButton toggleLamp, togglepump;
    private CompoundButton.OnCheckedChangeListener mPumpListener, mLampListener;
    private ListView listView;
    private SpeechListAdapter speechListAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initComponent() {
        this.handler = new myHandler();
        newName = new EditText(DeviceActivity.this);

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
        mLampListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mGHCB.lampOFF();
                else
                    mGHCB.lampON();
                cameraButton.performClick();
            }
        };
//        this.toggleLamp.setOnCheckedChangeListener(mLampListener);
        this.togglepump = ((ToggleButton) findViewById(R.id.togglePump));
        mPumpListener = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mGHCB.pumpOFF();
                else
                    mGHCB.pumpON();

                cameraButton.performClick();
            }
        };
//        this.togglepump.setOnCheckedChangeListener(mPumpListener);
        this.listView = ((ListView) findViewById(R.id.descriptionListView));
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View l, int position, long id) {
                ((SpeechListAdapter) listView.getAdapter()).toggle(position);
            }
        });
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mGHCB.getDevAlias());
        }
    }

    private void setLampStatus() {
        lamp.setText(MyUtil.togglgText(mGHCB.isLamp()));
        toggleLamp.setOnCheckedChangeListener(null);
        toggleLamp.setChecked(!mGHCB.isLamp());
        toggleLamp.setOnCheckedChangeListener(mLampListener);
    }

    private void setPumpStatus() {
        pump.setText(MyUtil.togglgText(mGHCB.isPump()));
        this.togglepump.setOnCheckedChangeListener(null);
        togglepump.setChecked(!mGHCB.isPump());
        this.togglepump.setOnCheckedChangeListener(mPumpListener);
    }

    private void refresUI() {

        humidity.setText(mGHCB.getHumidity());
        temperature.setText(mGHCB.getTemperature());
        setLampStatus();
        setPumpStatus();

        if (speechListAdapter != null) {
            speechListAdapter.refresh();
            speechListAdapter.notifyDataSetChanged();

        }
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
                setLampStatus();
                return;
            case GHCBAPP.PUMP_CHANGED:
                setPumpStatus();
                return;
            case GHCBAPP.HASIMAGE_CHANGED:
                ImageOptions localImageOptions = new ImageOptions.Builder().setPlaceholderScaleType(ImageView.ScaleType.MATRIX).setImageScaleType(ImageView.ScaleType.CENTER).build();
                x.image().bind(DeviceActivity.this.cameraImage, "http://7xq5wl.com2.z0.glb.qiniucdn.com/" + key + "?imageMogr2/thumbnail/512x384!", localImageOptions);
//                x.image().bind(DeviceActivity.this.cameraImage, "http://7xq5wl.com2.z0.glb.qiniucdn.com/MyTest.jpg?imageMogr2/thumbnail/480x320!", localImageOptions);
                return;
            case GHCBAPP.IPADDRESS_CHANGED:
                if (speechListAdapter != null) {
                    speechListAdapter.refresh();
                    speechListAdapter.notifyDataSetChanged();

                }
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

        mGHCB = GHCBAPP.CURRENTGHCB;
        //mGHCB = GHCBManage.GHCBs.get(getIntent().getStringExtra("com.hhnext.myeasylink.DevID"));

        if (mGHCB.getIPAddress().equals("0.0.0.0")) mGHCB.getGHCBStatus();
        initComponent();
        speechListAdapter = new SpeechListAdapter(this);
        listView.setAdapter(speechListAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent localIntent = new Intent(this, DevicesActivity.class);
                localIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(localIntent);
                break;
            case R.id.action_rename:
                if (ad == null)
                    ad = new AlertDialog.Builder(DeviceActivity.this)
                            .setTitle("请输入设备新名称：")
                            .setIcon(R.drawable.tranlate48)
                            .setView(newName)
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RequestParams requestParams = new RequestParams(GHCBManage.modifyURL);
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("device_id", mGHCB.getDevID());
                                    jsonObject.addProperty("alias", newName.getText().toString());
                                    requestParams.setBodyContent(jsonObject.toString());
                                    MyUtil.setRequestParamsHeader(requestParams);
                                    x.http().post(requestParams, new Callback.CommonCallback<String>() {
                                        @Override
                                        public void onSuccess(String result) {
                                            String name = newName.getText().toString();
                                            mGHCB.setDevAlias(name);
                                            actionBar.setTitle(name);
                                            Toast.makeText(DeviceActivity.this, "设备名称修改成功！", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(Throwable ex, boolean isOnCallback) {
                                            Toast.makeText(DeviceActivity.this, "设备名称修改失败！", Toast.LENGTH_SHORT).show();
                                            Log.i("orinoco", "rename error :");
                                            ex.printStackTrace();
                                        }

                                        @Override
                                        public void onCancelled(CancelledException cex) {

                                        }

                                        @Override
                                        public void onFinished() {

                                        }
                                    });
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                else
                    ad.show();
                break;
            case R.id.action_authorize:
                startActivity(new Intent(this, UserAuthorizeActivity.class));
                break;
        }
        return super.onOptionsItemSelected(menuItem);
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
            refresh();
        }

        public void refresh() {
            this.mDialogue[0] =
                    "设备IP地址: " + mGHCB.getPublicIPAddress() + "/" + mGHCB.getIPAddress() +
                            "\r\n设备MAC: " + mGHCB.getMAC().toUpperCase() +
                            "\r\n设备编号: " + mGHCB.getDevID().toUpperCase() +
                            "\r\n设备创建时间: " + mGHCB.getCreateTime() +
                            "\r\n设备离线时间: " + mGHCB.getOfflineTime() +
                            "\r\n设备上线时间: " + mGHCB.getOnlineTime()
            ;
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
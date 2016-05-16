package com.hhnext.myeasylink;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qiniu.android.storage.UploadManager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DeviceActivity extends AppCompatActivity implements RefreshActivity {
    protected myHandler handler;
    protected GHCB mGHCB;

//    private GHCBManage mGHCBManage;

    //    List<String> rlyArray, tempArray;
    UploadManager uploadManager = new UploadManager();
    String key, token;
    ActionBar actionBar;
    AlertDialog ad;
    CountDownLatch c;
    private EditText newName;
    private TextView rlyName, rlyState, lamp, pump, portName, portModelText;
    private ImageView cameraImage;
    private ImageButton cameraButton;
    private ToggleButton rlytoggleButton, togglepump;
    private Button portOKButton, portCancelButton;
    //    private CompoundButton.OnCheckedChangeListener mtoggleListener;
    private ListView listView, rlyListView, tempListView;
    private Spinner portModel, portNo, portType;
    //    private SpeechListAdapter speechListAdapter;
    private RlyAdapter rlyAdapter;
    private TempAdapter tempAdapter;
    private View portView;
    private ArrayAdapter<String> portModelAdapter;
    private List<String> portModelArrayList;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private Gson gson = new Gson();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void initComponent() {
        this.handler = new myHandler();
        newName = new EditText(DeviceActivity.this);

        builder = new AlertDialog.Builder(DeviceActivity.this);
        builder.setView(portView);
        alertDialog = builder.create();

        portView = getLayoutInflater().inflate(R.layout.port, null);
        portNo = (Spinner) portView.findViewById(R.id.portNo);
        portName = (TextView) portView.findViewById(R.id.portName);
        portType = (Spinner) portView.findViewById(R.id.portType);
        portModelText = (TextView) portView.findViewById(R.id.portModelText);
        portModel = (Spinner) portView.findViewById(R.id.portModel);
        portModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> portTypeArrayList = GHCBManage.PortDescriptions.get(portModelArrayList.get(position)).getPortType();
                ArrayAdapter<String> portTypeAdapter = new ArrayAdapter<String>(DeviceActivity.this, android.R.layout.simple_spinner_dropdown_item, portTypeArrayList);
//                portTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                portType.setAdapter(portTypeAdapter);
                portModelText.setText(GHCBManage.PortDescriptions.get(portModelArrayList.get(position)).getPortDesc());
//                        portModelText.setText(portModelArrayList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        portOKButton = (Button) portView.findViewById(R.id.portOK);
        portOKButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String model = portModel.getSelectedItem().toString();
                    String type = portType.getSelectedItem().toString();
                    String no = portNo.getSelectedItem().toString();
                    String name = portName.getText().toString();

                    PortDescription pd = GHCBManage.PortDescriptions.get(model);
                    String typeId = pd.getMap().get(type);

                    Port p = (Port) Class.forName(pd.getPortClassInfo()).newInstance();

                    p.setPortDescName(pd.getPortDesc());
                    p.setPortModel(model);
                    p.setPortType(type);
                    p.setPort(Integer.valueOf(no));
                    p.setPortName(name);
                    p.setPortTypeId(Integer.valueOf(typeId));
                    p.setGHCB(mGHCB);

                    mGHCB.AddOrDelToCloud(p, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                alertDialog.dismiss();

            }
        });

        portCancelButton = (Button) portView.findViewById(R.id.portCancel);
        portCancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });


//        Object[] mGHCBPorts = mGHCB.getPorts().values().toArray();
//        rlyArray = new ArrayList<>();
//        tempArray = new ArrayList<>();
//        for (Object o : mGHCBPorts
//                ) {
//            Port p = (Port) o;
//            if (p.getPortModel().equals("rly"))
//                rlyArray.add(String.valueOf(p.getPort()));
//            if (p.getPortModel().equals("temp"))
//                tempArray.add(String.valueOf(p.getPort()));
//        }
//        Collections.sort(rlyArray);
//        Collections.sort(tempArray);
//        this.temperature = ((TextView) findViewById(R.id.temperature));
//        this.humidity = ((TextView) findViewById(R.id.humidity));
//        this.lamp = ((TextView) findViewById(R.id.lamp));
//        this.pump = ((TextView) findViewById(R.id.pump));
        this.cameraImage = ((ImageView) findViewById(R.id.cameraImage));
        this.cameraButton = ((ImageButton) findViewById(R.id.cameraButton));
        //region camera
        this.cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {

                /*SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//                String key = "//"+mGHCB.getMAC()+"//"+APPUser.userName+"//"+df.format(new Date())+".jpg";
                key = df.format(new Date()) + ".jpg";
                token = APPUser.QiniuAuth.uploadToken(APPUser.Bucket, key);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("key", key);
                jsonObject.addProperty("token", token);
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.add("take_photo", jsonObject);
                mGHCB.takePhoto(jsonObject1.toString());
                Log.i("orinoco", jsonObject1.toString());*/

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
        //endregion
//        this.toggleLamp = ((ToggleButton) findViewById(R.id.toggleLamp));
//        mLampListener = new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked)
//                    ((RlyPort)mGHCB.getPorts().get("2")).OFF();
//                else
//                    ((RlyPort)mGHCB.getPorts().get("2")).ON();
////                cameraButton.performClick();
//            }
//        };
////        this.toggleLamp.setOnCheckedChangeListener(mLampListener);
//        this.togglepump = ((ToggleButton) findViewById(R.id.togglePump));
//        mPumpListener = new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked)
//                    ((RlyPort)mGHCB.getPorts().get("3")).OFF();
//                else
//                    ((RlyPort)mGHCB.getPorts().get("3")).ON();
//
////                cameraButton.performClick();
//            }
//        };
//        this.togglepump.setOnCheckedChangeListener(mPumpListener);
//        this.listView = ((ListView) findViewById(R.id.descriptionListView));
//        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> adapterView, View l, int position, long id) {
//                ((SpeechListAdapter) listView.getAdapter()).toggle(position);
//            }
//        });

        tempListView = ((ListView) findViewById(R.id.tempListView));

        TempPort.tempListView = tempListView;


        rlyListView = ((ListView) findViewById(R.id.rlyListView));

        RlyPort.rlyListView = rlyListView;


//        mtoggleListener = new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                RlyPort r = (RlyPort) buttonView.getTag();
////                RlyPort r= ((RlyPort)mGHCB.getPorts().get("2"));
//
//                if (isChecked)
//                    r.OFF();
//                else
//                    r.ON();
////                Toast.makeText(DeviceActivity.this, "xxx" + buttonView.toString(), Toast.LENGTH_LONG).show();
////                cameraButton.performClick();
//            }
//        };


        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mGHCB.getDevAlias());
        }
    }

//    private void setLampStatus() {
//        RlyPort r=(RlyPort)mGHCB.getPorts().get("2");
//        lamp.setText(MyUtil.togglgText(r.isRlyState()));
//        toggleLamp.setOnCheckedChangeListener(null);
//        toggleLamp.setChecked(!r.isRlyState());
//        toggleLamp.setOnCheckedChangeListener(mLampListener);
//    }
//
//    private void setPumpStatus() {
//        RlyPort r=(RlyPort)mGHCB.getPorts().get("3");
//        pump.setText(MyUtil.togglgText(r.isRlyState()));
//        this.togglepump.setOnCheckedChangeListener(null);
//        togglepump.setChecked(!r.isRlyState());
//        this.togglepump.setOnCheckedChangeListener(mPumpListener);
//    }

//    private void refresUI() {
//        int no = mGHCB.getInBoardTempPort();
//        TempPort r = (TempPort) mGHCB.getPorts().get(String.valueOf(no));
//        humidity.setText(String.valueOf(r.getHumidity()));
//        temperature.setText(String.valueOf(r.getTemperature()));
//        setLampStatus();
//        setPumpStatus();

//        if (speechListAdapter != null) {
//            speechListAdapter.refresh();
//            speechListAdapter.notifyDataSetChanged();
//
//        }
//    }

    private void refreshView(Message msg) {
        int msgID = msg.arg1;
        GHCB g = (GHCB) msg.obj;
//
        Port port = g.getPort(String.valueOf(msgID));
        port.refreshListView();


//        if (tempArray.contains(String.valueOf(port.getPort()))) {
//            TempPort t = (TempPort) port;
//            start = this.tempListView.getFirstVisiblePosition();
//            last = this.tempListView.getLastVisiblePosition();
//            current = port.getListViewIndex();
//            TempViewHolder viewHolder;
//            if ((current <= last) && (current >= start)) {
//                viewHolder = (TempViewHolder) this.tempListView.getChildAt(current - start).getTag();
//                viewHolder.TempHumi.setText(String.valueOf(t.getHumidity()));
//                viewHolder.TempTemp.setText(String.valueOf(t.getTemperature()));
//
//            }
//        }

//        switch (msgID) {
//            case GHCBAPP.HUMIDITY_CHANGED:
//                this.humidity.setText(mGHCB.getHumidity());
//                return;
//            case GHCBAPP.TEMPERATURE_CHANGED:
//                temperature.setText(mGHCB.getTemperature());
//                return;
//            case GHCBAPP.LAMP_CHANGED:
//                setLampStatus();
//                return;
//            case GHCBAPP.PUMP_CHANGED:
//                setPumpStatus();
//                return;
//            case GHCBAPP.HASIMAGE_CHANGED:
//                ImageOptions localImageOptions = new ImageOptions.Builder().setPlaceholderScaleType(ImageView.ScaleType.MATRIX).setImageScaleType(ImageView.ScaleType.CENTER).build();
//                x.image().bind(DeviceActivity.this.cameraImage, "http://7xq5wl.com2.z0.glb.qiniucdn.com/" + key + "?imageMogr2/thumbnail/512x384!", localImageOptions);
////                x.image().bind(DeviceActivity.this.cameraImage, "http://7xq5wl.com2.z0.glb.qiniucdn.com/MyTest.jpg?imageMogr2/thumbnail/480x320!", localImageOptions);
//                return;
//            case GHCBAPP.IPADDRESS_CHANGED:
//                if (speechListAdapter != null) {
//                    speechListAdapter.refresh();
//                    speechListAdapter.notifyDataSetChanged();
//
//                }
//                return;
//            case GHCBAPP.ALL_CHANGED:
//                refresUI();
//                return;
//            default:
//                return;
//        }
    }

    public void portReset() {
        for (Port p : mGHCB.getPorts().values()
                ) {
            p.setPortDelFlag(false);
        }

        refreshRlyTempListView();
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
                                    RequestParams requestParams = new RequestParams(APPUser.modifyURL);
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
            case R.id.action_port_del:
                for (Port p : mGHCB.getPorts().values()
                        ) {
                    p.setPortDelFlag(!p.isPortDelFlag());
                }
                refreshRlyTempListView();
                break;
            case R.id.action_port_add:
                portReset();

                List<String> tmpArrayList = new ArrayList<>(GHCBManage.PortArrays);
                tmpArrayList.removeAll(mGHCB.getPorts().keySet());
                String[] tmpArray = new String[tmpArrayList.size()];
                tmpArrayList.toArray(tmpArray);

                ArrayAdapter<String> portNoAdapter = new ArrayAdapter<String>(DeviceActivity.this, android.R.layout.simple_spinner_item, tmpArray);
                portNoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                portNo.setAdapter(portNoAdapter);
//                portNo.requestFocus();
//                portNo.requestFocusFromTouch();

                int num = GHCBManage.PortDescriptions.size();
                if (num == 0)
                    mGHCB.getPortDescriptionFromCloud();
                else {
                    refreshPortDescriptionListView();
                }
                alertDialog.show();
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_device);

        mGHCB = GHCBAPP.CURRENTGHCB;
        mGHCB.setRefreshActivity(this);
//        GHCBManage.setCtx(DeviceActivity.this);
//        mGHCB.AttachToMqTT(true);

//        mGHCB = GHCBManage.GHCBs.get(getIntent().getStringExtra("com.hhnext.myeasylink.DevID"));


        initComponent();

        if (mGHCB.getPorts().size() == 1) {
            mGHCB.getPortsFromCloud();
//            TempPort t = new TempPort(mGHCB);
////            t.setPort(10);
////            t.setPortModel("temp");
////            t.setPortName("系统");
//
////            mGHCB.AddPort(t);
//
//            t = new TempPort(mGHCB);
//            t.setPort(9);
//            t.setPortModel("temp");
//            t.setPortName("北边");
//
//            mGHCB.AddPort(t);
//
//            RlyPort r = new RlyPort(mGHCB);
//            r.setPortName("东边");
//            r.setPort(2);
//            r.setPortModel("rly");
//
//            mGHCB.AddPort(r);
//
//            r = new RlyPort(mGHCB);
//            r.setPortName("西边");
//            r.setPort(1);
//            r.setPortModel("rly");
//
//            mGHCB.AddPort(r);
//
//            r = new RlyPort(mGHCB);
//            r.setPortName("南边");
//            r.setPort(0);
//            r.setPortModel("rly");
//
//            mGHCB.AddPort(r);


        } else {

            refreshRlyTempListView();
        }


//        speechListAdapter = new SpeechListAdapter(this);
//        listView.setAdapter(speechListAdapter);


//        GHCB[] mmm = (GHCB[]) GHCBManage.GHCBs.values().toArray(new GHCB[0]);
//        for (int i = 0; i < mmm.length; i++) {
//            Log.i("orinoco", "=======>" + mmm[i].getRly1() + "," + mmm[i].getRly2());
//        }
    }

    protected void onPause() {
        super.onPause();
//        if (mGHCB.getStatus() == GHCB.GHCBStatus.online)
//            mGHCB.AttachToMqTT(false);
        this.mGHCB.setHandler(null);
    }

    protected void onResume() {
        super.onResume();

//        refresUI();
//        GHCBManage.setCtx(DeviceActivity.this);
//        if (mGHCB.getStatus() == GHCB.GHCBStatus.online)
//            mGHCB.AttachToMqTT(true);
        this.mGHCB.setHandler(this.handler);

//        rlyAdapter.notifyDataSetChanged();
//        tempAdapter.notifyDataSetChanged();

    }

//    public class TempViewHolder extends ViewHolder {
//        public TextView TempTemp;
//        public TextView TempHumi;
//        public ImageButton TempDelButton;
//
//        private TempViewHolder() {
//        }
//    }

//    protected class TempAdapter extends BaseAdapter {
//        private Context context;
//        private List<String> tempArray = TempPort.tempArray;
//
//        public TempAdapter(Context ctx) {
//            this.context = ctx;
//            Collections.sort(tempArray);
////            this.mGHCBList = (GHCB[]) GHCBManage.GHCBs.values().toArray(new GHCB[0]);
//        }
//
//        public int getCount() {
//            return tempArray.size();
//        }
//
//        public Object getItem(int position) {
//            return mGHCB.getPort(tempArray.get(position));
//        }
//
//        public long getItemId(int position) {
//            return position;
//        }
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            final TempPort t = (TempPort) mGHCB.getPort(tempArray.get(position));
//            t.setListViewIndex(position);
//
//            TempViewHolder viewHolder;
//            if (convertView == null) {
//                convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.temp, null);
//                viewHolder = new TempViewHolder();
//                viewHolder.Index = position;
////                viewHolder.RlyDescription = ((LinearLayout) convertView.findViewById(R.id.devicesLinerLayout));
//                viewHolder.Name = ((TextView) convertView.findViewById(R.id.tempName));
//                viewHolder.TempTemp = ((TextView) convertView.findViewById(R.id.tempTemp));
//                viewHolder.TempHumi = ((TextView) convertView.findViewById(R.id.tempHumi));
//                viewHolder.TempDelButton = ((ImageButton) convertView.findViewById(R.id.tempDelete));
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (TempViewHolder) convertView.getTag();
//            }
//
//            viewHolder.Name.setText(t.getPortName() + "：");
//            viewHolder.TempTemp.setText(t.getTemperature() + "");
//            viewHolder.TempHumi.setText(t.getHumidity() + "");
//
//            viewHolder.TempDelButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    mGHCB.getPorts().remove(String.valueOf(t.getPort()));
////                    tempArray.remove(String.valueOf(t.getPort()));
//                    mGHCB.DelPort(t.getPort());
//                    tempAdapter.notifyDataSetChanged();
//                }
//            });
//
//            if (t.IsPortDelFlag())
//                viewHolder.TempDelButton.setVisibility(View.VISIBLE);
//            else
//                viewHolder.TempDelButton.setVisibility(View.GONE);
//
//            return convertView;
//        }
//    }

//    public class RlyViewHolder extends ViewHolder{
////        public TextView RlyName;
//        public TextView RlyState;
//        public ToggleButton RlytoggleButton;
//        public ImageButton RlyDelButton;
//
//        private RlyViewHolder() {
//        }
//    }


//    private class SpeechListAdapter extends BaseAdapter {
//        private Context mContext;
//        private String[] mDialogue = new String[1];
//        private boolean[] mExpanded = {false};
//        private String[] mTitles = {"设备详细信息："};
//
//        public SpeechListAdapter(Context ctx) {
//            this.mContext = ctx;
//            refresh();
//        }
//
//        public void refresh() {
//            this.mDialogue[0] =
//                    "设备IP地址: " + mGHCB.getPublicIPAddress() + "/" + mGHCB.getIPAddress() +
//                            "\r\n设备MAC: " + mGHCB.getMAC().toUpperCase() +
//                            "\r\n设备编号: " + mGHCB.getDevID().toUpperCase() +
//                            "\r\n设备创建时间: " + mGHCB.getCreateTime() +
//                            "\r\n设备离线时间: " + mGHCB.getOfflineTime() +
//                            "\r\n设备上线时间: " + mGHCB.getOnlineTime()
//            ;
//        }
//
//        public int getCount() {
//            return this.mTitles.length;
//        }
//
//        public Object getItem(int position) {
//            return position;
//        }
//
//        public long getItemId(int position) {
//            return position;
//        }
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null)
//                return new DeviceActivity.SpeechView(this.mContext, this.mTitles[position], this.mDialogue[position], this.mExpanded[position]);
//            DeviceActivity.SpeechView localSpeechView = (DeviceActivity.SpeechView) convertView;
//            localSpeechView.setTitle(this.mTitles[position]);
//            localSpeechView.setDialogue(this.mDialogue[position]);
//            localSpeechView.setExpanded(this.mExpanded[position]);
//            return localSpeechView;
//        }
//
//        public void toggle(int position) {
//            mExpanded[position] = !mExpanded[position];
//            notifyDataSetChanged();
//        }
//    }

//    private class SpeechView extends LinearLayout {
//        private TextView mDialogue;
//        private TextView mTitle;
//
//        public SpeechView(Context ctx, String title, String dialogue, boolean expanded) {
//            super(ctx);
//            setOrientation(VERTICAL);
//            this.mTitle = new TextView(ctx);
//            this.mTitle.setText(title);
//            this.mTitle.setTextSize(20.0F);
//            addView(this.mTitle, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//
//            this.mDialogue = new TextView(ctx);
//            this.mDialogue.setText(dialogue);
//            this.mDialogue.setTextSize(18.0F);
//            this.mDialogue.setLineSpacing(2.0F, 1.2F);
//            LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//            localLayoutParams.topMargin = 15;
//            addView(this.mDialogue, localLayoutParams);
//
//            mDialogue.setVisibility(expanded ? VISIBLE : GONE);
//        }


//        public void setExpanded(boolean expanded) {
//            mDialogue.setVisibility(expanded ? VISIBLE : GONE);
//        }

    //        public void setDialogue(String words) {
//            this.mDialogue.setText(words);
//        }
//
//        public void setTitle(String title) {
//            this.mTitle.setText(title);
//        }
//    }


    public void refreshRlyTempListView() {


        if (rlyAdapter == null) {
            if (mGHCB.getPortTypeNums().get("rly") != null && mGHCB.getPortTypeNums().get("rly").size() > 0) {
                rlyAdapter = new RlyAdapter(this, mGHCB);
                rlyListView.setAdapter(rlyAdapter);
                mGHCB.getGHCBStatus();
            }
        } else {
            rlyAdapter.notifyDataSetChanged();
        }

        if (tempAdapter == null) {
            tempAdapter = new TempAdapter(this, mGHCB);
            tempListView.setAdapter(tempAdapter);
        } else {
            tempAdapter.notifyDataSetChanged();
        }
    }

    public void refreshPortDescriptionListView() {
        portModelArrayList = new ArrayList<>(GHCBManage.PortDescriptions.keySet());
        portModelAdapter = new ArrayAdapter<>(DeviceActivity.this, android.R.layout.simple_spinner_dropdown_item, portModelArrayList);
//    portModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        portModel.setAdapter(portModelAdapter);
        portModel.setSelection(0, true);
    }


    public class myHandler extends Handler {
        public myHandler() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0)
                refreshView(msg);
        }
    }
}
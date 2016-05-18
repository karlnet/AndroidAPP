package com.hhnext.myeasylink;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.qiniu.android.storage.UploadManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DeviceActivity extends AppCompatActivity implements RefreshActivity {
    private static String titleText = "请输入设备新名称：";
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

    private void initComponent() {
        this.handler = new myHandler();

        newName = new EditText(DeviceActivity.this);

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

//
        portView = getLayoutInflater().inflate(R.layout.port, null);
        portNo = (Spinner) portView.findViewById(R.id.portNo);
        portName = (TextView) portView.findViewById(R.id.portName);
        portType = (Spinner) portView.findViewById(R.id.portType);
        portModelText = (TextView) portView.findViewById(R.id.portModelText);
        portModel = (Spinner) portView.findViewById(R.id.portModel);
        portModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String model = portModel.getSelectedItem().toString();
                List<String> tmpArrayList = new ArrayList<>(GHCBManage.PortArrays);
                tmpArrayList.removeAll(mGHCB.getPorts().keySet());
                if (!model.equals("rly")) {
                    tmpArrayList.removeAll(new HashSet<String>() {
                        {
                            add("0");
                            add("1");
                            add("2");
                            add("3");
                            add("4");
                            add("5");
                            add("6");
                            add("7");
                            add("8");

                        }
                    });
                    tmpArrayList.remove("1");
                } else {

                    tmpArrayList.removeAll(new HashSet<String>() {
                        {
                            add("9");

                        }
                    });
                    tmpArrayList.remove("1");
                }
                String[] tmpArray = new String[tmpArrayList.size()];
                tmpArray = tmpArrayList.toArray(tmpArray);

                ArrayAdapter<String> portNoAdapter = new ArrayAdapter<String>(DeviceActivity.this, android.R.layout.simple_spinner_dropdown_item, tmpArray);
                portNo.setAdapter(portNoAdapter);


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

        builder = new AlertDialog.Builder(DeviceActivity.this);
        builder.setView(portView);
        alertDialog = builder.create();

        tempListView = ((ListView) findViewById(R.id.tempListView));
        TempPort.tempListView = tempListView;
        rlyListView = ((ListView) findViewById(R.id.rlyListView));
        RlyPort.rlyListView = rlyListView;

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mGHCB.getDevAlias());
        }
    }

    private void refreshView(Message msg) {
        int msgID = msg.arg1;
        GHCB g = (GHCB) msg.obj;

        Port port = g.getPort(String.valueOf(msgID));
        port.refresh();


    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_device);


        mGHCB = GHCBAPP.CURRENTGHCB;

        mGHCB.setRefreshActivity(this);

        initComponent();

    }

    protected void onPause() {
        super.onPause();
        GHCBManage.setContext(null);
        this.mGHCB.setHandler(null);
        if (mGHCB.getStatus() == GHCB.GHCBStatus.online)
            mGHCB.AttachToMqTT(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        GHCBManage.setContext(this);

        if (mGHCB.getPorts().size() == 1) {
            mGHCB.getPortsFromCloud();

        } else {
            refreshRlyTempListView();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
                    ad = new AlertDialog.Builder(DeviceActivity.this).setTitle(titleText)
                            .setIcon(R.drawable.tranlate48).setView(newName).setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String t = newName.getText().toString();
                                    mGHCB.BoardRename(t);
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

//                List<String> tmpArrayList = new ArrayList<>(GHCBManage.PortArrays);
//                tmpArrayList.removeAll(mGHCB.getPorts().keySet());
//                String[] tmpArray = new String[tmpArrayList.size()];
//                tmpArrayList.toArray(tmpArray);
//
//                ArrayAdapter<String> portNoAdapter = new ArrayAdapter<String>(DeviceActivity.this, android.R.layout.simple_spinner_item, tmpArray);
//                portNoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                portNo.setAdapter(portNoAdapter);
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

    public void portReset() {
        for (Port p : mGHCB.getPorts().values()
                ) {
            p.setPortDelFlag(false);
        }

        refreshRlyTempListView();
    }

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

        this.mGHCB.setHandler(this.handler);
        if (mGHCB.getStatus() == GHCB.GHCBStatus.online)
            mGHCB.AttachToMqTT(true);
    }

    public void refreshPortDescriptionListView() {
        portModelArrayList = new ArrayList<>(GHCBManage.PortDescriptions.keySet());
        portModelAdapter = new ArrayAdapter<>(DeviceActivity.this, android.R.layout.simple_spinner_dropdown_item, portModelArrayList);
//    portModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        portModel.setAdapter(portModelAdapter);
        portModel.setSelection(0, true);
    }

    public void reName() {
        String name = mGHCB.getDevAlias();
        actionBar.setTitle(name);
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
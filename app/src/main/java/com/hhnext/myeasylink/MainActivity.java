package com.hhnext.myeasylink;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.mxchip.easylink.EasyLinkAPI;
import com.mxchip.jmdns.JmdnsAPI;
import com.mxchip.jmdns.JmdnsListener;

import org.json.JSONArray;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class MainActivity extends AppCompatActivity {
    private static boolean first = true;
    //    private final String wifiTagHead = "_easylink._tcp.local.";
    private String ip, mac;
    private JmdnsAPI mDNSAPI;
    private EasyLinkAPI mEasylinkAPI;

    private TextView msgText;
    private EditText password, ssid;
    private Button startSearch;


    private void initComponent() {
        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
            localActionBar.setDisplayHomeAsUpEnabled(true);

        msgText = ((TextView) findViewById(R.id.textView));
        ssid = ((EditText) findViewById(R.id.ssid));
        password = ((EditText) findViewById(R.id.password));
        startSearch = ((Button) findViewById(R.id.startsearch));
        startSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("请稍等...");
                progressDialog.setMessage("正在配置网络");
                progressDialog.setCancelable(false);
                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "停止配置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDNSAPI.stopMdnsService();
                        mEasylinkAPI.stopEasyLink();
                    }
                });
                progressDialog.show();
//                new Thread(new Runnable() {
//                    public void run() {
                mEasylinkAPI.startEasyLink(MainActivity.this, ssid.getText().toString().trim(), password.getText().toString().trim());
                mDNSAPI.startMdnsService(APPUser.WifiTagHead, new JmdnsListener() {
                            public void onJmdnsFind(JSONArray jsonArray) {
                                if (/*(MainActivity.first) && */(jsonArray != null) && (jsonArray.length() != 0)) {
                                   /* first = false;*/
                                    try {
                                        mEasylinkAPI.stopEasyLink();
                                        mDNSAPI.stopMdnsService();
                                        progressDialog.dismiss();

                                        ip = jsonArray.getJSONObject(0).getString("deviceIP");
                                        mac = jsonArray.getJSONObject(0).getString("deviceMac");

//                                        RequestParams request = new RequestParams(APPUser.getActiveURL(ip));
//                                        request.addHeader("content-type", "application/json");
//                                        JsonObject jsonObject = new JsonObject();
//                                        jsonObject.addProperty("login_id", GHCB.userName);
//                                        jsonObject.addProperty("dev_passwd", GHCB.password);
//                                        jsonObject.addProperty("user_token", APPUser.getActiveToken(mac));
//                                        Log.i("orinoco", jsonObject.toString());
//                                        request.setBodyContent(jsonObject.toString());
//                                        x.http().post(request, new CommonCallback<String>() {
//                                            public void onCancelled(CancelledException cex) {
//                                            }
//
//                                            public void onError(Throwable ex, boolean b) {
//                                                Log.i("orinoco", "active:" + ex.toString());
//                                            }
//
//                                            public void onFinished() {
//                                            }
//
//                                            public void onSuccess(String jsonStr) {

//                                                try {
                                        RequestParams request = new RequestParams(APPUser.MyAuthorizeURL);
                                        APPUser.setMyRequestParamsHeader(request);
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("active_token", APPUser.getActiveToken(mac));
                                        request.setBodyContent(jsonObject.toString());
                                        x.http().post(request, new CommonCallback<String>() {
                                            public void onCancelled(CancelledException ex) {

                                                        }

                                            public void onError(Throwable ex, boolean b) {
                                                Log.i("orinoco", "bind reponse:" + ex.toString());
                                                        }

                                                        public void onFinished() {
                                                            Log.i("orinoco", "bind finish");
                                                        }

                                            public void onSuccess(String jsonStr) {
                                                            Intent intent = new Intent(MainActivity.this, DevicesActivity.class);
                                                startActivity(intent);
                                                        }
                                                    });
//                                                } catch (Exception e) {
//
//                                                }
//                                            }
//                                        });

                                    } catch (Exception e2) {

                                    }
                                }
                            }
                        });
//                    }
//                }).start();
            }
        });
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        initComponent();
        mEasylinkAPI = new EasyLinkAPI(this);
        mDNSAPI = new JmdnsAPI(this);
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

    protected void onResume() {
        super.onResume();
        String str = this.mEasylinkAPI.getSSID();
        Log.i("orinoco", "ssid is:" + str);
        if ((str != null) && (!str.equals("")) && (!str.contains("unknown"))) {
            ssid.setText(str);
            password.setText("iloveyou");
            return;
        }
        msgText.setText("请将手机连入WiFi网络...");
        msgText.setTextColor(Color.parseColor("#ffff0000"));
        password.setEnabled(false);
        startSearch.setEnabled(false);
    }
}
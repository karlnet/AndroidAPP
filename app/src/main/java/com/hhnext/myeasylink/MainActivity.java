package com.hhnext.myeasylink;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.mxchip.easylink.EasyLinkAPI;
import com.mxchip.jmdns.JmdnsAPI;
import com.mxchip.jmdns.JmdnsListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.HttpManager;
import org.xutils.common.Callback;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import static org.xutils.common.Callback.*;

public class MainActivity extends AppCompatActivity {
    private static boolean first = true;
    private final String wifiTagHead = "_easylink._tcp.local.";
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

        this.msgText = ((TextView) findViewById(R.id.textView));
        this.ssid = ((EditText) findViewById(R.id.ssid));
        this.password = ((EditText) findViewById(R.id.password));
        this.startSearch = ((Button) findViewById(R.id.startsearch));
        this.startSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("请稍等...");
                progressDialog.setMessage("正在配置网络");
                progressDialog.setCancelable(false);
                progressDialog.setButton(-2, "停止配置", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int) {
                        MainActivity.this.mDNSAPI.stopMdnsService();
                        MainActivity.this.mEasylinkAPI.stopEasyLink();
                    }
                });
                progressDialog.show();
                new Thread(new Runnable() {
                    public void run() {
                        MainActivity.this.mEasylinkAPI.startEasyLink(MainActivity.this, MainActivity.this.ssid.getText().toString().trim(), MainActivity.this.password.getText().toString().trim());
                        MainActivity.this.mDNSAPI.startMdnsService("_easylink._tcp.local.", new JmdnsListener() {
                            public void onJmdnsFind(JSONArray jsonArray) {
                                if ((MainActivity.first) && (jsonArray != null) && (jsonArray.length() != 0))
                                    first = false;
                                try {
                                    MainActivity.this.mEasylinkAPI.stopEasyLink();
                                    MainActivity.this.mDNSAPI.stopMdnsService();
                                    progressDialog.dismiss();

                                    ip = ((JsonObject) jsonArray.get(0)).get("deviceIP").getAsString();
                                    mac = ((JsonObject) jsonArray.get(0)).get("deviceMac").getAsString();
                                } catch (Exception e) {

                                }

                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("login_id", "admin");
                                jsonObject.addProperty("dev_passwd", "123456");
                                jsonObject.addProperty("user_token", GHCBManage.getActiveToken(MainActivity.this.mac));

                                RequestParams localRequestParams = new RequestParams(GHCBManage.getActiveURL(MainActivity.this.ip));
                                localRequestParams.addHeader("content-type", "application/json");
                                localRequestParams.setBodyContent(jsonObject.toString());
                                try {
                                    x.http().post(localRequestParams, new CommonCallback<String>() {
                                        public void onCancelled(CancelledException paramAnonymous4CancelledException) {
                                        }

                                        public void onError(Throwable paramAnonymous4Throwable, boolean paramAnonymous4Boolean) {
                                            Log.i("orinoco", "active:" + paramAnonymous4Throwable.toString());
                                        }

                                        public void onFinished() {
                                        }

                                        public void onSuccess(String paramAnonymous4String) {
                                            RequestParams localRequestParams = new RequestParams("http://api.easylink.io/v1/key/authorize");
                                            MyUtil.setRequestParamsHeader(localRequestParams);
                                            JsonObject localJSONObject = new JsonObject();
                                            try {
                                                localJSONObject.addProperty("active_token", GHCBManage.getActiveToken(MainActivity.this.mac));
                                                localRequestParams.setBodyContent(localJSONObject.toString());
                                                x.http().post(localRequestParams, new CommonCallback<String>() {
                                                    public void onCancelled(CancelledException paramAnonymous5CancelledException) {
                                                    }

                                                    public void onError(Throwable paramAnonymous5Throwable, boolean paramAnonymous5Boolean) {
                                                        Log.i("orinoco", "bind reponse:" + paramAnonymous5Throwable.toString());
                                                    }

                                                    public void onFinished() {
                                                    }

                                                    public void onSuccess(String paramAnonymous5String) {
                                                        Intent localIntent = new Intent(MainActivity.this, DevicesActivity.class);
                                                        MainActivity.this.startActivity(localIntent);
                                                    }
                                                });
                                            } catch (Exception e) {

                                            }
                                        }
                                    });

                                } catch (Exception e2) {

                                }

                            }
                        });
                    }
                }).start();
            }
        });
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        initComponent();
        this.mEasylinkAPI = new EasyLinkAPI(this);
        this.mDNSAPI = new JmdnsAPI(this);
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
        if ((str != null) && (str != "") && (!str.contains("unknown"))) {
            this.ssid.setText(str);
            this.password.setText("iloveyou");
            return;
        }
        this.msgText.setText("请将手机连入WiFi网络...");
        this.msgText.setTextColor(Color.parseColor("#ffff0000"));
        this.password.setEnabled(false);
        this.startSearch.setEnabled(false);
    }
}
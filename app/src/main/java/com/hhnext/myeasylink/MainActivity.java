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
import com.mxchip.easylink.EasyLinkAPI;
import com.mxchip.jmdns.JmdnsAPI;
import com.mxchip.jmdns.JmdnsListener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.HttpManager;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class MainActivity extends AppCompatActivity
{
    private final String wifiTagHead = "_easylink._tcp.local.";

    private static boolean findGHCB = true;
    private String ip,mac;
    private JmdnsAPI mDNSAPI;
    private EasyLinkAPI mEasylinkAPI;

    private TextView msgText;
    private EditText password,ssid;
    private Button startSearch;


    private void initComponent()
    {
        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
            localActionBar.setDisplayHomeAsUpEnabled(true);
        this.msgText = ((TextView)findViewById(R.id.textView));
        this.ssid = ((EditText)findViewById(R.id.ssid));
        this.password = ((EditText)findViewById(R.id.password));
        this.startSearch = ((Button)findViewById(R.id.startsearch));
        this.startSearch.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("请稍等...");
                progressDialog.setMessage("正在配置网络");
                progressDialog.setCancelable(false);
                progressDialog.setButton(-2, "停止配置", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                    {
                        MainActivity.this.mDNSAPI.stopMdnsService();
                        MainActivity.this.mEasylinkAPI.stopEasyLink();
                    }
                });
                progressDialog.show();
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        MainActivity.this.mEasylinkAPI.startEasyLink(MainActivity.this, MainActivity.this.ssid.getText().toString().trim(), MainActivity.this.password.getText().toString().trim());
                        MainActivity.this.mDNSAPI.startMdnsService("_easylink._tcp.local.", new JmdnsListener()
                        {
                            public void onJmdnsFind(JSONArray jsonArray)
                            {
                                if ((MainActivity.findGHCB) && (jsonArray != null) && (jsonArray.length() != 0))
                                    MainActivity.access$402(false);
                                try
                                {
                                    MainActivity.this.mEasylinkAPI.stopEasyLink();
                                    MainActivity.this.mDNSAPI.stopMdnsService();
                                    progressDialog.dismiss();
                                    JSONObject localJSONObject = new JSONObject();
                                    try
                                    {
                                        MainActivity.access$502(MainActivity.this, jsonArray.getJSONObject(0).getString("deviceIP"));
                                        MainActivity.access$602(MainActivity.this, jsonArray.getJSONObject(0).getString("deviceMac"));
                                        localJSONObject.put("login_id", "admin");
                                        localJSONObject.put("dev_passwd", "123456");
                                        localJSONObject.put("user_token", GHCBManage.getActiveToken(MainActivity.this.mac));
                                        label165: RequestParams localRequestParams = new RequestParams(GHCBManage.getActiveURL(MainActivity.this.ip));
                                        localRequestParams.addHeader("content-type", "application/json");
                                        localRequestParams.setBodyContent(localJSONObject.toString());
                                        x.http().post(localRequestParams, new Callback.CommonCallback()
                                        {
                                            public void onCancelled(Callback.CancelledException paramAnonymous4CancelledException)
                                            {
                                            }

                                            public void onError(Throwable paramAnonymous4Throwable, boolean paramAnonymous4Boolean)
                                            {
                                                Log.i("orinoco", "active:" + paramAnonymous4Throwable.toString());
                                            }

                                            public void onFinished()
                                            {
                                            }

                                            public void onSuccess(String paramAnonymous4String)
                                            {
                                                RequestParams localRequestParams = new RequestParams("http://api.easylink.io/v1/key/authorize");
                                                MyUtil.setRequestParamsHeader(localRequestParams);
                                                JSONObject localJSONObject = new JSONObject();
                                                try
                                                {
                                                    localJSONObject.put("active_token", GHCBManage.getActiveToken(MainActivity.this.mac));
                                                    label48: localRequestParams.setBodyContent(localJSONObject.toString());
                                                    x.http().post(localRequestParams, new Callback.CommonCallback()
                                                    {
                                                        public void onCancelled(Callback.CancelledException paramAnonymous5CancelledException)
                                                        {
                                                        }

                                                        public void onError(Throwable paramAnonymous5Throwable, boolean paramAnonymous5Boolean)
                                                        {
                                                            Log.i("orinoco", "bind reponse:" + paramAnonymous5Throwable.toString());
                                                        }

                                                        public void onFinished()
                                                        {
                                                        }

                                                        public void onSuccess(String paramAnonymous5String)
                                                        {
                                                            Intent localIntent = new Intent(MainActivity.this, DevicesActivity.class);
                                                            MainActivity.this.startActivity(localIntent);
                                                        }
                                                    });
                                                    return;
                                                }
                                                catch (Exception localException)
                                                {
                                                    break label48;
                                                }
                                            }
                                        });
                                        return;
                                    }
                                    catch (Exception localException2)
                                    {
                                        break label165;
                                    }
                                }
                                catch (Exception localException1)
                                {
                                    break label64;
                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        initComponent();
        this.mEasylinkAPI = new EasyLinkAPI(this);
        this.mDNSAPI = new JmdnsAPI(this);
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
            localIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(localIntent);
        }
    }

    protected void onResume()
    {
        super.onResume();
        String str = this.mEasylinkAPI.getSSID();
        if ((str != null) && (str != "") && (!str.contains("unknown")))
        {
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
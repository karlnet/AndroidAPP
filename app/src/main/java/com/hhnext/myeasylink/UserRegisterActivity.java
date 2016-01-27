package com.hhnext.myeasylink;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.xutils.HttpManager;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class UserRegisterActivity extends AppCompatActivity {
    private Button loginButton;
    private TextView mobileNumber;
    private TextView msgText;
    private TextView password;
    private Button smsButton;
    private TextView smsCode;

    private void initComponent() {
        this.msgText = ((TextView) findViewById(2131492995));
        this.msgText.setVisibility(8);
        this.smsCode = ((TextView) findViewById(2131493002));
        this.smsCode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                UserRegisterActivity.this.msgText.setVisibility(8);
            }
        });
        this.mobileNumber = ((TextView) findViewById(2131492998));
        this.mobileNumber.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                UserRegisterActivity.this.msgText.setVisibility(8);
            }
        });
        this.password = ((TextView) findViewById(2131493005));
        this.smsButton = ((Button) findViewById(2131493000));
        this.smsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                RequestParams localRequestParams = new RequestParams("http://easylink.io/v2/users");
                localRequestParams.addHeader("content-type", "application/json");
                MyUtil.setRequestParamsHeader(localRequestParams);
                String str = "{\"username\":\"" + UserRegisterActivity.this.mobileNumber.getText().toString() + "\"}";
                Log.i("orinoco", "username= " + str);
                localRequestParams.setBodyContent(str);
                x.http().post(localRequestParams, new Callback.CommonCallback() {
                    public void onCancelled(Callback.CancelledException paramAnonymous2CancelledException) {
                    }

                    public void onError(Throwable paramAnonymous2Throwable, boolean paramAnonymous2Boolean) {
                        Log.i("orinoco", "login reponse:" + paramAnonymous2Throwable.toString());
                        UserRegisterActivity.this.msgText.setText("短信服务器错误，请重新输入手机号码：");
                        UserRegisterActivity.this.msgText.setTextColor(-65536);
                        UserRegisterActivity.this.msgText.setVisibility(0);
                    }

                    public void onFinished() {
                    }

                    public void onSuccess(String paramAnonymous2String) {
                        if (((JsonObject) new JsonParser().parse(paramAnonymous2String)).get("result").getAsString().equals("success"))
                            UserRegisterActivity.this.loginButton.setEnabled(false);
                    }
                });
            }
        });
        this.loginButton = ((Button) findViewById(2131493006));
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                RequestParams localRequestParams = new RequestParams("http://easylink.io/v2/users");
                localRequestParams.addHeader("content-type", "application/json");
                MyUtil.setRequestParamsHeader(localRequestParams);
                String str = "{\"username\":\"" + UserRegisterActivity.this.mobileNumber.getText().toString() + "\"," + "\"verification_code\":\"" + UserRegisterActivity.this.smsCode.getText().toString() + "\"," + "\"password\":\"" + UserRegisterActivity.this.password.getText().toString() + "\"}";
                Log.i("orinoco", "s= " + str);
                localRequestParams.setBodyContent(str);
                x.http().post(localRequestParams, new Callback.CommonCallback() {
                    public void onCancelled(Callback.CancelledException paramAnonymous2CancelledException) {
                    }

                    public void onError(Throwable paramAnonymous2Throwable, boolean paramAnonymous2Boolean) {
                        Log.i("orinoco", "login reponse:" + paramAnonymous2Throwable.toString());
                        UserRegisterActivity.this.msgText.setText("验证码错误，请重新输入：");
                        UserRegisterActivity.this.msgText.setTextColor(-65536);
                        UserRegisterActivity.this.msgText.setVisibility(0);
                    }

                    public void onFinished() {
                    }

                    public void onSuccess(String paramAnonymous2String) {
                        JsonObject localJsonObject = (JsonObject) new JsonParser().parse(paramAnonymous2String);
                        APPUser.userToken = localJsonObject.get("user_token").getAsString();
                        APPUser.userID = localJsonObject.get("user_id").getAsString();
                        UserRegisterActivity.this.startActivity(new Intent(UserRegisterActivity.this, DevicesActivity.class));
                    }
                });
            }
        });
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(2130968605);
        initComponent();
    }
}
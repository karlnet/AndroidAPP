package com.hhnext.myeasylink;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
            localActionBar.setDisplayHomeAsUpEnabled(true);

        this.msgText = ((TextView) findViewById(R.id.msgTextR));
        this.msgText.setVisibility(View.GONE);
        this.smsCode = ((TextView) findViewById(R.id.verificationNumber));
        this.smsCode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                UserRegisterActivity.this.msgText.setVisibility(View.GONE);
            }
        });
        this.mobileNumber = ((TextView) findViewById(R.id.mobileNumberRegister));
        this.mobileNumber.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                UserRegisterActivity.this.msgText.setVisibility(View.GONE);
            }
        });
        this.password = ((TextView) findViewById(R.id.newPassword));
        this.smsButton = ((Button) findViewById(R.id.smsButton));
        this.smsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                RequestParams localRequestParams = new RequestParams("http://easylink.io/v2/users");
                localRequestParams.addHeader("content-type", "application/json");
                MyUtil.setRequestParamsHeader(localRequestParams);
                String str = "{\"username\":\"" + UserRegisterActivity.this.mobileNumber.getText().toString() + "\"}";
                Log.i("orinoco", "username= " + str);
                localRequestParams.setBodyContent(str);
                x.http().post(localRequestParams, new CommonCallback<String>() {
                    public void onCancelled(CancelledException paramAnonymous2CancelledException) {
                    }

                    public void onError(Throwable paramAnonymous2Throwable, boolean paramAnonymous2Boolean) {
                        Log.i("orinoco", "login reponse:" + paramAnonymous2Throwable.toString());
                        UserRegisterActivity.this.msgText.setText("短信服务器错误，请重新输入手机号码：");
                        UserRegisterActivity.this.msgText.setTextColor(Color.RED);
                        UserRegisterActivity.this.msgText.setVisibility(View.VISIBLE);
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
        this.loginButton = ((Button) findViewById(R.id.RegisterButton));
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                RequestParams localRequestParams = new RequestParams("http://easylink.io/v2/users");
                localRequestParams.addHeader("content-type", "application/json");
                MyUtil.setRequestParamsHeader(localRequestParams);
                String str = "{\"username\":\"" + UserRegisterActivity.this.mobileNumber.getText().toString() + "\"," + "\"verification_code\":\"" + UserRegisterActivity.this.smsCode.getText().toString() + "\"," + "\"password\":\"" + UserRegisterActivity.this.password.getText().toString() + "\"}";
                Log.i("orinoco", "s= " + str);
                localRequestParams.setBodyContent(str);
                x.http().post(localRequestParams, new CommonCallback<String>() {
                    public void onCancelled(CancelledException paramAnonymous2CancelledException) {
                    }

                    public void onError(Throwable paramAnonymous2Throwable, boolean paramAnonymous2Boolean) {
                        Log.i("orinoco", "login reponse:" + paramAnonymous2Throwable.toString());
                        UserRegisterActivity.this.msgText.setText("验证码错误，请重新输入：");
                        UserRegisterActivity.this.msgText.setTextColor(Color.RED);
                        UserRegisterActivity.this.msgText.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_register);
        initComponent();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default:
                return super.onOptionsItemSelected(menuItem);
            case android.R.id.home:
                Intent localIntent = new Intent(this, LoginActivity.class);
                localIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(localIntent);
                return true;
        }

    }
}
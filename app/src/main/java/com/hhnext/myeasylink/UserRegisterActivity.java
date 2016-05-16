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

//    private final static String resetPasswordUrl = "http://easylink.io/v2/users/password/reset";
//    private final static String registerUserUrl = "http://easylink.io/v2/users";
//    private final static String getSMSVerificationUrl = "http://easylink.io/v2/users/sms_verification_code";
//    private final static String myAddUserUrl="";
//    private final static String myResetPasswordUrl="";

    private String submitURL = APPUser.RegisterURL;
    private String mySubmitURL = APPUser.MyAddUserURL;


    private void initComponent() {

        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
            localActionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final int urlSource = intent.getIntExtra("urlSource", 0);
        if (urlSource == 1) {
            submitURL = APPUser.ResetPasswordURL;
            mySubmitURL = APPUser.MyResetPasswordURL;
        }
        msgText = ((TextView) findViewById(R.id.msgTextR));
        msgText.setVisibility(View.GONE);
        smsCode = ((TextView) findViewById(R.id.verificationNumber));
        smsCode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                msgText.setVisibility(View.GONE);
            }
        });
        mobileNumber = ((TextView) findViewById(R.id.mobileNumberRegister));
        mobileNumber.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                msgText.setVisibility(View.GONE);
            }
        });
        password = ((TextView) findViewById(R.id.newPassword));
        smsButton = ((Button) findViewById(R.id.smsButton));
        smsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                RequestParams localRequestParams = new RequestParams(APPUser.SMSVerificationURL);
                localRequestParams.addHeader("content-type", "application/json");
                MyUtil.setRequestParamsHeader(localRequestParams);
                String str = "{\"username\":\"" + mobileNumber.getText().toString() + "\"}";
                Log.i("orinoco", "username= " + str);
                localRequestParams.setBodyContent(str);
                x.http().post(localRequestParams, new CommonCallback<String>() {
                    public void onCancelled(CancelledException paramAnonymous2CancelledException) {
                    }

                    public void onError(Throwable paramAnonymous2Throwable, boolean paramAnonymous2Boolean) {
                        Log.i("orinoco", "login reponse:" + paramAnonymous2Throwable.toString());
                        msgText.setText("短信服务器错误，请重新输入手机号码：");
                        msgText.setTextColor(Color.RED);
                        msgText.setVisibility(View.VISIBLE);
                    }

                    public void onFinished() {
                    }

                    public void onSuccess(String res) {
                        if (((JsonObject) new JsonParser().parse(res)).get("result").getAsString().equals("success"))
                            loginButton.setEnabled(true);
                    }
                });
            }
        });
        loginButton = ((Button) findViewById(R.id.RegisterButton));
        if (urlSource == 1)
            loginButton.setText("重置密码");
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                RequestParams localRequestParams = new RequestParams(submitURL);
////                localRequestParams.addHeader("content-type", "application/json");
//                MyUtil.setRequestParamsHeader(localRequestParams);
//                String str = "{\"username\":\"" + mobileNumber.getText().toString() + "\"," + "\"verification_code\":\"" + smsCode.getText().toString() + "\"," + "\"password\":\"" + password.getText().toString() + "\"}";
//                Log.i("orinoco", "s= " + str);
//                localRequestParams.setBodyContent(str);
//                x.http().post(localRequestParams, new CommonCallback<String>() {
//                    public void onCancelled(CancelledException paramAnonymous2CancelledException) {
//                    }
//
//
//                    public void onError(Throwable paramAnonymous2Throwable, boolean paramAnonymous2Boolean) {
//                        Log.i("orinoco", "login reponse:" + paramAnonymous2Throwable.toString());
////                        JsonObject   jsonObject = (JsonObject) new JsonParser().parse(paramAnonymous2Throwable.getMessage());
////        if (jsonObject.has("payload")) {
//                       msgText.setText(paramAnonymous2Throwable.getMessage());
//                       msgText.setTextColor(Color.RED);
//                       msgText.setVisibility(View.VISIBLE);
//                    }
//
//                    public void onFinished() {
//                    }
//
//                    public void onSuccess(String result) {
//                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(result);
//                        APPUser.UserToken = jsonObject.get("token").getAsString();
//                        APPUser.UserID = jsonObject.get("user_id").getAsString();

                RequestParams localRequestParams = new RequestParams(mySubmitURL);
                MyUtil.setRequestParamsHeader(localRequestParams);
                String str = "{\"username\":\"" + mobileNumber.getText().toString() + "\"," + "\"verification_code\":\"" + smsCode.getText().toString() + "\"," + "\"password\":\"" + password.getText().toString() + "\"}";
//                String str = "{\"username\":\"" + mobileNumber.getText().toString() + "\"," + "\"password\":\"" + password.getText().toString() + "\"}";
                Log.i("orinoco", "s= " + str);
                localRequestParams.setBodyContent(str);
                x.http().post(localRequestParams, new CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        if (result != null) {
                            JsonObject jsonObject = (JsonObject) new JsonParser().parse(result);
                            APPUser.MyUserToken = jsonObject.get("user_token").getAsString();
                            APPUser.UserToken = jsonObject.get("fog_user_token").getAsString();
                            APPUser.UserID = jsonObject.get("fog_user_id").getAsString();
                            startActivity(new Intent(UserRegisterActivity.this, DevicesActivity.class));
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                        msgText.setText(ex.getMessage());
                        msgText.setTextColor(Color.RED);
                        msgText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });

//
//                    }
//                });
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
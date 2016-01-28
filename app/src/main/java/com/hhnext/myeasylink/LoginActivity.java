package com.hhnext.myeasylink;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class LoginActivity extends AppCompatActivity {

    private static final String text1 = "注册新用户";
    private static final String text2 = "忘记密码";

    private TextView msgText, mobileNumber, password, registerUserLink, forgetPasswordLink;
    private Button testButton, loginButton;

    private void initComponent() {


        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
            localActionBar.setDisplayHomeAsUpEnabled(true);

        SpannableString spanString1 = new SpannableString(text1);
        SpannableString spanString2 = new SpannableString(text2);
        spanString1.setSpan(new ClickableSpan() {
            public void onClick(View paramAnonymousView) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, UserRegisterActivity.class));
            }
        }, 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString2.setSpan(new ClickableSpan() {
            public void onClick(View paramAnonymousView) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, UserRegisterActivity.class));
            }
        }, 0, text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        this.forgetPasswordLink = ((TextView) findViewById(R.id.forgetPasswordLink));
        this.forgetPasswordLink.setText(spanString2);
        this.forgetPasswordLink.setMovementMethod(LinkMovementMethod.getInstance());

        this.registerUserLink = ((TextView) findViewById(R.id.registerUserLink));
        this.registerUserLink.setText(spanString1);
        this.registerUserLink.setMovementMethod(LinkMovementMethod.getInstance());


        this.msgText = ((TextView) findViewById(R.id.textView00));
        this.mobileNumber = ((TextView) findViewById(R.id.mobileNumber));
        this.password = ((TextView) findViewById(R.id.password));

        this.testButton = ((Button) findViewById(R.id.testButton));
        this.testButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {



            }
        });
        this.loginButton = ((Button) findViewById(R.id.loginButton));
        this.loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                LoginActivity.this.loginButton.setEnabled(false);
                RequestParams localRequestParams = new RequestParams("http://easylink.io/v2/users/login");
                localRequestParams.addHeader("content-type", "application/json");
                MyUtil.setRequestParamsHeader(localRequestParams);
                localRequestParams.setBodyContent("{\"username\":\"" + LoginActivity.this.mobileNumber.getText().toString() + "\"," + "\"password\":\"" + LoginActivity.this.password.getText().toString() + "\"}");
                x.http().post(localRequestParams, new CommonCallback<String>() {
                    public void onCancelled(CancelledException
                                                    paramAnonymous2CancelledException) {
                    }

                    public void onError(Throwable paramAnonymous2Throwable,
                                        boolean paramAnonymous2Boolean) {
                        Log.i("orinoco", "login reponse:" + paramAnonymous2Throwable.toString());
                        LoginActivity.this.msgText.setText("用户名或者密码错误，请重新输入：");
                        LoginActivity.this.msgText.setTextColor(Color.RED);
                    }

                    public void onFinished() {
                    }

                    public void onSuccess(String paramAnonymous2String) {
                        JsonObject localJsonObject = (JsonObject) new JsonParser().parse(paramAnonymous2String);
                        APPUser.userToken = localJsonObject.get("user_token").getAsString();
                        APPUser.userID = localJsonObject.get("user_id").getAsString();
                        Intent localIntent = new Intent(LoginActivity.this, DevicesActivity.class);
                        localIntent.setFlags(32768);
                        LoginActivity.this.startActivity(localIntent);
                    }
                });
            }
        });
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_login);
        initComponent();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:
                System.exit(0);
            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }
}
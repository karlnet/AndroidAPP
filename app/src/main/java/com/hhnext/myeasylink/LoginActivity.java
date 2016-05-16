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
    private static final String text3 = "请输入手机号和密码：";


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
                Intent intent = new Intent(LoginActivity.this, UserRegisterActivity.class);
                intent.putExtra("urlSource", 0);
                LoginActivity.this.startActivity(intent);
            }
        }, 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString2.setSpan(new ClickableSpan() {
            public void onClick(View paramAnonymousView) {
                Intent intent = new Intent(LoginActivity.this, UserRegisterActivity.class);
                intent.putExtra("urlSource", 1);
                LoginActivity.this.startActivity(intent);
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
        this.mobileNumber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                msgText.setTextColor(Color.BLACK);
                msgText.setText(text3);
            }
        });

//        this.mobileNumber.setText("13701308059@vip.163.com");

        this.mobileNumber.setText("13241695515");

        this.password = ((TextView) findViewById(R.id.password));
        this.password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                msgText.setTextColor(Color.BLACK);
                msgText.setText(text3);
            }
        });

        this.testButton = ((Button) findViewById(R.id.testButton));
        this.testButton.setVisibility(View.GONE);
//        this.testButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//
////                String JsonString = "{\"scope\":\"my-bucket:sunflower.jpg\",\"deadline\":1451491200,\"returnBody\":\"{\\\"name\\\":$(fname),\\\"size\\\":$(fsize),\\\"w\\\":$(imageInfo.width),\\\"h\\\":$(imageInfo.height),\\\"hash\\\":$(etag)}\"}" ;
////                byte[] data = null;
////                try {
////                    data = JsonString.getBytes("UTF-8");
////                } catch (Exception e1) {
////                    e1.printStackTrace();
////                }
////                String base64String = Base64.encodeToString(data, Base64.DEFAULT);
////                Log.i("orinoco", base64String);
////                String sign= null;
////                try {
////                    sign = MyUtil.hmacSha1(base64String, "MY_SECRET_KEY");
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//                UploadManager uploadManager = new UploadManager();
//                String key = "/images/yunan001.jpg";
//                String token = APPUser.QiniuAuth.uploadToken(APPUser.Bucket, key);
//
//                AssetManager a = getAssets();
//                byte[] data = null;
//                try {
//                    InputStream is = a.open("yunan001.jpg");
//                    data = new byte[is.available()];
//                    is.read(data);
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//                uploadManager.put(data, key, token, new UpCompletionHandler() {
//                    @Override
//                    public void complete(String key, ResponseInfo info, JSONObject response) {
//                        Log.i("orinoco", response.toString());
//                    }
//                }, null);
//
//
//            }
//        });
        this.loginButton = ((Button) findViewById(R.id.loginButton));
        this.loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                loginButton.setEnabled(false);
//                RequestParams localRequestParams = new RequestParams(APPUser.LoginURL);
//                MyUtil.setRequestParamsHeader(localRequestParams);
//                localRequestParams.setBodyContent("{\"username\":\"" + mobileNumber.getText().toString() + "\"," + "\"password\":\"" + password.getText().toString() + "\"}");
//                x.http().post(localRequestParams, new CommonCallback<String>() {
//                    public void onCancelled(CancelledException
//                                                    exception) {
//                    }
//
//                    public void onError(Throwable ex,
//                                        boolean b) {
//                        Log.i("orinoco", "login reponse:" + ex.toString());
//                        msgText.setText("用户名或者密码错误，请重新输入：");
//                        msgText.setTextColor(Color.RED);
//                    }
//
//                    public void onFinished() {
//                    }
//
//                    public void onSuccess(String s) {
//                        JsonObject localJsonObject = (JsonObject) new JsonParser().parse(s);
//                        APPUser.UserToken = localJsonObject.get("user_token").getAsString();
//                        APPUser.UserID = localJsonObject.get("user_id").getAsString();


                RequestParams localRequestParams = new RequestParams(APPUser.MyLoginURL);
                MyUtil.setRequestParamsHeader(localRequestParams);
                final String user = mobileNumber.getText().toString();
                final String pass = password.getText().toString();
                String str = "{\"username\":\"" + user + "\"," + "\"password\":\"" + pass + "\"}";
//                Log.i("orinoco", "s= " + str);
                localRequestParams.setBodyContent(str);
                x.http().post(localRequestParams, new CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        JsonObject localJsonObject = (JsonObject) new JsonParser().parse(result);
                        APPUser.MyUserToken = localJsonObject.get("user_token").getAsString();
                        APPUser.UserToken = localJsonObject.get("fog_user_token").getAsString();
                        APPUser.UserID = localJsonObject.get("fog_user_id").getAsString();
                        APPUser.UserName = user;
                        APPUser.UserPassword = pass;

                        Intent intent = new Intent(LoginActivity.this, DevicesActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        loginButton.setEnabled(true);
                        LoginActivity.this.startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.i("orinoco", "s= " + ex.toString());
                        msgText.setText("用户名或者密码错误，请重新输入：");
                        msgText.setTextColor(Color.RED);
                        loginButton.setEnabled(true);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
//                        Log.i("orinoco", "s= " + cex.toString());
                    }

                    @Override
                    public void onFinished() {

//                        Log.i("orinoco", "s= over");
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
package com.hhnext.myeasylink;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserAuthorizeActivity extends AppCompatActivity {
    protected GHCB mGHCB;
    List<User> users;
    @Bind(R.id.usersListView)
    ListView usersListView;
    @Bind(R.id.usersMsgText)
    TextView userMsgText;
    @Bind(R.id.submitButton)
    Button submitButton;
    ActionBar actionBar;
    AlertDialog ad;
    int currentOP;
    private EditText userMobile;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_user_authorize, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent localIntent = new Intent(this, DeviceActivity.class);
                localIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(localIntent);
                break;
            case R.id.action_addUser:
                userMsgText.setText(mGHCB.getDevAlias() + " 的授权用户：");
                submitButton.setVisibility(View.GONE);
                usersListView.setAdapter(new ArrayAdapter<User>(UserAuthorizeActivity.this, android.R.layout.simple_list_item_1, users));

                if (ad == null)
                    ad = new AlertDialog.Builder(UserAuthorizeActivity.this)
                            .setTitle("请输入用户手机号：")
                            .setIcon(R.drawable.tranlate48)
                            .setView(userMobile)
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    RequestParams requestParams = new RequestParams(GHCBManage.authorizeURL);
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("device_id", mGHCB.getDevID());
                                    jsonObject.addProperty("owner_type", "Share");
                                    jsonObject.addProperty("login_id", userMobile.getText().toString());
                                    Log.i("orinoco", jsonObject.toString());
                                    requestParams.setBodyContent(jsonObject.toString());
                                    MyUtil.setRequestParamsHeader(requestParams);
                                    x.http().post(requestParams, new Callback.CommonCallback<String>() {
                                        @Override
                                        public void onSuccess(String result) {
                                            Toast.makeText(UserAuthorizeActivity.this, "添加用户成功！", Toast.LENGTH_SHORT).show();
                                            scanUsers();
                                        }

                                        @Override
                                        public void onError(Throwable ex, boolean isOnCallback) {
                                            Toast.makeText(UserAuthorizeActivity.this, "添加用户失败！", Toast.LENGTH_SHORT).show();
                                            Log.i("orinoco", "add user error .");
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
                            .setNegativeButton("取消", null)
                            .show();
                else
                    ad.show();
                break;
            case R.id.action_deleteUser:
                currentOP = 1;
                userMsgText.setText("请选择删除的用户：");
                submitButton.setVisibility(View.VISIBLE);
                usersListView.setAdapter(new ArrayAdapter<User>(UserAuthorizeActivity.this, android.R.layout.simple_list_item_multiple_choice, users));
                usersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                break;
            case R.id.action_changeUserRole:
                currentOP = 2;
                userMsgText.setText("请选择成为管理员的用户：");
                submitButton.setVisibility(View.VISIBLE);
                usersListView.setAdapter(new ArrayAdapter<User>(UserAuthorizeActivity.this, android.R.layout.simple_list_item_single_choice, users));
                usersListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.submitButton)
    public void submit() {
        if (currentOP == 1)
            scanUsers();
        ;

        if (currentOP == 2)
            scanUsers();
        ;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authorize);
        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mGHCB = GHCBAPP.CURRENTGHCB;
        userMobile = new EditText(UserAuthorizeActivity.this);
        userMsgText.setText(mGHCB.getDevAlias() + " 的授权用户：");
        submitButton.setVisibility(View.GONE);
        scanUsers();

    }


    public void scanUsers() {
//        Log.i("orinoco", "start scan");
        RequestParams requestParams = new RequestParams(GHCBManage.userQueryURL);
        requestParams.setBodyContent("{\"device_id\":\"" + mGHCB.getDevID() + "\"}");
        MyUtil.setRequestParamsHeader(requestParams);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            public void onCancelled(CancelledException paramAnonymousCancelledException) {
            }

            public void onError(Throwable ex, boolean paramAnonymousBoolean) {
                Log.i("orinoco", "user query reponse:" + ex.toString());
            }

            public void onFinished() {
            }

            public void onSuccess(String jsonString) {
                Type listType = new TypeToken<ArrayList<User>>() {
                }.getType();
                users = new Gson().fromJson(jsonString, listType);
                usersListView.setAdapter(new ArrayAdapter<User>(UserAuthorizeActivity.this, android.R.layout.simple_list_item_1, users));
                submitButton.setVisibility(View.GONE);
            }
        });
    }


    class User {


        /**
         * id : a48ee14e-96a8-4f1f-b40e-6c27af902e51
         * username : 13701308059@vip.163.com
         * role : owner
         */

        private String id;
        private String username;
        private String role;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        @Override
        public String toString() {
            if (role.equals("owner"))
                return username + " 管理员";
            else
                return username;
        }
    }
}

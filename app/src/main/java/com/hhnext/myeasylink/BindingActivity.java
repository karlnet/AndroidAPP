package com.hhnext.myeasylink;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class BindingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);

        ActionBar localActionBar = getSupportActionBar();
        if (localActionBar != null)
            localActionBar.setDisplayHomeAsUpEnabled(true);

        final TextView macTextView = (TextView) findViewById(R.id.bindingMAC);
        macTextView.setText("C8:93:46:52:E2:DB");

        Button bindingButton = ((Button) findViewById(R.id.bindingButton));

        bindingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RequestParams request = new RequestParams(APPUser.MyAuthorizeURL);
                    APPUser.setMyRequestParamsHeader(request);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("mac", macTextView.getText().toString());
                    String ss = APPUser.getActiveToken(macTextView.getText().toString());
                    request.setBodyContent(jsonObject.toString());

                    x.http().post(request, new Callback.CommonCallback<String>() {
                        public void onCancelled(CancelledException ex) {

                        }

                        public void onError(Throwable ex, boolean b) {

                        }

                        public void onFinished() {
                        }

                        public void onSuccess(String jsonStr) {
                            Intent intent = new Intent(BindingActivity.this, DevicesActivity.class);
                            startActivity(intent);
                        }
                    });
                } catch (Exception e) {

                }
            }
        });

    }


}

package com.ftmouse5g.slotmv4;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.appsflyer.AppsFlyerLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AppPlay extends AppCompatActivity implements Tools.ConnectionHelper.ReceiverListener {

    private static final String API_URL = "https://5gbgaming.site/api/";
    private static final String PARAM1 = "";
    private static final String PARAM2 = "true";
    private static final String PARAM3 = "5GB04";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_play);

        getWindow().setFlags(1024, 1024);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        checkNetStatus();
    }

    public void onNetworkChange(boolean isConnected) { notifyNetStatus(isConnected); }

    private void checkNetStatus() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new Tools.ConnectionHelper(), intentFilter);

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();

        boolean isConnected = netInfo != null && netInfo.isConnectedOrConnecting();
        notifyNetStatus(isConnected);
    }

    private void notifyNetStatus(boolean isConnected) {
        if (isConnected) {
            Tools.VolleyHelper.init(this);

            AppsFlyerLib.getInstance().init(Tools.AFID, null, this);
            AppsFlyerLib.getInstance().start(this);

            RequestQueue queue = Tools.VolleyHelper.getRequestQueue();

            StringRequest myReq = new StringRequest(Request.Method.POST, API_URL,
                    createMyReqSuccessListener(),
                    createMyReqErrorListener()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("request", PARAM1);
                    params.put("decrypt", PARAM2);
                    params.put("appid", PARAM3);
                    return params;
                }
            };
            queue.add(myReq);
        } else {
            showNetStatusDialog();
        }
    }

    private void showNetStatusDialog() {
        LinearLayout noNetLayout = findViewById(R.id.noNetworkContainer);
        ProgressBar loadingLayout = findViewById(R.id.progressBar);

        loadingLayout.setVisibility(View.GONE);
        noNetLayout.setVisibility(View.VISIBLE);

        Button btnOkay = findViewById(R.id.btn_networkOkay);
        Button btnCancel = findViewById(R.id.btn_networkCancel);

        btnOkay.setOnClickListener(view -> checkNetStatus());
        btnCancel.setOnClickListener(view -> finishAndRemoveTask());
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return this::setNotifyNet;
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return error -> showNetStatusDialog();
    }

    private void setNotifyNet(String str) {
        parseResponse(str);
    }

    private void parseResponse(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            int mBtn = jsonObj.getInt("btn");
            String mURL = jsonObj.getString("gameUrl");

            // For debugging purposes only. Enable this line of code on any area to see the response needed
            // Log.e("INF:DBG", mURL + ":" + mBtn);

            new Handler().postDelayed(() -> {
                Intent i = new Intent(getApplicationContext(), IntroApp.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("mURL", mURL);
                i.putExtra("mBtn", mBtn);
                startActivity(i);
                finish();
            }, 600);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

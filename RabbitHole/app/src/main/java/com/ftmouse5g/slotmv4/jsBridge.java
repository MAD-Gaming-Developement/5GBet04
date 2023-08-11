package com.ftmouse5g.slotmv4;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.appsflyer.AppsFlyerLib;
import com.google.firebase.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class jsBridge {

    public static final String TAG = "jsBridge";
    private FirebaseAnalytics mAnalytics;
    Context mContext;

    public jsBridge(Context context) {
        mAnalytics = FirebaseAnalytics.getInstance(context);
        mContext = context;
    }

    @JavascriptInterface
    public void postMessage(String name, String data) {
        LOGD("logEvent:" + name);
        mAnalytics.logEvent(name, bundleFromJson(data));
        AppsFlyerLib.getInstance().logEvent(mContext, name, mapFromJson(data));
    }

    @JavascriptInterface
    public void setUserProperty(String name, String value) {
        LOGD("setUserProperty:" + name);
        mAnalytics.setUserProperty(name, value);
    }

    private void LOGD(String message) {
        // Only log on debug builds, for privacy
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    private Map<String, Object> mapFromJson(String json) {
        // [START_EXCLUDE]
        if (TextUtils.isEmpty(json)) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                if (value instanceof String) {
                    result.put(key, (String) value);
                } else if (value instanceof Integer) {
                    result.put(key, (Integer) value);
                } else if (value instanceof Double) {
                    result.put(key, (Double) value);
                } else {
                    Log.w(TAG, "Value for key " + key + " not one of [String, Integer, Double]");
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, "Failed to parse JSON, returning empty Bundle.", e);
            return new HashMap<>();
        }
        return result;
        // [END_EXCLUDE]
    }

    private Bundle bundleFromJson(String json) {
        // [START_EXCLUDE]
        if (TextUtils.isEmpty(json)) {
            return new Bundle();
        }

        Bundle result = new Bundle();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                if (value instanceof String) {
                    result.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    result.putInt(key, (Integer) value);
                } else if (value instanceof Double) {
                    result.putDouble(key, (Double) value);
                } else {
                    Log.w(TAG, "Value for key " + key + " not one of [String, Integer, Double]");
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, "Failed to parse JSON, returning empty Bundle.", e);
            return new Bundle();
        }
        return result;
        // [END_EXCLUDE]
    }

}

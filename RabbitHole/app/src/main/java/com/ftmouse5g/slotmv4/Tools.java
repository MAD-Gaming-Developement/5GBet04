package com.ftmouse5g.slotmv4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Tools {

    public final static String AFID = "SFWVDHBkhS2ApqrvpWGtCk";

    public static class ConnectionHelper extends BroadcastReceiver {
        public static ReceiverListener Listener;

        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager connectionManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo netInfo = connectionManager.getActiveNetworkInfo();

            if (Listener != null) {
                boolean isConnected = netInfo != null && netInfo.isConnectedOrConnecting();
                Listener.onNetworkChange(isConnected);
            }
        }

        public interface ReceiverListener {
            void onNetworkChange(boolean isConnected);
        }
    }

    public static class VolleyHelper
    {
        private static RequestQueue mRequestQueue;

        private VolleyHelper() { }

        public static void init(Context context) { mRequestQueue = Volley.newRequestQueue(context); }

        public static RequestQueue getRequestQueue() {
            if (mRequestQueue != null) {
                return mRequestQueue;
            } else {
                throw new IllegalStateException("Volley not initialized");
            }
        }
    }

}

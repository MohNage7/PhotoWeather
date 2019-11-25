package com.mohnage7.weather.data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mohnage7.weather.WeatherPhotoApplication;


public class NetworkUtils {
    public static final int CONNECTION_TIMEOUT = 60; // 60 seconds
    public static final int READ_TIMEOUT = 60; // 60 seconds
    public static final int WRITE_TIMEOUT = 60; // 60 seconds
    public static final int CACHE_TIMEOUT = 2; // 2 minutes
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) WeatherPhotoApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}

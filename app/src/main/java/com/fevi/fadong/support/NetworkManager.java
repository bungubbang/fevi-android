package com.fevi.fadong.support;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 1000742 on 15. 7. 7..
 */
public class NetworkManager {

    private NetworkManager() { }

    public static boolean isOnline(Context context) {
        if(context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null) {
            return false;
        }

        NetworkInfo MOBILE = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo WIFI = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo LTE = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);

        boolean online = false;
        if(LTE != null) {
            online = LTE.isConnected();
        }
        if(MOBILE != null) {
            if (MOBILE.isConnected() || WIFI.isConnected() || online) {
                online = true;
            }
        } else {
            if(WIFI.isConnected() || online) {
                online = true;
            }
        }
        return online;
    }

    public static boolean isOffline(Context context) {
        return isOnline(context) == false;
    }
}


package me.gall.sgp.android.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetworkInfo {
    public static final String LOG_TAG = NetworkInfo.class.getSimpleName();

    /**
     * @param connectivity ConnectivityManager instance
     * @return if there is an available connection
     */
    public static final boolean isConnectedOrConnecting(Context context) {
        return isWifiConnect(context) || isMobileConnect(context);
    }

    public static boolean isWifiConnect(Context context) {
        if (context
                .checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "ACCESS_WIFI_STATE permission denied.");
            return true;
        }
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo wifi = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifi.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setWifiEnabled(Context context, boolean on) {
        WifiManager wManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        wManager.setWifiEnabled(on);
        SystemClock.sleep(2000);
    }

    public static boolean isMobileConnect(Context context) {
        if (context
                .checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "ACCESS_NETWORK_STATE permission denied.");
            return true;
        }
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo mobile = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobile.isConnectedOrConnecting()
                    && mobile.isAvailable()
                    && getTelephonyManager(context).getDataState() == TelephonyManager.DATA_CONNECTED) {
                Log.d(LOG_TAG,
                        "mobile is connected." + " Type:"
                                + mobile.getTypeName() + " APN:"
                                + mobile.getExtraInfo());
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static String getDataConnectionNetworkInfo(Context context) {
        if (context
                .checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "ACCESS_NETWORK_STATE permission denied.");
            return null;
        }
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo mobile = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return mobile.getExtraInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

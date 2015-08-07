
package me.gall.sgp.android.common;

import java.io.FileNotFoundException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public final class Configuration {

    public static final String LOG_TAG = Configuration.class.getSimpleName();

    public static String getManifestMetaValue(Context context, String metakey) {
        String value = null;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            value = applicationInfo.metaData.getString(metakey);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage()
                    + " unknown meta key or not exist:" + metakey);
        }
        return value;
    }

    public static boolean containsManifestMetaKey(Context context, String metakey) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return applicationInfo.metaData.containsKey(metakey);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage()
                    + " unknown meta key or not exist:" + metakey);
        }
        return false;
    }

    public static boolean getManifestMetaBooleanValue(Context context, String metakey, boolean defaultValue) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return applicationInfo.metaData.getBoolean(metakey);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage()
                    + " unknown meta key or not exist:" + metakey);
        }
        return defaultValue;
    }

    public static int getManifestMetaIntValue(Context context, String metakey, int defaultValue) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return applicationInfo.metaData.getInt(metakey);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage()
                    + " unknown meta key or not exist:" + metakey);
        }
        return defaultValue;
    }

    public static final int getIdentifier(Context context, String name,
            String type) throws FileNotFoundException {
        int id = context.getResources().getIdentifier(name, type,
                context.getPackageName());
        if (id == 0)
            throw new FileNotFoundException(name + " is not found in res/"
                    + type + "/.");
        return id;
    }

    public static final int getLayoutIdentifier(Context context, String name)
            throws FileNotFoundException {
        return getIdentifier(context, name, "layout");
    }

    public static final int getDrawableIdentifier(Context context, String name)
            throws FileNotFoundException {
        return getIdentifier(context, name, "drawable");
    }

    public static final int getStringIdentifier(Context context, String name)
            throws FileNotFoundException {
        return getIdentifier(context, name, "string");
    }

    public static final int getViewIdentifier(Context context, String name)
            throws NullPointerException {
        try {
            return getIdentifier(context, name, "id");
        } catch (FileNotFoundException e) {
            throw new NullPointerException(name + " is not found.");
        }
    }

    public static final View findViewByName(Context context, View root,
            String name) {
        return root.findViewById(getViewIdentifier(context, name));
    }

    public static final View inflateView(Context context, String name)
            throws FileNotFoundException {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(getLayoutIdentifier(context, name), null);
    }

    private static final String SGP_SETTING = "SGP_SETTING";

    public static SharedPreferences getSetting(Context context) {
        return context.getApplicationContext().getSharedPreferences(SGP_SETTING, 0);
    }
}

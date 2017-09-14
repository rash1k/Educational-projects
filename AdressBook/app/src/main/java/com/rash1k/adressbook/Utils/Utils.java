package com.rash1k.adressbook.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Utils {

    public static boolean checkEnableGPS(Context context) {

        try {
            int locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isNetworkAvailableAndConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

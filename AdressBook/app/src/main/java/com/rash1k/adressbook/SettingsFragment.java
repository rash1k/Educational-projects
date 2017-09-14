package com.rash1k.adressbook;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_PREF_LOCATION = "location";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
/*
        if (key.equals(KEY_PREF_LOCATION)) {
//            LocationServices.FusedLocationApi.requestLocationUpdates()
        }*/
    }

    public static boolean getAccessLocation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_PREF_LOCATION, false);
    }

    public static void setAccessLocation(Context context, boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(KEY_PREF_LOCATION,isOn).apply();
    }
}

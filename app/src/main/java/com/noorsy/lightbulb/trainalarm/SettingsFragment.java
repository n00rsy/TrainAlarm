package com.noorsy.lightbulb.trainalarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    //private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listner;

    private final String RINGTONE_KEY = "ringetone_preference";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        //Preference p = findPreference("parent_checkbox_preference");
        //p.setSummary("test sauce");
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        onSharedPreferenceChanged(prefs, RINGTONE_KEY);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Log.d("TAG", "SHARED PREFERENCES CHANGED");
        if (key.equals(RINGTONE_KEY)) {
            Preference connectionPref = findPreference(key);
            String r = sharedPreferences.getString(key, "");
            Uri ringtoneUri = Uri.parse(r);
            Ringtone ringtone = RingtoneManager.getRingtone(getContext(), ringtoneUri);
            String name = ringtone.getTitle(getContext());
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(name);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "RINGTONE RESULT");
        //Preference connectionPref = findPreference(RINGTONE_KEY);
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        onSharedPreferenceChanged(prefs, RINGTONE_KEY);
        //connectionPref.setSummary("test");
    }
}



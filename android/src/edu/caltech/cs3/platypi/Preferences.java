package edu.caltech.cs3.platypi;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private SignalFinderApp app;
    private enum Pref {PREF_COLLECT_DATA, PREF_COLLECTION_FREQ, PREF_APIROOT, PREF_DISPLAY_CARRIERS};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        app = (SignalFinderApp) getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        Pref p = Pref.valueOf(key);
        Log.d("froo","here we are");
        switch (p) {
        case PREF_COLLECT_DATA:
        case PREF_COLLECTION_FREQ:
            if (sharedPreferences.getBoolean("PREF_COLLECT_DATA", true)) {
                app.turnAlarmOn();
            } else {
                app.turnAlarmOff();
            }
            break;
        case PREF_APIROOT:
        case PREF_DISPLAY_CARRIERS:
            break;
        }
    }
}

package com.weather.app.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.weather.R;

/**
 *  Use PreferenceFragment which automatically save user preferences into SharedPreferences
 *  */
public class SettingsPreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the Preferences XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}

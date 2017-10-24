package com.weather.app.settings;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.weather.R;

/**
 * Stores the last user's configuration such as the last US city and preferred temperature units.
 * */
public class SettingsActivity extends FragmentActivity {

    // Constants
    public static final String SETTINGS_PREFERENCE_FRAGMENT_TAG = "SettingsPreferenceFragmentTag";

    // Fields
    private EditTextPreference cityNamePreference;
    private ListPreference unitsPreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsPreferencesFragment(), SETTINGS_PREFERENCE_FRAGMENT_TAG).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SettingsPreferencesFragment settingsPreferencesFragment = (SettingsPreferencesFragment) getFragmentManager().findFragmentByTag(SETTINGS_PREFERENCE_FRAGMENT_TAG);

        cityNamePreference = (EditTextPreference) settingsPreferencesFragment.findPreference(getString(R.string.city_name_preference_key));
        cityNamePreference.setSummary((cityNamePreference.getText() != null && !cityNamePreference.getText().equals("")) ? cityNamePreference.getText() : getString(R.string.city_name_preference_default_summary));
        cityNamePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener());

        unitsPreference = (ListPreference) settingsPreferencesFragment.findPreference(getString(R.string.units_preference_key));
        unitsPreference.setSummary(unitsPreference.getEntry());
        unitsPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener());
    }

    /** Called when the preferred city name has been changed */
    private class OnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            if (preference.getKey() == getString(R.string.city_name_preference_key)) {

                // Convert newValue to String
                String newCityNameString = newValue.toString();

                // If the new Value is not empty, set it, else set the placeHolder
                cityNamePreference.setSummary(!newCityNameString.equals("") ? newCityNameString : getString(R.string.city_name_preference_default_summary));

            } else if (preference.getKey() == getString(R.string.units_preference_key)) {

                // Get the index of newValue in the array of values
                int index = unitsPreference.findIndexOfValue(newValue.toString());

                // Get the array of entries
                CharSequence[] entries = unitsPreference.getEntries();

                // Set the new summary with the selected entry
                String newSummaryString = entries[index].toString();
                unitsPreference.setSummary(newSummaryString);

            }

            return true;
        }
    }
}

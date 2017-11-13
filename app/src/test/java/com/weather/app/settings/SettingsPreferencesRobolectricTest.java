package com.weather.app.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.widget.Button;

import com.weather.R;
import com.weather.app.dialog.ApplicationDialogFragment;

import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowApplication;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class SettingsPreferencesRobolectricTest {

    private Activity activity;

    /*** Listener */
    private OnPreferenceChangeListener onPreferenceChangeListener;
    public interface OnPreferenceChangeListener {
        boolean onPreferenceChange(Preference preference, Object newValue);
    }
    public void setOnPreferenceChangeListener(OnPreferenceChangeListener onPreferenceChangeListener) {
        this.onPreferenceChangeListener = onPreferenceChangeListener;
    }

    /** Constructor */
    public SettingsPreferencesRobolectricTest(Activity activity) {
        this.activity = activity;
    }

    /**
     * Checks if the city name is stored in SharedPreferences.
     *
     * The city name will be stored in SharedPreferences through a SettingsActivity which
     * controls the application Preferences
     */
    public void setSettingsPreferences_shouldSetCityName(String cityString) {

        String cityName = SettingsPreferencesManager.getCityName();

        // If the city name will be set for the first time, a dialog is shown asking user for inserting a city
        if (cityName.equals("")) {

            // Check that applicationDialogFragment has been opened
            ApplicationDialogFragment applicationDialogFragment = (ApplicationDialogFragment) activity.getFragmentManager().findFragmentByTag("applicationDialogFragment");
            assertNotNull(applicationDialogFragment.getDialog());

            // Click the OK button of applicationDialogFragment and check that applicationDialogFragment has been dismissed
            AlertDialog alertDialog = (AlertDialog) applicationDialogFragment.getDialog();
            Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            okButton.performClick();
            assertNull(applicationDialogFragment.getDialog());

            // Check if the correct Intent to start Settings Activity is started
            Intent expectedIntent = new Intent(activity, SettingsActivity.class);
            Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());

            // Check if SettingsActivity's lifecycle has been started
            SettingsActivity settingsActivity = Robolectric.buildActivity(SettingsActivity.class, expectedIntent).create().start().resume().get();
            assertNotNull(settingsActivity);

            // Check if SettingsPreferencesFragment is started
            SettingsPreferencesFragment settingsPreferencesFragment = (SettingsPreferencesFragment) settingsActivity.getFragmentManager().findFragmentByTag(SettingsActivity.SETTINGS_PREFERENCE_FRAGMENT_TAG);
            assertNotNull(settingsPreferencesFragment);

            // Set a city name in the cityNamePreference
            EditTextPreference cityNamePreference = (EditTextPreference) settingsPreferencesFragment.findPreference(activity.getString(R.string.city_name_preference_key));
            cityNamePreference.setText(cityString);

            // Finish SettingsActivity
            settingsActivity.finish();

            // Check that the city name has been stored in SharedPreferences
            assertEquals(cityString, SettingsPreferencesManager.getCityName());

            if (this.onPreferenceChangeListener != null) {
                this.onPreferenceChangeListener.onPreferenceChange(cityNamePreference, cityString);
            }

        } else { // Else, If the city name will be set for subsequent times, the user has to click the settings button

            // Click the properties button to start the SettingsActivity
            Button settingsButton = activity.findViewById(R.id.settingsButton);
            settingsButton.performClick();

            Intent expectedIntent = new Intent(activity, SettingsActivity.class);
            Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());

            // Check if SettingsActivity's lifecycle has been started
            SettingsActivity settingsActivity = Robolectric.buildActivity(SettingsActivity.class, expectedIntent).create().start().resume().get();
            assertNotNull(settingsActivity);

            // Check if SettingsPreferencesFragment is started
            SettingsPreferencesFragment settingsPreferencesFragment = (SettingsPreferencesFragment) settingsActivity.getFragmentManager().findFragmentByTag(SettingsActivity.SETTINGS_PREFERENCE_FRAGMENT_TAG);
            assertNotNull(settingsPreferencesFragment);

            // Set a city name in the cityNamePreference
            EditTextPreference cityNamePreference = (EditTextPreference) settingsPreferencesFragment.findPreference(activity.getString(R.string.city_name_preference_key));
            cityNamePreference.setText(cityString);

            // Finish SettingsActivity
            settingsActivity.finish();

            // Check that the city name has been stored in SharedPreferences
            assertEquals(cityString, SettingsPreferencesManager.getCityName());

            if (this.onPreferenceChangeListener != null) {
                this.onPreferenceChangeListener.onPreferenceChange(cityNamePreference, cityString);
            }
        }
    }
}

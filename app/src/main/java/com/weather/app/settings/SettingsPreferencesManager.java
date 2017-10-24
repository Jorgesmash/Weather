package com.weather.app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.weather.R;

/**
 * Singleton class to manage the access to the user's preferences.
 *
 * Controls the access to the user's preferences and makes them available along all the application
 * */
public class SettingsPreferencesManager {

    public final static String FAHRENHEIT_UNITS = "imperial";
    public final static String CELSIUS_UNITS = "metric";

    private static Context context;

    private static SettingsPreferencesManager settingsPreferencesManager;
    private static SharedPreferences sharedPreferences;

    public static SettingsPreferencesManager newInstance(Context context) {

        SettingsPreferencesManager.context = context;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        settingsPreferencesManager = new SettingsPreferencesManager();

        return settingsPreferencesManager;
    }

    /**
     * Returns the last city name entered by the user, or null if user has not entered a city name
     */
    public static String getCityName() {
        return sharedPreferences.getString(context.getString(R.string.city_name_preference_key), "");
    }

    /**
     * Returns the temperature units code preferred by the user,
     * or Fahrenheit as default if user has not entered preferred units
     */
    public static String getTemperatureUnits() {

        String value = sharedPreferences.getString(context.getString(R.string.units_preference_key), "0");

        if (value.equals("1")) {
            return SettingsPreferencesManager.FAHRENHEIT_UNITS;

        } else if (value.equals("2")) {
            return SettingsPreferencesManager.CELSIUS_UNITS;
        }

        return "";
    }
}

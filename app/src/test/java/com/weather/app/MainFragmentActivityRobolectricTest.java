package com.weather.app;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import com.weather.BuildConfig;
import com.weather.R;
import com.weather.app.api.WeatherAPIManager;
import com.weather.app.api.WeatherAPIRobolectricTest;
import com.weather.app.datamodels.CurrentWeatherDataModel;
import com.weather.app.main.MainFragmentActivity;
import com.weather.app.settings.SettingsPreferencesRobolectricTest;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertThat;

/**
 * Performs an non instrumented test of the MainFragmentActivity flow to store
 * a city name in the Application SharedPreferences and then performs an API call.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainFragmentActivityRobolectricTest {

    // The FragmentActivity which holds the view hierarchy
    private MainFragmentActivity mainFragmentActivity;

    private SettingsPreferencesRobolectricTest settingsPreferencesRobolectricTest;

    private WeatherAPIRobolectricTest weatherAPIRobolectricTest;

    @Before
    public void setUp() throws Exception {
        mainFragmentActivity = Robolectric.setupActivity(MainFragmentActivity.class);

        settingsPreferencesRobolectricTest = new SettingsPreferencesRobolectricTest(mainFragmentActivity);
        settingsPreferencesRobolectricTest.setOnPreferenceChangeListener(new OnPreferenceChangeListener());

        weatherAPIRobolectricTest = new WeatherAPIRobolectricTest(mainFragmentActivity);
        weatherAPIRobolectricTest.setOnConnectionResultListener(new WeatherAPIRobolectricTestOnConnectionResultListener());
    }

    /**
     * This test checks the functionality of the application flow when the user inserts a city for the first time
     * */
    @Test
    public void checkCityInsertedForTheFirstTimeTest() throws Exception {

        // Make the first city name insertion
        String cityString = "Westerville";
        settingsPreferencesRobolectricTest.setSettingsPreferences_shouldSetCityName(cityString);
    }

    /**
     *  This test checks the functionality of the application flow when the user inserts a city for second time
     *  */
    @Test
    public void checkCityInsertedForSubsequentTimesTest() throws Exception {

        // Since this is a test without instruments, there is no a real storage. So to simulate a subsequent
        // city name insertion, first we have to make a first one
        String cityString = "Pasadena";
        settingsPreferencesRobolectricTest.setSettingsPreferences_shouldSetCityName(cityString);

        // Make the second city name insertion
        cityString = "Orlando";
        settingsPreferencesRobolectricTest.setSettingsPreferences_shouldSetCityName(cityString);
    }

    /**
     *  This test checks the functionality of the API call
     *  */
    @Test
    public void checkAPICallTest() throws Exception {

        // Insert a city name
        String cityString = "Gahanna";
        weatherAPIRobolectricTest.callAPICall_shouldRetrieveCurrentWeatherInformation(cityString);
    }

    /**
     * This test checks the functionality of the application flow when the user inserts a city for the first time.
     *
     * First stores a city name in the SharedPreferences and then makes an API call to retrieve \
     * its current weather information
     * */
    @Test
    public void performWalkthroughTest() throws Exception {

        // Make the second city name insertion
        String cityString = "Westerville";
        settingsPreferencesRobolectricTest.setSettingsPreferences_shouldSetCityName(cityString);
    }

    /** Called when the preferred city name has been changed */
    private class OnPreferenceChangeListener implements SettingsPreferencesRobolectricTest.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            if (preference.getKey().equals(mainFragmentActivity.getString(R.string.city_name_preference_key))) {

                assertThat(preference, CoreMatchers.instanceOf(EditTextPreference.class));

                EditTextPreference editTextPreference = (EditTextPreference) preference;

                // Convert newValue to String
                String newCityNameString = newValue.toString();

                // If the new Value is not empty, set it, else set the placeHolder
                retrieveCurrentWeatherInformation(newCityNameString);

            } else if (preference.getKey().equals(mainFragmentActivity.getString(R.string.units_preference_key))) {

                assertThat(preference, CoreMatchers.instanceOf(ListPreference.class));

                ListPreference unitsPreference = (ListPreference) preference;

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

    /**
     * After user inserts a city name, next step is to make an API call in order to retrieve
     * its current weather
     * */
    private void retrieveCurrentWeatherInformation(String cityNameString) {
        weatherAPIRobolectricTest.callAPICall_shouldRetrieveCurrentWeatherInformation(cityNameString);
    }

    /** Called when the API has finished retrieving the forecast information */
    private class WeatherAPIRobolectricTestOnConnectionResultListener implements WeatherAPIRobolectricTest.OnConnectionResultListener {

        @Override
        public void onConnectionResult(String status, CurrentWeatherDataModel currentWeatherDataModel) {

            if (status.equals(WeatherAPIManager.NETWORK_ERROR)) {
                System.out.println(mainFragmentActivity.getString(R.string.no_network_dialog_fragment_title));
                System.out.println(mainFragmentActivity.getString(R.string.no_network_dialog_fragment_message));

            } else if (status.equals(WeatherAPIManager.RESULT_TIMEOUT)) {
                System.out.println(mainFragmentActivity.getString(R.string.result_timeout_dialog_fragment_title));
                System.out.println(mainFragmentActivity.getString(R.string.result_timeout_dialog_fragment_message));

            } else if (status.equals(WeatherAPIManager.RESULT_ERROR)) {
                System.out.println(mainFragmentActivity.getString(R.string.result_error_dialog_fragment_title));
                System.out.println(mainFragmentActivity.getString(R.string.result_error_dialog_fragment_message));


            } else if (status.equals(WeatherAPIManager.RESULT_OK)) {
                System.out.println("API call Successful");
                System.out.println("City: " + currentWeatherDataModel.getCityString());
                System.out.println("Conditions: " + currentWeatherDataModel.getSkyConditionsDescriptionString());
                System.out.println("Current Temp °C: " + currentWeatherDataModel.getCurrentTemperatureCelsiusString());
                System.out.println("Current Temp °F: " + currentWeatherDataModel.getCurrentTemperatureFahrenheitString());
                System.out.println("Minimum Temp °C: " + currentWeatherDataModel.getMinimumTemperatureCelsiusString());
                System.out.println("Minimum Temp °F: " + currentWeatherDataModel.getMinimumTemperatureFahrenheitString());
                System.out.println("Maximum Temp °C: " + currentWeatherDataModel.getMaximumTemperatureCelsiusString());
                System.out.println("Maximum Temp °F: " + currentWeatherDataModel.getMaximumTemperatureFahrenheitString());
                System.out.println("Humidity: " + currentWeatherDataModel.getHumidityString());
                System.out.println("Pressure: " + currentWeatherDataModel.getPressureString());
                System.out.println("Wind Speed: " + currentWeatherDataModel.getWindSpeedString());
                System.out.println("Visibility: " + currentWeatherDataModel.getVisibilityString());
            }
        }
    }
}

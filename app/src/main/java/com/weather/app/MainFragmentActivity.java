package com.weather.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weather.R;
import com.weather.app.dialog.ApplicationDialogFragment;
import com.weather.app.api.WeatherAPIManager;
import com.weather.app.datamodels.CurrentWeatherDataModel;
import com.weather.app.settings.SettingsActivity;
import com.weather.app.settings.SettingsPreferencesManager;

/**
 * Entry activity of the weather application.
 *
 * Presents the current weather by showing an icon for the sky condition, the temperature in Fahrenheit
 * or Celsius, and the city the current weather belongs to.
 *
 * If the current temperature is below 60° F the background of the application will be blue, if it is
 * above or equal to 60° F, background of the application will be orange.
 * */
public class MainFragmentActivity extends FragmentActivity {

    // Widgets
    private RelativeLayout currentWeatherRelativeLayout;
    private TextView cityTextView;
    private TextView currentTemperatureTextView;

    private TextView minimumTemperatureLabelTextView;
    private TextView minimumTemperatureTextView;

    private TextView maximumTemperatureLabelTextView;
    private TextView maximumTemperatureTextView;

    private TextView skyConditionTextView;
    private ImageView skyConditionImageView;

    private TextView humidityLabelTextView;
    private TextView humidityTextView;

    private TextView pressureLabelTextView;
    private TextView pressureTextView;

    private TextView windSpeedLabelTextView;
    private TextView windSpeedTextView;

    private TextView visibilityLabelTextView;
    private TextView visibilityTextView;

    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);

        // Create a singleton instance for SettingsPreferencesManager
        SettingsPreferencesManager.newInstance(this);

        /** Get the components of the content layout */

        // Current weather section
        currentWeatherRelativeLayout = findViewById(R.id.currentWeatherRelativeLayout);

        cityTextView = findViewById(R.id.cityTextView);

        currentTemperatureTextView = findViewById(R.id.currentTemperatureTextView);

        minimumTemperatureLabelTextView = findViewById(R.id.minimumTemperatureLabelTextView);
        minimumTemperatureTextView = findViewById(R.id.minimumTemperatureTextView);

        maximumTemperatureLabelTextView = findViewById(R.id.maximumTemperatureLabelTextView);
        maximumTemperatureTextView = findViewById(R.id.maximumTemperatureTextView);

        skyConditionTextView = findViewById(R.id.skyConditionTextView);
        skyConditionImageView = findViewById(R.id.skyConditionImageView);

        humidityLabelTextView = findViewById(R.id.humidityLabelTextView);
        humidityTextView = findViewById(R.id.humidityTextView);

        pressureLabelTextView = findViewById(R.id.pressureLabelTextView);
        pressureTextView = findViewById(R.id.pressureTextView);

        windSpeedLabelTextView = findViewById(R.id.windSpeedLabelTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);

        visibilityLabelTextView = findViewById(R.id.visibilityLabelTextView);
        visibilityTextView = findViewById(R.id.visibilityTextView);

        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new SettingsButtonOnClickListener());
    }

    @Override
    protected void onStart() {
        super.onStart();

        retrieveForecastInformation();
    }

    /**
     * Checks if user has already typed a city name and if true, creates a new API connection
     * to retrieve the current weather information for the next 7 days. Also sets an OnConnectionResultListener
     * which will be called when the result of the API connection has finished retrieving
     * the weather information
     * */
    private void retrieveForecastInformation() {

        String cityName = SettingsPreferencesManager.getCityName();
        if (cityName.equals("")) {

            currentWeatherRelativeLayout.setBackgroundColor(this.getResources().getColor(android.R.color.darker_gray, null));
            cityTextView.setVisibility(View.INVISIBLE);
            skyConditionImageView.setVisibility(View.INVISIBLE);
            currentTemperatureTextView.setVisibility(View.INVISIBLE);
            skyConditionTextView.setVisibility(View.INVISIBLE);
            minimumTemperatureLabelTextView.setVisibility(View.INVISIBLE);
            minimumTemperatureTextView.setVisibility(View.INVISIBLE);
            maximumTemperatureLabelTextView.setVisibility(View.INVISIBLE);
            maximumTemperatureTextView.setVisibility(View.INVISIBLE);
            humidityLabelTextView.setVisibility(View.INVISIBLE);
            humidityTextView.setVisibility(View.INVISIBLE);
            pressureLabelTextView.setVisibility(View.INVISIBLE);
            pressureTextView.setVisibility(View.INVISIBLE);
            windSpeedLabelTextView.setVisibility(View.INVISIBLE);
            windSpeedTextView.setVisibility(View.INVISIBLE);
            visibilityLabelTextView.setVisibility(View.INVISIBLE);
            visibilityTextView.setVisibility(View.INVISIBLE);

            showDialogFragment(this.getString(R.string.city_name_dialog_fragment_title), this.getString(R.string.city_name_dialog_fragment_message), new NoCityNameDialogFragmentPositiveButtonOnClickListener());
            return;
        }

        WeatherAPIManager weatherAPIManager = new WeatherAPIManager(this);
        weatherAPIManager.setOnConnectionResultListener(new WeatherAPIManagerOnConnectionResultListener());
        weatherAPIManager.connect(cityName);
    }

    /** Listens if the API has finished retrieving the forecast informations.
     * If there any error, this will be reported to user in a Dialog.
     * If a successful result is received, we'll populate both current weather a forecast sections
     * */
    private class WeatherAPIManagerOnConnectionResultListener implements WeatherAPIManager.OnConnectionResultListener {

        @Override
        public void onConnectionResult(String status, CurrentWeatherDataModel currentWeatherDataModel) {

            if (status.equals(WeatherAPIManager.NETWORK_ERROR)) {
                showDialogFragment(getString(R.string.no_network_dialog_fragment_title), getString(R.string.no_network_dialog_fragment_message), new NoNetworkDialogFragmentPositiveButtonOnClickListener());

            }  else if (status.equals(WeatherAPIManager.RESULT_TIMEOUT)) {

                showDialogFragment(getString(R.string.result_timeout_dialog_fragment_title), getString(R.string.result_timeout_dialog_fragment_message), new ResultTimeoutDialogFragmentPositiveButtonOnClickListener());

            } else if (status.equals(WeatherAPIManager.RESULT_ERROR)) {

                showDialogFragment(getString(R.string.result_error_dialog_fragment_title), getString(R.string.result_error_dialog_fragment_message), new ResultErrorDialogFragmentPositiveButtonOnClickListener());

            } else if (status.equals(WeatherAPIManager.RESULT_OK)) {

                populateCurrentWeatherView(currentWeatherDataModel);
            }
        }
    }

    /**
     * Populates the Current Weather Section.
     * It takes currentWeatherDataModel to show the city, current temperature
     * and current sky condition.
     * */
    private void populateCurrentWeatherView(CurrentWeatherDataModel currentWeatherDataModel) {

        cityTextView.setVisibility(View.VISIBLE);
        skyConditionImageView.setVisibility(View.VISIBLE);
        currentTemperatureTextView.setVisibility(View.VISIBLE);
        skyConditionTextView.setVisibility(View.VISIBLE);
        minimumTemperatureLabelTextView.setVisibility(View.VISIBLE);
        minimumTemperatureTextView.setVisibility(View.VISIBLE);
        maximumTemperatureLabelTextView.setVisibility(View.VISIBLE);
        maximumTemperatureTextView.setVisibility(View.VISIBLE);
        humidityLabelTextView.setVisibility(View.VISIBLE);
        humidityTextView.setVisibility(View.VISIBLE);
        pressureLabelTextView.setVisibility(View.VISIBLE);
        pressureTextView.setVisibility(View.VISIBLE);
        windSpeedLabelTextView.setVisibility(View.VISIBLE);
        windSpeedTextView.setVisibility(View.VISIBLE);
        visibilityLabelTextView.setVisibility(View.VISIBLE);
        visibilityTextView.setVisibility(View.VISIBLE);

        // Populate location with city and state
        String cityString = currentWeatherDataModel.getCityString();
        cityTextView.setText(cityString);

        // Populate the temperatures with the user's preferred units
        String currentTemperatureString;
        String minimumTemperatureString;
        String maximumTemperatureString;

        // Select preferred units from user's preferences
        String temperatureUnits = SettingsPreferencesManager.getTemperatureUnits();
        if (temperatureUnits.equals(SettingsPreferencesManager.FAHRENHEIT_UNITS)) {
            currentTemperatureString = currentWeatherDataModel.getCurrentTemperatureFahrenheitString() + "° F";
            minimumTemperatureString = currentWeatherDataModel.getMinimumTemperatureFahrenheitString() + "° F";
            maximumTemperatureString = currentWeatherDataModel.getMaximumTemperatureFahrenheitString() + "° F";
        } else {
            currentTemperatureString = currentWeatherDataModel.getCurrentTemperatureCelsiusString() + "° C";
            minimumTemperatureString = currentWeatherDataModel.getMinimumTemperatureCelsiusString() + "° C";
            maximumTemperatureString = currentWeatherDataModel.getMaximumTemperatureCelsiusString() + "° C";
        }
        currentTemperatureTextView.setText(currentTemperatureString);
        minimumTemperatureTextView.setText(minimumTemperatureString);
        maximumTemperatureTextView.setText(maximumTemperatureString);

        // Select background color depending of the current temperature. If the temperature is below
        // 60 °F, the application background will be blue (cold), otherwise will be orange (orange)
        int color;
        int temperature = (int) Double.parseDouble(currentWeatherDataModel.getCurrentTemperatureFahrenheitString());
        if (temperature < 60) {
            color = R.color.coolWeatherColor;
        } else {
            color = R.color.warmWeatherColor;
        }
        currentWeatherRelativeLayout.setBackgroundColor(getColor(color));

        // Populate sky conditions label
        String skyConditionsString = currentWeatherDataModel.getSkyConditionsDescriptionString();
        skyConditionTextView.setText(skyConditionsString);

        // Populate sky conditions icon
        int imageResource = this.getResources().getIdentifier(currentWeatherDataModel.getSkyConditionsIconString(), "drawable", this.getPackageName());
        Drawable iconDrawable = this.getResources().getDrawable(imageResource, null);
        iconDrawable.setColorFilter(new LightingColorFilter(this.getColor(android.R.color.black), this.getColor(android.R.color.white)));
        skyConditionImageView.setImageDrawable(iconDrawable);

        // Populate pressure
        String pressureString = currentWeatherDataModel.getPressureString();
        pressureTextView.setText(pressureString + " mmHg");

        String windSpeedString = currentWeatherDataModel.getWindSpeedString();
        windSpeedTextView.setText(windSpeedString + " m/h");

        String visibilityString = currentWeatherDataModel.getVisibilityString();
        visibilityTextView.setText(visibilityString + " ft");
    }

    /**
     * Called when the user presses the Settings button.
     * It will open the settings activity
     * */
    private class SettingsButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            startSettingsActivity();
        }
    }

    /**
     * Shows a multi purpose DialogFragment.
     */
    private void showDialogFragment(String title, String message, DialogInterface.OnClickListener onClickListener) {

        ApplicationDialogFragment applicationDialogFragment = ApplicationDialogFragment.newInstance(this);
        applicationDialogFragment.setTitle(title);
        applicationDialogFragment.setMessage(message);
        applicationDialogFragment.setCancelable(false);
        applicationDialogFragment.setPositiveButtonOnClickListener(onClickListener);
        applicationDialogFragment.show(getFragmentManager(), "applicationDialogFragment");
    }

    /**
     * Called when user presses the OK button of NoCityNameDialogFragment
     * It will open the settings activity, so the user can enter a city name
     * */
    private class NoCityNameDialogFragmentPositiveButtonOnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            startSettingsActivity();
        }
    }

    /**
     * Called when user presses the OK button in the NoNetworkDialogFragment.
     * It will try to connect again
     * */
    private class NoNetworkDialogFragmentPositiveButtonOnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            retrieveForecastInformation();
        }
    }

    /**
     * Called when user presses the OK button in the ResultTimeoutDialogFragment.
     * It will try to connect again
     * */
    private class ResultTimeoutDialogFragmentPositiveButtonOnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            retrieveForecastInformation();
        }
    }

    /**
     * Called when user presses the OK button in the ResultErrorDialogFragment.
     * It will show the SettingsActivity
     * */
    private class ResultErrorDialogFragmentPositiveButtonOnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            startSettingsActivity();
        }
    }

    /**
     * Start the Settings Activity
     * */
    private void startSettingsActivity() {

        Intent intent = new Intent(MainFragmentActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}

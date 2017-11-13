package com.weather.app.main;

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

        /* Get the components of the content layout */

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

            // Make UI elements invisible
            setUIElementsVisible(View.INVISIBLE);

            showDialogFragment(this.getString(R.string.city_name_dialog_fragment_title), this.getString(R.string.city_name_dialog_fragment_message), new RetrySettingsActivityDialogFragmentPositiveButtonOnClickListener());

            return;
        }

        WeatherAPIManager weatherAPIManager = new WeatherAPIManager(this);
        weatherAPIManager.setOnConnectionResultListener(new WeatherAPIManagerOnConnectionResultListener());
        weatherAPIManager.connectToWeatherEndpoint(cityName);
    }

    /** Listens if the API has finished retrieving the forecast informations.
     * If there any error, this will be reported to user in a Dialog.
     * If a successful result is received, we'll populate both current weather a forecast sections
     * */
    private class WeatherAPIManagerOnConnectionResultListener implements WeatherAPIManager.OnConnectionResultListener {

        @Override
        public void onConnectionResult(String status, CurrentWeatherDataModel currentWeatherDataModel) {

            if (status.equals(WeatherAPIManager.NETWORK_ERROR)) {
                showDialogFragment(getString(R.string.no_network_dialog_fragment_title), getString(R.string.no_network_dialog_fragment_message), new RetryAPIConnectionDialogFragmentPositiveButtonOnClickListener());

            }  else if (status.equals(WeatherAPIManager.RESULT_TIMEOUT)) {
                showDialogFragment(getString(R.string.result_timeout_dialog_fragment_title), getString(R.string.result_timeout_dialog_fragment_message), new RetryAPIConnectionDialogFragmentPositiveButtonOnClickListener());

            } else if (status.equals(WeatherAPIManager.RESULT_ERROR)) {
                showDialogFragment(getString(R.string.result_error_dialog_fragment_title), getString(R.string.result_error_dialog_fragment_message), new RetrySettingsActivityDialogFragmentPositiveButtonOnClickListener());

            } else if (status.equals(WeatherAPIManager.RESULT_NOTFOUND)) {
                showDialogFragment(getString(R.string.result_notfound_dialog_fragment_title), getString(R.string.result_notfound_dialog_fragment_message), new RetrySettingsActivityDialogFragmentPositiveButtonOnClickListener());

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

        // Make UI elements visible
        setUIElementsVisible(View.VISIBLE);

        // Select background color depending of the current temperature. If the temperature is below
        // 60 °F, the application background will be blue (cold), otherwise will be orange (orange)
        int color;
        int temperature = (int) Double.parseDouble(currentWeatherDataModel.getCurrentTemperatureFahrenheitString());
        if (temperature < 60) {
            color = R.color.coolWeatherColor;
        } else {
            color = R.color.warmWeatherColor;
        }
        currentWeatherRelativeLayout.setBackgroundColor(this.getColor(color));

        // Populate location with city
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
     * Shows a multi purpose DialogFragment.
     */
    private void showDialogFragment(String title, String message, DialogInterface.OnClickListener onClickListener) {

        ApplicationDialogFragment applicationDialogFragment = ApplicationDialogFragment.newInstance();
        applicationDialogFragment.setTitle(title);
        applicationDialogFragment.setMessage(message);
        applicationDialogFragment.setCancelable(false);
        applicationDialogFragment.setPositiveButtonOnClickListener(onClickListener);
        applicationDialogFragment.show(getFragmentManager(), "applicationDialogFragment");
    }

    /**
     * Called when user presses the OK button of a DialogFragment where user will try to enter a valid city again.
     * It will open the settings activity, so the user can enter a city name.
     * */
    private class RetrySettingsActivityDialogFragmentPositiveButtonOnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            startSettingsActivity();
        }
    }

    /**
     * Called when user presses the OK button of a DialogFragment where user will try to enter a valid city again.
     * It will try to connect again.
     * */
    private class RetryAPIConnectionDialogFragmentPositiveButtonOnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            retrieveForecastInformation();
        }
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
     * Start the Settings Activity
     * */
    private void startSettingsActivity() {

        Intent intent = new Intent(MainFragmentActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Sets the visibility of the view's UI elements
     * */
    private void setUIElementsVisible(int visibility) {

        cityTextView.setVisibility(visibility);
        skyConditionImageView.setVisibility(visibility);
        currentTemperatureTextView.setVisibility(visibility);
        skyConditionTextView.setVisibility(visibility);
        minimumTemperatureLabelTextView.setVisibility(visibility);
        minimumTemperatureTextView.setVisibility(visibility);
        maximumTemperatureLabelTextView.setVisibility(visibility);
        maximumTemperatureTextView.setVisibility(visibility);
        humidityLabelTextView.setVisibility(visibility);
        humidityTextView.setVisibility(visibility);
        pressureLabelTextView.setVisibility(visibility);
        pressureTextView.setVisibility(visibility);
        windSpeedLabelTextView.setVisibility(visibility);
        windSpeedTextView.setVisibility(visibility);
        visibilityLabelTextView.setVisibility(visibility);
        visibilityTextView.setVisibility(visibility);

        if (visibility == View.INVISIBLE) {
            currentWeatherRelativeLayout.setBackgroundColor(this.getResources().getColor(android.R.color.darker_gray, null));
        }
    }
}

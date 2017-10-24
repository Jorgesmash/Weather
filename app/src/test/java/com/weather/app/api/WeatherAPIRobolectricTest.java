package com.weather.app.api;

import android.app.Activity;

import com.weather.BuildConfig;
import com.weather.R;
import com.weather.app.MainFragmentActivity;
import com.weather.app.datamodels.CurrentWeatherDataModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Performs an non instrumented test of the MainFragmentActivity flow to retrieve the
 * current weather information.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class WeatherAPIRobolectricTest {

    private Activity activity;

    /**  Listener to inform that a result from the API connection has been gotten */
    private OnConnectionResultListener onConnectionResultListener;
    public interface OnConnectionResultListener {
        void onConnectionResult(String status, CurrentWeatherDataModel currentWeatherDataModel);
    }
    public void setOnConnectionResultListener(OnConnectionResultListener onConnectionResultListener) {
        this.onConnectionResultListener = onConnectionResultListener;
    }

    /** Constructor */
    public WeatherAPIRobolectricTest(Activity activity) {
        this.activity = activity;
    }

    /**
     * Checks the proper functionality of the API call.
     *
     * An AsyncTask is executed to retrieve weather information from API
     * */
    public void callAPICall_shouldRetrieveCurrentWeatherInformation(String cityName) {

        WeatherAPIManager weatherAPIManager = new WeatherAPIManager(activity);
        weatherAPIManager.setOnConnectionResultListener(new WeatherAPIManagerOnConnectionResultListener());
        weatherAPIManager.connect(cityName);
    }

    /** Listens if the API has finished retrieving the forecast information.
     * If there any error, this will be reported to user in a Dialog.
     * If a successful result is received, we'll populate both current weather a forecast sections
     * */
    private class WeatherAPIManagerOnConnectionResultListener implements WeatherAPIManager.OnConnectionResultListener {

        @Override
        public void onConnectionResult(String status, CurrentWeatherDataModel currentWeatherDataModel) {

            if (status.equals(WeatherAPIManager.NETWORK_ERROR)) {
                if (onConnectionResultListener != null) {
                    onConnectionResultListener.onConnectionResult(WeatherAPIManager.NETWORK_ERROR, null);
                }

            }  else if (status.equals(WeatherAPIManager.RESULT_TIMEOUT)) {
                if (onConnectionResultListener != null) {
                    onConnectionResultListener.onConnectionResult(WeatherAPIManager.RESULT_TIMEOUT, null);
                }

            } else if (status.equals(WeatherAPIManager.RESULT_ERROR)) {
                if (onConnectionResultListener != null) {
                    onConnectionResultListener.onConnectionResult(WeatherAPIManager.RESULT_ERROR, null);
                }

            } else if (status.equals(WeatherAPIManager.RESULT_OK)) {
                if (onConnectionResultListener != null) {
                    onConnectionResultListener.onConnectionResult(WeatherAPIManager.RESULT_OK, currentWeatherDataModel);
                }
            }
        }
    }
}

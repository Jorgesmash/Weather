package com.weather.app.api;

import android.app.Activity;

import com.weather.app.datamodels.CurrentWeatherDataModel;

/**
 * Performs an non instrumented test of the MainFragmentActivity flow to retrieve the
 * current weather information.
 */
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
        weatherAPIManager.setOnResponseListener(new WeatherAPIManagerOnResponseListener());
        weatherAPIManager.connectToWeatherEndpoint(cityName);
    }

    /** Listens if the API has finished retrieving the forecast information.
     * If there any error, this will be reported to user in a Dialog.
     * If a successful result is received, we'll populate both current weather a forecast sections
     * */
    private class WeatherAPIManagerOnResponseListener implements WeatherAPIManager.OnResponseListener {

        @Override
        public void onResponse(String status, CurrentWeatherDataModel currentWeatherDataModel) {

            if (status.equals(WeatherAPIManager.NETWORK_ERROR)) {
                if (onConnectionResultListener != null) {
                    onConnectionResultListener.onConnectionResult(WeatherAPIManager.NETWORK_ERROR, null);
                }

            }  else if (status.equals(WeatherAPIManager.RESPONSE_TIMEOUT)) {
                if (onConnectionResultListener != null) {
                    onConnectionResultListener.onConnectionResult(WeatherAPIManager.RESPONSE_TIMEOUT, null);
                }

            } else if (status.equals(WeatherAPIManager.RESPONSE_NOTFOUND)) {
                if (onConnectionResultListener != null) {
                    onConnectionResultListener.onConnectionResult(WeatherAPIManager.RESPONSE_NOTFOUND, null);
                }

            } else if (status.equals(WeatherAPIManager.RESPONSE_OK)) {
                if (onConnectionResultListener != null) {
                    onConnectionResultListener.onConnectionResult(WeatherAPIManager.RESPONSE_OK, currentWeatherDataModel);
                }
            }
        }
    }
}

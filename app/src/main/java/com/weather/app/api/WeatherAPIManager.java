package com.weather.app.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.weather.app.constants.Constants;
import com.weather.app.datamodels.CurrentWeatherDataModel;
import com.weather.app.util.TemperatureUnitsConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Manages the connection to the forecast API.
 *
 * It makes some necessary tasks previous connecting to the API, like verify that the mobile has
 * network connection. Also receives the API call response and process it by taking the necessary
 * information to fill the data model.
 * */
public class WeatherAPIManager {

    // Network status
    public static final String NETWORK_ERROR = "NETWORK_ERROR";

    // Response status
    public static final String RESPONSE_OK = WeatherAPIConnectionAsyncTask.NETWORK_OK;
    public static final String RESPONSE_TIMEOUT = WeatherAPIConnectionAsyncTask.NETWORK_TIMEOUT;
    public static final String RESPONSE_NOTFOUND = WeatherAPIConnectionAsyncTask.NETWORK_NOTFOUND;

    private Context context;

    /**  Listener to inform that a result from the API connection has been gotten */
    private OnResponseListener onResponseListener;
    public interface OnResponseListener {
        void onResponse(String status, CurrentWeatherDataModel currentWeatherDataModel);
    }
    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    /** Constructor */
    public WeatherAPIManager(Context context) {
        this.context = context;
    }

    /**
     * Weather connection endpoint:
     *
     * Calls the 'weather' endpoint which receives the city name entered by the user and
     * sends the location info
     *
     * Example: http://api.openweathermap.org/data/2.5/weather?q=Westerville,OH,US&appid=c06f6494e5dcffdb0cd5ed54d03dbc24
     */
    public void connectToWeatherEndpoint(String cityName) {

        if (isNetworkConnected()) {

            String endpointName = Constants.WEATHER_ENDPOINT_NAME;

            String endpointURL = Constants.BASE_URL + Constants.WEATHER_ENDPOINT_NAME + "?" + "q=" + cityName + "," + "US" + "&" + "appid=" + Constants.WEATHER_API_KEY;

            executeAPIConnectionAsyncTask(endpointName, endpointURL);

        } else {
            onResponseListener.onResponse(NETWORK_ERROR, null);
        }
    }

    /**
     *  Creates a new instance of the async task and executes it.
     *
     *  It is called for each connection to the RESTful API endpoint
     **/
    private void executeAPIConnectionAsyncTask(String endpointName, String endpointURL) {

        WeatherAPIConnectionAsyncTask weatherAPIConnectionAsyncTask = new WeatherAPIConnectionAsyncTask();
        weatherAPIConnectionAsyncTask.setOnConnectionResultListener(new WeatherAPIConnectionAsyncTaskOnConnectionResultListener());
        weatherAPIConnectionAsyncTask.execute(endpointName, endpointURL);
    }

    /**
     * Validates that the device is connected to network.
     * */
    private boolean isNetworkConnected() {

        // Check if a network connection is available
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;

        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     *  Receives the result of the asynchronous task to the API.
     *
     *  Connect to an endpoint which receives the city name and sends a response with the
     *  information for the current weather.
     *  */
    private class WeatherAPIConnectionAsyncTaskOnConnectionResultListener implements WeatherAPIConnectionAsyncTask.OnConnectionResultListener {

        @Override
        public void onConnectionResult(String endpointName, String status, String result) {

            if (status.equals(RESPONSE_OK)) {

                // Convert the JSON data into Model data
                CurrentWeatherDataModel currentWeatherDataModel = processWeatherEndpointResult(result);

                onResponseListener.onResponse(status, currentWeatherDataModel);

            } else {

                onResponseListener.onResponse(status, null);
            }
        }
    }

    /**
     * Processing of the first connection endpoint response:
     * Parses the JSON result of the endpoint call to get the current weather information.
     */
    private CurrentWeatherDataModel processWeatherEndpointResult(String result) {

        // The instance of the data model is created here, once we're sure that the web service
        // has responded successful. Before this, it is not necessary to instance the data model
        CurrentWeatherDataModel currentWeatherDataModel = new CurrentWeatherDataModel();

        try {

            JSONObject resultJSONObject = new JSONObject(result);

            // Take the city from JSON and set it in currentWeatherDataModel
            String cityString = resultJSONObject.getString("name");
            currentWeatherDataModel.setCityString(cityString);

            // Take the current temperature and set it in currentWeatherDataModel
            JSONObject mainJSONObject = resultJSONObject.getJSONObject("main");
            String currentTemperatureKelvinString = mainJSONObject.getString("temp");
            int currentTemperatureKelvin = (int) Double.parseDouble(currentTemperatureKelvinString);
            // Convert the current temperature from Kelvin to Celsius and set it in currentWeatherDataModel
            int currentTemperatureFahrenheit = TemperatureUnitsConverter.convertKelvinToFahrenheit(currentTemperatureKelvin);
            String currentTemperatureFahrenheitString = Integer.toString(currentTemperatureFahrenheit);
            currentWeatherDataModel.setCurrentTemperatureFahrenheitString(currentTemperatureFahrenheitString);
            // Convert the current temperature from Kelvin to Fahrenheit and set it in currentWeatherDataModel
            int currentTemperatureCelsius = TemperatureUnitsConverter.convertKelvinToCelsius(currentTemperatureKelvin);
            String currentTemperatureCelsiusString = Integer.toString(currentTemperatureCelsius);
            currentWeatherDataModel.setCurrentTemperatureCelsiusString(currentTemperatureCelsiusString);

            // Get the weather JSON array from JSON response
            JSONArray weatherJSONArray = resultJSONObject.getJSONArray("weather");
            // Here we're taking the first item of the array of sky conditions because as the API documentation says, the first item is the primary one:
            JSONObject weatherJSONObject = (JSONObject) weatherJSONArray.get(0);

            // Take the sky conditions and set it in currentWeatherDataModel
            String skyConditionsString = weatherJSONObject.getString("main");
            currentWeatherDataModel.setSkyConditionsDescriptionString(skyConditionsString);

            // Take the sky conditions icon identifier and set it in currentWeatherDataModel
            String skyConditionsIconString = weatherJSONObject.getString("icon");
            currentWeatherDataModel.setSkyConditionsIconString(skyConditionsIconString);

            // Take the minimum temperature from JSON response
            String minimumTemperatureKelvinString = mainJSONObject.getString("temp_min");
            int minimumTemperatureKelvin = (int) Double.parseDouble(minimumTemperatureKelvinString);
            // Convert the minimum temperature from Kelvin to Fahrenheit and set it in currentWeatherDataModel
            int minimumTemperatureFahrenheit = TemperatureUnitsConverter.convertKelvinToFahrenheit(minimumTemperatureKelvin);
            String minimumTemperatureFahrenheitString = Integer.toString(minimumTemperatureFahrenheit);
            currentWeatherDataModel.setMinimumTemperatureFahrenheitString(minimumTemperatureFahrenheitString);
            // Convert the minimum temperature from Kelvin to Celsius and set it in currentWeatherDataModel
            int minimumTemperatureCelsius = TemperatureUnitsConverter.convertKelvinToCelsius(minimumTemperatureKelvin);
            String minimumTemperatureCelsiusString = Integer.toString(minimumTemperatureCelsius);
            currentWeatherDataModel.setMinimumTemperatureCelsiusString(minimumTemperatureCelsiusString);

            // Take the maximum temperature from JSON response
            String maximumTemperatureKelvinString = mainJSONObject.getString("temp_max");
            int maximumTemperatureKelvin = (int) Double.parseDouble(maximumTemperatureKelvinString);
            // Convert the maximum temperature from Kelvin to Fahrenheit and set it in currentWeatherDataModel
            int maximumTemperatureFahrenheit = TemperatureUnitsConverter.convertKelvinToFahrenheit(maximumTemperatureKelvin);
            String maximumTemperatureFahrenheitString = Integer.toString(maximumTemperatureFahrenheit);
            currentWeatherDataModel.setMaximumTemperatureFahrenheitString(maximumTemperatureFahrenheitString);
            // Convert the maximum temperature from Kelvin to Celsius and set it in currentWeatherDataModel
            int maximumTemperatureCelsius = TemperatureUnitsConverter.convertKelvinToCelsius(maximumTemperatureKelvin);
            String maximumTemperatureCelsiusString = Integer.toString(maximumTemperatureCelsius);
            currentWeatherDataModel.setMaximumTemperatureCelsiusString(maximumTemperatureCelsiusString);

            // Take the humidity from JSON response
            String humidityString = mainJSONObject.getString("humidity");
            currentWeatherDataModel.setHumidityString(humidityString);

            // Take the pressure from JSON response
            String pressureString = mainJSONObject.getString("pressure");
            currentWeatherDataModel.setPressureString(pressureString);

            // Take the wind speed from JSON response
            JSONObject windJSONObject = resultJSONObject.getJSONObject("wind");
            String windSpeedString = windJSONObject.getString("speed");
            currentWeatherDataModel.setWindSpeedString(windSpeedString);

            // Take the visibility from JSON response
            String visibilityString = resultJSONObject.getString("visibility");
            currentWeatherDataModel.setVisibilityString(visibilityString);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currentWeatherDataModel;
    }
}

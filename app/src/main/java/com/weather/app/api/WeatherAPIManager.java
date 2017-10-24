package com.weather.app.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.weather.app.constants.Constants;
import com.weather.app.datamodels.CurrentWeatherDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.weather.app.util.TemperatureUnitsConverter.convertKelvinToCelsius;
import static com.weather.app.util.TemperatureUnitsConverter.convertKelvinToFahrenheit;

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
    public static final String RESULT_OK = WeatherAPIConnectionAsyncTask.RESPONSE_OK;
    public static final String RESULT_ERROR = WeatherAPIConnectionAsyncTask.RESPONSE_ERROR;
    public static final String RESULT_TIMEOUT = WeatherAPIConnectionAsyncTask.RESPONSE_TIMEOUT;

    private Context context;

    // Data Models
    private CurrentWeatherDataModel currentWeatherDataModel;

    /**  Listener to inform that a result from the API connection has been gotten */
    private OnConnectionResultListener onConnectionResultListener;
    public interface OnConnectionResultListener {
        void onConnectionResult(String status, CurrentWeatherDataModel currentWeatherDataModel);
    }
    public void setOnConnectionResultListener(OnConnectionResultListener onConnectionResultListener) {
        this.onConnectionResultListener = onConnectionResultListener;
    }

    /** Constructor */
    public WeatherAPIManager(Context context) {
        this.context = context;
    }

    /** Connects to the Weather API to retrieve the weather information */
    public void connect(String cityName) {
        connectToWeatherEndpoint(cityName);
    }

    /**
     *  Creates a new instance of the async task and executes it.
     *
     *  It is called for each connection to the RESTful API endpoint
     **/
    private void executeAPIConnectionAsyncTask(String endpointName, String endpointURL) {

        WeatherAPIConnectionAsyncTask weatherAPIConnectionAsyncTask = new WeatherAPIConnectionAsyncTask();
        weatherAPIConnectionAsyncTask.setOnResponseListener(new WeatherAPIConnectionAsyncTaskOnResponseListener());
        weatherAPIConnectionAsyncTask.execute(endpointName, endpointURL);
    }

    /**
     * Validates that the device is connected to network
     * */
    private boolean isNetworkConnected() {

        // Check if a network connection is available
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    /**
     * First connection endpoint:
     *
     * Calls the 'weather' endpoint which receives the city name entered by the user and
     * sends the location info
     *
     * Example: http://api.openweathermap.org/data/2.5/weather?q=Westerville,OH,US&appid=c06f6494e5dcffdb0cd5ed54d03dbc24
     */
    private void connectToWeatherEndpoint(String cityName) {

        if (isNetworkConnected()) {

            String endpointName = Constants.WEATHER_ENDPOINT_NAME;

            String endpointURL = Constants.BASE_URL + Constants.WEATHER_ENDPOINT_NAME + "?" + "q=" + cityName + "," + "US" + "&" + "appid=" + Constants.WEATHER_API_KEY;

            executeAPIConnectionAsyncTask(endpointName, endpointURL);

        } else {
            onConnectionResultListener.onConnectionResult(NETWORK_ERROR, null);
        }
    }

    /**
     *  Receives the result of the asynchronous task to the API.
     *
     *  Connect to an endpoint which receives the city name and sends a response with the
     *  information for the current weather.
     *
     *  Second, connect to an endpoint which receives the city and state and sends a response with
     *  the forecast information for the next 10 days, but we'll take only 7 days.
     *  */
    private class WeatherAPIConnectionAsyncTaskOnResponseListener implements WeatherAPIConnectionAsyncTask.OnResponseListener {

        @Override
        public void onResponse(String endpointName, String status, String result) {

            if (status.equals(RESULT_ERROR) || status.equals(RESULT_TIMEOUT)) {

                onConnectionResultListener.onConnectionResult(status, null);

            } else if (status.equals(RESULT_OK)) {

                if (endpointName.equals(Constants.WEATHER_ENDPOINT_NAME)) { // First connection

                    processWeatherEndpointResult(result);

                    onConnectionResultListener.onConnectionResult(status, currentWeatherDataModel); // testing

                }
            }
        }
    }

    /**
     * Processing of the first connection endpoint response:
     * Parses the result of the 'weather' endpoint call to get both the city and the state
     */
    private void processWeatherEndpointResult(String result) {

        try {

            JSONObject resultJSONObject = new JSONObject(result);

            // The instance of the data model is created here, once we're sure that the web service
            // has responded successful. Before this, it is not necessary to instance the data model
            currentWeatherDataModel = new CurrentWeatherDataModel();

            // Take the city from JSON and set it in currentWeatherDataModel
            String cityString = resultJSONObject.getString("name");
            currentWeatherDataModel.setCityString(cityString);

            // Take the current temperature and set it in currentWeatherDataModel
            JSONObject mainJSONObject = resultJSONObject.getJSONObject("main");
            String currentTemperatureKelvinString = mainJSONObject.getString("temp");
            int currentTemperatureKelvin = (int) Double.parseDouble(currentTemperatureKelvinString);
            // Convert the current temperature from Kelvin to Celsius and set it in currentWeatherDataModel
            int currentTemperatureFahrenheit = convertKelvinToFahrenheit(currentTemperatureKelvin);
            String currentTemperatureFahrenheitString = Integer.toString(currentTemperatureFahrenheit);
            currentWeatherDataModel.setCurrentTemperatureFahrenheitString(currentTemperatureFahrenheitString);
            // Convert the current temperature from Kelvin to Fahrenheit and set it in currentWeatherDataModel
            int currentTemperatureCelsius = convertKelvinToCelsius(currentTemperatureKelvin);
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
            int minimumTemperatureFahrenheit = convertKelvinToFahrenheit(minimumTemperatureKelvin);
            String minimumTemperatureFahrenheitString = Integer.toString(minimumTemperatureFahrenheit);
            currentWeatherDataModel.setMinimumTemperatureFahrenheitString(minimumTemperatureFahrenheitString);
            // Convert the minimum temperature from Kelvin to Celsius and set it in currentWeatherDataModel
            int minimumTemperatureCelsius = convertKelvinToCelsius(minimumTemperatureKelvin);
            String minimumTemperatureCelsiusString = Integer.toString(minimumTemperatureCelsius);
            currentWeatherDataModel.setMinimumTemperatureCelsiusString(minimumTemperatureCelsiusString);

            // Take the maximum temperature from JSON response
            String maximumTemperatureKelvinString = mainJSONObject.getString("temp_max");
            int maximumTemperatureKelvin = (int) Double.parseDouble(maximumTemperatureKelvinString);
            // Convert the maximum temperature from Kelvin to Fahrenheit and set it in currentWeatherDataModel
            int maximumTemperatureFahrenheit = convertKelvinToFahrenheit(maximumTemperatureKelvin);
            String maximumTemperatureFahrenheitString = Integer.toString(maximumTemperatureFahrenheit);
            currentWeatherDataModel.setMaximumTemperatureFahrenheitString(maximumTemperatureFahrenheitString);
            // Convert the maximum temperature from Kelvin to Celsius and set it in currentWeatherDataModel
            int maximumTemperatureCelsius = convertKelvinToCelsius(maximumTemperatureKelvin);
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
    }
}

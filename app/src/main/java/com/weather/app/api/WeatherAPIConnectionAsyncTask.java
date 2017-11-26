package com.weather.app.api;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  Asynchronous task to connect to the API.
 *
 *  Connect to an endpoint which receives the city name and sends a response with the
 *  information for the current weather.
 *  */
public class WeatherAPIConnectionAsyncTask extends AsyncTask<String, Void, String> {

    // API endpoint to be connected
    private String endpointNameString;

    // HTTP constants
    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    // Network status
    static final String NETWORK_OK = "NETWORK_OK";
    static final String NETWORK_TIMEOUT = "NETWORK_TIMEOUT";
    static final String NETWORK_NOTFOUND = "NETWORK_NOTFOUND";

    /** Listener to let know when a response has been received */
    private OnConnectionResultListener onConnectionResultListener;
    public interface OnConnectionResultListener {
        void onConnectionResult(String endpointName, String status, String result);
    }
    void setOnConnectionResultListener(OnConnectionResultListener onConnectionResultListener) {
        this.onConnectionResultListener = onConnectionResultListener;
    }

    @Override
    protected String doInBackground(String... params) {

        this.endpointNameString = params[0];
        String resultURLString = params[1];

        String result = null;

        try {

            // Create a URL object from the URL param
            URL url = new URL(resultURLString);

            // Create a connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            // Set methods and timeouts
            httpURLConnection.setRequestMethod(REQUEST_METHOD);
            httpURLConnection.setReadTimeout(READ_TIMEOUT);
            httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);

            // Connect to URL
            httpURLConnection.connect();

            // Create an InputStreamReader
            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());

            // Create a BufferReader and StringBuilder
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            // Read lines
            String inputLineString;
            while ((inputLineString = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLineString);
            }

            // Close both InputStreamReader and BufferedReader
            bufferedReader.close();
            inputStreamReader.close();

            // Set the result
            result = stringBuilder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = NETWORK_NOTFOUND;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // If result is null, return TIMEOUT network status
        if (result == null) {
            this.onConnectionResultListener.onConnectionResult(endpointNameString, NETWORK_TIMEOUT, null);
            return;

        } else if (result.equals(NETWORK_NOTFOUND)) {
            this.onConnectionResultListener.onConnectionResult(endpointNameString, NETWORK_NOTFOUND, null);
            return;
        }

        // Otherwise return OK network status
        this.onConnectionResultListener.onConnectionResult(endpointNameString, NETWORK_OK, result);
    }
}

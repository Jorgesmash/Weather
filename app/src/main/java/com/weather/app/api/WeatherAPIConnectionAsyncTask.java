package com.weather.app.api;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    // Response status
    public static final String RESPONSE_OK = "RESPONSE_OK";
    public static final String RESPONSE_ERROR = "RESPONSE_ERROR";
    public static final String RESPONSE_TIMEOUT = "RESPONSE_TIMEOUT";
    public static final String RESPONSE_NOTFOUND = "RESPONSE_NOTFOUND";

    /** Listener to let know when a response has been received */
    private OnResponseListener onResponseListener;
    public interface OnResponseListener {
        void onResponse(String endpointName, String status, String result);
    }
    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
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
            result = RESPONSE_NOTFOUND;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // If result is null, return TIMEOUT error status
        if (result == null) {
            this.onResponseListener.onResponse(endpointNameString, RESPONSE_TIMEOUT, null);
            return;

        } else if (result.equals(RESPONSE_NOTFOUND)) {
            this.onResponseListener.onResponse(endpointNameString, RESPONSE_NOTFOUND, null);
            return;
        }

        // If result is not successful (200), return ERROR status
        try {
            JSONObject resultJSONObject = new JSONObject(result);
            String codeString = resultJSONObject.getString("cod");

            if (!codeString.equals("200")) {

                this.onResponseListener.onResponse(endpointNameString, RESPONSE_ERROR, result);

                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Otherwise return OK status
        this.onResponseListener.onResponse(endpointNameString, RESPONSE_OK, result);
    }
}

package com.weather.app.datamodels;

/**
 * Holds the user's visible information which comes to the weather API.
 * */
public class CurrentWeatherDataModel {

    private String cityString;

    private String currentTemperatureFahrenheitString;
    private String currentTemperatureCelsiusString;

    private String minimumTemperatureFahrenheitString;
    private String minimumTemperatureCelsiusString;

    private String maximumTemperatureFahrenheitString;
    private String maximumTemperatureCelsiusString;

    private String skyConditionsDescriptionString;
    private String skyConditionsIconString;

    private String humidityString;
    private String pressureString;
    private String windSpeedString;
    private String visibilityString;

    public String getCityString() {
        return cityString;
    }

    public void setCityString(String cityString) {
        this.cityString = cityString;
    }

    public String getCurrentTemperatureFahrenheitString() {
        return currentTemperatureFahrenheitString;
    }

    public void setCurrentTemperatureFahrenheitString(String temperatureFahrenheitString) {
        this.currentTemperatureFahrenheitString = temperatureFahrenheitString;
    }

    public String getCurrentTemperatureCelsiusString() {
        return currentTemperatureCelsiusString;
    }

    public void setCurrentTemperatureCelsiusString(String temperatureCelsiusString) {
        this.currentTemperatureCelsiusString = temperatureCelsiusString;
    }

    public String getMinimumTemperatureFahrenheitString() {
        return minimumTemperatureFahrenheitString;
    }

    public void setMinimumTemperatureFahrenheitString(String minimumTemperatureFahrenheitString) {
        this.minimumTemperatureFahrenheitString = minimumTemperatureFahrenheitString;
    }

    public String getMinimumTemperatureCelsiusString() {
        return minimumTemperatureCelsiusString;
    }

    public void setMinimumTemperatureCelsiusString(String minimumTemperatureCelsiusString) {
        this.minimumTemperatureCelsiusString = minimumTemperatureCelsiusString;
    }

    public String getMaximumTemperatureFahrenheitString() {
        return maximumTemperatureFahrenheitString;
    }

    public void setMaximumTemperatureFahrenheitString(String maximumTemperatureFahrenheitString) {
        this.maximumTemperatureFahrenheitString = maximumTemperatureFahrenheitString;
    }

    public String getMaximumTemperatureCelsiusString() {
        return maximumTemperatureCelsiusString;
    }

    public void setMaximumTemperatureCelsiusString(String maximumTemperatureCelsiusString) {
        this.maximumTemperatureCelsiusString = maximumTemperatureCelsiusString;
    }

    public String getSkyConditionsDescriptionString() {
        return skyConditionsDescriptionString;
    }

    public void setSkyConditionsDescriptionString(String skyConditionsDescriptionString) {
        this.skyConditionsDescriptionString = skyConditionsDescriptionString;
    }

    public String getSkyConditionsIconString() {
        return skyConditionsIconString;
    }

    public void setSkyConditionsIconString(String skyConditionsIconString) {
        this.skyConditionsIconString = "ic_" + skyConditionsIconString;
    }

    public String getHumidityString() {
        return humidityString;
    }

    public void setHumidityString(String humidityString) {
        this.humidityString = humidityString;
    }

    public String getPressureString() {
        return pressureString;
    }

    public void setPressureString(String pressureString) {
        this.pressureString = pressureString;
    }

    public String getWindSpeedString() {
        return windSpeedString;
    }

    public void setWindSpeedString(String windSpeedString) {
        this.windSpeedString = windSpeedString;
    }

    public String getVisibilityString() {
        return visibilityString;
    }

    public void setVisibilityString(String visibilityString) {
        this.visibilityString = visibilityString;
    }
}

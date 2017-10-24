package com.weather.app.util;

public class TemperatureUnitsConverter {

    private static TemperatureUnitsConverter temperatureUnitsConverter;

    public static void newInstance() {
        if (temperatureUnitsConverter == null) {
            temperatureUnitsConverter = new TemperatureUnitsConverter();
        }
    }

    /**
     * Converts temperature from Kelvin to Fahrenheit
     *
     * This API may have problems to convert temperatures, so this is done on client side.
     * Reference: https://openweathermap.desk.com/customer/portal/questions/16681817-forecase-returning-kelvin-despite-units-imperial-
     * */
    public static int convertKelvinToFahrenheit(double temperatureKelvin) {

        // Convert from Kelvin to Fahrenheit
        int temperatureFahrenheit = (int) (temperatureKelvin * (9f / 5f) - 459.67);

        return temperatureFahrenheit;
    }

    /**
     * Converts temperature from Kelvin to Celsius
     *
     * This API may have problems to convert temperatures, so this is done on client side.
     * Reference: https://openweathermap.desk.com/customer/portal/questions/16681817-forecase-returning-kelvin-despite-units-imperial-
     * */
    public static int convertKelvinToCelsius(double temperatureKelvin) {

        int temperatureCelsius = (int) (temperatureKelvin - 273.15f);

        return temperatureCelsius;
    }
}

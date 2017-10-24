package com.weather.app.api;

import com.weather.app.util.TemperatureUnitsConverter;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class WeatherAPIJUnitTest {

    @Before
    public void setUp() throws Exception {
        TemperatureUnitsConverter.newInstance();
    }

    /**
     * Test to convert any decimal number from Kelvin to Fahrenheit
     * */
    @Test
    public void convertKelvinToFahrenheitTest1() throws Exception {

        double temperatureKelvin = 277.3;
        double temperatureFahrenheit = TemperatureUnitsConverter.convertKelvinToFahrenheit(temperatureKelvin);

        Assert.assertEquals(39, (int) temperatureFahrenheit);
    }

    /**
     * Test to convert 0 Kelvin to Fahrenheit
     * */
    @Test
    public void convertKelvinToFahrenheitTest2() throws Exception {

        double temperatureKelvin = 0;
        double temperatureFahrenheit = TemperatureUnitsConverter.convertKelvinToFahrenheit(temperatureKelvin);

        Assert.assertEquals(-459, (int) temperatureFahrenheit);
    }

    /**
     * Test to convert any temperature below zero from Kelvin to Fahrenheit
     * */
    @Test
    public void convertKelvinToFahrenheitTest3() throws Exception {

        double temperatureKelvin = -16;
        double temperatureFahrenheit = TemperatureUnitsConverter.convertKelvinToFahrenheit(temperatureKelvin);

        Assert.assertEquals(-488, (int) temperatureFahrenheit);
    }


    /**
     * Test to convert any decimal temperature from Kelvin to Celsius
     * */
    @Test
    public void convertKelvinToCelsiusTest4() throws Exception {

        double temperatureKelvin = 288.54;
        double temperatureFahrenheit = TemperatureUnitsConverter.convertKelvinToCelsius(temperatureKelvin);

        Assert.assertEquals(15, (int) temperatureFahrenheit);
    }

    /**
     * Test to convert any temperature below zero from Kelvin to Celsius
     * */
    @Test
    public void convertKelvinToCelsiusTest5() throws Exception {

        double temperatureKelvin = 250.85;
        double temperatureFahrenheit = TemperatureUnitsConverter.convertKelvinToCelsius(temperatureKelvin);

        Assert.assertEquals(-22, (int) temperatureFahrenheit);
    }
}

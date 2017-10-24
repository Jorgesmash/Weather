package com.weather.app;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import com.weather.R;
import com.weather.app.settings.SettingsPreferencesManager;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;

/**
 * Performs an instrumented test of the MainFragmentActivity flow to store
 * a city name in the Application SharedPreferences and then performs an API call.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainFragmentActivityEspressoTest {

    @Rule
    public ActivityTestRule<MainFragmentActivity> activityTestRule = new ActivityTestRule<>(MainFragmentActivity.class);

    @Test
    /**
     * Test with only an US city
     * */
    public void exampleInstrumentedTest1() throws Exception {

        // Make the first city name insertion
        String cityString = "Westerville";

        performInstrumentedTest(cityString);
    }

    @Test
    /**
     * Test with an US city of two words
     * */
    public void exampleInstrumentedTest2() throws Exception {

        // Make the first city name insertion
        String cityString = "San Francisco";

        performInstrumentedTest(cityString);
    }

    @Test
    /**
     * Test with an non-US city
     * */
    public void exampleInstrumentedTest3() throws Exception {

        // Make the first city name insertion
        String cityString = "Bangladesh";

        performInstrumentedTest(cityString);
    }

    @Test
    /**
     * Test with an blank city name
     * */
    public void exampleInstrumentedTest4() throws Exception {

        // Make the first city name insertion
        String cityString = "";

        performInstrumentedTest(cityString);
    }

    /**
     * Performs the instrumented test with a given city name
     * */
    private void performInstrumentedTest(String cityString) {

        String cityName = SettingsPreferencesManager.getCityName();

        // If the city name will be set for the first time, a dialog is shown asking user for inserting a city
        if (cityName.equals("")) {

            // Dismiss the dialog which asks for inserting a city
            onView(withText("OK")).perform(click());
            onView(withText("OK")).check(doesNotExist());

            // Click on cityNamePreference
            onData(withKey("cityNamePreferenceKey")).perform(click());
            onView(withClassName(equalTo(EditText.class.getName()))).perform(typeText(cityString));
            onView(withText("OK")).perform(click());
            onView(withText("OK")).check(doesNotExist());

            onView(isRoot()).perform(pressBack());

        } else { // Else, If the city name will be set for subsequent times, the user has to click the settings button

            onView(withId(R.id.settingsButton)).perform(click());

            // Click on cityNamePreference
            onData(withKey("cityNamePreferenceKey")).perform(click());
            onView(withClassName(equalTo(EditText.class.getName()))).perform(clearText());
            onView(withClassName(equalTo(EditText.class.getName()))).perform(typeText(cityString), closeSoftKeyboard());
            onView(withText("OK")).perform(click());
            onView(withText("OK")).check(doesNotExist());

            onView(isRoot()).perform(pressBack());
        }
    }
}

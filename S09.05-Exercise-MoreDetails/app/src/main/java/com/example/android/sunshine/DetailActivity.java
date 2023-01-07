/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.COLUMN_DATE;
import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.COLUMN_DEGREES;
import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.COLUMN_HUMIDITY;
import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.COLUMN_MAX_TEMP;
import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.COLUMN_MIN_TEMP;
import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.COLUMN_PRESSURE;
import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.COLUMN_WEATHER_ID;
import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.COLUMN_WIND_SPEED;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
//       (21) Implement LoaderManager.LoaderCallbacks<Cursor>

    /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private static final String[] colNames ={
    /* Weather ID as returned by API, used to identify the icon to be used */
            COLUMN_DATE,
            COLUMN_WEATHER_ID,
            COLUMN_MIN_TEMP,
            COLUMN_MAX_TEMP,
            COLUMN_HUMIDITY,
            COLUMN_PRESSURE,
            COLUMN_WIND_SPEED,
            COLUMN_DEGREES
    };
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_CONDITION_ID = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_MAX_TEMP = 3;
    public static final int INDEX_HUMIDITY =4;
    public static final int INDEX_PRESSURE = 5;
    public static final int INDEX_WIND_SPEED = 6;
    public static final int INDEX_DEGREES = 7;






//   (18) Create a String array containing the names of the desired data columns from our ContentProvider
//   (19) Create constant int values representing each column name's position above
//   (20) Create a constant int to identify our loader used in DetailActivity
    private int FORECAST_LOADER_ID;
    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

//   (15) Declare a private Uri field called mUri
    private Uri mUri;
//   (10) Remove the mWeatherDisplay TextView declaration
    private TextView mWeatherDisplay;

//   (11) Declare TextViews for the date, description, high, low, humidity, wind, and pressure
    private TextView mDateView;
    private TextView mWeatherDescriptionView;
    private TextView mHighTemperatureView;
    private TextView mLowTemperatureView;
    private TextView mhumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//       (12) Remove mWeatherDisplay TextView

//       (13) Find each of the TextViews by ID
        mDateView = (TextView) findViewById(R.id.date);
        mWeatherDescriptionView = (TextView) findViewById(R.id.weather_description);
        mHighTemperatureView = (TextView) findViewById(R.id.high_temperature);
        mLowTemperatureView = (TextView) findViewById(R.id.low_temperature);
        mhumidityView = (TextView) findViewById(R.id.humidity);
        mWindView = (TextView) findViewById(R.id.wind);
        mPressureView = (TextView) findViewById(R.id.pressure);

//       (14) Remove the code that checks for extra text
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
           // if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
           //     mForecastSummary = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
           //     mWeatherDisplay.setText(mForecastSummary);
           // }
            Uri uri = intentThatStartedThisActivity.getData();
            if (uri == null){
                throw new NullPointerException();
            }
            else{
                mUri=uri;
            }
        }
//       (16) Use getData to get a reference to the URI passed with this Activity's Intent
//       (17) Throw a NullPointerException if that URI is null
//       (35) Initialize the loader for DetailActivity
        LoaderManager loaderManager=getSupportLoaderManager();
        loaderManager.initLoader(FORECAST_LOADER_ID,null,this);

    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }
//   (22) Override onCreateLoader
//           (23) If the loader requested is our detail loader, return the appropriate CursorLoader

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i==FORECAST_LOADER_ID){
            CursorLoader cursorLoader = new CursorLoader(this,
                    mUri,
                    colNames,
                    null,
                    null,
                    null);
            return cursorLoader;

        }
        else{
            throw new RuntimeException("Loader Not Implemented: " + i);
        }
    }
    //   (24) Override onLoadFinished
//       (25) Check before doing anything that the Cursor has valid data
//       (26) Display a readable data string
//       (27) Display the weather description (using SunshineWeatherUtils)
//       (28) Display the high temperature
//       (29) Display the low temperature
//       (30) Display the humidity
//       (31) Display the wind speed and direction
//       (32) Display the pressure
//       (33) Store a forecast summary in mForecastSummary
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        boolean cursorDateIsValid =false;
        if(cursor !=null && cursor.moveToFirst()) {
            cursorDateIsValid = true;
        }
        if (cursorDateIsValid){
            long localDateMidnightGmt = cursor.getLong(INDEX_WEATHER_DATE);
            String dateText = SunshineDateUtils.getFriendlyDateString(this,localDateMidnightGmt, true);
            mDateView.setText(dateText);

            int weatherDescriptionId = cursor.getInt(INDEX_WEATHER_CONDITION_ID);
            String weatherDescription = SunshineWeatherUtils.getStringForWeatherCondition(this,weatherDescriptionId);
            mWeatherDescriptionView.setText(weatherDescription);

            double highInCelsius = cursor.getDouble(INDEX_WEATHER_MAX_TEMP);
            String highString = SunshineWeatherUtils.formatTemperature(this, highInCelsius);
            mHighTemperatureView.setText(highString);

            double lowInCelsius = cursor.getDouble(INDEX_WEATHER_MIN_TEMP);
            String lowString = SunshineWeatherUtils.formatTemperature(this, lowInCelsius);
            mLowTemperatureView.setText(lowString);

            float humidity = cursor.getFloat(INDEX_HUMIDITY);
            String humidityString = getString(R.string.format_humidity,humidity);
            mhumidityView.setText(humidityString);

            /* Read wind speed (in MPH) and direction (in compass degrees) from the cursor  */
            float windSpeed = cursor.getFloat(INDEX_WIND_SPEED);
            float windDirection = cursor.getFloat(INDEX_DEGREES);
            String windString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection);

            /* Set the text */
            mWindView.setText(windString);

            float pressure = cursor.getFloat(INDEX_PRESSURE);
            String pressureString = getString(R.string.format_pressure,pressure);
            mPressureView.setText(pressureString);

            mForecastSummary = String.format("%s - %s - %s/%s",
                    dateText, weatherDescription, highString, lowString);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }





//   (34) Override onLoaderReset, but don't do anything in it yet

}
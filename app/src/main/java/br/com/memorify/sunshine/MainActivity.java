package br.com.memorify.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import br.com.memorify.sunshine.data.WeatherContract;

public class MainActivity extends AppCompatActivity {

    private ForecastAdapter forecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String locationSetting = Utility.getPreferredLocation(getBaseContext());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cur = getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        forecastAdapter = new ForecastAdapter(getBaseContext(), cur, 0);

        ListView forecastListView = (ListView) findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(forecastAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchWeather();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_action:
                fetchWeather();
                return true;
            case R.id.location_on_map_action:
                showLocationOnMap();
                return true;
            case R.id.settings_action:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask(getBaseContext());

        String location = Utility.getPreferredLocation(getBaseContext());
        weatherTask.execute(location);
    }

    private void showLocationOnMap() {
        final String PREF_LOCATION_KEY = getString(R.string.pref_location_key);
        final String PREF_LOCATION_DEFAULT = getString(R.string.pref_location_default_value);
        final String LOCATION_QUERY = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext()).getString(PREF_LOCATION_KEY, PREF_LOCATION_DEFAULT);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri geoLocation = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", LOCATION_QUERY).build();
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "There is no app to show location on map", Toast.LENGTH_SHORT).show();
        }
    }
}

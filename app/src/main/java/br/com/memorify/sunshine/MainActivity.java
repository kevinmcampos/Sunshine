package br.com.memorify.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> weekForecast;
    private ArrayAdapter<String> forecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forecastAdapter = new ArrayAdapter<>(
                getBaseContext(), // The current context (this activity)
                R.layout.list_item_forecast, // The name of the layout ID.
                R.id.list_item_forecast_textview, // The ID of the textview to populate.
                new ArrayList<String>());
        forecastAdapter.setNotifyOnChange(false);

        ListView forecastListView = (ListView) findViewById(R.id.listview_forecast);
        forecastListView.setAdapter(forecastAdapter);
        forecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, forecastAdapter.getItem(position));
                MainActivity.this.startActivity(intent);
            }
        });
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
            case R.id.settings_action:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchWeather() {
        final String PREF_LOCATION_KEY = getString(R.string.pref_location_key);
        final String PREF_LOCATION_DEFAULT = getString(R.string.pref_location_default_value);
        final String LOCATION_QUERY = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext()).getString(PREF_LOCATION_KEY, PREF_LOCATION_DEFAULT);

        new FetchWeatherTask(getBaseContext(), LOCATION_QUERY, new FetchWeatherTask.FetchWeatherCallbacks() {
            @Override
            public void onSuccess(String[] weatherForecasts) {
                weekForecast = Arrays.asList(weatherForecasts);

                forecastAdapter.clear();
                for (String weekString : weekForecast) {
                    forecastAdapter.add(weekString);
                }
                forecastAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed() {
                Toast.makeText(getBaseContext(), "Failed to fetch weather data.", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
}

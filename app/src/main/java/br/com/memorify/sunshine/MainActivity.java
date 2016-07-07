package br.com.memorify.sunshine;

import android.content.Intent;
import android.os.Bundle;
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

        String[] dummyData = { "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7" };
        weekForecast = new ArrayList<>(Arrays.asList(dummyData));

        forecastAdapter = new ArrayAdapter<>(
                getBaseContext(), // The current context (this activity)
                R.layout.list_item_forecast, // The name of the layout ID.
                R.id.list_item_forecast_textview, // The ID of the textview to populate.
                weekForecast);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchWeather() {
        final String MOUNTAIN_VIEW_LOCATION_QUERY = "94043";
        new FetchWeatherTask(MOUNTAIN_VIEW_LOCATION_QUERY, new FetchWeatherTask.FetchWeatherCallbacks() {
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

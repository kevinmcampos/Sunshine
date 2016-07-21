package br.com.memorify.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.memorify.sunshine.data.WeatherContract.WeatherEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int FORECAST_DETAIL_LOADER_ID = 0;

    private String forecast;
    private ShareActionProvider shareActionProvider;

    private TextView detailTextView;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportLoaderManager().initLoader(FORECAST_DETAIL_LOADER_ID, null, this);

        String forecast = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        detailTextView = (TextView) findViewById(R.id.detail_text);
        detailTextView.setText(forecast);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        MenuItem item =  menu.findItem(R.id.share_action);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (forecast != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
        return true;
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + " #SunshineApp");
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (getIntent() == null) {
            throw new IllegalArgumentException();
        }
        return new CursorLoader(getBaseContext(),
                getIntent().getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { throw new IllegalArgumentException(); }

        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));

        String weatherDescription = data.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getBaseContext());

        String high = Utility.formatTemperature(
                data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);

        String low = Utility.formatTemperature(
                data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        forecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
        detailTextView.setText(forecast);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // do nothing
    }
}

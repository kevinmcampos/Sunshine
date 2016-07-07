package br.com.memorify.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String forecast = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        TextView detailTextView = (TextView) findViewById(R.id.detail_text);
        detailTextView.setText(forecast);
    }
}

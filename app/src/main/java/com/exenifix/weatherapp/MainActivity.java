package com.exenifix.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText inputField;
    private TextView dataField;
    private ProgressBar progressBar;
    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressBar.setVisibility(View.INVISIBLE);
            dataField.setText(intent.getStringExtra("DATA"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = findViewById(R.id.inputField);
        dataField = findViewById(R.id.dataField);
        progressBar = findViewById(R.id.searchIndicator);

        ((Button) findViewById(R.id.inputButton)).setOnClickListener((View v) -> {
            progressBar.setVisibility(View.VISIBLE);
            dataField.setText("");
            String text = inputField.getText().toString();
            if (text.equals("")) text = "Москва";
            Intent i = new Intent(this, WeatherService.class);
            i.putExtra("CITY", text);
            startService(i);
        });

        registerReceiver(receiver, new IntentFilter(WeatherService.CHANNEL));
    }
}

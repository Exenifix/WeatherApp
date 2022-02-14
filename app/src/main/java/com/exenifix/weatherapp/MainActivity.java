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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText inputField;
    private TextView dataField;
    private ProgressBar progressBar;
    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("DATA");
            progressBar.setVisibility(View.INVISIBLE);
            if (data == null) {
                Toast.makeText(getApplicationContext(), "Такого города не существует!", Toast.LENGTH_SHORT).show();
                return;
            }
            dataField.setText(data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = findViewById(R.id.inputField);
        dataField = findViewById(R.id.dataField);
        progressBar = findViewById(R.id.searchIndicator);

        findViewById(R.id.inputButton).setOnClickListener((View v) -> {
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

package com.exenifix.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText inputField;
    private TextView dataField;
    private EditText serverInputField;
    private TextView currentServerText;
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
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEditor;
    private String serverURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = findViewById(R.id.inputField);
        dataField = findViewById(R.id.dataField);
        progressBar = findViewById(R.id.searchIndicator);
        serverInputField = findViewById(R.id.server_field);
        currentServerText = findViewById(R.id.server_text);

        sharedPrefs = getPreferences(MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();
        serverURL = sharedPrefs.getString("baseURL", getString(R.string.default_server));
        if (!serverURL.endsWith("/")) {
            serverURL += "/";
        }
        currentServerText.setText(getString(R.string.current_server, serverURL));

        findViewById(R.id.inputButton).setOnClickListener(this::onSearchButtonClick);
        findViewById(R.id.update_server_button).setOnClickListener(this::onSetServerButtonClick);

        registerReceiver(receiver, new IntentFilter(WeatherService.CHANNEL));
    }

    private void onSearchButtonClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        dataField.setText("");
        String text = inputField.getText().toString();
        if (text.equals("")) text = "Москва";
        Intent i = new Intent(this, WeatherService.class);
        i.putExtra("CITY", text);
        i.putExtra("URL", serverURL);
        startService(i);
    }

    private void onSetServerButtonClick(View v) {
        String newUrl = serverInputField.getText().toString();
        if (newUrl.length() == 0) {
            Toast.makeText(this, "Server field must not be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newUrl.endsWith("/")) {
            newUrl += "/";
        }
        serverURL = newUrl;
        currentServerText.setText(getString(R.string.current_server, newUrl));
        sharedPrefsEditor.putString("baseURL", newUrl);
        sharedPrefsEditor.commit();
        Toast.makeText(this, "Updated the server successfully!", Toast.LENGTH_SHORT).show();
    }
}

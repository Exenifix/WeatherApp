package com.exenifix.weatherapp;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class WeatherService extends Service {
    public static final String CHANNEL = "WEATHER_SERVICE";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WeatherGetter weatherGetter = new WeatherGetter();
        weatherGetter.execute(intent.getStringExtra("CITY"));

        return START_NOT_STICKY;
    }

    private class WeatherGetter extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            Intent i = new Intent(CHANNEL);
            i.putExtra("DATA", s);
            sendBroadcast(i);
        }

        @Override
        protected String doInBackground(String... cities) {
            String city = cities[0];
            String result;
            //WARNING: this API may not be available. You can find the API script on https://github.com/Exenifix/weather-getter-api
            String url = "http://128.199.6.166:1234/weather?city=" + city;
            JSONObject json;
            try {
                json = readFromURL(url);
            } catch (APIException e) {
                return null;
            }

            try {
                JSONObject weatherMain = json.getJSONObject("main");
                JSONObject wind = json.getJSONObject("wind");

                result = String.format(Locale.getDefault(), "Погода: %s\nТемпература: %d°C\nДавление: %d\nВлажность: %d%%\nСкорость ветра: %.2f м/с\nНаправление ветра: %d°",
                        json.getJSONArray("weather").getJSONObject(0).getString("description"),
                        weatherMain.getInt("temp"),
                        weatherMain.getInt("pressure"),
                        weatherMain.getInt("humidity"),
                        wind.getDouble("speed"),
                        wind.getInt("deg"));
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        private JSONObject readFromURL(String urlString) throws APIException {
            InputStream input;
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if (connection.getResponseCode() != 200) {
                    throw new APIException();
                }
                input = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();

                int cp;
                while ((cp = reader.read()) != -1) {
                    sb.append((char) cp);
                }
                input.close();
                return new JSONObject(sb.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherScreen extends AppCompatActivity {

    TextView windWarning;
    TextView temperatureText;
    TextView weatherInfo;


    Button refreshButton;
    Button toNotifsButton;

    double lat;
    double lon;

    double[] windValues = new double[40];

    String apiResponse = "";
    String warningText;

    LocationManager locationManager;
    LocationProvider provider;

    private final String FORECAST_API_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private final String CURRENT_API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final String API_KEY = "7e56f6b8a976d06a222de9729c608e86";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        windWarning = findViewById(R.id.windWarningText);
        temperatureText = findViewById(R.id.temperatureText);
        refreshButton = findViewById(R.id.refreshWeatherData);
        toNotifsButton = findViewById(R.id.toNotifsButton);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);

        toNotifsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MainActivity.class));
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRefreshButton(v);
            }
        });
    }

    protected void onStart() {
        super.onStart();

        //Checks that location services are enabled and the GPS is responsive.
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (!enabled) {
            Toast.makeText(this, "Location services are disabled", Toast.LENGTH_SHORT).show();
            enableLocationPrompt();
        }

        //Request new location data. (if it has not been requested in the last minute)

    }

    private void enableLocationPrompt() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    @SuppressLint("MissingPermission")
    public void onClickRefreshButton(View view) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permissions not granted, please grant in Settings app", Toast.LENGTH_SHORT).show();
            return;
        }

        //suppress warnings for missing permissions, this is checked in onStart()

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Toast.makeText(getApplicationContext(), "Location updated", Toast.LENGTH_SHORT).show();
            }
        });

        if (location == null) {
            lat = 0;
            lon = 0;
        } else {
            lat = location.getLatitude();
            lon = location.getLongitude();
        }


        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        String forecastRequestUrl = FORECAST_API_URL + "?lat=" + String.valueOf(lat) + "&lon=" + String.valueOf(lon) + "&appid=" + API_KEY;

        StringRequest forecastStringRequest = new StringRequest(Request.Method.POST, forecastRequestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                apiResponse = s;
                Toast.makeText(getApplicationContext(), "Data fetched", Toast.LENGTH_LONG).show();

                try {

                    JSONObject currentItem;
                    JSONObject currentWind;
                    JSONObject jsonResponse = new JSONObject(apiResponse);
                    JSONArray jsonArray = jsonResponse.getJSONArray("list");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        currentItem = jsonArray.getJSONObject(i);
                        currentWind = currentItem.getJSONObject("wind");
                        windValues[i] = currentWind.getDouble("speed");
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 0; i < windValues.length; i++) {
                    if (windValues[i] > 25) {
                        warningText = "Heavy winds in " + String.valueOf(i*3) + " hours, of speed " + String.valueOf(windValues[i]) + "mph";
                        windWarning.setText(warningText);
                        break;
                    } else {
                        warningText = "No wind warnings in next 5 days";
                        windWarning.setText(warningText);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                windWarning.setText(error.toString());
            }
        });

        rq.add(forecastStringRequest);

        //////

        String weatherRequestUrl = CURRENT_API_URL + "?lat=" + String.valueOf(lat) + "&lon=" + String.valueOf(lon) + "&appid=" + API_KEY;

        StringRequest weatherStringRequest = new StringRequest(Request.Method.POST, weatherRequestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                apiResponse = s;
                try {
                    JSONObject currentItem;
                    JSONObject currentTemp;
                    String weatherDescription;

                    JSONObject jsonResponse = new JSONObject(apiResponse);

                    JSONArray mainWeather = jsonResponse.getJSONArray("weather");
                    currentTemp = jsonResponse.getJSONObject("main");

                    currentItem = mainWeather.getJSONObject(0);
                    weatherDescription = currentItem.getString("main");

                    double temp = currentTemp.getDouble("temp") - 273.15;

                    temperatureText = findViewById(R.id.temperatureText);

                    String newTempText = "Currently " + String.valueOf(Math.round(temp)) + "°C";
                    temperatureText.setText(newTempText);

                    weatherInfo = findViewById(R.id.weatherDesc);
                    weatherInfo.setText(weatherDescription);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                windWarning.setText(error.toString());

            }
        });

        rq.add(weatherStringRequest);

    }
}
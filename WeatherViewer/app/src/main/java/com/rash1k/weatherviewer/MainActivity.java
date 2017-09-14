package com.rash1k.weatherviewer;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.rash1k.weatherviewer.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "MainActivity";
    private List<Weather> mWeatherList = new ArrayList<>();
    private WeatherArrayAdapter mWeatherArrayAdapter;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Связываем список WeatherList с WeatherArrayAdapter и связываем адаптер с WeatherRecyclerView
        RecyclerView weatherRecyclerView = (RecyclerView) findViewById(R.id.weatherRecyclerView);
        mWeatherArrayAdapter = new WeatherArrayAdapter(this, mWeatherList);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        weatherRecyclerView.addItemDecoration(new ItemDivider(this));
        weatherRecyclerView.setAdapter(mWeatherArrayAdapter);

//      Кнопка FAB делает запрос к веб сервису и скрывает клавиатуру
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Получаем текст из EditText, создаем URL для веб сервиса
                TextInputLayout locationEditText = (TextInputLayout) findViewById(R.id.locationTextInputLayout);

                String query = locationEditText.getEditText().getText().toString();

                if (query.length() > 0) {

                    URL url = createURL(query);

//                Скрываем клавиатуру и запускаем поток для получения данных с веб-сервиса
                    if (url != null) {
                        dismissKeyboard(locationEditText);
                        GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                        getLocalWeatherTask.execute(url);
                    } else {
                        Snackbar.make(findViewById(R.id.coordinatorLayout),
                                R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    locationEditText.setError(getString(R.string.invalid_query));
                }
            }
        });
    }

    private void dismissKeyboard(View locationEditText) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(locationEditText.getWindowToken(), 0);
    }

    @Nullable
    private URL createURL(String city) {

        String apiKey = getString(R.string.api_key);
        String baseURL = getString(R.string.web_service_url);

//        Создание URL запроса для заданного города и температурной шкалы (Цельсия)

        try {
            String urlString = baseURL + URLEncoder.encode(city, "UTF-8") +
                    "&units=metric&cnt=16&APPID=" + apiKey;
            return new URL(urlString);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... urls) {

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
//                connection.setRequestMethod("GET");
//                connection.connect();

                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {
                        String line;

                        while ((line = reader.readLine()) != null) {
                            builder.append(line).append("\n");
                        }
                    } catch (IOException e) {
                        Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.read_error,
                                Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString());

                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.connect_error,
                            Snackbar.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.read_error,
                        Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                convertJSONtoArrayList(jsonObject);
                mWeatherArrayAdapter.notifyDataSetChanged();
            }
        }

        private void convertJSONtoArrayList(JSONObject forecast) {

            mWeatherList.clear();

            try {
                JSONArray list = forecast.getJSONArray("list");

                for (int i = 0; i < list.length(); ++i) {

                    JSONObject day = list.getJSONObject(i);

                    JSONObject temperatures = day.getJSONObject("temp");
                    JSONObject weather = day.getJSONArray("weather").getJSONObject(0);

                    mWeatherList.add(new Weather(day.getLong("dt"), temperatures.getDouble("min"),
                            temperatures.getDouble("max"), day.getDouble("humidity"),
                            weather.getString("description"), weather.getString("icon")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

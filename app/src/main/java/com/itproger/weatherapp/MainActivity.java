package com.itproger.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_button;
    private TextView result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_button = findViewById(R.id.main_button);
        result_info = findViewById(R.id.result_info);

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_field.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                } else {
                    String city = user_field.getText().toString();
                    String key = "09cc25e36d4dab57430f56170d80b167";
                    String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    new getUrlData().execute(url);
                }
            }
        });
    }

    private class getUrlData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String resultString = "";
            try {
                JSONObject jsonObject = new JSONObject(result);

                resultString += getMainWeatherInfo(jsonObject);
                resultString += getWindDirection(jsonObject);
                result_info.setText(resultString);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected String getMainWeatherInfo(JSONObject jsonObject){
            String result = "";
            try {
                result = "Температура: " + jsonObject.getJSONObject("main").getDouble("temp") + "\n" +
                        "Влажность: " + jsonObject.getJSONObject("main").getDouble("humidity") + "%\n";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        //windDirection
        protected String getWindDirection(JSONObject jsonObject){
            String result = "";
            try {

                int wind = jsonObject.getJSONObject("wind").getInt("deg");
                String windDirection = "";
                switch (wind) {
                    case 0:
                        windDirection = "С";
                        break;
                    case 90:
                        windDirection = "В";
                        break;
                    case 180:
                        windDirection = "Ю";
                        break;
                    case 270:
                        windDirection = "З";
                        break;
                    default:
                        windDirection = "";
                        break;
                }
                if (wind > 0 & wind < 90) windDirection = "СВ";
                if (wind > 90 & wind < 180) windDirection = "ЮВ";
                if (wind > 180 & wind < 270) windDirection = "ЮЗ";
                if (wind > 270 & wind < 359) windDirection = "СЗ";

                result = "Ветер: " + jsonObject.getJSONObject("wind").getDouble("speed") + "м/с " + "\n" +
                        wind + " " + windDirection;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

}
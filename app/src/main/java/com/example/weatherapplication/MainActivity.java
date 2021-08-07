package com.example.weatherapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView outText;
    private Button buttonSend;
    private EditText searchSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outText = findViewById(R.id.text_out);
        searchSite= findViewById(R.id.text_input);
        buttonSend = findViewById(R.id.btn_weather);
    }

    public void onClickButton(View view) {
        String sity = searchSite.getText().toString().trim();
        if (sity.equals("")) {
            Toast.makeText(this, R.string.text_no_input, Toast.LENGTH_SHORT).show();
        } else {
            String apiKey = "467ab13a9bef0b4d1621596f722778bf";
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + sity +
                         "&appid=" + apiKey + "&units=metric&lang=ru";

            new GetURLData().execute(url);
        }
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            outText.setText("Загрузка погоды...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer stringBuffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line).append("\n");
                }

                return stringBuffer.toString();

            } catch (IOException e) {

                e.printStackTrace();
            } finally {

                if (connection != null){
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

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            double temp = -300000.0;
            String description = "";

            try {
                JSONObject json = new JSONObject(result);
                temp = json.getJSONObject("main").getDouble("temp");
                description = json.getJSONArray("weather").getJSONObject(0).getString("description");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            outText.setText(description.substring(0, 1).toUpperCase() + description.substring(1) +
                            "\nТемпература: " + temp);
        }
    }
}
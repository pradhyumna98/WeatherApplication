package com.example.pradhyumna.weatherapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
EditText weatherLocation;
TextView resultWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherLocation = findViewById(R.id.editText);
        resultWeather = findViewById(R.id.weatherPredict);
    }


    public void getWeather(View view){
            DownloadTask jsonProcess = new DownloadTask();

        String encodedCorrect = null;
        try {
            encodedCorrect = URLEncoder.encode(weatherLocation.getText().toString() , "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        jsonProcess.execute("https://openweathermap.org/data/2.5/weather?q="+ encodedCorrect +"&appid=b6907d289e10d714a6e88b30761fae22");

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(weatherLocation.getWindowToken() , 0);
    }

    public class DownloadTask extends AsyncTask<String , Void , String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader readerInput = new InputStreamReader(in);
                int data = readerInput.read();

                while (data != -1){
                    char current= (char) data;
                    result+=current;
                    data = readerInput.read();
                }
                return  result;

            }catch (Exception e){
                e.printStackTrace();

                Toast.makeText(getApplicationContext() , "Weather Not Found" , Toast.LENGTH_SHORT).show();
                return  null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject= new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");

                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";
                for(int i=0;i<arr.length();i++){

                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if(!main.equals("") && !description.equals("")){
                        message+= main + ":" + description + "\r\n";
                    }
                }

                if(!message.equals("")){
                    resultWeather.setText(message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

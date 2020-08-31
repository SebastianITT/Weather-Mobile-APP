package com.example.weatherappazure;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView max_temprature,min_temprature,wind_speed,wind_direction,weather_condition,next_day_weather;
    FloatingActionButton search_floating;
    Button button;
    public static String BASE_URL = "https://webapinew.azurewebsites.net/weatherforecast/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));





        editText = findViewById(R.id.search_edit);
        max_temprature = findViewById(R.id.max_temp);
        min_temprature = findViewById(R.id.min_temp);
        wind_speed = findViewById(R.id.wind_speed);
        wind_direction = findViewById(R.id.wind_direction);
        weather_condition = findViewById(R.id.weather_condition);
        next_day_weather = findViewById(R.id.next_day_weather);
        search_floating=findViewById(R.id.floating_search);
        button = findViewById(R.id.change);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePopUp();
            }
        });

     search_floating.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             getCity(editText.getText().toString().trim());
         }
     });

    }
    ////*****API CALL METHOD****/////
    private void getCity(String cityName) {
        String SEARCH_URL = BASE_URL + "getweatherbycity/" + cityName;
        Log.e("@SEARCH_URL @", SEARCH_URL);
        StringRequest newsSubCategory = new StringRequest(Request.Method.GET, SEARCH_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("@@DATA@@", response.toString());
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject subCategory = jsonArray.getJSONObject(i);
                            String lat = subCategory.getString("lat");
                            String donor_phone = subCategory.getString("lon");

                            String max_temp = subCategory.getString("maxtemp");
                            String min_temp = subCategory.getString("mintemp");
                            String w_speed = subCategory.getString("windspeed");
                            String w_direction = subCategory.getString("winddirection");
                            String weather = subCategory.getString("weather");
                            String n_d_weather = subCategory.getString("nextdayweather");


                            //**Set Value in TextView**///


                            max_temprature.setText(""+max_temp);
                            min_temprature.setText(""+min_temp);
                            wind_speed.setText(""+w_speed);
                            wind_direction.setText(""+w_direction);
                            weather_condition.setText(""+weather);
                            next_day_weather.setText(""+n_d_weather);


                            ////****Isi traa jo baqi values ha wo b get kr li***////
                        }


                    } else {
                        Toast.makeText(MainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
                error.printStackTrace();

                    Toast.makeText(MainActivity.this, getResources().getString(R.string.str_connection), Toast.LENGTH_SHORT).show();

            }
        });
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(newsSubCategory);
    }

    private void showChangePopUp()
    {
        final String [] list = {"English","French"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Choose Language");
        builder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              if(which == 0)
              {
                  setLocal("eng");
                  recreate();
              }
              else if(which == 1){
                  setLocal("fr");
                  recreate();
              }

            dialog.dismiss();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void setLocal(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My Lang",lang);
        editor.apply();


    }

 public void loadLocale()
 {
     SharedPreferences sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
     String language = sharedPreferences.getString("My Lang","");
     setLocal(language);
 }
}
package com.quadrivium.devs.bonvoyage;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CityActivity extends AppCompatActivity implements View.OnClickListener{

    private GeoDataClient mGeoDataClient;
    TextView detailsField, currentTemperatureField, humidity_field, weatherIcon;
    Typeface weatherFont;
    String id = "-1", name = "Place not found";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        mGeoDataClient = Places.getGeoDataClient(this, null);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            id = b.getString("id");
            name = b.getString("name");
        }
        TextView textView = findViewById(R.id.place_name);
        textView.setText(name);
        getPhotos(id);

        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        detailsField = findViewById(R.id.details_field);
        currentTemperatureField = findViewById(R.id.current_temperature_field);
        humidity_field = findViewById(R.id.humidity_field);
        weatherIcon = findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        Weather.placeIdTask asyncTask =new Weather.placeIdTask(new Weather.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_sunrise,String weather_sunset, String weather_updatedOn, String weather_iconText, String sun_rise) {

                detailsField.setText("Conditions: "+weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidity: "+weather_humidity);
                weatherIcon.setText(Html.fromHtml(weather_iconText));

            }
        });
        asyncTask.execute(name);

        ImageButton btnHotel =findViewById(R.id.showHotels);
        btnHotel.setOnClickListener(this);

        ImageButton btnRestaurant =findViewById(R.id.showRestaurants);
        btnRestaurant.setOnClickListener(this);

        ImageButton btnCafe =findViewById(R.id.showCafes);
        btnCafe.setOnClickListener(this);

        ImageButton btnPlacesOfInterest =findViewById(R.id.showPlacesOfInterest);
        btnPlacesOfInterest.setOnClickListener(this);

        ImageButton btnMarket =findViewById(R.id.showMarkets);
        btnMarket.setOnClickListener(this);

        ImageButton btnMalls =findViewById(R.id.showMalls);
        btnMalls.setOnClickListener(this);

        FloatingActionButton favoriteBtn = findViewById(R.id.favoriteBtn);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToFav();
                }
            }
        );
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(CityActivity.this, PlaceActivity.class);
        Bundle b = new Bundle();
        switch (v.getId()){
            case R.id.showHotels: b.putString("query", "Hotels");
                                    break;
            case R.id.showRestaurants: b.putString("query", "Restaurants");
                break;
            case R.id.showCafes: b.putString("query", "Cafes");
                break;
            case R.id.showPlacesOfInterest: b.putString("query", "Places of Interest");
                break;
            case R.id.showMarkets: b.putString("query", "Markets");
                break;
            case R.id.showMalls: b.putString("query", "Malls");
                break;
        }
        TextView textView = findViewById(R.id.place_name);
        b.putString("name", textView.getText().toString());
        intent.putExtras(b);
        startActivity(intent);
    }

    private void getPhotos(String placeId) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                try {
                    PlacePhotoMetadataResponse photos = task.getResult();

                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);


                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            ImageView imageView = findViewById(R.id.place_photo);
                            if (bitmap != null) {
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setImageBitmap(bitmap);
                            } else
                                imageView.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                catch (Exception e) {
                    Toast.makeText(CityActivity.this,"No photo found",Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    public void addToFav(){
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_FAV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        try {
                            progressBar.setVisibility(View.GONE);
                            JSONObject response=new JSONObject(res);
                            String result=response.getString("response");
                            Toast.makeText(getApplicationContext(),result, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Volley Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail());
                params.put("cityID", id);
                params.put("cityName", name);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
    }
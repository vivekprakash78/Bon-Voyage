package com.quadrivium.devs.bonvoyage;


import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class CityActivity extends AppCompatActivity {

    private GeoDataClient mGeoDataClient;
    TextView detailsField, currentTemperatureField, humidity_field, weatherIcon;
    Typeface weatherFont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        mGeoDataClient = Places.getGeoDataClient(this, null);
        Bundle b = getIntent().getExtras();
        String id = "-1", name = "Place not found";
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
        findHotels();
    }
    private void getPhotos(String placeId) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {

                PlacePhotoMetadataResponse photos = task.getResult();

                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);


                Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        Bitmap bitmap = photo.getBitmap();
                        ImageView imageView=findViewById(R.id.place_photo);
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    private void findHotels(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GetPlaces,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject obj = new JSONObject(response);

                            TextView PlaceList=findViewById(R.id.PlaceList);
                                    PlaceList.setText(obj.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("query", "Hotels in Kolkata");
                params.put("key", "AIzaSyBlOCbMZbhhrCCvrlOo0H2GKsT1vLNtQ8U");
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
    }
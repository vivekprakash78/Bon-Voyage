package com.quadrivium.devs.bonvoyage;


import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
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
        findHotels(name);
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

    private void findHotels(String name){
        String url=URLs.GetPlaces+"?query=Hotels in "+name+"&key=AIzaSyBlOCbMZbhhrCCvrlOo0H2GKsT1vLNtQ8U";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray place=response.getJSONArray("results");

                            ArrayList<String> listdata = new ArrayList<>();
                            if (place != null) {
                                for (int i=0;i<place.length();i++){
                                    listdata.add(place.getString(i));
                                }
                            }


                            ArrayList<Spot> androidFlavors = new ArrayList<>();

                            for (int i=0;i<place.length();i++){
                                JSONObject placeObj=new JSONObject( listdata.get(i) );
                                Boolean open_now=Boolean.TRUE;
                                if (placeObj.has("opening_hours"))
                                    open_now=placeObj.getJSONObject("opening_hours").getBoolean("open_now");

                                androidFlavors.add(new Spot(placeObj.getString("place_id"),
                                                            placeObj.getString("name"),
                                                            placeObj.getString("formatted_address"),
                                                            open_now,
                                                            placeObj.getDouble("rating")));
                            }

                            SpotListAdapter spotAdapter = new SpotListAdapter(CityActivity.this, androidFlavors);


                            ListView listView = findViewById(R.id.PlaceList);
                            listView.setAdapter(spotAdapter);
                        }
                        catch (JSONException e){
                            Toast.makeText(CityActivity.this,"Error",Toast.LENGTH_SHORT);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }
    }
package com.quadrivium.devs.bonvoyage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    TextView cityField, detailsField, currentTemperatureField, humidity_field, sunrise_field, sunset_field, weatherIcon, updatedField;
    Typeface weatherFont;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    GoogleSignInClient mGoogleSignInClient;
    private GeoDataClient mGeoDataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mGeoDataClient = Places.getGeoDataClient(this, null);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        CardView currentLocation = findViewById(R.id.current_location);
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSearch();
            }
        }) ;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        TextView name = header.findViewById(R.id.userName);
        TextView email = header.findViewById(R.id.userEmail);
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        name.setText(user.getName());
        email.setText(user.getEmail());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            else
                try {
                    getLocation();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        else{
            try {
                getLocation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ImageButton btnHosptital =findViewById(R.id.showHospital);
        btnHosptital.setOnClickListener(this);

        ImageButton btnPharmacy =findViewById(R.id.showPharmacy);
        btnPharmacy.setOnClickListener(this);

        ImageButton btnAtm =findViewById(R.id.showAtm);
        btnAtm.setOnClickListener(this);

        ImageButton btnEvents =findViewById(R.id.showEvents);
        btnEvents.setOnClickListener(this);

        ImageButton btnGasStations =findViewById(R.id.showGasStations);
        btnGasStations.setOnClickListener(this);

        ImageButton btnPlacesOfInterest =findViewById(R.id.showPlacesOfInterest);
        btnPlacesOfInterest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(HomeActivity.this, PlaceActivity.class);
        Bundle b = new Bundle();
        switch (view.getId()){
            case R.id.showHospital: b.putString("query", "Hospital");
                break;
            case R.id.showPharmacy: b.putString("query", "Pharmacy");
                break;
            case R.id.showAtm: b.putString("query", "ATM");
                break;
            case R.id.showEvents:
                    Toast.makeText(HomeActivity.this, "Events : Work in progress", Toast.LENGTH_SHORT).show();
                    return;
            case R.id.showGasStations: b.putString("query", "Gas stations");
                break;
            case R.id.showPlacesOfInterest: b.putString("query", "Places of interest");
                break;
        }

        TextView cityName=findViewById(R.id.city_field);
        b.putString("name", cityName.getText().toString());
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        getLocation();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorite) {
            startActivity(new Intent( HomeActivity.this, FavoriteActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent( HomeActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_about_us) {

        }
        else if (id == R.id.nav_sign_out) {
            finish();
            if(SharedPrefManager.getInstance(getApplicationContext()).getUser().getId()==0)
            {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(getApplicationContext(),"Sign out Successfull",Toast.LENGTH_SHORT).show();


                            }
                        });

            }
            SharedPrefManager.getInstance(getApplicationContext()).logout();
            startActivity(new Intent(this, SplashActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menu){

        switch (menu.getItemId()){
            case R.id.menu_search :{
                googleSearch();
            }
        }
        return true;
    }
    protected void googleSearch(){
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Intent intent = new Intent(this, CityActivity.class);
                Bundle b = new Bundle();
                b.putString("id", place.getId());
                b.putString("name", place.getName().toString());
                intent.putExtras(b);
                startActivity(intent);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this,status.getStatusMessage(),Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show();
            }
        }
    }
    protected void getLocation() throws IOException {
        GPSService mGPSService = new GPSService(HomeActivity.this);
        mGPSService.getLocation();

        if (mGPSService.isLocationAvailable == false) {
            Toast.makeText(getApplicationContext(), "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String,String> address = mGPSService.getLocationAddress();
            if(!address.containsKey("name")){
                findName(address.get("latitude"),address.get("longitude"));
            }
            else{
                TextView cityName=findViewById(R.id.city_field);
                cityName.setText(address.get("name"));
                findWeather(address.get("name"));
            }
        }
        mGPSService.closeGPS();
    }

    private void findName(String latitude,String longitude){
        String url=String.format(URLs.GetLocationName+"&key=AIzaSyC0tT8196dV1vKo2zaBelKUudm--AbXZfQ",latitude,longitude);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray results = null;
                        String city ="";
                        String placeID ="";
                        try {
                            results = response.getJSONArray("results");
                            JSONObject address_comp = results.getJSONObject(0);
                            JSONArray address = address_comp.getJSONArray("address_components");
                            for (int i = 0; i < address.length(); i++) {
                                JSONObject add_Comp = address.getJSONObject(i);
                                JSONArray types = add_Comp.getJSONArray("types");
                                for (int j = 0; j < types.length(); j++) {
                                    if("locality".equals(types.get(j).toString()))
                                        city = add_Comp.getString("long_name");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        TextView cityName=findViewById(R.id.city_field);
                        cityName.setText(city);
                        findWeather(city);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    protected void findWeather(String locationAddress){

        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        cityField = findViewById(R.id.city_field);
        updatedField = findViewById(R.id.updated_field);
        detailsField = findViewById(R.id.details_field);
        currentTemperatureField = findViewById(R.id.current_temperature_field);
        humidity_field = findViewById(R.id.humidity_field);
        sunrise_field = findViewById(R.id.sunrise_field);
        sunset_field =findViewById(R.id.sunset_field);
        weatherIcon = findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);


        Weather.placeIdTask asyncTask =new Weather.placeIdTask(new Weather.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_sunrise,String weather_sunset, String weather_updatedOn, String weather_iconText, String sun_rise) {


                updatedField.setText("Updated: "+weather_updatedOn);
                detailsField.setText("Conditions: "+weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidity: "+weather_humidity);
                sunrise_field.setText("Sunrise: "+weather_sunrise);
                sunset_field.setText("Sunset: "+weather_sunset);
                weatherIcon.setText(Html.fromHtml(weather_iconText));

            }
        });
        asyncTask.execute(locationAddress);
    }

}
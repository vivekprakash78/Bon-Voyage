package com.quadrivium.devs.bonvoyage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusAccent));
        String email=SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail();
        findCity(email);
        final ListView placeList =findViewById(R.id.CityList);
        placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textID=view.findViewById(R.id.cityID);
                TextView textName=view.findViewById(R.id.cityName);

                Intent intent = new Intent(FavoriteActivity.this, CityActivity.class);
                Bundle b = new Bundle();
                b.putString("id", textID.getText().toString());
                b.putString("name", textName.getText().toString());
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    private void findCity(String email){
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String url= URLs.URL_GET_FAV+"?email="+email;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try{
                            if(response.getBoolean("status")) {
                                JSONArray city = response.getJSONArray("city");
                                ArrayList<String> listdata = new ArrayList<>();
                                if (city != null) {
                                    for (int i = 0; i < city.length(); i++) {
                                        listdata.add(city.getString(i));
                                    }
                                }
                                ArrayList<City> cityList = new ArrayList<>();

                                for (int i = 0; i < city.length(); i++) {
                                    JSONObject cityObj = new JSONObject(listdata.get(i));
                                    String city_id = cityObj.getString("cityID");
                                    String city_name = cityObj.getString("cityName");
                                    cityList.add(new City(city_id, city_name));
                                }

                                CityListAdapter cityAdapter = new CityListAdapter(FavoriteActivity.this, cityList);
                                ListView listView = findViewById(R.id.CityList);
                                listView.setAdapter(cityAdapter);
                            }
                            else
                                Toast.makeText(FavoriteActivity.this,"No favorite city found",Toast.LENGTH_SHORT).show();
                        }
                        catch (JSONException e){
                            Toast.makeText(FavoriteActivity.this,"No record found",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FavoriteActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }
}

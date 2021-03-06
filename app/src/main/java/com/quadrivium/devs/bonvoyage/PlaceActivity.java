package com.quadrivium.devs.bonvoyage;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
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

public class PlaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusAccent));
        TextView textHeader = findViewById(R.id.PlaceHeading);
        Bundle b = getIntent().getExtras();
        String query = "Places", name = "Mumbai";
        if (b != null) {
            query = b.getString("query");
            name = b.getString("name");
        }
        textHeader.setText(query);
        findPlaces(query,name);

        final ListView placeList =findViewById(R.id.PlaceList);
        placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView placeName = view.findViewById(R.id.name);
                TextView placeID = view.findViewById(R.id.placeID);
                String ID=placeID.getText().toString().replaceAll("\\s","%20");
                String name=placeName.getText().toString();
                String URI="https://www.google.com/maps/search/?api=1&query="+name+"&query_place_id="+ID;
                Uri IntentUri = Uri.parse(URI);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, IntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }

    private void findPlaces(String query,String name){
        query = query.replaceAll(" ", "%20");
        String url=URLs.GetPlaces+"?query="+query+"%20in%20"+name+"&key=AIzaSyBlOCbMZbhhrCCvrlOo0H2GKsT1vLNtQ8U";
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
                            ArrayList<Spot> spotList = new ArrayList<>();

                            for (int i=0;i<place.length();i++){
                                JSONObject placeObj=new JSONObject( listdata.get(i) );
                                String name=null,place_id=null,formatted_address=null;
                                Boolean open_now=Boolean.TRUE;
                                Double rating=0.0;

                                if (placeObj.has("place_id"))
                                    place_id=placeObj.getString("place_id");

                                if (placeObj.has("name"))
                                    name=placeObj.getString("name");

                                if (placeObj.has("formatted_address"))
                                    formatted_address=placeObj.getString("formatted_address");

                                if (placeObj.has("opening_hours"))
                                    if (placeObj.getJSONObject("opening_hours").has("open_now"))
                                        open_now=placeObj.getJSONObject("opening_hours").getBoolean("open_now");

                                if (placeObj.has("rating"))
                                    rating=placeObj.getDouble("rating");

                                spotList.add(new Spot(place_id,name,formatted_address,open_now,rating));
                            }

                            SpotListAdapter spotAdapter = new SpotListAdapter(PlaceActivity.this, spotList);


                            ListView listView = findViewById(R.id.PlaceList);
                            listView.setAdapter(spotAdapter);
                        }
                        catch (JSONException e){
                            Toast.makeText(PlaceActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PlaceActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
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
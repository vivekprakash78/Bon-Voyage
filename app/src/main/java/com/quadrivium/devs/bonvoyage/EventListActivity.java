package com.quadrivium.devs.bonvoyage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusAccent));
        String location="";
        Bundle b = getIntent().getExtras();
        if (b != null) {
            location = b.getString("location");
        }
        findEvents(location);

    }

    private void findEvents(String location){
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String url= URLs.URL_GET_EVENT+"?place="+location;
        url=url.replaceAll(" ","%20");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try{
                            if(response.getBoolean("status")) {
                                JSONArray event = response.getJSONArray("event");
                                ArrayList<String> listdata = new ArrayList<>();
                                if (event != null) {
                                    for (int i = 0; i < event.length(); i++) {
                                        listdata.add(event.getString(i));
                                    }
                                }
                                ArrayList<Event> eventList = new ArrayList<>();

                                for (int i = 0; i < event.length(); i++) {
                                    JSONObject eventObj = new JSONObject(listdata.get(i));
                                    String eventID = eventObj.getString("eventID");
                                    String eventName = eventObj.getString("eventName");
                                    String eventLocation = eventObj.getString("place");
                                    String eventFromDate = eventObj.getString("startDate");
                                    String eventToDate= eventObj.getString("endDate");
                                    String eventFromTime= eventObj.getString("startTime");
                                    String eventToTime= eventObj.getString("endTime");
                                    String eventDescription= eventObj.getString("description");
                                    String eventCreator= eventObj.getString("creator");
                                    eventList.add(new Event(eventID, eventName, eventLocation, eventFromDate, eventToDate, eventFromTime, eventToTime, eventDescription, eventCreator));
                                }

                                EventListAdapter eventAdapter = new EventListAdapter(EventListActivity.this, eventList);
                                ListView listView = findViewById(R.id.eventList);
                                listView.setAdapter(eventAdapter);
                            }
                            else
                                Toast.makeText(EventListActivity.this,"No events found",Toast.LENGTH_SHORT).show();
                        }
                        catch (JSONException e){
                            Toast.makeText(EventListActivity.this,"No events found",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EventListActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
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
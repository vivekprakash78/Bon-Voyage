package com.quadrivium.devs.bonvoyage;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.view.View;
import android.widget.DatePicker;

import org.json.JSONException;
import org.json.JSONObject;

public class EventActivity extends AppCompatActivity {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private TextInputLayout eventName;
    private TextInputLayout placePicker;
    private TextView fromDateEtxt;
    private TextView toDateEtxt;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private TimePickerDialog fromTimePickerDialog;
    private TimePickerDialog toTimePickerDialog;
    private TextView fromTimeEtxt;
    private TextView toTimeEtxt;
    private TextInputLayout description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusAccent));
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        findViewsById();

        setDateTimeField();

        findViewById(R.id.placePickerEdit).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                            .build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setFilter(typeFilter)
                                    .build(EventActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }

        });

        findViewById(R.id.datePickerEdit).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventActivity.this,"Select start date",Toast.LENGTH_SHORT).show();
                fromDatePickerDialog.show();

            }
        });

        findViewById(R.id.timePickerEdit).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventActivity.this,"Select start time",Toast.LENGTH_SHORT).show();
                fromTimePickerDialog.show();
            }
        });

       findViewById(R.id.addOn).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventData();
            }
        });

    }

    public  void eventData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_EVENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        try {
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
                params.put("event_name", eventName.getEditText().getText().toString());
                params.put("place", placePicker.getEditText().getText().toString());
                params.put("start_date", fromDateEtxt.getText().toString());
                params.put("end_date", toDateEtxt.getText().toString());
                params.put("start_time", fromTimeEtxt.getText().toString());
                params.put("end_time", toTimeEtxt.getText().toString());
                params.put("description",description.getEditText().getText().toString());
                params.put("creator", SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail());
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
    private void findViewsById() {
        eventName = findViewById(R.id.eventname);
        placePicker=findViewById(R.id.placePicker);
        fromDateEtxt =  findViewById(R.id.etxt_fromdate);
        toDateEtxt = findViewById(R.id.etxt_todate);
        fromTimeEtxt= findViewById(R.id.etxt_fromtime);
        toTimeEtxt=findViewById(R.id.etxt_totime);
        description=findViewById(R.id.description);
    }
    private void setDateTimeField() {

        final Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
                Toast.makeText(EventActivity.this,"Select end date",Toast.LENGTH_SHORT).show();
                toDatePickerDialog.show();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }


        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        fromTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                fromTimeEtxt.setText( selectedHour + ":" + selectedMinute);
                Toast.makeText(EventActivity.this,"Select end time",Toast.LENGTH_SHORT).show();
                toTimePickerDialog.show();
            }
        }, hour, minute, true);//Yes 24 hour time

        Calendar ncurrentTime = Calendar.getInstance();
        int nhour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int nminute = mcurrentTime.get(Calendar.MINUTE);

        toTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                toTimeEtxt.setText( selectedHour + ":" + selectedMinute);
            }
        }, nhour, nminute, true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                placePicker.getEditText().setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


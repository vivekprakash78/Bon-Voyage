package com.quadrivium.devs.bonvoyage;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {
    TextInputLayout editTextName, editTextEmail, editTextPassword, editTextReenterPassword;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        progressBar =  view.findViewById(R.id.progressBar);


        editTextName = view.findViewById(R.id.name);
        editTextEmail = view.findViewById(R.id.email);
        editTextPassword = view.findViewById(R.id.password);
        editTextReenterPassword = view.findViewById(R.id.reenter_password);


        view.findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        return view;
    }
    private void registerUser() {
        final String name = editTextName.getEditText().getText().toString().trim();
        final String email = editTextEmail.getEditText().getText().toString().trim();
        final String password = editTextPassword.getEditText().getText().toString().trim();
        final String reenterPassword = editTextReenterPassword.getEditText().getText().toString().trim();
        //first we will do the validations

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Please enter your name");
            editTextName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Enter a password");
            editTextPassword.requestFocus();
            return;
        }
        if (!password.equals(reenterPassword)) {
            editTextReenterPassword.setError("Password mismatch");
            editTextReenterPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getContext(), "Succesfully Signed up", Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getInt("id"),
                                        userJson.getString("name"),
                                        userJson.getString("email")
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getContext()).userLogin(user);

                                //starting the profile activity
                                getActivity().finish();
                                startActivity(new Intent(getContext(), HomeActivity.class));
                            } else {
                                Toast.makeText(getContext(), obj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);

    }

    }
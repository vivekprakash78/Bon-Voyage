package com.quadrivium.devs.bonvoyage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusAccent));

        TextView name = (TextView) findViewById(R.id.user_profile_name);
        TextView email = (TextView) findViewById(R.id.user_profile_email);
        User user=SharedPrefManager.getInstance(getApplicationContext()).getUser();
        name.setText(user.getName());
        email.setText(user.getEmail());
    }
}

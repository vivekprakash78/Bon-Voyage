package com.quadrivium.devs.bonvoyage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

        }
        else
        {
            RelativeLayout view = findViewById(R.id.relativeLayout);
            Snackbar snackbar = Snackbar
                    .make(view, "No internet connection!", Snackbar.LENGTH_INDEFINITE);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }


        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));


        TabLayout tabLayout =  findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        TextView skipText = findViewById(R.id.skipText);
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }
}

package com.quadrivium.devs.bonvoyage;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                if(SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn())
                {
                    startActivity(new Intent( SplashActivity.this, HomeActivity.class));
                    finish();
                }else
                {
                    startActivity(new Intent( SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }.start();
    }
}
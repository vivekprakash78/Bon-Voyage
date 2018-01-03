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
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("Pref", Context.MODE_PRIVATE);
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                if(pref.getString("user_id", "").length() == 0)
                {
                    startActivity(new Intent( SplashActivity.this, MainActivity.class));
                    finish();
                }else
                {
                    startActivity(new Intent( SplashActivity.this, HomeActivity.class));
                    finish();
                }
            }
        }.start();
    }
}
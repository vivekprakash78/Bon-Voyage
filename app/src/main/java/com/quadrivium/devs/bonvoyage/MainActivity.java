package com.quadrivium.devs.bonvoyage;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void showError(View view){
        TextInputLayout t1 = (TextInputLayout) findViewById(R.id.email);
        TextInputLayout t2 = (TextInputLayout) findViewById(R.id.password);
        if(i==0){
            t1.setError("Wrong username");
            t2.setError("Wrong password");
            i=1;
        }
        else{
            t1.setError(null);
            t2.setError(null);
            i=0;
        }
    }
}

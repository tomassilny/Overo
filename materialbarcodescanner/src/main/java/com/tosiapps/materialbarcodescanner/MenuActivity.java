package com.tosiapps.materialbarcodescanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_menu);

    }
}

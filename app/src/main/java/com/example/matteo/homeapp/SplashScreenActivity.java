package com.example.matteo.homeapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;

import xdroid.toaster.Toaster;

public class SplashScreenActivity extends AppCompatActivity {
    static{
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_splash_screen);*/
        final Intent mainIntent = new Intent(SplashScreenActivity.this,MainActivity.class);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                startActivity(mainIntent);
                finish();
            }
        }, 1500);
    }
}

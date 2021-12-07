package com.app.restaurantpos;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.app.restaurantpos.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {


    public static int splashTimeOut = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Constant.st_pprint=0;
        Constant.btDev=null;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, activePage.class);
            startActivity(intent);
            finish();
        }, splashTimeOut);
    }
}


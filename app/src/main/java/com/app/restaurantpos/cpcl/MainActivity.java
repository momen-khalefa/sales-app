package com.app.restaurantpos.cpcl;

import android.Manifest;
import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class MainActivity extends TabActivity {

    private static final int TARGET_VERSION = 22;
    private static final int PERMISSIONS_REQUEST = 1;
    private static String[] PERMISSIONS_LIST =
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SetCheckPermission();
        final TabHost tabHost = getTabHost();
        tabHost.getTabWidget().setDividerDrawable(null);

        tabHost.addTab(tabHost.newTabSpec("Bluetooth")
                .setIndicator("Bluetooth")
                .setContent(new Intent(this, Bluetooth_Activity.class)));

       /* tabHost.addTab(tabHost.newTabSpec("Wi-Fi")
                .setIndicator("Wi-Fi")
                .setContent(new Intent(this, WiFi_Activity.class)));

        */

        tabHost.setCurrentTab(0);

        for(int i=0; i<tabHost.getTabWidget().getChildCount(); i++)
        {
            tabHost.getTabWidget().getChildAt(i)
                    .setBackgroundColor(Color.parseColor("#10000000"));
        }

        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
                .setBackgroundColor(Color.parseColor("#ffffff"));

        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                for(int i=0; i<tabHost.getTabWidget().getChildCount(); i++)
                {
                    tabHost.getTabWidget().getChildAt(i)
                            .setBackgroundColor(Color.parseColor("#10000000"));
                }

                tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
                        .setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });
    }

    public void SetCheckPermission()
    {
        int nVersion = android.os.Build.VERSION.SDK_INT;

        if(nVersion > TARGET_VERSION)
        {
            if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                this.requestPermissions(PERMISSIONS_LIST, PERMISSIONS_REQUEST);
            }
        }

    }


}

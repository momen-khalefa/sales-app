package com.app.restaurantpos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.provider.Settings.Secure;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.app.restaurantpos.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class activePage extends AppCompatActivity {

    SharedPreferences sp;
    String Json;
    String company;
    String person;
    String date;
    String android_id;
    Handler ha;
    boolean stop=false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activison_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        TextView etxt_active=findViewById(R.id.etxt_active);
        TextView active=findViewById(R.id.etxt_active2);
        TextView button=findViewById(R.id.txt_active);
        TextView button2=findViewById(R.id.txt_active2);

        android_id = Secure.getString(activePage.this.getContentResolver(),
                Secure.ANDROID_ID);
        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String activee = sp.getString("active", "");
        String api= sp.getString("api", "");
        if(activee.length()>0 && api.length()>0 && activee.equals("true")){
            Intent intent = new Intent(activePage.this, LoginActivity.class);
            startActivity(intent);
        }
        else if(isConnected()) {
            check_device(android_id);
        }
        else {
            if(activee.equals("true")) {
                Intent intent = new Intent(activePage.this, LoginActivity.class);
                startActivity(intent);
            }
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                    //H denote 24 hours and h denote 12 hour hour format
                    String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()); //HH:mm:ss a
                    date = currentDate + " " + currentTime;
                    company = active.getText().toString();
                    person = etxt_active.getText().toString();
                    check(android_id);
                }
                else {
                    Toasty.error(activePage.this, "افحص الاتصال بالانترنت", Toast.LENGTH_SHORT).show();

                }


            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    check_device(android_id);
                }
                else {
                    Toasty.error(activePage.this, "افحص الاتصال بالانترنت", Toast.LENGTH_SHORT).show();

                }


            }
        });

    }


    public void check(String id){
        new Background_Image(id).execute();
    }
    class Background_Image extends AsyncTask<Void,Void,String> {

        String id;
        public Background_Image(String id){

            this.id=id;
        }
        String json_url;
        @Override
        protected void onPreExecute() {

            json_url="http://ttssystems.truetime.ps/api/RegestedDevices/NewRequest?DeviceCode="+id+"&CompanyName="+company+"&PersonName="+person+"&ProductID=23&RequestStatus=0&Requestdate="+date;

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url= new URL(json_url);
                HttpURLConnection huc= (HttpURLConnection) url.openConnection();
                InputStream isr= huc.getInputStream();
                BufferedReader br= new BufferedReader(new InputStreamReader(isr));
                StringBuilder sb= new StringBuilder();
                while((Json= br.readLine()) != null){
                    sb.append(Json+"\n");
                }
                br.close();
                isr.close();
                huc.disconnect();
                return sb.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String string) {

            try {
                loadInto_Image(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadInto_Image(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
            JSONObject obj = jsonArray.getJSONObject(0);
            String entrey=obj.getString("ColResult");
            if(entrey.equals("faild")){
            Toasty.error(activePage.this, "هذا الجهاز مغلق حاليا", Toast.LENGTH_SHORT).show();
        }
        else {

        }



    }
    public void check_device(String id){
        new Background_check_device(id).execute();
    }
    class Background_check_device extends AsyncTask<Void,Void,String> {

        String id;
        public Background_check_device(String id){

            this.id=id;
        }
        String json_url;
        @Override
        protected void onPreExecute() {

            json_url="http://ttssystems.truetime.ps/api/RegestedDevices/GetDeviceStatus?DeviceCode="+id+"&ProductID=23";


        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url= new URL(json_url);
                HttpURLConnection huc= (HttpURLConnection) url.openConnection();
                InputStream isr= huc.getInputStream();
                BufferedReader br= new BufferedReader(new InputStreamReader(isr));
                StringBuilder sb= new StringBuilder();
                while((Json= br.readLine()) != null){
                    sb.append(Json+"\n");
                }
                br.close();
                isr.close();
                huc.disconnect();
                return sb.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String string) {

            if(string!=null) {
                try {
                    loadInto(string);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void loadInto(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject obj = jsonArray.getJSONObject(0);
        String entrey=obj.getString("Activation");
        String DataName=obj.getString("DataName");
        if(entrey.equals("true")){
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("api", DataName);
            editor.putString("active", "true");
            editor.apply();
            Intent intent = new Intent(activePage.this, LoginActivity.class);
            startActivity(intent);
        }




    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }


}

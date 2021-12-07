package com.app.restaurantpos.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.HomeActivity;
import com.app.restaurantpos.R;
import com.app.restaurantpos.activePage;
import com.app.restaurantpos.cust_home;
import com.app.restaurantpos.customers.CustomersActivity;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.utils.BaseActivity;

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
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends BaseActivity {

    EditText etxtPhone, etxtPassword;
    TextView txtLogin;
    SharedPreferences sp;
    String Json,android_id,api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();


        etxtPhone = findViewById(R.id.etxt_phone);
        etxtPassword = findViewById(R.id.etxt_password);
        txtLogin = findViewById(R.id.txt_login);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        api=sp.getString("api","");

        String phone = sp.getString(Constant.SP_PHONE, "");
        String password = sp.getString(Constant.SP_PASSWORD, "");

        if(isConnected()){
            android_id = Settings.Secure.getString(LoginActivity.this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            check_device(android_id);
            getusers();
        }

        etxtPhone.setText(phone);
        etxtPassword.setText(password);

        if (phone.length() >= 3 && password.length() >= 3) {
            login(phone, password);
        }


        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etxtPhone.getText().toString().trim();
                String password = etxtPassword.getText().toString().trim();

                if (phone.isEmpty()) {
                    etxtPhone.setError(getString(R.string.please_enter_phone_number));
                    etxtPhone.requestFocus();
                } else if (password.isEmpty()) {
                    etxtPassword.setError(getString(R.string.please_enter_password));
                    etxtPassword.requestFocus();
                } else {
                    login(phone, password);
                }
            }
        });


    }

    private void login(String phone, String password) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(LoginActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> userData;
        userData = databaseAccess.checkUser(phone, password);

        if (userData.isEmpty()) {
            Toasty.error(this, "تم ادخال معلومات غير صحيحة", Toast.LENGTH_SHORT).show();
        } else {

            String userName = userData.get(0).get("user_name");
            String userType = userData.get(0).get("user_type");

            if(userType.equals("false")){
                Toasty.error(this, "تم ايقاف تصريح الدخول لهذا المستخدم", Toast.LENGTH_SHORT).show();

            }
            else {
                //Creating editor to store values to shared preferences
                SharedPreferences.Editor editor = sp.edit();
                //Adding values to editor

                editor.putString(Constant.SP_PHONE, phone);
                editor.putString(Constant.SP_PASSWORD, password);

                editor.putString(Constant.SP_USER_NAME, userName);
                editor.putString(Constant.SP_USER_TYPE, userType);

                editor.apply();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);

                Toasty.success(this, R.string.login_successful, Toast.LENGTH_SHORT).show();

            }
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

            try {
                loadInto(string);
            } catch (JSONException e) {
                e.printStackTrace();
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
        }
        else{
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("api", "");
            editor.putString("active", "false");
            editor.apply();
            Intent intent = new Intent(LoginActivity.this, activePage.class);
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


    public void getusers(){
        new Background_users().execute();
    }
    class Background_users extends AsyncTask<Void,Void,String> {

        String json_url;
        @Override
        protected void onPreExecute() {

            json_url="http://ttssystems.truetime.ps/api/employeesdata/getusersdata?company="+api;
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
                loadInto_users(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadInto_users(String json) throws JSONException {

        JSONArray jsonArray = new JSONArray(json);
        String[] name = new String[jsonArray.length()];
        String[] id = new String[jsonArray.length()];
        String[] password = new String[jsonArray.length()];
        String[] active = new String[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            name [i] =obj.getString("EmployeeName");
            id [i] =obj.getString("EmployeeID");
            password [i] =obj.getString("Password");
            active [i] =obj.getString("AllowUseOrdersApp");

        }
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(LoginActivity.this);
        databaseAccess.open();
        databaseAccess.delete_users();
        for(int i=0;i<id.length;i++){
            databaseAccess.open();
            databaseAccess.addUser(name[i],id[i],password[i],active[i]);
        }
        databaseAccess.close();

    }

}

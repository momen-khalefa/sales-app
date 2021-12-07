package com.app.restaurantpos.customers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.app.restaurantpos.Constant;
import com.app.restaurantpos.HomeActivity;
import com.app.restaurantpos.R;
import com.app.restaurantpos.activePage;
import com.app.restaurantpos.adapter.CustomerAdapter;
import com.app.restaurantpos.cust_home;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.database.DatabaseOpenHelper;
import com.app.restaurantpos.login.LoginActivity;
import com.app.restaurantpos.orders.OrdersActivity;
import com.app.restaurantpos.utils.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.obsez.android.lib.filechooser.ChooserDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class CustomersActivity extends BaseActivity {


    ProgressDialog loading;
    private RecyclerView recyclerView;
    String Json;
    ImageView imgNoProduct;
    EditText etxtSearch;
    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(CustomersActivity.this);
    FloatingActionButton fabAdd;
    String api;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        //getSupportActionBar().setTitle(R.string.all_customer);
        getSupportActionBar().setTitle("Last Update :"+ Constant.time);
        recyclerView = findViewById(R.id.recycler_view);
        imgNoProduct = findViewById(R.id.image_no_product);
        etxtSearch = findViewById(R.id.etxt_customer_search);
        fabAdd = findViewById(R.id.fab_add);
        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        String time=sp.getString("time","");
        getSupportActionBar().setTitle("Last Update :"+time);
        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        api=sp.getString("api","");
        String activee = sp.getString("active", "");
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> customerData;
        customerData = databaseAccess.getCustomers();

        Log.d("data", "" + customerData.size());

        if (customerData.isEmpty()) {
            Toasty.info(this, R.string.no_customer_found, Toast.LENGTH_SHORT).show();
            imgNoProduct.setImageResource(R.drawable.not_found);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            CustomerAdapter customerAdapter = new CustomerAdapter(CustomersActivity.this, customerData);

            recyclerView.setAdapter(customerAdapter);


        }

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomersActivity.this, AddCustomersActivity.class);
                startActivity(intent);
            }
        });


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                Log.d("data", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(CustomersActivity.this);
                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchCustomerList;

                searchCustomerList = databaseAccess.searchCustomers(s.toString());


                if (searchCustomerList.isEmpty()) {

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.not_found);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);


                    CustomerAdapter customerAdapter = new CustomerAdapter(CustomersActivity.this, searchCustomerList);

                    recyclerView.setAdapter(customerAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.d("data", s.toString());
            }


        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.language_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(isConnected()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CustomersActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.load, null);
            dialog.setView(dialogView);
            dialog.setCancelable(false);
            final AlertDialog alertDialog = dialog.create();
            new Handler().postDelayed(() -> {

                alertDialog.dismiss();
            }, 10000);
            android_id = Settings.Secure.getString(CustomersActivity.this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            check_device(android_id);
            get_customer();


            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
            //H denote 24 hours and h denote 12 hour hour format
            String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()); //HH:mm:ss a
            Constant.time = currentDate + " " + currentTime;
            editor = sp.edit();
            editor.putString("time",currentDate + " " + currentTime);
            editor.apply();
            getSupportActionBar().setTitle("Last Update :" + sp.getString("time",""));
        }
        else{
            Toasty.error(CustomersActivity.this, "افحص الإتصال بالانترنت", Toast.LENGTH_SHORT).show();
        }

         int id = item.getItemId();
        if (id == R.id.menu_export_customer) {


            folderChooser();

            return true;
        } else if (id == android.R.id.home) {
            this.finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void folderChooser() {
        new ChooserDialog(CustomersActivity.this)

                .displayPath(true)
                .withFilter(true, false)

                // to handle the result(s)
                .withChosenListener((path, pathFile) -> {
                    onExport(path);
                    Log.d("path", path);

                })
                .build()
                .show();
    }


    public void onExport(String path) {

        String directoryPath = path;
        File file = new File(directoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Export SQLite DB as EXCEL FILE
        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, directoryPath);

        sqliteToExcel.exportSingleTable("customers", "customers.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(CustomersActivity.this);
                loading.setMessage(getString(R.string.data_exporting_please_wait));
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            public void onCompleted(String filePath) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(CustomersActivity.this, R.string.data_successfully_exported, Toast.LENGTH_SHORT).show();


                }, 5000);

            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Toasty.error(CustomersActivity.this, R.string.data_export_fail, Toast.LENGTH_SHORT).show();

                Log.d("Error", e.toString());
            }
        });
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
    public void get_customer(){
        new Background_customer().execute();
    }
    class Background_customer extends AsyncTask<Void,Void,String> {

        String json_url;
        @Override
        protected void onPreExecute() {

            json_url="http://"+api+".truetime.ps/api/referencess";
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

            if (string!=null) {
                try {
                    loadInto_customer(string);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void loadInto_customer(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        int[] id = new int[jsonArray.length()];
        String[] name = new String[jsonArray.length()];
        String[] phone = new String[jsonArray.length()];
        String[] email = new String[jsonArray.length()];
        String[] address = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            id [i] =obj.getInt("RefNo");
            name [i] =obj.getString("RefName");
            phone [i] =obj.getString("RefMobile");
            email [i] =obj.getString("Balance");
            address [i] =obj.getString("Address");

        }
        databaseAccess.open();
        databaseAccess.delete_cust();
        for(int i=0;i<name.length;i++){
            databaseAccess.open();
            databaseAccess.addCustomer(id[i],name[i],phone[i],email[i],address [i]);
        }
        databaseAccess.close();
        this.recreate();
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
            Intent intent = new Intent(CustomersActivity.this, activePage.class);
            startActivity(intent);
        }




    }


}


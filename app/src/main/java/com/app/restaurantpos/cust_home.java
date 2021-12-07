package com.app.restaurantpos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.app.restaurantpos.categories.CategoriesActivity;
import com.app.restaurantpos.customers.Account_bills;
import com.app.restaurantpos.customers.bills;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.login.LoginActivity;
import com.app.restaurantpos.utils.BaseActivity;
import com.app.restaurantpos.utils.acc_stat;
import com.app.restaurantpos.utils.checksModels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class cust_home extends BaseActivity {


    CardView card_pos_1, card_pos_2, card_bill,  card_cash ,card_back_sales;
    //for double back press to exit
    private static final int TIME_DELAY = 2000;
    private static long backPressed;
    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(cust_home.this);
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String userType;
    TextView txt_name , cust_town , text_mony;
    String getCustomerId, getCustomerName, getCustomerCell, getCustomerEmail, getCustomerAddress,android_id;
    String Json;
    String api;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Last Update :" + Constant.time);
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        card_pos_1 = findViewById(R.id.card_pos_1);
        card_pos_2 = findViewById(R.id.card_pos_2);
        card_bill = findViewById(R.id.card_bill);
        card_cash = findViewById(R.id.card_cash);
        txt_name = findViewById(R.id.txt_name);
        cust_town = findViewById(R.id.cust_town);
        text_mony = findViewById(R.id.text_mony);
        card_back_sales=findViewById(R.id.card_back_sales);
        getCustomerId = getIntent().getExtras().getString("customer_id");
        getCustomerName = getIntent().getExtras().getString("customer_name");
        getCustomerCell = getIntent().getExtras().getString("customer_cell");
        getCustomerEmail = getIntent().getExtras().getString("customer_email");
        getCustomerAddress = getIntent().getExtras().getString("customer_address");
        txt_name.setText(getCustomerName);
        cust_town.setText(getCustomerAddress);

        databaseAccess.open();
        text_mony.setText(databaseAccess.getCustomerBalance(getCustomerId) +"₪");

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        String activee = sp.getString("active", "");
       /* if(activee.equals("false")){
            Intent intent = new Intent(cust_home.this, activePage.class);
            startActivity(intent);
        }*/
        editor = sp.edit();
        String time=sp.getString("time","");
        getSupportActionBar().setTitle("Last Update :"+time);
        editor = sp.edit();

        userType = sp.getString(Constant.SP_USER_TYPE, "");

        api=sp.getString("api","");


        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();
        databaseAccess.close();
        databaseAccess.open();
        databaseAccess.close();


        card_pos_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(cust_home.this, CategoriesActivity.class);
                intent.putExtra("customer_id",getCustomerId);
                intent.putExtra("customer_name",getCustomerName);
                intent.putExtra("type","2");
                startActivity(intent);


            }
        });
        card_pos_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cust_home.this, CategoriesActivity.class);
                intent.putExtra("customer_id",getCustomerId);
                intent.putExtra("customer_name",getCustomerName);
                intent.putExtra("type","11");
                startActivity(intent);


            }
        });
        card_back_sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cust_home.this, CategoriesActivity.class);
                intent.putExtra("customer_id",getCustomerId);
                intent.putExtra("customer_name",getCustomerName);
                intent.putExtra("type","12");
                startActivity(intent);


            }
        });

        card_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    dialogSuccess();
                }
                else{
                    Toasty.error(cust_home.this, "افحص الإتصال بالانترنت", Toast.LENGTH_SHORT).show();
                }


            }
        });
        card_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cust_home.this, bills.class);
                intent.putExtra("cus_name",getCustomerName);
                intent.putExtra("cus_id",getCustomerId);
                startActivity(intent);


            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_cust_pos, menu);
        return true;
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.menu_cart_button1:
                if(isConnected()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(cust_home.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.load, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                    final AlertDialog alertDialog = dialog.create();
                    new Handler().postDelayed(() -> {

                        alertDialog.dismiss();
                    }, 10000);
                    android_id = Settings.Secure.getString(cust_home.this.getContentResolver(),
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
                    Toasty.error(cust_home.this, "افحص الإتصال بالانترنت", Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.menu_cart_button2:
                Intent intent = new Intent(cust_home.this, HomeActivity.class);
                startActivity(intent);
                return true;

            case android.R.id.home:

                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressed + TIME_DELAY > System.currentTimeMillis()) {

            finishAffinity();

        } else {
            Toasty.info(this, R.string.press_once_again_to_exit,
                    Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
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

            try {
                loadInto_customer(string);
            } catch (JSONException e) {
                e.printStackTrace();
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
            Intent intent = new Intent(cust_home.this, activePage.class);
            startActivity(intent);
        }




    }


    public void get_report(String id ,String date1,String date2){
        new Background_get_report(id , date1 ,date2).execute();
    }
    class Background_get_report extends AsyncTask<Void,Void,String> {

        String id;
        String date1;
        String date2;
        public Background_get_report(String id,String date1,String date2){

            this.id=id;
            this.date1=date1;
            this.date2=date2;

        }
        String json_url;
        @Override
        protected void onPreExecute() {

            json_url="http://"+api+".truetime.ps/api/ReportRefStatment?Referance="+id+"&_DateFrom="+date1+"&_DateTo="+date2;


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
                loadInto_get_report(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadInto_get_report(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] DocID = new String[jsonArray.length()];
        String[] DocName = new String[jsonArray.length()];
        String[] DocDate = new String[jsonArray.length()];
        String[] DebitAmount = new String[jsonArray.length()];
        String[] CredAmount = new String[jsonArray.length()];
        String[] Balance = new String[jsonArray.length()];
        String[] DocNotes = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            DocID[i]=obj.getString("DocID");
            DocName[i]=obj.getString("DocName");
            DocDate[i]=obj.getString("DocDate").substring(0,10);
            DebitAmount[i]=obj.getString("DebitAmount");
            CredAmount[i]=obj.getString("CredAmount");
            Balance[i]=obj.getString("Balance");
            DocNotes[i]=obj.getString("DocNotes");

        }
        Intent intent = new Intent(cust_home.this, Account_bills.class);
        Bundle args = new Bundle();
        args.putStringArray("DocID",DocID);
        args.putStringArray("DocName",DocName);
        args.putStringArray("DocDate",DocDate);
        args.putStringArray("DebitAmount",DebitAmount);
        args.putStringArray("CredAmount",CredAmount);
        args.putStringArray("Balance",Balance);
        args.putStringArray("DocNotes",DocNotes);
        intent.putExtras(args);
        startActivity(intent);
    }


    public void dialogSuccess() {


        AlertDialog.Builder dialog = new AlertDialog.Builder(cust_home.this);
        View dialogView = getLayoutInflater().inflate(R.layout.getdate, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        ImageButton dialogBtnCloseDialog = dialogView.findViewById(R.id.btn_close);
        Button submit=dialogView.findViewById(R.id.button_submit);
        TextView date1=dialogView.findViewById(R.id.text_view_date1);
        TextView date2=dialogView.findViewById(R.id.text_view_date2);

        date1.setText("2021-01-01");
        date2.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date()));
        AlertDialog alertDialogSuccess = dialog.create();

        dialogBtnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogSuccess.dismiss();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_report(getCustomerId,date1.getText().toString(),date2.getText().toString());

            }
        });
        date1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        cust_home.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        date2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        cust_home.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener2,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String Smonth="0";
                String Sday="0";
                if (month<10){
                    Smonth="0"+month;
                }
                else{
                    Smonth=""+month;
                }
                if (day<10){
                    Sday="0"+day;
                }
                else{
                    Sday=""+day;
                }
                String date = year + "-" + Smonth + "-" + Sday;
                date1.setText(date);
            }
        };
        mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String Smonth="0";
                String Sday="0";
                if (month<10){
                    Smonth="0"+month;
                }
                else{
                    Smonth=""+month;
                }
                if (day<10){
                    Sday="0"+day;
                }
                else{
                    Sday=""+day;
                }
                String date = year + "-" + Smonth + "-" + Sday;
                date2.setText(date);
            }
        };

        alertDialogSuccess.show();


    }

}

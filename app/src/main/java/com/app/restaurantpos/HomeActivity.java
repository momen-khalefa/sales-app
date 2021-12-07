package com.app.restaurantpos;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import org.json.JSONObject;

import com.app.restaurantpos.assist.ResourceInstaller;
import com.app.restaurantpos.assist.Sample_Print;
import com.app.restaurantpos.cpcl.MainActivity;
import com.app.restaurantpos.cpcl.Menu_Activity;
import com.app.restaurantpos.customers.CustomersActivity;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.login.LoginActivity;
import com.app.restaurantpos.orders.OrdersActivity;
import com.app.restaurantpos.pos.ProductCart;
import com.app.restaurantpos.utils.BaseActivity;
import com.app.restaurantpos.utils.settings;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sewoo.jpos.command.CPCLConst;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
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


import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class HomeActivity extends BaseActivity {


    CardView cardCustomers, cardRefresh, cardOrderList,  cardLogout,carPrinter,card_settingss;
    private static final int TIME_DELAY = 2000;
    private static long backPressed;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String userType ,type;
    TextView txtShopName;
    String Json;
    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(HomeActivity.this);
    String d_date,d_name,d_amount, d_id,reference,p_id,p_unit,p_count,p_price,code,bill_id,device_name,itemNo,image,android_id,date_del ,not;
    int counter;
    Double amount;
    int checks=0;
    String api , userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_home);

        cardCustomers = findViewById(R.id.card_customers);
        cardOrderList = findViewById(R.id.card_all_orders);
        cardLogout = findViewById(R.id.card_logout);
        txtShopName = findViewById(R.id.txt_shop_name);
        cardRefresh = findViewById(R.id.card_refresh);
        carPrinter = findViewById(R.id.card_settings);
        card_settingss = findViewById(R.id.card_settingss);




        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        String time=sp.getString("time","");
        getSupportActionBar().setTitle("Last Update :"+time);
        userType = sp.getString(Constant.SP_USER_TYPE, "");
       userName = sp.getString(Constant.SP_PHONE, "");

        api=sp.getString("api","");


        device_name=getDeviceName();
        if (Build.VERSION.SDK_INT >= 23) //Android MarshMellow Version or above
        {
            requestPermission();

        }

//        Admob initialization
        MobileAds.initialize(this, initializationStatus -> {
            //add your action here if need
        });

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();

        String shopName = "True Time solutions";
        txtShopName.setText(shopName);
        databaseAccess.close();



        cardCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CustomersActivity.class);
                startActivity(intent);


            }
        });


        cardOrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
                intent.putExtra("cust_id","");
                startActivity(intent);


            }
        });

        cardRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.load, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                    final AlertDialog alertDialog = dialog.create();
                    new Handler().postDelayed(() -> {
                        alertDialog.dismiss();

                    }, 100000);
                    alertDialog.show();
                    android_id = Settings.Secure.getString(HomeActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    check_device(android_id);
                    getreports();
                    getproduct();
                    getunits();

                    databaseAccess.open();
                    databaseAccess.delete_all_image();
                    databaseAccess.open();
                    ArrayList<HashMap<String, String>> products = databaseAccess.getProducts();
                    for (counter = 0; counter < products.size(); counter++) {
                        itemNo = products.get(counter).get("product_id");
                        get_Image(itemNo);
                    }
                    databaseAccess.close();
                }
                else{
                    Toasty.error(HomeActivity.this, "افحص الإتصال بالانترنت", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(HomeActivity.this);
                dialogBuilder
                        .withTitle(getString(R.string.logout))
                        .withMessage(R.string.want_to_logout_from_app)
                        .withEffect(Slidetop)
                        .withDialogColor("#f29161") //use color code for dialog
                        .withButton1Text(getString(R.string.yes))
                        .withButton2Text(getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editor.putString(Constant.SP_PHONE, "");
                                editor.putString(Constant.SP_PASSWORD, "");
                                editor.putString(Constant.SP_USER_NAME, "");
                                editor.putString(Constant.SP_USER_TYPE, "");
                                editor.apply();

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                dialogBuilder.dismiss();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogBuilder.dismiss();
                            }
                        })
                        .show();




            }
        });
        carPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);


            }
        });
        card_settingss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, settings.class);
                startActivity(intent);


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
            AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.load, null);
            dialog.setView(dialogView);
            dialog.setCancelable(false);
            final AlertDialog alertDialog = dialog.create();
            new Handler().postDelayed(() -> {

                alertDialog.dismiss();
            }, 10000);
            getproduct();
            get_customer();
            getunits();
            get_shop_info();
            getusers();
            getreports();
            databaseAccess.open();
            ArrayList<HashMap<String, String>> orderList = databaseAccess.getOrderList();
            databaseAccess.close();
            databaseAccess.open();

            for (int i = 0; i < orderList.size(); i++) {
                databaseAccess.open();
                ArrayList<HashMap<String, String>> details = databaseAccess.getOrderDetailsList(orderList.get(i).get("invoice_id"));
                d_date = orderList.get(i).get("order_date") + " " + orderList.get(i).get("order_time");
                d_name = orderList.get(i).get("customer_name");
                d_amount = orderList.get(i).get("price");
                d_id = orderList.get(i).get("order_id");
                type = orderList.get(i).get("order_type");
                code = orderList.get(i).get("tax");
                bill_id = orderList.get(i).get("invoice_id");
                date_del = orderList.get(i).get("order_payment_method");
                not = orderList.get(i).get("discount");

                checks=0;
                if (orderList.get(i).get("table_no").equals("0")) {
                    for (int j = 0; j < details.size(); j++) {
                        if (details.get(j).get("product_stauts").equals("0")) {
                            p_id = details.get(j).get("product_name");
                            p_count = details.get(j).get("product_qty");
                            p_unit = details.get(j).get("product_weight");
                            p_price = details.get(j).get("product_price");
                            amount = Double.valueOf(p_count) * Double.valueOf(p_price);
                            add(code, d_date, type, amount, d_name, d_date, p_id, p_unit, p_count, p_price, bill_id, device_name, details.get(j).get("order_details_id"),date_del,not);
                        }
                    }

                        databaseAccess.open();
                        databaseAccess.updateorderSt(orderList.get(i).get("order_id"), "1");


                }
                if(type.equals("2")){
                    String co=""+details.size();
                    if(orderList.get(i).get("bill_status").equals("0")) {
                        get_bill_id(bill_id, co, orderList.get(i).get("order_id"));
                    }
                }
                if(type.equals("12")){
                    String co=""+details.size();
                    if(orderList.get(i).get("bill_status").equals("0")) {
                        get_back_id(bill_id, co, orderList.get(i).get("order_id"));
                    }
                }
            }
            databaseAccess.close();


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
            Toasty.error(HomeActivity.this, "افحص الإتصال بالانترنت", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    //double back press to exit
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


    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                            //write your action if needed
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }
    public void getreports(){
        new Background_rep().execute();
    }
    class Background_rep extends AsyncTask<Void,Void,String> {

        String json_url;
        @Override
        protected void onPreExecute() {

            json_url="http://"+api+".truetime.ps/api/itemsgroups";


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
                loadInto_report(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadInto_report(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        int[] id = new int[jsonArray.length()];
        String[] name = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            id [i] =obj.getInt("GroupID");
            name [i] =obj.getString("GroupName");
        }
        databaseAccess.open();
        databaseAccess.delete_catg();
        for(int i=0;i<name.length;i++){
            databaseAccess.open();
            databaseAccess.addCategory(id[i],name[i]);
        }
        databaseAccess.close();

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
    public void getproduct(){
        new Background_product().execute();
    }
    class Background_product extends AsyncTask<Void,Void,String> {

        String json_url;
        @Override
        protected void onPreExecute() {

            json_url="http://"+api+".truetime.ps/api/itemss";
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
                loadInto_product(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadInto_product(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        int[] id = new int[jsonArray.length()];
        int[] item_no = new int[jsonArray.length()];
        String[] name = new String[jsonArray.length()];
        int[] group_id = new int[jsonArray.length()];
        String[] price = new String[jsonArray.length()];
        String[] unit1 = new String[jsonArray.length()];
        String[] unit1_code = new String[jsonArray.length()];
        String[] unit2 = new String[jsonArray.length()];
        String[] unit2_price = new String[jsonArray.length()];
        String[] unit2_code = new String[jsonArray.length()];
        String[] unit_count = new String[jsonArray.length()];
        String[] balance = new String[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            id [i] =obj.getInt("ItemNo");
            item_no[i]=obj.getInt("id");
            name [i] =obj.getString("ItemName");
            group_id[i]=obj.getInt("GroupID");
            price [i] =obj.getString("price");
            unit1 [i] =obj.getString("MainUnit");
            unit1_code [i] =obj.getString("Barcode1");
            unit2 [i] =obj.getString("unit_id2");
            unit2_price [i] =obj.getString("Price2");
            unit2_code [i] =obj.getString("Barcode2");
            unit_count [i] =obj.getString("UnitsCount");
            balance [i] =obj.getString("Balance");

        }
        databaseAccess.open();
        databaseAccess.delete_product();
        for(int i=0;i<name.length;i++){

            databaseAccess.open();
            databaseAccess.addProduct(id[i],name[i],item_no[i],group_id[i],balance[i],price[i],unit_count [i],"",unit1 [i],  unit1_code [i],unit2 [i], unit2_price [i],unit2_code [i]);
        }
        databaseAccess.close();

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


    }

    public void add(String doc_id, String doc_date,String doc_name ,double doc_amount ,String reference,String InputUser ,String stock_id, String stock_unit , String stock_qnt ,String stock_price , String doc_code , String device_name ,String order_details_id ,String note, String date_deliverd ){
        new  add_bill(doc_id,doc_date,doc_name,doc_amount,reference,InputUser,stock_id,stock_unit,stock_qnt,stock_price,doc_code,device_name , order_details_id , note,  date_deliverd).execute();
    }
    class add_bill extends AsyncTask<Void,Void,String> {

        String json_url;
        double doc_amount;
        String doc_id,  doc_date, doc_name , reference, InputUser , stock_id,  stock_unit ,  stock_qnt , stock_price ,  doc_code ,  device_name , order_details_id , note,  date_deliverd;
        public add_bill(String doc_id, String doc_date,String doc_name ,double doc_amount ,String reference,String InputUser ,String stock_id, String stock_unit , String stock_qnt ,String stock_price , String doc_code , String device_name,String order_details_id ,String date_deliverd, String note){
            this.doc_id=doc_id;
            this.doc_date=doc_date;
            this.doc_name=doc_name;
            this.doc_amount=doc_amount;
            this.reference=reference;
            this.InputUser=InputUser;
            this.stock_id=stock_id;
            this.stock_unit=stock_unit;
            this.stock_qnt=stock_qnt;
            this.stock_price=stock_price;
            this.doc_code=doc_code;
            this.device_name=device_name;
            this.order_details_id=order_details_id;
            this.note=note;
            this.date_deliverd=date_deliverd;

        }
        @Override
        protected void onPreExecute() {

            json_url="http://"+api+".truetime.ps/api/orders/Get?DocID="+doc_id+"&DocDate="+doc_date+"&DocName="+doc_name+"&DocAmount="+doc_amount+"&Referance="+reference+"&InputUser="+userName+"&InputDateTime="+doc_date+"&StockID="+stock_id+"&StockUnit="+stock_unit+"&StockQuantity="+stock_qnt+"&StockPrice="+stock_price+"&DocCode="+doc_code+"&DeviceName="+device_name+"&DeliverDate="+date_deliverd+"&DocNotes="+note;

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
                loadInto_order(string ,order_details_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    private void loadInto_order(String json ,String order_details_id) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject obj = jsonArray.getJSONObject(0);
        if(obj.toString().equals("{\"ColResult\":\"success\"}")){
            databaseAccess.open();
            databaseAccess.updateorder_details_St(order_details_id,"1");
            checks++;

        }


    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public void get_Image(String itemNo){
        new Background_Image(itemNo).execute();
    }
    class Background_Image extends AsyncTask<Void,Void,String> {

        String itemNo;
        public Background_Image(String itemNo){

            this.itemNo=itemNo;
        }
        String json_url;
        @Override
        protected void onPreExecute() {

            json_url="http://"+api+".truetime.ps/api/ItemsImages?ItemNo="+itemNo;

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
                databaseAccess.open();
                databaseAccess.add_image(itemNo,image);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadInto_Image(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            image=obj.getString("ItemImage");

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
            Intent intent = new Intent(HomeActivity.this, activePage.class);
            startActivity(intent);
        }




    }



    public void getunits(){
        new Background_units().execute();
    }
    class Background_units extends AsyncTask<Void,Void,String> {

        String json_url;
        @Override
        protected void onPreExecute() {

            json_url="http://"+api+".truetime.ps/api/units";
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
                loadInto_units(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadInto_units(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        int[] id = new int[jsonArray.length()];
        String[] name = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            id [i] =obj.getInt("id");
            name [i] =obj.getString("name");
        }
        databaseAccess.open();
        databaseAccess.delete_units();
        for(int i=0;i<name.length;i++){
            databaseAccess.open();
            databaseAccess.addunit(id[i],name[i]);
        }
        databaseAccess.close();

    }


    public void get_shop_info(){
        new Background_shop_info().execute();
    }
    class Background_shop_info extends AsyncTask<Void,Void,String> {

        String json_url;
        @Override
        protected void onPreExecute() {

            json_url="http://"+api+".truetime.ps/api/companydata?customer="+api;
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
                    loadInto_shop_info(string);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void loadInto_shop_info(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject obj = jsonArray.getJSONObject(0);
        String companyName=obj.getString("CompanyName");
        String address=obj.getString("CompanyAddress");
        String mobile=obj.getString("CompanyMobile");
        String reg_cade=obj.getString("CompanyVAT");
        String image=obj.getString("CompanyLogo");


        databaseAccess.open();
        databaseAccess.delete_shop();
        databaseAccess.open();
        databaseAccess.add_shop(companyName,address,mobile,reg_cade,image);

    }


    public void get_bill_id(String doc,String count ,String order_id){
        new Background_bill_id(doc,count,order_id).execute();
    }
    class Background_bill_id extends AsyncTask<Void,Void,String> {

        String json_url;
        String doc;
        String count;
        String order_id;


        public Background_bill_id(String doc,String count ,String order_id){
            this.doc=doc;
            this.count=count;
            this.order_id=order_id;
        }
        @Override
        protected void onPreExecute() {

            json_url="http://"+api+".truetime.ps/api/orders/BuildVoucher?DocCode="+doc+"&ItemsCount="+count;


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
                    loadInto_shop_info(string,order_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void loadInto_shop_info(String json,String id) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject obj = jsonArray.getJSONObject(0);
        String no=obj.getString("ColResult");


        if(no.equals("0")){
            databaseAccess.open();
            databaseAccess.updateorderSt(id, "0");
        }
        else {
            databaseAccess.open();
            databaseAccess.updatebillid(id,no);
            Constant.bill_no++;
        }


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
        databaseAccess.open();
        databaseAccess.delete_users();
        for(int i=0;i<id.length;i++){
            databaseAccess.open();
            databaseAccess.addUser(name[i],id[i],password[i],active[i]);
        }
        databaseAccess.close();

    }

    public void get_back_id(String doc,String count ,String order_id){
        new Background_back_id(doc,count,order_id).execute();
    }
    class Background_back_id extends AsyncTask<Void,Void,String> {

        String json_url;
        String doc;
        String count;
        String order_id;


        public Background_back_id(String doc,String count ,String order_id){
            this.doc=doc;
            this.count=count;
            this.order_id=order_id;
        }
        @Override
        protected void onPreExecute() {

            json_url="http://"+api+".truetime.ps/api/orders/BuildReturnVoucher?DocCode="+doc+"&ItemsCount="+count;


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
                    loadInto_back_info(string,order_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void loadInto_back_info(String json,String id) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject obj = jsonArray.getJSONObject(0);
        String no=obj.getString("ColResult");


        if(no.equals("0")){
            databaseAccess.open();
            databaseAccess.updateorderSt(id, "0");
        }
        else {
            databaseAccess.open();
            databaseAccess.updatebillid(id,no);
            Constant.bill_no++;
        }

    }

}

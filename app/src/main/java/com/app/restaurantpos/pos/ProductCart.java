package com.app.restaurantpos.pos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.HomeActivity;
import com.app.restaurantpos.R;
import com.app.restaurantpos.adapter.CartAdapter;
import com.app.restaurantpos.assist.ResourceInstaller;
import com.app.restaurantpos.assist.Sample_Print;
import com.app.restaurantpos.categories.CategoriesActivity;
import com.app.restaurantpos.cpcl.MainActivity;
import com.app.restaurantpos.customers.bills;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.orders.OrderDetailsActivity;
import com.app.restaurantpos.orders.OrdersActivity;
import com.app.restaurantpos.utils.BaseActivity;
import com.sewoo.jpos.command.CPCLConst;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class ProductCart extends BaseActivity {


    boolean noPrint=true;
    CartAdapter productCartAdapter;
    ImageView imgNoProduct;
    Button btnSubmitOrder,btnSubmitOrder2;
    TextView txtNoProduct, txtTotalPrice;
    LinearLayout linearLayout;
    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ProductCart.this);
    String getCustomerId,type;
    List<String> customerNames, orderTypeNames, paymentMethodNames;
    ArrayAdapter<String> customerAdapter, orderTypeAdapter, paymentMethodAdapter;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String d_date,d_name,d_amount, d_id,reference,p_id,p_unit,p_count,p_price,code,bill_id,device_name,itemNo,image ,date_del,not,typee;
    Double amount;
    int checks=0,p_b_id=0;
    String Json;
    SharedPreferences sp;
    String api,getCustomerName ,userName;
    int print=0;
    List<HashMap<String, String>> orderDetailsList;
    double totalPrice, calculatedTotalPrice;
    private BluetoothPort bluetoothPort;
    private Thread btThread;
    private BroadcastReceiver connectDevice;
    double totalCost ;
    String notee;
    TextView datee;

    Sample_Print sample;


    String con_type = "";
    int count = 1;
    int paperType = CPCLConst.LK_CPCL_CONTINUOUS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.product_cart);
        getCustomerName = getIntent().getExtras().getString("customer_name");

        RecyclerView recyclerView = findViewById(R.id.cart_recyclerview);
        imgNoProduct = findViewById(R.id.image_no_product);
        btnSubmitOrder = findViewById(R.id.btn_submit_order);
        btnSubmitOrder2 = findViewById(R.id.btn_submit_order2);

        txtNoProduct = findViewById(R.id.txt_no_product);
        linearLayout = findViewById(R.id.linear_layout);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        getCustomerId = getIntent().getExtras().getString("customer_id");
        type = getIntent().getExtras().getString("type");
        txtNoProduct.setVisibility(View.GONE);
         datee=findViewById(R.id.textView11);
        datee.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date()));
        TextView note =findViewById(R.id.editTextTextMultiLine);
        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userName = sp.getString(Constant.SP_PHONE, "");

        api=sp.getString("api","");

        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);
        datee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ProductCart.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }


        });
        device_name=getDeviceName();

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
                datee.setText(date);
            }
        };

        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


      recyclerView.setHasFixedSize(true);

        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> cartProductList;
        cartProductList = databaseAccess.getCartProduct();


        Log.d("CartSize", "" + cartProductList.size());

        if (cartProductList.isEmpty()) {

            imgNoProduct.setImageResource(R.drawable.empty_cart);
            imgNoProduct.setVisibility(View.VISIBLE);
            txtNoProduct.setVisibility(View.VISIBLE);
            btnSubmitOrder.setVisibility(View.GONE);
            btnSubmitOrder2.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            txtTotalPrice.setVisibility(View.GONE);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            productCartAdapter = new CartAdapter(ProductCart.this, cartProductList, txtTotalPrice, btnSubmitOrder,btnSubmitOrder2, imgNoProduct, txtNoProduct);

            recyclerView.setAdapter(productCartAdapter);


        }

        if(type.equals("2")){
            btnSubmitOrder.setText("حفظ الفاتورة");
             btnSubmitOrder2.setText("حفظ و طباعة الفاتورة");
        }
        if(type.equals("12")){
            btnSubmitOrder.setText("حفظ ");
            btnSubmitOrder2.setText("حفظ و طباعة ");
        }
        databaseAccess.open();
        totalCost = databaseAccess.getTotalPrice();


        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseAccess.open();
                 totalCost = databaseAccess.getTotalPrice();
                 notee=note.getText().toString();

                if(notee==null){
                    notee=" ";
                }
                proceedOrder(type, datee.getText().toString(), getCustomerId, Constant.no, notee , totalCost, "0");

            }
        });
        btnSubmitOrder2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constant.btDev!=null) {
                    print=1;
                    databaseAccess.open();
                     totalCost = databaseAccess.getTotalPrice();
                     notee = note.getText().toString();
                    if (notee == null) {
                        notee = " ";
                    }
                    proceedOrder(type, datee.getText().toString(), getCustomerId, Constant.no, notee, totalCost, "0");

                }
                else{
                    Intent intent = new Intent(ProductCart.this, MainActivity.class);
                    startActivity(intent);
                    Toasty.error(ProductCart.this, "يرجى الاتصال بالطابعة", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    public void proceedOrder(String type, String paymentMethod, String customerName, int tax, String discount, double price, String tableNo) {


        databaseAccess = DatabaseAccess.getInstance(ProductCart.this);
        databaseAccess.open();

        int itemCount = databaseAccess.getCartItemCount();

        if (itemCount > 0) {

            databaseAccess.open();
            //get data from local database
            final List<HashMap<String, String>> lines;
            lines = databaseAccess.getCartProduct();

            if (lines.isEmpty()) {
                Toasty.error(ProductCart.this, R.string.no_product_found, Toast.LENGTH_SHORT).show();
            } else {

                //get current timestamp
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                //H denote 24 hours and h denote 12 hour hour format
                String currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()); //HH:mm:ss a

                //timestamp use for invoice id for unique
                Long tsLong = System.currentTimeMillis() / 1000;
                String timeStamp = tsLong.toString();
                Log.d("Time", timeStamp);

                final JSONObject obj = new JSONObject();
                try {


                    obj.put("order_date", currentDate);
                    obj.put("order_time", currentTime);
                    obj.put("order_type", type);
                    obj.put("order_payment_method", paymentMethod);
                    obj.put("customer_name", customerName);

                    obj.put("tax", Constant.no++);
                    obj.put("discount", discount);

                    obj.put("price", price);

                    obj.put("table_no", tableNo);


                    JSONArray array = new JSONArray();


                    for (int i = 0; i < lines.size(); i++) {

                        databaseAccess.open();
                        String productId = lines.get(i).get("product_id");
                        String productName = databaseAccess.getProductName(productId);

                        databaseAccess.open();
                        String productWeightUnit = lines.get(i).get("product_weight_unit");
                        String weightUnit = databaseAccess.getWeightUnitName(productWeightUnit);


                        databaseAccess.open();
                        String productImage = databaseAccess.getProductImage(productId);

                        JSONObject objp = new JSONObject();
                        objp.put("product_name", productId);
                        objp.put("product_weight",  lines.get(i).get("product_weight_unit"));
                        objp.put("product_qty", lines.get(i).get("product_qty"));
                        objp.put("product_price", lines.get(i).get("product_price"));
                        objp.put("product_image", productImage);
                        objp.put("product_order_date", currentDate);
                        array.put(objp);
                    }
                    obj.put("lines", array);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                saveOrderInOfflineDb(obj);


            }

        } else {
            Toasty.error(ProductCart.this, R.string.no_product_in_cart, Toast.LENGTH_SHORT).show();
        }
    }


    //for save data in offline
    private void saveOrderInOfflineDb(final JSONObject obj) {

        //get current timestamp
        Long tsLong = System.currentTimeMillis() / 1000;
        String timeStamp = randomString();

        databaseAccess = DatabaseAccess.getInstance(ProductCart.this);

        databaseAccess.open();
        /*
        timestamp used for un sync order and make it unique id
         */
        databaseAccess.insertOrder(timeStamp, obj);
        if(isConnected()) {
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
                typee = orderList.get(i).get("order_type");
                code = orderList.get(i).get("tax");
                bill_id = orderList.get(i).get("invoice_id");
                date_del = orderList.get(i).get("order_payment_method");
                not = orderList.get(i).get("discount");
                checks = 0;
                if (orderList.get(i).get("table_no").equals("0")) {
                    for (int j = 0; j < details.size(); j++) {
                        if (details.get(j).get("product_stauts").equals("0")) {
                            p_id = details.get(j).get("product_name");
                            p_count = details.get(j).get("product_qty");
                            p_unit =  details.get(j).get("product_weight");
                            p_price = details.get(j).get("product_price");
                            amount = Double.valueOf(p_count) * Double.valueOf(p_price);
                            add(code, d_date, typee, amount, d_name, d_date, p_id, p_unit, p_count, p_price, bill_id, device_name, details.get(j).get("order_details_id"),date_del,not);
                        }
                    }

                    databaseAccess.open();
                    databaseAccess.updateorderSt(orderList.get(i).get("order_id"), "1");
                    Toasty.success(ProductCart.this, "تم الترحيل بنجاح", Toast.LENGTH_SHORT).show();
                    if(typee.equals("2")){
                        String co=""+details.size();
                        if(orderList.get(i).get("bill_status").equals("0")) {
                            get_bill_id(bill_id, co, orderList.get(i).get("order_id"));
                        }
                    }
                    if(typee.equals("12")){
                        String co=""+details.size();
                        if(orderList.get(i).get("bill_status").equals("0")) {
                            get_back_id(bill_id, co, orderList.get(i).get("order_id"));
                        }
                    }
                    if(typee.equals("11") && print==1){
                        try {
                            databaseAccess.open();
                            //get data from local database
                            orderDetailsList = databaseAccess.getOrderDetailsList(timeStamp);
                            databaseAccess.open();
                            totalPrice = databaseAccess.totalOrderPrice(timeStamp);
                            double getTax = 0.0;
                            double getDiscount = 0.0;
                            calculatedTotalPrice = totalPrice + getTax - getDiscount;
                            sample=new Sample_Print();
                            sample.Print_recipte3(count, paperType, orderDetailsList, ProductCart.this, calculatedTotalPrice, p_b_id, type);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toasty.error(ProductCart.this, "يرجى الاتصال ", Toast.LENGTH_SHORT).show();

                        }

                    }


                }

            }
            databaseAccess.close();

        }
        else{
            try {
                databaseAccess.open();
                //get data from local database
                orderDetailsList = databaseAccess.getOrderDetailsList(timeStamp);
                databaseAccess.open();
                totalPrice = databaseAccess.totalOrderPrice(timeStamp);
                double getTax = 0.0;
                double getDiscount = 0.0;
                calculatedTotalPrice = totalPrice + getTax - getDiscount;

                sample=new Sample_Print();
                sample.Print_recipte3(count, paperType, orderDetailsList, ProductCart.this, calculatedTotalPrice, p_b_id, type);

            } catch (IOException e) {
                e.printStackTrace();
                Toasty.error(ProductCart.this, "يرجى الاتصال ", Toast.LENGTH_SHORT).show();

            }
            Toasty.error(ProductCart.this, "افحص الإتصال بالانترنت", Toast.LENGTH_SHORT).show();
        }

        databaseAccess.open();


        //get data from local database
        orderDetailsList = databaseAccess.getOrderDetailsList(timeStamp);
        databaseAccess.open();
        totalPrice = databaseAccess.totalOrderPrice(timeStamp);
        double getTax = 0.0;
        double getDiscount = 0.0;

        calculatedTotalPrice = totalPrice + getTax - getDiscount;
            dialogSuccess();


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

    public void dialogSuccess() {



        AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        ImageButton dialogBtnCloseDialog = dialogView.findViewById(R.id.btn_close_dialog);
        TextView name = dialogView.findViewById(R.id.name);
        TextView text = dialogView.findViewById(R.id.text_succ);
        if(type.equals("2")){
            name.setText("إضافة الفاتورة");
            text.setText("تم إضافة الفاتورة بنجاح");
        }
        AlertDialog alertDialogSuccess = dialog.create();

        dialogBtnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogSuccess.dismiss();

                Intent intent = new Intent(ProductCart.this, OrdersActivity.class);
                intent.putExtra("cust_id",getCustomerId);
                startActivity(intent);
                finish();

            }
        });

        alertDialogSuccess.show();


    }


    //dialog for taking otp code
    public void dialog() {


        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();
        String shopCurrency = shopData.get(0).get("shop_currency");
        String tax = shopData.get(0).get("tax");

        double getTax = Double.parseDouble(tax);

        AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        final Button dialogBtnSubmit = dialogView.findViewById(R.id.btn_submit);
        final ImageButton dialogBtnClose = dialogView.findViewById(R.id.btn_close);
        final TextView dialogOrderPaymentMethod = dialogView.findViewById(R.id.dialog_order_status);
        final TextView dialogOrderType = dialogView.findViewById(R.id.dialog_order_type);
        final TextView dialogCustomer = dialogView.findViewById(R.id.dialog_customer);
        final TextView dialogTxtTotal = dialogView.findViewById(R.id.dialog_txt_total);
        final TextView dialogTxtTotalTax = dialogView.findViewById(R.id.dialog_txt_total_tax);
        final TextView dialogTxtLevelTax = dialogView.findViewById(R.id.dialog_level_tax);
        final TextView dialogTxtTotalCost = dialogView.findViewById(R.id.dialog_txt_total_cost);
        final EditText dialogEtxtDiscount = dialogView.findViewById(R.id.etxt_dialog_discount);
        final EditText dialogEtxtDialogTableNo = dialogView.findViewById(R.id.etxt_dialog_table_no);


        final ImageButton dialogImgCustomer = dialogView.findViewById(R.id.img_select_customer);
        final ImageButton dialogImgOrderPaymentMethod = dialogView.findViewById(R.id.img_order_payment_method);
        final ImageButton dialogImgOrderType = dialogView.findViewById(R.id.img_order_type);


        dialogTxtLevelTax.setText(getString(R.string.total_tax) + "( " + tax + "%) : ");
        double totalCost = CartAdapter.totalPrice;
        dialogTxtTotal.setText(shopCurrency + totalCost);

        //double calculatedTax = (totalCost * getTax) / 100.0;
        double calculatedTax = 0.0;
        dialogTxtTotalTax.setText(shopCurrency + calculatedTax);


        double discount = 0;
        double calculatedTotalCost = totalCost + calculatedTax - discount;
        dialogTxtTotalCost.setText(shopCurrency + calculatedTotalCost);


        dialogEtxtDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("data", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                double discount = 0;
                String getDiscount = s.toString();
                if (!getDiscount.isEmpty()) {
                    double calculatedTotalCost = totalCost + calculatedTax;
                    discount = Double.parseDouble(getDiscount);
                    if (discount > calculatedTotalCost) {
                        dialogEtxtDiscount.setError(getString(R.string.discount_cant_be_greater_than_total_price));
                        dialogEtxtDiscount.requestFocus();

                        dialogBtnSubmit.setVisibility(View.INVISIBLE);

                    } else {

                        dialogBtnSubmit.setVisibility(View.VISIBLE);
                        calculatedTotalCost = totalCost + calculatedTax - discount;
                        dialogTxtTotalCost.setText(shopCurrency + calculatedTotalCost);
                    }
                } else {

                    double calculatedTotalCost = totalCost + calculatedTax - discount;
                    dialogTxtTotalCost.setText(shopCurrency + calculatedTotalCost);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("data", s.toString());
            }
        });


        customerNames = new ArrayList<>();


        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> customer;
        customer = databaseAccess.getCustomers();

        for (int i = 0; i < customer.size(); i++) {

            // Get the ID of selected Country
            customerNames.add(customer.get(i).get("customer_name"));

        }


        orderTypeNames = new ArrayList<>();
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> orderType;
        orderType = databaseAccess.getOrderType();

        for (int i = 0; i < orderType.size(); i++) {

            // Get the ID of selected Country
            orderTypeNames.add(orderType.get(i).get("order_type_name"));

        }


        //payment methods
        paymentMethodNames = new ArrayList<>();
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> paymentMethod;
        paymentMethod = databaseAccess.getPaymentMethod();

        for (int i = 0; i < paymentMethod.size(); i++) {

            // Get the ID of selected Country
            paymentMethodNames.add(paymentMethod.get(i).get("payment_method_name"));

        }


        dialogImgOrderPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paymentMethodAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
                paymentMethodAdapter.addAll(paymentMethodNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);


                dialogTitle.setText(R.string.select_payment_method);
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(paymentMethodAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        paymentMethodAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        String selectedItem = paymentMethodAdapter.getItem(position);
                        dialogOrderPaymentMethod.setText(selectedItem);


                    }
                });
            }


        });


        dialogImgOrderType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ProductCart.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }


        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                dialogOrderType.setText(date);
            }
        };


        dialogImgCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
                customerAdapter.addAll(customerNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialogButton = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialogInput = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialogList = (ListView) dialogView.findViewById(R.id.dialog_list);

                dialogTitle.setText(R.string.select_customer);
                dialogList.setVerticalScrollBarEnabled(true);
                dialogList.setAdapter(customerAdapter);

                dialogInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.d("data", s.toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        customerAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.d("data", s.toString());
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        String selectedItem = customerAdapter.getItem(position);


                        dialogCustomer.setText(selectedItem);


                    }
                });
            }
        });


        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();


        dialogBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String orderType = dialogOrderType.getText().toString().trim();
                String orderPaymentMethod = dialogOrderPaymentMethod.getText().toString().trim();
                String customerName = dialogCustomer.getText().toString().trim();
                String discount = dialogEtxtDiscount.getText().toString().trim();
                if (discount.isEmpty()) {
                    discount = "0.00";
                }
                String tableNo = dialogEtxtDialogTableNo.getText().toString().trim();

                if (tableNo.isEmpty()) {
                    tableNo = "N/A";
                }

                proceedOrder(orderType, orderPaymentMethod, customerName, (int) calculatedTax, discount, calculatedTotalCost, tableNo);


                alertDialog.dismiss();
            }


        });


        dialogBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });


    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_cart_button3) {
            Intent intent1 = new Intent(ProductCart.this, HomeActivity.class);
            startActivity(intent1);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static Random RANDOM = new Random();
    public static String randomString() {
        StringBuilder sb = new StringBuilder(13);

        for (int i = 0; i < 13; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }

    public void add(String doc_id, String doc_date,String doc_name ,double doc_amount ,String reference,String InputUser ,String stock_id, String stock_unit , String stock_qnt ,String stock_price , String doc_code , String device_name ,String order_details_id ,String date_del,String not){
        new add_bill(doc_id,doc_date,doc_name,doc_amount,reference,InputUser,stock_id,stock_unit,stock_qnt,stock_price,doc_code,device_name , order_details_id, date_del, not).execute();
    }
    class add_bill extends AsyncTask<Void,Void,String> {

        String json_url;
        double doc_amount;
        String doc_id,  doc_date, doc_name , reference, InputUser , stock_id,  stock_unit ,  stock_qnt , stock_price ,  doc_code ,  device_name , order_details_id, date_del, not;
        public add_bill(String doc_id, String doc_date,String doc_name ,double doc_amount ,String reference,String InputUser ,String stock_id, String stock_unit , String stock_qnt ,String stock_price , String doc_code , String device_name,String order_details_id,String date_del,String not){
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
            this.date_del=date_del;
            this.not=not;

        }
        @Override
        protected void onPreExecute() {

            json_url="http://"+api+".truetime.ps/api/orders/Get?DocID="+doc_id+"&DocDate="+doc_date+"&DocName="+doc_name+"&DocAmount="+doc_amount+"&Referance="+reference+"&InputUser="+userName+"&InputDateTime="+doc_date+"&StockID="+stock_id+"&StockUnit="+stock_unit+"&StockQuantity="+stock_qnt+"&StockPrice="+stock_price+"&DocCode="+doc_code+"&DeviceName="+device_name+"&DeliverDate="+date_del+"&DocNotes="+not;
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
                   loadInto_order(string, order_details_id);
               } catch (JSONException e) {
                   e.printStackTrace();
               }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.export_suppliers_menu, menu);

        MenuItem item = menu.findItem(R.id.name_set);
        item.setTitle(getCustomerName);

        return super.onCreateOptionsMenu(menu);
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
        String no = obj.getString("ColResult");


        if (no.equals("0")) {
            databaseAccess.open();
            databaseAccess.updateorderSt(id, "0");
        } else {
            databaseAccess.open();
            databaseAccess.updatebillid(id, no);
            p_b_id = Integer.parseInt(no);
            Constant.bill_no++;

            if (print == 1) {
                ArrayAdapter<String> adapter_media;

                Sample_Print sample;


                String con_type = "";
                int count = 1;
                int paperType = CPCLConst.LK_CPCL_CONTINUOUS;


                String[] str_media = {"Gap Paper", "Black Mark Paper", "Continuous Paper"};
                ResourceInstaller ri = new ResourceInstaller();
                ri.copyAssets(getAssets(), "temp");
                con_type = "BlueTooth";
                sample = new Sample_Print();

                int re_val = 0;
                int input_count = 1;
                if (p_b_id > 0) {
                    /*
                    java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss");
                    java.util.Date date1 = null;
                    java.util.Date date2 = null;
                    try {
                        date1 = df.parse(Constant.ttime);
                        date2 = df.parse(new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH).format(new Date()));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long diff = date2.getTime() - date1.getTime();
                    int timeInSeconds = (int) (diff / 1000);
                    int minutes = timeInSeconds / 60;

                    if (minutes > 4 || minutes < -4) {
                        try {
                            btConn(Constant.btDev);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        try {
                            sample.Print_recipte(count, paperType, orderDetailsList, ProductCart.this, calculatedTotalPrice, p_b_id, type);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toasty.error(ProductCart.this, "يرجى الاتصال ", Toast.LENGTH_SHORT).show();

                        }
                    }

                     */
                    try {
                        sample.Print_recipte3(count, paperType, orderDetailsList, ProductCart.this, calculatedTotalPrice, p_b_id, type);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toasty.error(ProductCart.this, "يرجى الاتصال ", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toasty.error(ProductCart.this, "لم يتم ترحيل الفاتورة", Toast.LENGTH_SHORT).show();


                }
            }
        }
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
            p_b_id=Integer.parseInt(no);
            Constant.bill_no++;

            if(print==1) {
                    ArrayAdapter<String> adapter_media;

                    Sample_Print sample;


                    String con_type = "";
                    int count = 1;
                    int paperType = CPCLConst.LK_CPCL_CONTINUOUS;


                    String[] str_media = {"Gap Paper", "Black Mark Paper", "Continuous Paper"};
                    ResourceInstaller ri = new ResourceInstaller();
                    ri.copyAssets(getAssets(), "temp");
                    con_type = "BlueTooth";
                    sample = new Sample_Print();

                    int re_val = 0;
                    int input_count = 1;

                        if(p_b_id>0) {
                            /*
                            java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss");
                            java.util.Date date1 = null;
                            java.util.Date date2=null;
                            try {
                                date1 = df.parse(Constant.ttime);
                                date2 = df.parse(new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH).format(new Date()));

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long diff = date2.getTime() - date1.getTime();
                            int timeInSeconds = (int) (diff / 1000);
                            int minutes = timeInSeconds / 60;

                            if(minutes>4  || minutes<-4){
                                try {
                                    btConn(Constant.btDev);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                try {
                                    sample.Print_recipte(count, paperType, orderDetailsList, ProductCart.this, calculatedTotalPrice, p_b_id, type);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toasty.error(ProductCart.this, "يرجى الاتصال ", Toast.LENGTH_SHORT).show();

                                }
                            }

                             */
                            try {
                                sample.Print_recipte3(count, paperType, orderDetailsList, ProductCart.this, calculatedTotalPrice, p_b_id, type);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toasty.error(ProductCart.this, "يرجى الاتصال ", Toast.LENGTH_SHORT).show();

                            }

                        }
                        else{
                            Toasty.error(ProductCart.this, "لم يتم ترحيل الفاتورة", Toast.LENGTH_SHORT).show();

                        }



            }
        }

    }



    public void btConn(final BluetoothDevice btDev) throws IOException
    {
        new connBT().execute(btDev);
    }

    class connBT extends AsyncTask<BluetoothDevice, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(ProductCart.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(ProductCart.this);

        BluetoothDevice btDev;
        @Override
        protected void onPreExecute()
        {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Connecting Device...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params)
        {
            Integer retVal = null;
            btDev=params[0];
            try
            {
                bluetoothPort.connect(params[0]);

                retVal = Integer.valueOf(0);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                retVal = Integer.valueOf(-1);
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if(dialog.isShowing())
                dialog.dismiss();

            if(result.intValue() == 0)	// Connection success.
            {
                RequestHandler rh = new RequestHandler();
                btThread = new Thread(rh);
                btThread.start();

                registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));

                Constant.ttime=new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH).format(new Date());

                try {
                    sample.Print_recipte(count, paperType, orderDetailsList, ProductCart.this, calculatedTotalPrice, p_b_id, type);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*Intent in = new Intent(Bluetooth_Activity.this, HomeActivity.class);
               in.putExtra("Connection", "BlueTooth");
               startActivity(in);*/


            }
            else	// Connection failed.
            {
                alert
                        .setTitle("Error")
                        .setMessage("Failed to connect Bluetooth device.")
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                        .show();
                if(noPrint){
                    try {
                        btConn(Constant.btDev);
                        noPrint=false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            super.onPostExecute(result);
        }
    }


}


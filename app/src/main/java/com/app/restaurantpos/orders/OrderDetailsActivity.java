package com.app.restaurantpos.orders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.HomeActivity;
import com.app.restaurantpos.R;
import com.app.restaurantpos.adapter.OrderDetailsAdapter;
import com.app.restaurantpos.assist.ResourceInstaller;
import com.app.restaurantpos.assist.Sample_Print;
import com.app.restaurantpos.cpcl.Bluetooth_Activity;
import com.app.restaurantpos.cpcl.MainActivity;
import com.app.restaurantpos.cpcl.Menu_Activity;
import com.app.restaurantpos.customers.bills;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.pdf_report.BarCodeEncoder;
import com.app.restaurantpos.pdf_report.PDFTemplate;
import com.app.restaurantpos.utils.BaseActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.sewoo.jpos.command.CPCLConst;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import es.dmoral.toasty.Toasty;

import static com.google.ads.AdRequest.LOGTAG;

public class OrderDetailsActivity extends BaseActivity {


    ImageView imgNoProduct;
    TextView txtNoProducts, txtTotalPrice, txtTax, txtDiscount, txtTotalCost;
    String orderId, orderDate, orderTime, customerName, tax, discount, tableNo;
    double totalPrice, calculatedTotalPrice;
    SharedPreferences sp;
    Button btnPdfReceipt, btnKitchenReceipt;
    String bill_id="0",order_type;
    private BluetoothPort bluetoothPort;
    private Thread btThread;
    private BroadcastReceiver connectDevice;
    //how many headers or column you need, add here by using ,
    //headers and get clients para meter must be equal
    private String[] header = {"الأصناف", "السعر"};
    Sample_Print sample;
    private String[] headerKitchen = {"Item", "Qty"};
    int count;
    int paperType;
    String longText, shortText, userName;
    List<HashMap<String, String>> orderDetailsList;
    private PDFTemplate templatePDF;
    String currency;
    Bitmap bm = null;

    private Vector<BluetoothDevice> remoteDevices;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver discoveryResult;
    private BroadcastReceiver searchFinish;
    private BroadcastReceiver searchStart;
    boolean searchflags;
    private static final int REQUEST_ENABLE_BT = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        txtTax = findViewById(R.id.txt_tax);
        txtDiscount = findViewById(R.id.txt_discount);
        txtTotalCost = findViewById(R.id.txt_total_cost);
        btnPdfReceipt = findViewById(R.id.btn_pdf_receipt);
        btnKitchenReceipt = findViewById(R.id.btn_kitchen_receipt);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        userName = sp.getString(Constant.SP_USER_NAME, "N/A");

        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);   //not using mac address filtering


        txtNoProducts = findViewById(R.id.txt_no_products);
        orderId = getIntent().getExtras().getString("order_id");
        orderDate = getIntent().getExtras().getString("order_date");
        orderTime = getIntent().getExtras().getString("order_time");
        customerName = getIntent().getExtras().getString("customer_name");
        tax = "0";
        discount = getIntent().getExtras().getString("discount");
        tableNo = getIntent().getExtras().getString("table_no");
        bill_id = getIntent().getExtras().getString("bill_id");
        order_type= getIntent().getExtras().getString("order_type");
        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.order_details);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);


        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderDetailsActivity.this);
        databaseAccess.open();


        //get data from local database

        orderDetailsList = databaseAccess.getOrderDetailsList(orderId);

        if (orderDetailsList.isEmpty()) {
            //if no data in local db, then load data from server
            Toasty.info(OrderDetailsActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
        } else {
            OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(OrderDetailsActivity.this, orderDetailsList);

            recyclerView.setAdapter(orderDetailsAdapter);


        }


        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();

        String shopName = "True Time Solutions";
        String shopContact = "0592978425";
        String shopEmail ="";
        String shopAddress = "ramallah palestine";
        currency = shopData.get(0).get("shop_currency");


        txtTax.setText(getString(R.string.total_tax) + " : " + currency + tax);
        txtDiscount.setText(getString(R.string.discount) + " : " + currency + discount);


        databaseAccess.open();
        totalPrice = databaseAccess.totalOrderPrice(orderId);
        double getTax = 0.0;
        double getDiscount = 0.0;

        calculatedTotalPrice = totalPrice + getTax - getDiscount;
        txtTotalPrice.setText(getString(R.string.sub_total)  + totalPrice);
        txtTotalCost.setText(getString(R.string.total_price)  + calculatedTotalPrice);


        //for pdf report
        databaseAccess.open();
        String customerN=databaseAccess.getCustomerNAme(customerName);
        shortText = "اسم الزبون : " + customerN;
        longText = " Have a nice day ";
        templatePDF = new PDFTemplate(getApplicationContext());


        BarCodeEncoder qrCodeEncoder = new BarCodeEncoder();
        try {
            bm = qrCodeEncoder.encodeAsBitmap(orderId, BarcodeFormat.CODE_128, 600, 300);
        } catch (WriterException e) {
            Log.d("Data", e.toString());
        }


        btnPdfReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ArrayAdapter<String> adapter_media;




                    String con_type = "";
                     count = 1;
                     paperType = CPCLConst.LK_CPCL_CONTINUOUS;


                    String[] str_media = {"Gap Paper", "Black Mark Paper", "Continuous Paper"};
                    ResourceInstaller ri = new ResourceInstaller();
                    ri.copyAssets(getAssets(), "temp");
                    con_type = "BlueTooth";
                    sample = new Sample_Print();

                    int re_val = 0;
                    int input_count = 1;
                    if(Constant.btDev!=null) {
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
                                sample.Print_recipte2(count, paperType, orderDetailsList, OrderDetailsActivity.this, calculatedTotalPrice, Integer.parseInt(bill_id), order_type);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toasty.error(OrderDetailsActivity.this, "يرجى الاتصال ", Toast.LENGTH_SHORT).show();

                            }
                        }

                         */

                        try {
                            sample.Print_recipte2(count, paperType, orderDetailsList, OrderDetailsActivity.this, calculatedTotalPrice, Integer.parseInt(bill_id), order_type);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toasty.error(OrderDetailsActivity.this, "يرجى الاتصال ", Toast.LENGTH_SHORT).show();

                        }


                    }
                    else{
                        Toasty.error(OrderDetailsActivity.this, "يرجى الاتصال بالطابعة", Toast.LENGTH_SHORT).show();

                    }



                /*templatePDF.openDocument();
                templatePDF.addMetaData(Constant.ORDER_RECEIPT, Constant.ORDER_RECEIPT, "Restaurant POS");
                templatePDF.addTitle(shopName + "\nطلبية الزبون", shopAddress + "\n Email: " + shopEmail + "\n  " + shopContact + "\nInvoice ID:" + orderId, orderTime + " " + orderDate + "\nServed By: " + userName );
                templatePDF.addParagraph(shortText);

                templatePDF.createTable(header, getPDFReceipt());

                templatePDF.addRightParagraph(longText);

                templatePDF.closeDocument();
                templatePDF.viewPDF();*/

            }
        });


        btnKitchenReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                templatePDF.openDocument();
                templatePDF.addMetaData("Order Receipt", "Order Receipt", "Smart POS");

                templatePDF.addTitle(shopName + "\nKitchen Receipt", shopAddress + "\n Email: " + shopEmail + "\nContact: " + shopContact + "\nInvoice ID:" + orderId, orderTime + " " + orderDate + "\nServed By: " + userName + "\nTable Number:" + tableNo);

                templatePDF.addParagraph(shortText);

                templatePDF.createTable(headerKitchen, getKitchenReceipt());
                templatePDF.addImage(bm);

                templatePDF.closeDocument();
                templatePDF.viewPDF();


            }
        });
    }


    //for pdf
    private ArrayList<String[]> getPDFReceipt() {
        ArrayList<String[]> rows = new ArrayList<>();

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderDetailsActivity.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = databaseAccess.getOrderDetailsList(orderId);
        String name, price, qty, weight;
        double costTotal;

        for (int i = 0; i < orderDetailsList.size(); i++) {
            databaseAccess.open();
            name = databaseAccess.getProductName(orderDetailsList.get(i).get("product_name"));
            price = orderDetailsList.get(i).get("product_price");
            qty = orderDetailsList.get(i).get("product_qty");
            weight = orderDetailsList.get(i).get("product_weight");

            costTotal = Integer.parseInt(qty) * Double.parseDouble(price);

            rows.add(new String[]{name + "\n" + weight + "\n" + "(" + qty + "x"  + price + ")", String.valueOf(costTotal)});


        }
        rows.add(new String[]{"..........................................", ".................................."});
        rows.add(new String[]{"Sub Total: ", String.valueOf(totalPrice)});
        rows.add(new String[]{"..........................................", ".................................."});
        rows.add(new String[]{"Total Price: ", String.valueOf(calculatedTotalPrice)});

//        you can add more row above format
        return rows;
    }


    //for pdf
    private ArrayList<String[]> getKitchenReceipt() {
        ArrayList<String[]> rows = new ArrayList<>();

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderDetailsActivity.this);
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = databaseAccess.getOrderDetailsList(orderId);
        String name, qty, weight;


        for (int i = 0; i < orderDetailsList.size(); i++) {
            name = orderDetailsList.get(i).get("product_name");
            qty = orderDetailsList.get(i).get("product_qty");
            weight = orderDetailsList.get(i).get("product_weight");
            rows.add(new String[]{name + "\n" + weight, qty});

        }

//        you can add more row above format
        return rows;
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void btConn(final BluetoothDevice btDev) throws IOException
    {
        new connBT().execute(btDev);
    }

    class connBT extends AsyncTask<BluetoothDevice, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(OrderDetailsActivity.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(OrderDetailsActivity.this);

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

                /*Intent in = new Intent(Bluetooth_Activity.this, HomeActivity.class);
               in.putExtra("Connection", "BlueTooth");
               startActivity(in);*/

                try {
                    sample.Print_recipte2(count, paperType, orderDetailsList, OrderDetailsActivity.this, calculatedTotalPrice, Integer.parseInt(bill_id), order_type);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toasty.error(OrderDetailsActivity.this, "يرجى الاتصال ", Toast.LENGTH_SHORT).show();

                }

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
            }
            super.onPostExecute(result);
        }
    }



}


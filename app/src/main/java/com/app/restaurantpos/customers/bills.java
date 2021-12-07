package com.app.restaurantpos.customers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.HomeActivity;
import com.app.restaurantpos.R;
import com.app.restaurantpos.activePage;
import com.app.restaurantpos.adapter.CartAdapter;
import com.app.restaurantpos.adapter.CustomListAdapterUpdateRows;
import com.app.restaurantpos.assist.ResourceInstaller;
import com.app.restaurantpos.assist.Sample_Print;
import com.app.restaurantpos.cpcl.MainActivity;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.orders.OrderDetailsActivity;
import com.app.restaurantpos.orders.OrdersActivity;
import com.app.restaurantpos.pos.ProductCart;
import com.app.restaurantpos.utils.BaseActivity;
import com.app.restaurantpos.utils.checksModels;
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

import es.dmoral.toasty.Toasty;

public class bills extends BaseActivity {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(bills.this);
    ArrayAdapter<String> customerAdapter;
    List<String> curns ;
    String getCustomerId, getCustomerName;
    ArrayList<checksModels> users=new ArrayList<>();
    static CustomListAdapterUpdateRows updateAdapter;
    SharedPreferences sp;
    int id_bill;
    boolean do_print=false;
    String Json ,api,phone;
     TextView dialogTxtTotalCost;
    EditText note;
    private BluetoothPort bluetoothPort;
    private Thread btThread;
    private BroadcastReceiver connectDevice;
    String entrey;
    int count;
    int paperType;
    Sample_Print sample;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_bills);
        getSupportActionBar().hide();
        getCustomerId = getIntent().getExtras().getString("cus_id");
        getCustomerName = getIntent().getExtras().getString("cus_name");
        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        api=sp.getString("api","");
        phone = sp.getString(Constant.SP_PHONE, "");

        final Button dialogBtnSubmit = findViewById(R.id.btn_submit1);
        final Button dialogBtn = findViewById(R.id.btn_submit_no);
        final Button dialogBtnPrint = findViewById(R.id.btn_submit_print);
        final ImageButton dialogBtnClose = findViewById(R.id.btn_close1);
        final TextView dialogOrderPaymentMethod = findViewById(R.id.dialog_order_status1);
        final TextView dialogOrderType = findViewById(R.id.dialog_order_type1);
        final TextView dialogCustomer = findViewById(R.id.dialog_customer1);
        final EditText dialogTxtTotal = findViewById(R.id.dialog_txt_total1);
        note = findViewById(R.id.note_bill);
        final TextView dialogTxtTotalTax = findViewById(R.id.dialog_txt_total_tax1);
        dialogTxtTotalCost = findViewById(R.id.dialog_txt_total_cost1);
        final ImageButton dialogImgCustomer = findViewById(R.id.img_select_customer1);
        final ImageButton dialogImgOrderType =findViewById(R.id.img_order_type1);
        final TextView cust_name =findViewById(R.id.textView2);
        final Button add =findViewById(R.id.button_add);
        final Switch sw=findViewById(R.id.switch1);
        final ListView list=findViewById(R.id.list);
        final LinearLayout lin=findViewById(R.id.liner);
        cust_name.setText(getCustomerName);
        list.setVisibility(View.GONE);
        add.setVisibility(View.GONE);
        dialogCustomer.setText("العملة  :   شيكل ");
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        dialogOrderType.setText("  التاريخ:  "+currentDate);

        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true) {
                    databaseAccess.open();
                    list.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);
                    users = databaseAccess.getAllChecks();
                    updateAdapter = new CustomListAdapterUpdateRows(bills.this, users);
                    list.setAdapter(updateAdapter);
                    databaseAccess.close();
                }
                else {
                    list.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                }
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSuccess();
                databaseAccess.open();
                users = databaseAccess.getAllChecks();
                updateAdapter = new CustomListAdapterUpdateRows(bills.this, users);
                list.setAdapter(updateAdapter);
                databaseAccess.close();
                lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150*users.size()));

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
                        bills.this,
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

        curns = new ArrayList<>();
        curns.add("دينار");
        curns.add("شيكل");
        curns.add("دولار");
        dialogImgCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerAdapter = new ArrayAdapter<>(bills.this, android.R.layout.simple_list_item_1);
                customerAdapter.addAll(curns);

                AlertDialog.Builder dialog = new AlertDialog.Builder(bills.this);
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

        dialogTxtTotal.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                count();

            }



        });
        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isConnected()) {
                    if(!dialogTxtTotal.getText().toString().equals("00")) {
                        databaseAccess.open();
                        databaseAccess.addbills(getCustomerId,"1",currentDate,dialogTxtTotalCost.getText().toString(),note.getText().toString());
                        databaseAccess.open();
                        ArrayList<HashMap<String, String>> bills=databaseAccess.getbills();
                        id_bill=Integer.parseInt(bills.get(bills.size()-1).get("id"));
                       add_bill(dialogTxtTotalCost.getText().toString(), note.getText().toString());
                    }
                    else{
                        Toasty.error(bills.this, "الرجاء ادخال مبلغ", Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toasty.error(bills.this, "الرجاء التحقق من الاتصال بالانترنت", Toast.LENGTH_SHORT).show();
                }
            }


        });
        dialogBtnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isConnected()) {
                    if(!dialogTxtTotal.getText().toString().equals("00")) {
                        if (Constant.btDev!=null) {
                            do_print=true;
                            databaseAccess.open();
                            databaseAccess.addbills(getCustomerId, "1", currentDate, dialogTxtTotalCost.getText().toString(), note.getText().toString());
                            databaseAccess.open();
                            ArrayList<HashMap<String, String>> bills = databaseAccess.getbills();
                            id_bill = Integer.parseInt(bills.get(bills.size() - 1).get("id"));
                            add_bill(dialogTxtTotalCost.getText().toString(), note.getText().toString());
                        }
                        else{
                            Intent intent = new Intent(bills.this, MainActivity.class);
                            startActivity(intent);
                            Toasty.error(bills.this, "يرجى الاتصال بالطابعة", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toasty.error(bills.this, "الرجاء ادخال مبلغ", Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toasty.error(bills.this, "الرجاء التحقق من الاتصال بالانترنت", Toast.LENGTH_SHORT).show();
                }
            }


        });



        dialogBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();
                databaseAccess.delete_check();
                databaseAccess.close();
                finish();
            }
        });
        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function

                count();
                ha.postDelayed(this, 1000);
            }
        }, 1000);


    }
     public void count(){
         final EditText dialogTxtTotal = findViewById(R.id.dialog_txt_total1);
         final TextView dialogTxtTotalCost = findViewById(R.id.dialog_txt_total_cost1);
         final TextView dialogTxtTotalTax = findViewById(R.id.dialog_txt_total_tax1);
         int one=0;
         if(dialogTxtTotal.getText().toString().length()>0){
             one=Integer.parseInt(dialogTxtTotal.getText().toString());
         }
         databaseAccess.open();
         //double two= databaseAccess.getcountAllChecks();
         double two=0.0;
         databaseAccess.close();
         if(two!=0) {
             dialogTxtTotalTax.setText(String.valueOf(two));
         }
         two+=one;
         String total;
         if(two!=0) {
              total = String.valueOf(two);
         }
         else {
              total="00";
         }
         dialogTxtTotalCost.setText(total);
    }

    public void dialogSuccess() {


        AlertDialog.Builder dialog = new AlertDialog.Builder(bills.this);
        View dialogView = getLayoutInflater().inflate(R.layout.diloge_add_check, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        ImageButton dialogBtnCloseDialog = dialogView.findViewById(R.id.btn_close);
        Button submit=dialogView.findViewById(R.id.button_submit);
        EditText check_no=dialogView.findViewById(R.id.edit_text_check_no);
        EditText acc_on=dialogView.findViewById(R.id.edit_text_acc_no);
        EditText bank_no=dialogView.findViewById(R.id.edit_text_bank_no);
        EditText branch_no=dialogView.findViewById(R.id.edit_text_branch_no);
        EditText balance=dialogView.findViewById(R.id.edit_text_balance);
        TextView datee=dialogView.findViewById(R.id.edit_text_date);
        datee.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date()));
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

                if(check_no.getText().toString().length()>0){
                    if(acc_on.getText().toString().length()>0){
                        if(bank_no.getText().toString().length()>0){
                            if(branch_no.getText().toString().length()>0){
                                if(balance.getText().toString().length()>0){
                                    databaseAccess.open();
                                    //get data from local database
                                    databaseAccess.addCheck(check_no.getText().toString(),datee.getText().toString(),balance.getText().toString(),branch_no.getText().toString(),bank_no.getText().toString(),acc_on.getText().toString());
                                    databaseAccess.close();
                                    databaseAccess.open();
                                }
                                else{
                                    Toasty.error(bills.this, "الرجاء ادخال المبلغ", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toasty.error(bills.this, "الرجاء ادخال رقم الفرع", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toasty.error(bills.this, "الرجاء ادخال رقم البنك", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toasty.error(bills.this, "الرجاء ادخال رقم الحساب", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toasty.error(bills.this, "الرجاء ادخال رقم الشيك", Toast.LENGTH_SHORT).show();
                }



            }
        });
        datee.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        bills.this,
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
                datee.setText(date);
            }
        };

        alertDialogSuccess.show();


    }

    public void add_bill(String amount ,String note){
        new Background_add_bill(amount,note).execute();
    }
    class Background_add_bill extends AsyncTask<Void,Void,String> {

        String amount,note;
        public Background_add_bill(String amount,String note){

            this.amount=amount;
            if(note.length()>0) {
                this.note = note;
            }
            else{
                this.note="-";
            }

        }
        String json_url;
        @Override
        protected void onPreExecute() {

            json_url="http://ttssystems.truetime.ps/api/CreateReceiptFromOrderApp/GetReceipt?Company="+api+"&RefNo="+getCustomerId+"&Amount="+amount+"&UserID="+phone+"&DocNote="+note+"&DeviceName="+ getDeviceName()+"&CurrencyID=1";


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

            if (string != null) {
                try {
                    loadInto(string);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toasty.error(bills.this, "يوجد خطأ", Toast.LENGTH_SHORT).show();

            }
        }


    }

    private void loadInto(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject obj = jsonArray.getJSONObject(0);
         entrey=obj.getString("ColResult");

        databaseAccess.open();
        databaseAccess.updatebill(""+id_bill,entrey);
        if(do_print){


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
                        sample.print_bill(paperType,entrey,getCustomerName,dialogTxtTotalCost.getText().toString(),note.getText().toString(),bills.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


                 */
                try {
                    sample.print_bill(paperType,entrey,getCustomerName,dialogTxtTotalCost.getText().toString(),note.getText().toString(),bills.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }


            else{
                Toasty.error(bills.this, "يرجى الاتصال بالطابعة", Toast.LENGTH_SHORT).show();

            }

            }

        finish();




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

    public void btConn(final BluetoothDevice btDev) throws IOException
    {
        new connBT().execute(btDev);
    }

    class connBT extends AsyncTask<BluetoothDevice, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(bills.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(bills.this);

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
                    sample.print_bill(paperType,entrey,getCustomerName,dialogTxtTotalCost.getText().toString(),note.getText().toString(),bills.this);
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
            }
            super.onPostExecute(result);
        }
    }
}

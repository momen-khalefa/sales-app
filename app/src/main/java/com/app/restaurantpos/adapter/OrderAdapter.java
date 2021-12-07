package com.app.restaurantpos.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.orders.OrderDetailsActivity;
import com.app.restaurantpos.pos.ProductCart;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import es.dmoral.toasty.Toasty;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {


    Context context;
    private List<HashMap<String, String>> orderData;
    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
    String d_date,d_name,d_amount, d_id,reference,p_id,p_unit,p_count,p_price,code,bill_id,device_name,itemNo,image ,date_del,not,type;
    Double amount;
    int checks=0;
    String Json,api;


    public OrderAdapter(Context context, List<HashMap<String, String>> orderData, String api) {
        this.context = context;
        this.orderData = orderData;
        this.api=api;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        String customerName = orderData.get(position).get(Constant.CUSTOMER_NAME);
        String invoiceId = orderData.get(position).get(Constant.INVOICE_ID);
        String orderDate = orderData.get(position).get(Constant.ORDER_DATE);
        String orderTime = orderData.get(position).get(Constant.ORDER_TIME);
        String orderPaymentMethod = orderData.get(position).get(Constant.ORDER_PAYMENT_METHOD);
        String orderType = orderData.get(position).get(Constant.ORDER_TYPE);
        String tableNo = orderData.get(position).get(Constant.TABLE_NO);

        databaseAccess.open();
        String name=databaseAccess.getCustomerNAme(orderData.get(position).get(Constant.CUSTOMER_NAME));
        holder.txtCustomerName.setText(name);
        holder.txtOrderId.setText("رقم الفاتورة: " + invoiceId);
        if(tableNo.equals("0")){
            orderPaymentMethod="محفوظ";
        }
        else{
            orderPaymentMethod="مرحل";
        }
        if(orderType.equals("11")){
            orderType="طلبية مبيعات";
            holder.txtOrderStatus.setText("حالة الطلبية : " + orderPaymentMethod);

        }
        if(orderType.equals("2")){
            orderType="فاتورة مبيعات";
            holder.txtOrderId.setText("رقم الفاتورة: "+orderData.get(position).get("bill_id"));
            holder.txtOrderStatus.setText("حالة الفاتورة : " + orderPaymentMethod);

        }
        if(orderType.equals("12")){
            orderType="مردودات مبيعات";
            holder.txtOrderId.setText("رقم الفاتورة: "+orderData.get(position).get("bill_id"));
            holder.txtOrderStatus.setText("حالة الفاتورة : " + orderPaymentMethod);

        }
        holder.txtOrderType.setText("النوع: " + orderType);
        holder.txtDate.setText(orderTime + " " + orderDate);

        databaseAccess.open();
        String totalPrice = databaseAccess.getTotalPrice_order( orderData.get(position).get(Constant.INVOICE_ID));
        holder.total.setText("المبلغ :"+totalPrice);
        if (tableNo == null || tableNo.equals("N/A")) {
            holder.txtTableNo.setVisibility(View.GONE);
        } else {
            holder.txtTableNo.setText(context.getString(R.string.table_number) + " :" + tableNo);
        }
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle(context.getString(R.string.delete))
                        .withMessage(context.getString(R.string.want_to_delete_order))
                        .withEffect(Slidetop)
                        .withDialogColor("#f29161") //use color code for dialog
                        .withButton1Text(context.getString(R.string.yes))
                        .withButton2Text(context.getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                boolean deleteOrder = databaseAccess.deleteOrder(invoiceId);

                                if (deleteOrder) {
                                    Toasty.error(context, R.string.order_deleted, Toast.LENGTH_SHORT).show();

                                    orderData.remove(holder.getAdapterPosition());

                                    // Notify that item at position has been removed
                                    notifyItemRemoved(holder.getAdapterPosition());

                                } else {
                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                }


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


  holder.imgOrderImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.diloge_password, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                    TextView button = dialogView.findViewById(R.id.edit_text_conform);
                    EditText text = dialogView.findViewById(R.id.edit_text_password);
                    ImageButton dialogBtnCloseDialog = dialogView.findViewById(R.id.btn_close);
                    AlertDialog alertDialogSuccess = dialog.create();

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(text.getText().toString().equals("100300100")){
                                if(isConnected()) {
                                    databaseAccess.open();
                                    ArrayList<HashMap<String, String>> orderList = databaseAccess.getOrderSelect(orderData.get(position).get("order_id"));
                                    databaseAccess.close();
                                    databaseAccess.open();

                                    device_name=getDeviceName();
                                    for (int i = 0; i < orderList.size(); i++) {
                                        databaseAccess.open();
                                        ArrayList<HashMap<String, String>> details = databaseAccess.getOrderDetailsList(orderList.get(i).get("invoice_id"));

                                        d_date = orderList.get(i).get("order_date") + " " + orderList.get(i).get("order_time");
                                        d_name = orderList.get(i).get("customer_name");
                                        d_amount = orderList.get(i).get("price");
                                        d_id = orderList.get(i).get("order_id");
                                        type = orderList.get(i).get("order_type");
                                        code = orderList.get(i).get("tax");
                                        bill_id = randomString();
                                        date_del = orderList.get(i).get("order_payment_method");
                                        not = orderList.get(i).get("discount");
                                        checks = 0;
                                        for (int j = 0; j < details.size(); j++) {

                                            databaseAccess.open();
                                            databaseAccess.updateorder_details_id(details.get(j).get("order_details_id"),bill_id);
                                            p_id = details.get(j).get("product_name");
                                            p_count = details.get(j).get("product_qty");
                                            p_unit =  details.get(j).get("product_weight");
                                            p_price = details.get(j).get("product_price");
                                            amount = Double.valueOf(p_count) * Double.valueOf(p_price);
                                            add(code, d_date, type, amount, d_name, d_date, p_id, p_unit, p_count, p_price, bill_id, device_name, details.get(j).get("order_details_id"),date_del,not);

                                        }
                                            databaseAccess.open();
                                            databaseAccess.updateorderCode(orderList.get(i).get("order_id"), bill_id);
                                            databaseAccess.open();
                                            databaseAccess.updateorderSt(orderList.get(i).get("order_id"), "1");
                                            Toasty.success(context, "تم الترحيل بنجاح", Toast.LENGTH_SHORT).show();



                                    }
                                    databaseAccess.close();
                                    alertDialogSuccess.dismiss();
                                    holder.txtOrderId.setText("رقم الفاتورة: " + bill_id);
                                }
                                else{
                                    Toasty.error(context, "افحص الإتصال بالانترنت", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toasty.error(context, "خطأ في رمز التحقق", Toast.LENGTH_SHORT).show();
                                alertDialogSuccess.dismiss();

                            }
                        }
                    });

                    dialogBtnCloseDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alertDialogSuccess.dismiss();

                        }
                    });

                    alertDialogSuccess.show();

                    return true;// returning true instead of false, works for me
                }
            });

    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
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

            json_url="http://"+api+".truetime.ps/api/orders/Get?DocID="+doc_id+"&DocDate="+doc_date+"&DocName="+doc_name+"&DocAmount="+doc_amount+"&Referance="+reference+"&InputUser=123456&InputDateTime="+doc_date+"&StockID="+stock_id+"&StockUnit="+stock_unit+"&StockQuantity="+stock_qnt+"&StockPrice="+stock_price+"&DocCode="+doc_code+"&DeviceName="+device_name+"&DeliverDate="+date_del+"&DocNotes="+not;


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
    public int getItemCount() {
        return orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtCustomerName, txtTableNo, txtOrderId, txtOrderType, txtOrderStatus, txtDate,total;
        ImageView imgDelete, imgOrderImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtCustomerName = itemView.findViewById(R.id.txt_customer_name);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtOrderType = itemView.findViewById(R.id.txt_order_type);
            txtOrderStatus = itemView.findViewById(R.id.txt_order_status);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtTableNo = itemView.findViewById(R.id.txt_table_no);
            imgDelete = itemView.findViewById(R.id.img_delete);
            imgOrderImage = itemView.findViewById(R.id.img_order_image);
            total=itemView.findViewById(R.id.txt_order_total);

            itemView.setOnClickListener(this);



        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, OrderDetailsActivity.class);
            i.putExtra("order_id", orderData.get(getAdapterPosition()).get("invoice_id"));
            i.putExtra(Constant.CUSTOMER_NAME, orderData.get(getAdapterPosition()).get(Constant.CUSTOMER_NAME));
            i.putExtra("order_date", orderData.get(getAdapterPosition()).get("order_date"));
            i.putExtra("order_time", orderData.get(getAdapterPosition()).get("order_time"));
            i.putExtra("tax", orderData.get(getAdapterPosition()).get("tax"));
            i.putExtra("discount", orderData.get(getAdapterPosition()).get("discount"));
            i.putExtra("table_no", orderData.get(getAdapterPosition()).get("table_no"));
            i.putExtra("bill_id", orderData.get(getAdapterPosition()).get("bill_id"));
            i.putExtra("order_type",orderData.get(getAdapterPosition()).get(Constant.ORDER_TYPE));

            context.startActivity(i);
        }

    }



}
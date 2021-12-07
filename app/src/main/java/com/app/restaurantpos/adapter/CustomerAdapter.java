package com.app.restaurantpos.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.R;
import com.app.restaurantpos.cust_home;
import com.app.restaurantpos.customers.EditCustomersActivity;
import com.app.restaurantpos.database.DatabaseAccess;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> {


    private List<HashMap<String, String>> customerData;
    private Context context;


    public CustomerAdapter(Context context, List<HashMap<String, String>> customerData) {
        this.context = context;
        this.customerData = customerData;

    }


    @NonNull
    @Override
    public CustomerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CustomerAdapter.MyViewHolder holder, int position) {

        final String customer_id = customerData.get(position).get(Constant.CUSTOMER_ID);
        String name = customerData.get(position).get(Constant.CUSTOMER_NAME);
        String cell = customerData.get(position).get(Constant.CUSTOMER_CELL);
        String email = customerData.get(position).get(Constant.CUSTOMER_EMAIL);
        String address = customerData.get(position).get(Constant.CUSTOMER_ADDRESS);

        holder.txtCustomerName.setText(name);
        holder.txtCell.setText(cell);
        holder.txtEmail.setText("الرصيد :"+email+"₪");
        holder.txtAddress.setText(address);


        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String phone = "tel:" + cell;
                callIntent.setData(Uri.parse(phone));
                context.startActivity(callIntent);
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, EditCustomersActivity.class);
                i.putExtra("customer_id", customer_id);
                i.putExtra("customer_name", name);
                i.putExtra("customer_cell",cell);
                i.putExtra("customer_email", email);
                i.putExtra("customer_address", address);
                context.startActivity(i);


            }
        });

    }

    @Override
    public int getItemCount() {
        return customerData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtCustomerName, txtCell, txtEmail, txtAddress;
        ImageView imgDelete, imgCall;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCustomerName = itemView.findViewById(R.id.txt_customer_name);
            txtCell = itemView.findViewById(R.id.txt_cell);
            txtEmail = itemView.findViewById(R.id.txt_email);
            txtAddress = itemView.findViewById(R.id.txt_address);

            imgDelete = itemView.findViewById(R.id.img_delete);
            imgCall = itemView.findViewById(R.id.img_call);


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, cust_home.class);
            i.putExtra("customer_id", customerData.get(getAdapterPosition()).get("customer_id"));
            i.putExtra("customer_name", customerData.get(getAdapterPosition()).get("customer_name"));
            i.putExtra("customer_cell", customerData.get(getAdapterPosition()).get("customer_cell"));
            i.putExtra("customer_email", customerData.get(getAdapterPosition()).get("customer_email"));
            i.putExtra("customer_address", customerData.get(getAdapterPosition()).get("customer_address"));
            context.startActivity(i);
        }
    }


}

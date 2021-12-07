package com.app.restaurantpos.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageButton;
import android.widget.TextView;

import com.app.restaurantpos.R;
import com.app.restaurantpos.orders.OrdersActivity;
import com.app.restaurantpos.pos.ProductCart;
import com.app.restaurantpos.utils.acc_stat;

import java.util.ArrayList;


public class AccountBillsAdapter extends BaseAdapter {
    Context c;
    ArrayList<acc_stat> details;
    String note;
    public AccountBillsAdapter(Context c, ArrayList<acc_stat> details) {
        this.c = c;
        this.details = details;
    }

    @Override
    public int getCount() {
        return details.size();
    }

    @Override
    public Object getItem(int i) {
        return details.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(c).inflate(R.layout.table_layout,viewGroup,false);
        }

        final TextView doc_no =  view.findViewById(R.id.doc_no);
        final TextView doc_date =  view.findViewById(R.id.doc_date);
        final TextView doc_type =  view.findViewById(R.id.doc_type);
        final TextView doc_maden =  view.findViewById(R.id.doc_maden);
        final TextView doc_daen = view.findViewById(R.id.doc_daen);
        final TextView doc_balance = view.findViewById(R.id.doc_balance);
        final TextView doc_note = view.findViewById(R.id.doc_note);
        final TextView show_details = view.findViewById(R.id.show_details);



        final acc_stat details= (acc_stat) this.getItem(i);
        if(details.getDoc_no().equals("null")) {
            doc_no.setText("0");
        }
        else{
            doc_no.setText(details.getDoc_no());

        }
        doc_date.setText(details.getDoc_date());
        if(details.getDoc_type().equals("null")) {
            doc_type.setText("-");
        }
        else{
            doc_type.setText(details.getDoc_type());

        }
        if(details.getDoc_maden().equals("null") ){
            doc_maden.setText("0");
        }
        else{
            doc_maden.setText(details.getDoc_maden());
        }
        if(details.getDoc_daen().equals("null")) {
            doc_daen.setText("0");
        }
        else{
            doc_daen.setText(details.getDoc_daen());

        }
        doc_balance.setText(details.getDoc_balance());
        note=details.getDoc_note();
        if(note.length()>20) {
            doc_note.setText(details.getDoc_note().substring(0,15)+"....");
        }
        else{
            doc_note.setText(details.getDoc_note());
        }

        doc_note.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               /* AlertDialog.Builder alertDialog = new AlertDialog.Builder((Activity) v.getContext());

                alertDialog.setMessage(details.getDoc_note());*/
                AlertDialog.Builder dialog = new AlertDialog.Builder(c);
                View dialogView = LayoutInflater.from(c).inflate(R.layout.dilaoge_details, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                TextView show_details = dialogView.findViewById(R.id.show_details);

                show_details.setText(details.getDoc_note());
                ImageButton dialogBtnCloseDialog = dialogView.findViewById(R.id.btn_close_dialog_2);

                AlertDialog alertDialogSuccess = dialog.create();

                dialogBtnCloseDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialogSuccess.dismiss();

                    }
                });

                alertDialogSuccess.show();



            }
        });


        return view;
    }

}



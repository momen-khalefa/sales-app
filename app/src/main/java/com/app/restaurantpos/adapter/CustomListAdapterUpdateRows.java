package com.app.restaurantpos.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.app.restaurantpos.R;
import com.app.restaurantpos.customers.bills;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.utils.checksModels;

import java.util.ArrayList;
import java.util.Calendar;


public class CustomListAdapterUpdateRows extends BaseAdapter {

    Context c;
    ArrayList<checksModels> users;
    DatePickerDialog.OnDateSetListener mDateSetListener = null;
    public CustomListAdapterUpdateRows(Context c, ArrayList<checksModels> users) {
        this.c = c;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(c).inflate(R.layout.listviewupdate_row,viewGroup,false);
        }

        final EditText meditText1 = (EditText) view.findViewById(R.id.editText_id);
        final EditText meditText2 = (EditText) view.findViewById(R.id.editText_date);
        final EditText meditText3 = (EditText) view.findViewById(R.id.editText_balance);
        final EditText meditText4 = (EditText) view.findViewById(R.id.editText_branch);
        final EditText meditText5 = (EditText) view.findViewById(R.id.editText_bank);
        final EditText meditText6 = (EditText) view.findViewById(R.id.editText_acc);
        Button updateBtn = (Button) view.findViewById(R.id.updateBtn);


        final checksModels user= (checksModels) this.getItem(i);
        meditText1.setText(user.getId_check());
        meditText2.setText(user.getdate());
        meditText3.setText(user.getbalance());
        meditText4.setText(user.getbalance());
        meditText5.setText(user.getbank());
        meditText6.setText(user.getaccount());
        String id=user.getID();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String col1value = meditText1.getText().toString();
                String col2value = meditText2.getText().toString();
                String col3value = meditText3.getText().toString();
                String col4value = meditText4.getText().toString();
                String col5value = meditText5.getText().toString();
                String col6value = meditText6.getText().toString();

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(c);
                databaseAccess.open();
                databaseAccess.updateCheck(id,col1value,col2value,col3value,col4value,col5value,col6value);
                databaseAccess.close();

            }
        });

        meditText2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        c,
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
                meditText2.setText(date);
            }
        };

        meditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String col1value = meditText1.getText().toString();
                if(col1value.length()>0) {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(c);
                    databaseAccess.open();
                    databaseAccess.updateCheck(id, col1value, "", "","","","");
                    databaseAccess.close();
                }

            }
        });
        meditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String col2value = meditText2.getText().toString();
                if(col2value.length()>0) {

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(c);
                    databaseAccess.open();
                    databaseAccess.updateCheck(id, "", col2value, "","","","");
                    databaseAccess.close();
                }

            }
        });
        meditText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String col3value = meditText3.getText().toString();
                if(col3value.length()>0) {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(c);
                    databaseAccess.open();
                    databaseAccess.updateCheck(id, "", "", col3value,"","","");
                    databaseAccess.close();
                }

            }
        });
        meditText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String col4value = meditText4.getText().toString();
                if(col4value.length()>0) {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(c);
                    databaseAccess.open();
                    databaseAccess.updateCheck(id, "", "", "",col4value,"","");
                    databaseAccess.close();
                }

            }
        });
        meditText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String col5value = meditText5.getText().toString();
                if(col5value.length()>0) {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(c);
                    databaseAccess.open();
                    databaseAccess.updateCheck(id, "", "", "","",col5value,"");
                    databaseAccess.close();
                }

            }
        });
        meditText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String col6value = meditText6.getText().toString();
                if(col6value.length()>0) {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(c);
                    databaseAccess.open();
                    databaseAccess.updateCheck(id, "", "", "","","",col6value);
                    databaseAccess.close();
                }

            }
        });



        return view;
    }
}
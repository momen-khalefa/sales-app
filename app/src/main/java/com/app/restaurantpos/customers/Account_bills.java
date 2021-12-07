package com.app.restaurantpos.customers;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.app.restaurantpos.R;
import com.app.restaurantpos.adapter.AccountBillsAdapter;
import com.app.restaurantpos.adapter.CustomListAdapterUpdateRows;
import com.app.restaurantpos.utils.BaseActivity;
import com.app.restaurantpos.utils.acc_stat;
import com.app.restaurantpos.utils.checksModels;

import java.util.ArrayList;


public class Account_bills extends BaseActivity {

    ArrayList<acc_stat> details=new ArrayList<>();
    static AccountBillsAdapter updateAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_table);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle("كشف حساب");
        TextView text_maden=findViewById(R.id.text_maden);
        TextView text_daen=findViewById(R.id.text_daen);
        TextView text_total=findViewById(R.id.text_total);
        ListView list=findViewById(R.id.list_balance);
        text_daen.setText("100");
        text_maden.setText("1000");
        text_total.setText("1000");
        Intent intent = getIntent();
        Bundle args = this.getIntent().getExtras();
        String[] DocID = args.getStringArray("DocID");
        String[] DocName = args.getStringArray("DocName");
        String[] DocDate =args.getStringArray("DocDate");
        String[] DebitAmount = args.getStringArray("DebitAmount");
        String[] CredAmount =args.getStringArray("CredAmount");
        String[] Balance = args.getStringArray("Balance");
        String[] DocNotes = args.getStringArray("DocNotes");

        Double m=0.0,d=0.0,b=0.0;
        for(int i=0; i<DocID.length;i++){
            acc_stat map= new acc_stat();
            map.setDoc_no(DocID[i]);
            map.setDoc_type(DocName[i]);
            map.setDoc_date(DocDate[i]);
            map.setDoc_maden(DebitAmount[i]);
            map.setDoc_daen(CredAmount[i]);
            map.setDoc_balance(Balance[i]);
            map.setDoc_note(DocNotes[i]);
            details.add(map);
            if(!DebitAmount[i].equals("null")) {
                m += Double.parseDouble(DebitAmount[i]);
            }
            if(!CredAmount[i].equals("null")) {
                d += Double.parseDouble(CredAmount[i]);
            }
            if(!Balance[i].equals("null")) {

                b += Double.parseDouble(Balance[i]);
            }
        }
        b=m-d;
        text_maden.setText(m.toString());
        text_daen.setText(d.toString());
        text_total.setText(b.toString());

        updateAdapter = new AccountBillsAdapter(Account_bills.this, details);
        list.setAdapter(updateAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

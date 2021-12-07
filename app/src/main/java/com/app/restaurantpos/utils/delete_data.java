package com.app.restaurantpos.utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.app.restaurantpos.HomeActivity;
import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.orders.OrdersActivity;

import es.dmoral.toasty.Toasty;

public class delete_data extends BaseActivity {

    CardView cart_empty, user_empty,order_empty,image_empty,product_empty,catagorey_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        getSupportActionBar().setTitle("حذف البيانات");
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cart_empty = findViewById(R.id.cart_empty);
        user_empty = findViewById(R.id.user_empty);
        order_empty = findViewById(R.id.order_empty);
        image_empty = findViewById(R.id.image_empty);
        product_empty = findViewById(R.id.product_empty);
        catagorey_empty = findViewById(R.id.catagorey_empty);


        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(delete_data.this);

        cart_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();
                databaseAccess.delete_cart();
                Toasty.success(delete_data.this, "تم حذف محتويات السلة", Toast.LENGTH_SHORT).show();


            }
        });
        user_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();
                databaseAccess.delete_cust();
                Toasty.success(delete_data.this, "تم حذف جميع الزبائن", Toast.LENGTH_SHORT).show();

            }
        });
        order_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();
                databaseAccess.delete_order();
                databaseAccess.open();
                databaseAccess.delete_order_details();
                Toasty.success(delete_data.this, "تم حذف جميع الطلبيات", Toast.LENGTH_SHORT).show();

            }
        });
        image_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();
                databaseAccess.delete_all_image();
                Toasty.success(delete_data.this, "تم حذف صور الاصناف", Toast.LENGTH_SHORT).show();


            }
        });
        product_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();
                databaseAccess.delete_product();

                Toasty.success(delete_data.this, "تم حذف  الاصناف", Toast.LENGTH_SHORT).show();

            }
        });
        catagorey_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();
                databaseAccess.delete_catg();
                Toasty.success(delete_data.this, "تم حذف مجموعات الاصناف", Toast.LENGTH_SHORT).show();

            }
        });

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

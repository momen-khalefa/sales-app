package com.app.restaurantpos.utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.app.restaurantpos.R;
import com.app.restaurantpos.customers.CustomersActivity;
import com.app.restaurantpos.database.DatabaseOpenHelper;
import com.app.restaurantpos.orders.OrdersActivity;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class Export_data extends BaseActivity {

    CardView export_users, export_product,export_order;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        getSupportActionBar().setTitle("تصدير البيانات");
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        export_users = findViewById(R.id.export_users);
        export_product = findViewById(R.id.export_product);
        export_order = findViewById(R.id.export_order);

        export_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderChooser("customers");

            }
        });
        export_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folderChooser("products");

            }
        });
        export_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                folderChooser();

            }
        });

    }

    public void folderChooser(String table) {
        new ChooserDialog(Export_data.this)

                .displayPath(true)
                .withFilter(true, false)

                // to handle the result(s)
                .withChosenListener((path, pathFile) -> {
                    onExport(path,table);
                    Log.d("path", path);

                })
                .build()
                .show();
    }


    public void onExport(String path ,String table) {

        String directoryPath = path;
        File file = new File(directoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Export SQLite DB as EXCEL FILE
        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, directoryPath);

        sqliteToExcel.exportSingleTable(table, table+".xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(Export_data.this);
                loading.setMessage(getString(R.string.data_exporting_please_wait));
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            public void onCompleted(String filePath) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(Export_data.this, R.string.data_successfully_exported, Toast.LENGTH_SHORT).show();


                }, 5000);

            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Toasty.error(Export_data.this, R.string.data_export_fail, Toast.LENGTH_SHORT).show();

                Log.d("Error", e.toString());
            }
        });
    }

    public void folderChooser() {
        new ChooserDialog(Export_data.this)

                .displayPath(true)
                .withFilter(true, false)

                // to handle the result(s)
                .withChosenListener((path, pathFile) -> {
                    onExport(path);
                    Log.d("path", path);

                })
                .build()
                .show();
    }


    public void onExport(String path ) {

        String directoryPath = path;
        File file = new File(directoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        List<String> table=new ArrayList<String>();;
        table.add("order_list");
        table.add("order_details");

        // Export SQLite DB as EXCEL FILE
        SQLiteToExcel sqliteToExcel = new SQLiteToExcel(getApplicationContext(), DatabaseOpenHelper.DATABASE_NAME, directoryPath);
        sqliteToExcel.exportSpecificTables(table,"orders.CSV" , new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

                loading = new ProgressDialog(Export_data.this);
                loading.setMessage(getString(R.string.data_exporting_please_wait));
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            public void onCompleted(String filePath) {

                Handler mHand = new Handler();
                mHand.postDelayed(() -> {

                    loading.dismiss();
                    Toasty.success(Export_data.this, R.string.data_successfully_exported, Toast.LENGTH_SHORT).show();


                }, 5000);

            }

            @Override
            public void onError(Exception e) {

                loading.dismiss();
                Toasty.error(Export_data.this, e.toString(), Toast.LENGTH_SHORT).show();

                Log.d("Error", e.toString());
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

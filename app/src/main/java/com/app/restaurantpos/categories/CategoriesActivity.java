package com.app.restaurantpos.categories;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.HomeActivity;
import com.app.restaurantpos.R;
import com.app.restaurantpos.adapter.CategoryAdapter;
import com.app.restaurantpos.cust_home;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.utils.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CategoriesActivity extends BaseActivity {


    private RecyclerView recyclerView;

    ImageView imgNoProduct;
    EditText etxtSearch;
    String getCustomerId,type,getCustomerName;
    FloatingActionButton fabAdd;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        getCustomerName = getIntent().getExtras().getString("customer_name");
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(" الأصناف");
        getCustomerId = getIntent().getExtras().getString("customer_id");
        type = getIntent().getExtras().getString("type");

        recyclerView = findViewById(R.id.recycler_view);
        imgNoProduct = findViewById(R.id.image_no_product);
        etxtSearch = findViewById(R.id.etxt_customer_search);
        fabAdd = findViewById(R.id.fab_add);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setHasFixedSize(true);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(CategoriesActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> categoryData;
        categoryData = databaseAccess.getProductCategory();

        Log.d("data", "" + categoryData.size());

        if (categoryData.isEmpty()) {
            Toasty.info(this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
            imgNoProduct.setImageResource(R.drawable.no_data);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            CategoryAdapter categoryAdapter = new CategoryAdapter(CategoriesActivity.this, categoryData , getCustomerId,type,getCustomerName);

            recyclerView.setAdapter(categoryAdapter);


        }

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                Log.d("data", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(CategoriesActivity.this);
                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchCategoryList;

                searchCategoryList = databaseAccess.searchProductCategory(s.toString());


                if (searchCategoryList.isEmpty()) {

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.no_data);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);


                    CategoryAdapter categoryAdapter = new CategoryAdapter(CategoriesActivity.this, searchCategoryList,getCustomerId,type,getCustomerName);

                    recyclerView.setAdapter(categoryAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("data", s.toString());
            }


        });


    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==  R.id.menu_cart_button4) {
            Intent intent = new Intent(CategoriesActivity.this, HomeActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.export_suppliers_menu, menu);

        MenuItem item = menu.findItem(R.id.name_set);
        if(getCustomerName.length()>20) {
            item.setTitle(getCustomerName.substring(0,20));
        }
        else{
            item.setTitle(getCustomerName);

        }
        return super.onCreateOptionsMenu(menu);
    }

}

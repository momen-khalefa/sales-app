package com.app.restaurantpos.pos;

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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.HomeActivity;
import com.app.restaurantpos.R;
import com.app.restaurantpos.adapter.PosProductAdapter;
import com.app.restaurantpos.cust_home;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.utils.BaseActivity;

import java.util.HashMap;
import java.util.List;

public class PosActivity extends BaseActivity {


    private RecyclerView recyclerView;
    PosProductAdapter productAdapter;
    TextView txtNoProducts;

    String category_id,getCustomerId,type,getCustomerName,getCategory_name;
    ImageView imgNoProduct,imgScanner;
    public static EditText etxtSearch;
    Menu optionsMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);

        getCustomerName = getIntent().getExtras().getString("customer_name");
        getCategory_name = getIntent().getExtras().getString("category_name");
        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(getCategory_name);
        category_id = getIntent().getExtras().getString("category_id");

        etxtSearch = findViewById(R.id.etxt_search);
        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        txtNoProducts = findViewById(R.id.txt_no_products);
        imgScanner=findViewById(R.id.img_scanner);
        getCustomerId = getIntent().getExtras().getString("customer_id");
        type = getIntent().getExtras().getString("type");
        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView




        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(PosActivity.this);
        databaseAccess.open();

        //get data from local database
        List<HashMap<String, String>> productList;
        productList = databaseAccess.getProducts(category_id);

        if (productList.isEmpty()) {

            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.not_found);
            txtNoProducts.setVisibility(View.VISIBLE);


        } else {


            recyclerView.setVisibility(View.VISIBLE);
            imgNoProduct.setVisibility(View.GONE);
            txtNoProducts.setVisibility(View.GONE);

            productAdapter = new PosProductAdapter(PosActivity.this, productList);

            recyclerView.setAdapter(productAdapter);


        }


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                Log.d("data",s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchProductList;

                searchProductList = databaseAccess.getSearchProducts(s.toString(),category_id);


                if (searchProductList.isEmpty()) {

                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.not_found);
                    txtNoProducts.setVisibility(View.VISIBLE);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);
                    txtNoProducts.setVisibility(View.GONE);

                    productAdapter = new PosProductAdapter(PosActivity.this, searchProductList);

                    recyclerView.setAdapter(productAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("data",s.toString());
            }


        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_cart, menu);
        optionsMenu=menu;
        MenuItem item = menu.findItem(R.id.name_sett);
        if(getCustomerName.length()>20) {
            item.setTitle(getCustomerName.substring(0,20));
        }
        else{
            item.setTitle(getCustomerName);

        }
        return  super.onCreateOptionsMenu(menu);
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.menu_cart_button:
                Intent intent = new Intent(PosActivity.this, ProductCart.class);
                intent.putExtra("customer_id",getCustomerId);
                intent.putExtra("customer_name",getCustomerName);
                intent.putExtra("type",type);
                startActivity(intent);
                return true;



            case R.id.menu_cart_button3:
                Intent intent1 = new Intent(PosActivity.this, HomeActivity.class);
                startActivity(intent1);
                return true;

            case android.R.id.home:

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        Intent i = new Intent(this, PosActivity.class);
        i.putExtra("category_id", category_id);
        i.putExtra("category_name",getCategory_name);
        i.putExtra("customer_id",getCustomerId);
        i.putExtra("customer_name",getCustomerName);
        i.putExtra("type",type);
        startActivity(i);
    }

}

package com.app.restaurantpos.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restaurantpos.Constant;
import com.app.restaurantpos.R;
import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.pos.ProductCart;
import com.app.restaurantpos.product.EditProductActivity;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PosProductAdapter extends RecyclerView.Adapter<PosProductAdapter.MyViewHolder> {


    private List<HashMap<String, String>> productData;
    private Context context;
    MediaPlayer player;


    public PosProductAdapter(Context context, List<HashMap<String, String>> productData) {
        this.context = context;
        this.productData = productData;
        player = MediaPlayer.create(context, R.raw.delete_sound);
    }

    @NonNull
    @Override
    public PosProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_product_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PosProductAdapter.MyViewHolder holder, int position) {

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        holder.setIsRecyclable(false);
        databaseAccess.open();
        String currency = databaseAccess.getCurrency();

        final String product_id = productData.get(position).get("product_id");
        String name = productData.get(position).get("product_name");
        final String product_weight = productData.get(position).get("product_weight");
        final String product_price = productData.get(position).get("product_sell_price");
        final String weight_unit_id = productData.get(position).get("product_weight_unit_id");
        databaseAccess.open();
        String base64Image = databaseAccess.get_image(productData.get(position).get("product_id"));
        databaseAccess.open();
        final String weight_unit_name = databaseAccess.getWeightUnitName(weight_unit_id);

        holder.txtProductName.setText(name);
        holder.txtWeight.setText(product_weight + " " + weight_unit_name);
        holder.txtPrice.setText(product_price + "₪");

        if(productData.get(position).get("product_supplier").equals("2")){
            holder.txtPrice.setFocusable(false);
        }
       holder.cardProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.daylog_pos_product, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                TextView unit_1_name = dialogView.findViewById(R.id.unit_1_name);
                TextView unit_1_barcode = dialogView.findViewById(R.id.unit_1_barcode);
                TextView unit_1_price = dialogView.findViewById(R.id.unit_1_price);
                TextView unit_2_name = dialogView.findViewById(R.id.unit_2_name);
                TextView unit_2_barcode = dialogView.findViewById(R.id.unit_2_barcode);
                TextView unit_2_price = dialogView.findViewById(R.id.unit_2_price);
                TextView balance = dialogView.findViewById(R.id.unit_2_price2);
                databaseAccess.open();
                unit_1_name.setText(databaseAccess.getWeightUnitName(weight_unit_id));
                unit_1_barcode.setText(product_weight);
                unit_1_price.setText(product_price);
                databaseAccess.open();
                unit_2_name.setText(databaseAccess.getWeightUnitName(productData.get(position).get("product_unit2")));
                unit_2_barcode.setText(productData.get(position).get("product_unit2_code"));
                unit_2_price.setText(productData.get(position).get("product_unit2_price"));
                balance.setText(productData.get(position).get(Constant.PRODUCT_DESCRIPTION));
                ImageButton dialogBtnCloseDialog = dialogView.findViewById(R.id.btn_close);

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



        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64base", base64Image);
                holder.productImage.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.productImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }


        databaseAccess.open();
        if(databaseAccess.check_in_card(product_id)!=0){
           holder.btnAddToCart.setBackgroundColor(Color.GREEN);

        }

        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ResourceAsColor", "ResourceType"})
            @Override
            public void onClick(View v) {
                databaseAccess.open();

                String price;
                if(holder.txtPrice.getText().toString().charAt(holder.txtPrice.getText().toString().length()-1)=='₪'){

                    price= holder.txtPrice.getText().toString().substring(0, holder.txtPrice.getText().toString().length()-1);
                }
                else{
                    price= holder.txtPrice.getText().toString();
                }
                if(productData.get(position).get("product_supplier").equals("2")){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.diloge_unit_chocer, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                    TextView show_details = dialogView.findViewById(R.id.show_details);
                    Button b1 = dialogView.findViewById(R.id.button_submit_unit_2);
                    Button b2 = dialogView.findViewById(R.id.button_submit_unit_1);
                    TextView txt_minus_unit2 = dialogView.findViewById(R.id.txt_minus_unit2);
                    TextView txt_plus_unit2 = dialogView.findViewById(R.id.txt_plus_unit2);
                    EditText txt_price_unit2 = dialogView.findViewById(R.id.txt_price_unit2);
                    TextView txt_minus_unit1 = dialogView.findViewById(R.id.txt_minus_unit1);
                    TextView txt_plus_unit1= dialogView.findViewById(R.id.txt_plus_unit1);
                    EditText txt_price_unit1 = dialogView.findViewById(R.id.txt_price_unit1);
                    databaseAccess.open();
                    b1.setText(databaseAccess.getWeightUnitName(weight_unit_id));
                    databaseAccess.open();
                    b2.setText(databaseAccess.getWeightUnitName(productData.get(position).get("product_unit2")));
                    ImageButton dialogBtnCloseDialog = dialogView.findViewById(R.id.btn_close);
                    txt_price_unit2.setText(product_price + "₪");
                    txt_price_unit1.setText(productData.get(position).get("product_unit2_price")  + "₪");
                    AlertDialog alertDialogSuccess = dialog.create();

                    dialogBtnCloseDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alertDialogSuccess.dismiss();

                        }
                    });
                     txt_minus_unit1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!txt_price_unit1.getText().toString().equals("") || txt_price_unit1.getText().toString()!=null)
                            {
                            String qty1="0";
                            if((txt_price_unit1.getText().toString()).charAt(txt_price_unit1.getText().toString().length()-1)=='₪') {
                                qty1 = txt_price_unit1.getText().toString().substring(0, txt_price_unit1.getText().toString().length() - 2);
                            }
                            else {
                                qty1 = txt_price_unit1.getText().toString();

                            }
                            float getQty = Float.valueOf(qty1);

                            getQty= (float) (getQty-1.0);

                            txt_price_unit1.setText(getQty + "₪");
                            }
                            else {
                                float getQty = 0;

                                getQty= (float) (getQty-1.0);

                                txt_price_unit1.setText(getQty + "₪");
                            }

                        }
                    });
                    txt_plus_unit1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!txt_price_unit1.getText().toString().equals("") || txt_price_unit1.getText().toString()!=null)
                            {
                                String qty1 = "0";
                                if ((txt_price_unit1.getText().toString()).charAt(txt_price_unit1.getText().toString().length() - 1) == '₪') {
                                    qty1 = txt_price_unit1.getText().toString().substring(0, txt_price_unit1.getText().toString().length() - 2);
                                } else {
                                    qty1 = txt_price_unit1.getText().toString();

                                }
                                double getQty = Double.valueOf(qty1);

                                getQty = getQty + 1.0;

                                txt_price_unit1.setText(getQty + "₪");
                            }
                            else {
                                double getQty = 0;

                                getQty = getQty + 1.0;

                                txt_price_unit1.setText(getQty + "₪");
                            }


                        }
                    });
                    txt_minus_unit2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!txt_price_unit2.getText().toString().equals("") || txt_price_unit2.getText().toString()!=null) {
                                String qty1 = "0";
                                if ((txt_price_unit2.getText().toString()).charAt(txt_price_unit2.getText().toString().length() - 1) == '₪') {
                                    qty1 = txt_price_unit2.getText().toString().substring(0, txt_price_unit2.getText().toString().length() - 2);
                                } else {
                                    qty1 = txt_price_unit2.getText().toString();

                                }
                                float getQty = Float.valueOf(qty1);

                                getQty = (float) (getQty - 1.0);

                                txt_price_unit2.setText(getQty + "₪");
                            }
                            else {
                                float getQty = 0;

                                getQty = (float) (getQty - 1.0);

                                txt_price_unit2.setText(getQty + "₪");
                            }


                        }
                    });
                    txt_plus_unit2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!txt_price_unit2.getText().toString().equals("") || txt_price_unit2.getText().toString()!=null) {

                                String qty1="0";
                            if((txt_price_unit2.getText().toString()).charAt(txt_price_unit2.getText().toString().length()-1)=='₪') {
                                qty1 = txt_price_unit2.getText().toString().substring(0, txt_price_unit2.getText().toString().length() - 2);
                            }
                            else {
                                qty1 =txt_price_unit2.getText().toString();

                            }
                            double getQty = Double.valueOf(qty1);

                            getQty=getQty+1.0;

                            txt_price_unit2.setText(getQty + "₪");
                            }
                            else {
                                double getQty = 0;

                                getQty=getQty+1.0;

                                txt_price_unit2.setText(getQty + "₪");
                            }

                        }
                    });
                    b2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String qty1="0";

                             if(!txt_price_unit1.getText().toString().equals("") || txt_price_unit1.getText().toString()!=null) {

                                 if ((txt_price_unit1.getText().toString()).charAt(txt_price_unit1.getText().toString().length() - 1) == '₪') {
                                     qty1 = txt_price_unit1.getText().toString().substring(0, txt_price_unit1.getText().toString().length() - 2);
                                 } else {
                                     qty1 = txt_price_unit1.getText().toString();

                                 }
                             }

                            databaseAccess.open();
                            int check = databaseAccess.addToCart(product_id, product_weight, productData.get(position).get("product_unit2"), qty1, Float.valueOf(holder.txtQtyNumber.getText().toString()));

                            if (check == 1) {
                                Toasty.success(context, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                                player.start();
                                holder.btnAddToCart.setBackgroundColor(Color.GREEN);



                            } else if (check == 2) {

                                Toasty.info(context, R.string.product_already_added_to_cart, Toast.LENGTH_SHORT).show();

                            } else {
                                Toasty.error(context, R.string.product_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                            }
                            alertDialogSuccess.dismiss();

                        }
                    });
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String qty1="0";

                             if(!txt_price_unit2.getText().toString().equals("") || txt_price_unit2.getText().toString()!=null) {

                                 if ((txt_price_unit2.getText().toString()).charAt(txt_price_unit2.getText().toString().length() - 1) == '₪') {
                                     qty1 = txt_price_unit2.getText().toString().substring(0, txt_price_unit2.getText().toString().length() - 2);
                                 } else {
                                     qty1 = txt_price_unit2.getText().toString();

                                 }
                             }

                            databaseAccess.open();
                            int check = databaseAccess.addToCart(product_id, product_weight, weight_unit_id, qty1, Float.valueOf(holder.txtQtyNumber.getText().toString()));

                            if (check == 1) {
                                Toasty.success(context, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                                player.start();
                                holder.btnAddToCart.setBackgroundColor(Color.GREEN);




                            } else if (check == 2) {

                                Toasty.info(context, R.string.product_already_added_to_cart, Toast.LENGTH_SHORT).show();

                            } else {
                                Toasty.error(context, R.string.product_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                            }
                            alertDialogSuccess.dismiss();

                        }
                    });


                    alertDialogSuccess.show();
                }
                else {
                    int check = databaseAccess.addToCart(product_id, product_weight, weight_unit_id, price, Float.valueOf(holder.txtQtyNumber.getText().toString()));

                    if (check == 1) {
                        Toasty.success(context, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
                        player.start();
                       holder.btnAddToCart.setBackgroundColor(Color.GREEN);


                    } else if (check == 2) {

                        Toasty.info(context, R.string.product_already_added_to_cart, Toast.LENGTH_SHORT).show();

                    } else {
                        Toasty.error(context, R.string.product_added_to_cart_failed_try_again, Toast.LENGTH_SHORT).show();

                    }
                }


            }
        });

        holder.txtPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String qty1 = holder.txtQtyNumber.getText().toString();
                float getQty = Float.valueOf(qty1);

                getQty= (float) (getQty+1.0);

                holder.txtQtyNumber.setText(String.valueOf(getQty));


            }
        });


        holder.txtMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String qty = holder.txtQtyNumber.getText().toString();
                float getQty = Float.valueOf(qty);

                getQty= (float) (getQty-1.0);
                holder.txtQtyNumber.setText(String.valueOf(getQty));




            }
        });
        holder.txtPlus_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!productData.get(position).get("product_supplier").equals("2")) {
                    if (!holder.txtPrice.getText().toString().equals("") || holder.txtPrice.getText().toString() != null) {
                        String qty1 = "0";
                        if ((holder.txtPrice.getText().toString()).charAt(holder.txtPrice.getText().toString().length() - 1) == '₪') {
                            qty1 = holder.txtPrice.getText().toString().substring(0, holder.txtPrice.getText().toString().length() - 2);
                        } else {
                            qty1 = holder.txtPrice.getText().toString();

                        }
                        double getQty = Double.valueOf(qty1);

                        getQty = getQty + 1.0;

                        holder.txtPrice.setText(getQty + "₪");
                    } else {
                        double getQty = 0;

                        getQty = getQty + 1.0;

                        holder.txtPrice.setText(getQty + "₪");
                    }
                }
                else{

                }

            }
        });


        holder.txtMinus_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!productData.get(position).get("product_supplier").equals("2")) {
                    if (!holder.txtPrice.getText().toString().equals("") || holder.txtPrice.getText().toString() != null) {
                        String qty1 = "0";
                        if ((holder.txtPrice.getText().toString()).charAt(holder.txtPrice.getText().toString().length() - 1) == '₪') {
                            qty1 = holder.txtPrice.getText().toString().substring(0, holder.txtPrice.getText().toString().length() - 2);
                        } else {
                            qty1 = holder.txtPrice.getText().toString();

                        }
                        float getQty = Float.valueOf(qty1);

                        getQty = (float) (getQty - 1.0);

                        holder.txtPrice.setText(getQty + "₪");
                    } else {
                        float getQty = 0;

                        getQty = (float) (getQty - 1.0);

                        holder.txtPrice.setText(getQty + "₪");
                    }
                }
                else{

                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardProduct;
        TextView txtProductName, txtWeight;
        EditText txtPrice;
        Button btnAddToCart;
        ImageView productImage;
        TextView txtPlus,txtPlus_price;
        TextView txtMinus,txtMinus_price;
        TextView txtQtyNumber;

        @SuppressLint("ResourceType")
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtWeight = itemView.findViewById(R.id.txt_weight);
            txtPrice = itemView.findViewById(R.id.txt_price);
            productImage = itemView.findViewById(R.id.img_product);
            btnAddToCart = itemView.findViewById(R.id.btn_add_cart);
            cardProduct=itemView.findViewById(R.id.card_product);
            txtMinus = itemView.findViewById(R.id.txt_minus);
            txtPlus = itemView.findViewById(R.id.txt_plus);
            txtQtyNumber = itemView.findViewById(R.id.txt_number);
            txtMinus_price = itemView.findViewById(R.id.txt_minus_price);
            txtPlus_price = itemView.findViewById(R.id.txt_plus_price);



        }
    }


}

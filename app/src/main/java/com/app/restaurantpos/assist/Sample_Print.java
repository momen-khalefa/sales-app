package com.app.restaurantpos.assist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;

import com.app.restaurantpos.database.DatabaseAccess;
import com.app.restaurantpos.orders.OrderDetailsActivity;
import com.sewoo.jpos.command.CPCLConst;
import com.sewoo.jpos.printer.CPCLPrinter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Sample_Print {

    private CPCLPrinter cpclPrinter;


    public Sample_Print()
    {
        cpclPrinter = new CPCLPrinter();    //Default = English.
        //cpclPrinter = new CPCLPrinter("EUC-KR"); // Korean.
        //cpclPrinter = new CPCLPrinter("GB2312"); //Chinese.
    }

    public void Print_Profile(int count, int paper_type) throws UnsupportedEncodingException
    {
        //2-inch
        cpclPrinter.setForm(0, 200, 200, 406, 384, count);
        cpclPrinter.setMedia(paper_type);

        cpclPrinter.printCPCLText(0, 5, 1, 1, 1, "SEWOO TECH CO.,LTD.", 0);
        cpclPrinter.printCPCLText(0, 0, 2, 1, 70, "Global leader in the mini-printer industry.", 0);
        cpclPrinter.printCPCLText(0, 0, 2, 1, 110, "Total Printing Solution", 0);
        cpclPrinter.printCPCLText(0, 0, 2, 1, 150, "Diverse innovative and reliable products", 0);
        // Telephone
        cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 7, 0, 1, 200, "TEL : 82-31-459-8200", 0);
        // Homepage
        cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_QRCODE, 0, 250, 4, 0, 1, 0, "http://www.miniprinter.com");
        cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 7, 0, 130, 250, "www.miniprinter.com", 0);
        cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 1, 0, 130, 300, "<-- Check This.", 0);
        cpclPrinter.printForm();

        /*//3-inch
        cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
		cpclPrinter.printCPCLText(0, 5, 2, 30, 1, "SEWOO TECH CO.,LTD.", 0);
    	cpclPrinter.printCPCLText(0, 0, 3, 30, 70, "Global leader in the mini-printer industry.", 0);
    	cpclPrinter.printCPCLText(0, 0, 3, 30, 110, "Total Printing Solution", 0);
    	cpclPrinter.printCPCLText(0, 0, 3, 30, 150, "Diverse innovative and reliable products", 0);
    	// Telephone
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 7, 1, 30, 220, "TEL : 82-31-459-8200", 0);
    	// Homepage
		cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_QRCODE, 30, 300, 6, 0, 1, 0, "http://www.miniprinter.com");
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 7, 1, 210, 300, "www.miniprinter.com", 0);
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 1, 1, 210, 390, "<-- Check This.", 0);
    	cpclPrinter.printForm();
        */
    }

    public void Print_1DBarcode(int count, int paper_type) throws UnsupportedEncodingException
    {
        cpclPrinter.setForm(0, 200, 200, 406, 384, count);
        cpclPrinter.setMedia(paper_type);
//		cpclPrinter.userString("BACKFEED", true); // Back feed.

        // CODABAR
        cpclPrinter.setCPCLBarcode(0, 0, 0);
        cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_CODABAR, 2, CPCLConst.LK_CPCL_BCS_0RATIO, 30, 19, 45, "A37859B", 0);
        cpclPrinter.printCPCLText(0, 7, 0, 19, 18, "CODABAR", 0);
        // Code 39
        cpclPrinter.setCPCLBarcode(0, 0, 0);
        cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_39, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 30, 19, 130, "0123456", 0);
        cpclPrinter.printCPCLText(0, 7, 0, 21, 103, "CODE 39", 0);
        // Code 93
        cpclPrinter.setCPCLBarcode(0, 0, 0);
        cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_93, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 30, 19, 215, "0123456", 0);
        cpclPrinter.printCPCLText(0, 7, 0, 21, 180, "CODE 93", 0);
        // BARCODE 128 1 1 50 150 10 HORIZ.
        cpclPrinter.setCPCLBarcode(0, 0, 0);
        cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_128, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 30, 19, 300, "A37859B", 0);
        cpclPrinter.printCPCLText(0, 7, 0, 21, 270, "CODE 128", 0);
        // Print
        cpclPrinter.printForm();

        /* //3-inch
        cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
		cpclPrinter.setCPCLBarcode(0, 2, 0);
		// CODABAR
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_CODABAR, 2, CPCLConst.LK_CPCL_BCS_0RATIO, 50, 109, 45, "A1234567890B", 0);
		// Code 39
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_39, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 50, 19, 150, "01234567890", 0);
		// Code 93
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_93, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 50, 79, 255, "01234567890", 0);
		// Code 128
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_128, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 50, 109, 360, "01234567890", 0);
		// Print
		cpclPrinter.printForm();
        */
    }

    public void Print_2DBarcode(int count, int paper_type) throws UnsupportedEncodingException
    {
        cpclPrinter.setForm(0, 200, 200, 406, 384, count);
        cpclPrinter.setMedia(paper_type);

        cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_DATAMATRIX, 10, 10, 4, 0, 0, 0, "1234567890");
        cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_PDF417, 80, 90, 2, 7, 2, 1, "SEWOO TECH\r\nLK-P11");
        cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_QRCODE, 30, 170, 4, 0, 1, 0, "LK-P11");
        cpclPrinter.printForm();
    }

    public void Print_Image(int count, int paper_type) throws IOException
    {
        cpclPrinter.setForm(0, 200, 200, 406, 384, count);
        cpclPrinter.setMedia(paper_type);

        cpclPrinter.printBitmap("//sdcard//temp//test//sample_2.jpg", 1, 200);
        cpclPrinter.printBitmap("//sdcard//temp//test//sample_3.jpg", 100, 200);
        cpclPrinter.printBitmap("//sdcard//temp//test//sample_4.jpg", 120, 245);
        cpclPrinter.printForm();
    }

    public void Print_Stamp(int count, int paper_type) throws IOException
    {
        cpclPrinter.setForm(0, 200, 200, 406, 384, count);
        cpclPrinter.setMedia(paper_type);

        cpclPrinter.printBitmap("//sdcard//temp//test//danmark_windmill.jpg", 10, 10);
        cpclPrinter.printBitmap("//sdcard//temp//test//denmark_flag.jpg", 222, 55);
        cpclPrinter.setCPCLBarcode(0, 0, 0);
        cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_128, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 30, 19, 290, "0123456", 1);
        cpclPrinter.printCPCLText(0, 0, 1, 21, 345, "Quantity 001", 1); //count
        cpclPrinter.printForm();
    }

    public void Print_Font(int count, int paper_type) throws UnsupportedEncodingException
    {
        cpclPrinter.setForm(0, 200, 200, 500, 384, count);
        cpclPrinter.setMedia(paper_type);

        //FONT Size
        cpclPrinter.printCPCLText(0, 0, 0, 1, 1,   "FONT-0-0", 2);
        cpclPrinter.printCPCLText(0, 0, 1, 100, 1,  "FONT-0-1", 0);
        cpclPrinter.printCPCLText(0, 0, 2, 1, 17, "FONT-0-2", 0);
        // FONT Type 0,1,2,4,5,6,7
        cpclPrinter.printCPCLText(0, 0, 0, 1, 75,   "ABCD1234", 0);
        cpclPrinter.printCPCLText(0, 1, 0, 1, 95,  "ABCD1234", 0);
        cpclPrinter.printCPCLText(0, 2, 0, 1, 145, "ABCD1234", 0);
        cpclPrinter.printCPCLText(0, 4, 0, 1, 160, "ABCD1234", 0);
        cpclPrinter.printCPCLText(0, 5, 0, 1, 200, "ABCD1234", 0);
        cpclPrinter.printCPCLText(0, 6, 0, 1, 230,  "ABCD1234", 0);
        cpclPrinter.printCPCLText(0, 7, 0, 1, 265,  "ABCD1234", 0);
        //FONT Concat
        cpclPrinter.setConcat(CPCLConst.LK_CPCL_CONCAT, 1, 310);
        cpclPrinter.concatText(4, 2, 5, "$");
        cpclPrinter.concatText(4, 3, 0, "12");
        cpclPrinter.concatText(4, 2, 5, "34");
        cpclPrinter.resetConcat();
        // Print
        cpclPrinter.printForm();
    }

    public void Print_SetMag(int count, int paper_type) throws UnsupportedEncodingException
    {
        cpclPrinter.setForm(0, 200, 200, 406, 384, count);
        cpclPrinter.setMedia(paper_type);

        cpclPrinter.setMagnify(CPCLConst.LK_CPCL_TXT_2WIDTH, CPCLConst.LK_CPCL_TXT_2HEIGHT);
        cpclPrinter.setJustification(CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_FONT_0, 0, 1, 1, "FONT-0-0", 2);
        cpclPrinter.setMagnify(CPCLConst.LK_CPCL_TXT_1WIDTH, CPCLConst.LK_CPCL_TXT_1HEIGHT);
        cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_FONT_0, 1, 1, 50,  "FONT-0-1", 0);
        cpclPrinter.setJustification(CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_FONT_4, 0, 1, 100, "FONT-4-0", 0);
        cpclPrinter.setMagnify(CPCLConst.LK_CPCL_TXT_2WIDTH, CPCLConst.LK_CPCL_TXT_1HEIGHT);
        cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_FONT_4, 1, 1, 150,  "FONT-4-1", 0);
        cpclPrinter.setMagnify(CPCLConst.LK_CPCL_TXT_1WIDTH, CPCLConst.LK_CPCL_TXT_1HEIGHT);
        cpclPrinter.setJustification(CPCLConst.LK_CPCL_RIGHT);
        cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_FONT_4, 2, 1, 260,  "4-2", 0);
        cpclPrinter.resetMagnify();
        // Print
        cpclPrinter.printForm();
    }

    public void Print_Multiline(int count, int paper_type) throws UnsupportedEncodingException
    {
        String data = "ABCDEFGHIJKLMNOPQRSTUVWXYZ;0123456789!@#%^&*\r\n";
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<16;i++)
        {
            sb.append(data);
        }

        cpclPrinter.setForm(0, 200, 200, 406, 384, count);
        cpclPrinter.setMedia(paper_type);

        // MultiLine mode.
        cpclPrinter.setMultiLine(15);
        cpclPrinter.multiLineText(0, 0, 0, 10, 20);
        cpclPrinter.multiLineData(sb.toString());
        cpclPrinter.resetMultiLine();
        // Print
        cpclPrinter.printForm();
    }

    public void Print_AndroidFont(int count, int paper_type) throws IOException
    {
        int nLineWidth = 384;   //2-inch
        //int nLineWidth = 576;   //3-inch
        //int nLineWidth = 832;   //4-inch

        String data = "Receipt";
//    	String data = "영수증";
        Typeface typeface = null;

        cpclPrinter.setForm(0, 200, 200, 406, nLineWidth, count);
        cpclPrinter.setMedia(paper_type);

        cpclPrinter.printAndroidFont(data, nLineWidth, 100, 0, CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont("Left Alignment", nLineWidth, 24, 120, CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont("Center Alignment", nLineWidth, 24, 150, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont("Right Alignment", nLineWidth, 24, 180, CPCLConst.LK_CPCL_RIGHT);

        cpclPrinter.printAndroidFont(Typeface.SANS_SERIF, "SANS_SERIF : 1234iwIW", nLineWidth, 24, 210, CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont(Typeface.SERIF, "SERIF : 1234iwIW", nLineWidth, 24, 240, CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont(typeface.MONOSPACE, "ComingSoon : 1234iwIW", nLineWidth, 24, 270, CPCLConst.LK_CPCL_LEFT);

        // Print
        cpclPrinter.printForm();
    }

    public void Print_MultilingualFont(int count, int paper_type) throws IOException
    {
        int nLineWidth = 384;   //2-inch
        //int nLineWidth = 576;   //3-inch
        //int nLineWidth = 832;   //4-inch

        cpclPrinter.setForm(0, 150, 200, 1000, nLineWidth, count);
        cpclPrinter.setMedia(paper_type);


        cpclPrinter.printAndroidFont(" شركة المصطفى                       .", nLineWidth, 24, 0, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont("\n", nLineWidth, 24, 30, CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont("رام الله شارع نابلس                    .", nLineWidth, 24, 60, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont("\n", nLineWidth, 24, 90, CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont("  هاتف:                           .", nLineWidth, 24, 120, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont("\n", nLineWidth, 24, 150, CPCLConst.LK_CPCL_LEFT);
            cpclPrinter.printAndroidFont("                       02-2981415", nLineWidth, 24, 180, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont("\n", nLineWidth, 24, 210, CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont("   مشتغل مرخص : 4578843223                    .", nLineWidth, 24, 240, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont("\n", nLineWidth, 24, 270, CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont(" فاتورة ضريبية (1234)                    .", nLineWidth, 24, 290, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont("\n", nLineWidth, 24, 310, CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont(" نسخة اصلية                         .", nLineWidth, 24, 340, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont(" الصنف         السعر        الكمية        المجموع ", nLineWidth, 24, 370, CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont("*******************************************************", nLineWidth, 24, 400, CPCLConst.LK_CPCL_LEFT);

        // Print
        cpclPrinter.printForm();
    }

    public void Print_recipte(int count, int paper_type , List<HashMap<String, String>>  details, Context context ,double total ,int bill_id ,String type) throws IOException
    {
        //int nLineWidth = 384;   //2-inch
        int nLineWidth = 576;   //3-inch
        //int nLineWidth = 832;   //4-inch
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        ArrayList<HashMap<String, String>> shop=databaseAccess.getShopInformation();
        if (details.size()<6) {
            cpclPrinter.setForm(0, 200, 200, 1200, nLineWidth, count);
        }
        else if (details.size()<14) {
            cpclPrinter.setForm(0, 200, 200, 1700, nLineWidth, count);

        }
        else if (details.size()<24) {
            cpclPrinter.setForm(0, 200, 200, 2200, nLineWidth, count);

        }
        else if (details.size()<32){
        cpclPrinter.setForm(0, 200, 200, 2700, nLineWidth, count);

    }
        else {
        cpclPrinter.setForm(0, 200, 200, 3200, nLineWidth, count);

    }
        cpclPrinter.setMedia(paper_type);


        databaseAccess.open();
        ArrayList<HashMap<String, String>> order=databaseAccess.getOrder_incoice(details.get(0).get("invoice_id"));
        databaseAccess.open();
        String name;
        name=databaseAccess.getCustomerNAme(order.get(0).get("customer_name"));

        int h=0;
        String imageee=shop.get(0).get("tax");
        byte[] decodedString = Base64.decode(imageee.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        cpclPrinter.printBitmap(decodedByte, 140, 0,0);
        h=h+160;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_name"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_address"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"  هاتف: "+shop.get(0).get("shop_contact"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," مشتغل مرخص :  "+shop.get(0).get("shop_email"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        if(type.equals("2")) {
            cpclPrinter.printAndroidFont(null,true," فاتورة ضريبية:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"اصلية ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
        }
        else  if(type.equals("12")){
            cpclPrinter.printAndroidFont(null,true,"مردودات مبيعات:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"اصلية ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
        }
        else{
            cpclPrinter.printAndroidFont(null,true,"طلبية مبيعات " , nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;

        }
        cpclPrinter.printAndroidFont(null,true,order.get(0).get("order_payment_method"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," السادة:  "+name, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"الصنف                             السعر            الكمية            المبلغ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);

        int y=h;
        for(int i=1; i<details.size()+1;i++){
            double price = Double.parseDouble(details.get(i-1).get("product_price"));
            int quantity = Integer.parseInt(details.get(i-1).get("product_qty"));
            Double cost = quantity * price;
            String ccc=cost.toString();
            String quntity=details.get(i-1).get("product_qty");
            String pricee=details.get(i-1).get("product_price");
            databaseAccess.open();
            String unit=databaseAccess.getWeightUnitName(details.get(i-1).get("product_weight"));
            if(ccc.length()<4){
                ccc=".."+ccc+" ";
                quntity=".."+quntity;
                pricee=".."+pricee;
            }
            y=y+30;
            databaseAccess.open();
            String name_of_product=databaseAccess.getProductName(details.get(i-1).get("product_name"));
            cpclPrinter.printAndroidFont("  "+ccc+"               "+quntity+"                "+pricee, nLineWidth, 24, y+0, CPCLConst.LK_CPCL_LEFT);
            if(isProbablyArabic(name_of_product)) {
                if (name_of_product.length() < 25) {
                    cpclPrinter.printAndroidFont(null,true,name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);

                } else {
                    String n1;
                    String n2;
                    n1 = name_of_product.substring(0, 25);
                    n2 = name_of_product.substring(25);
                    cpclPrinter.printAndroidFont(null,true,n1, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,n2, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);


                }
            }
            else{
                if (name_of_product.length() < 25) {
                    cpclPrinter.printAndroidFont(null,true,name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);

                } else {
                    String n1;
                    String n2;
                    n1 = name_of_product.substring(0, 25);
                    n2 = name_of_product.substring(25);
                    cpclPrinter.printAndroidFont(null,true,n1, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,n2, nLineWidth, 20, y, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);


                }
            }


        }

        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, y+30, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont(null,true," المجموع:  "+total, nLineWidth, 24, y+60, CPCLConst.LK_CPCL_RIGHT);
        cpclPrinter.printAndroidFont(null,true," العدد:  "+details.size(), nLineWidth, 24, y+60, CPCLConst.LK_CPCL_LEFT);
        if (type.equals("2")) {
            cpclPrinter.printAndroidFont(null,true," ض . ق .م : مشمولة  ", nLineWidth, 24, y + 100, CPCLConst.LK_CPCL_RIGHT);
            cpclPrinter.printAndroidFont(null,true," المبلغ:  " + total, nLineWidth, 24, y + 140, CPCLConst.LK_CPCL_RIGHT);
        }
        cpclPrinter.printAndroidFont(null,true," الملاحظة:  "+order.get(0).get("discount"), nLineWidth, 24, y+180, CPCLConst.LK_CPCL_LEFT);

        // Print
        cpclPrinter.printForm();
        if (type.equals("2") || type.equals("12")){
            if (details.size()<6) {
                cpclPrinter.setForm(0, 200, 200, 1200, nLineWidth, count);
            }
            else if (details.size()<14) {
                cpclPrinter.setForm(0, 200, 200, 1700, nLineWidth, count);

            }
            else if (details.size()<24) {
                cpclPrinter.setForm(0, 200, 200, 2200, nLineWidth, count);

            }
            else if (details.size()<32){
                cpclPrinter.setForm(0, 200, 200, 2700, nLineWidth, count);

            }
            else {
                cpclPrinter.setForm(0, 200, 200, 3200, nLineWidth, count);

            }
        cpclPrinter.setMedia(paper_type);


             h=0;
             imageee=shop.get(0).get("tax");
             decodedString = Base64.decode(imageee.getBytes(), Base64.DEFAULT);
             decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            cpclPrinter.printBitmap(decodedByte, 140, 0,0);
            h=h+160;
            cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_name"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_address"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"  هاتف: "+shop.get(0).get("shop_contact"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true," مشتغل مرخص :  "+shop.get(0).get("shop_email"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            if(type.equals("2")) {
                cpclPrinter.printAndroidFont(null,true," فاتورة ضريبية:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;
                cpclPrinter.printAndroidFont(null,true,"نسخة ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;
            }
            else  if(type.equals("12")) {
                cpclPrinter.printAndroidFont(null,true," مردودات مبيعات :" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;
                cpclPrinter.printAndroidFont(null,true,"نسخة ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;
            }
            else{
                cpclPrinter.printAndroidFont(null,true,"طلبية مبيعات " , nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;

            }
            cpclPrinter.printAndroidFont(null,true,order.get(0).get("order_payment_method"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true," السادة:  "+name, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"الصنف                             السعر            الكمية            المبلغ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);


            y = h;
        for (int i = 1; i < details.size() + 1; i++) {
            double price = Double.parseDouble(details.get(i - 1).get("product_price"));
            int quantity = Integer.parseInt(details.get(i - 1).get("product_qty"));
            Double cost = quantity * price;
            String ccc = cost.toString();
            String quntity = details.get(i - 1).get("product_qty");
            String pricee = details.get(i - 1).get("product_price");
            databaseAccess.open();
            String unit = databaseAccess.getWeightUnitName(details.get(i - 1).get("product_weight"));
            if (ccc.length() < 4) {
                ccc = ".." + ccc + " ";
                quntity = ".." + quntity;
                pricee = ".." + pricee;
            }
            if (ccc.length() == 4) {
                ccc = "0" + ccc;
            }
            y = y + 30;
            databaseAccess.open();
            String name_of_product = databaseAccess.getProductName(details.get(i - 1).get("product_name"));
            cpclPrinter.printAndroidFont("  " + ccc + "               " + quntity + "                " + pricee, nLineWidth, 24, y + 0, CPCLConst.LK_CPCL_LEFT);

            if(isProbablyArabic(name_of_product)) {
                if (name_of_product.length() < 25) {
                    cpclPrinter.printAndroidFont(null,true,name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);

                } else {
                    String n1;
                    String n2;
                    n1 = name_of_product.substring(0, 25);
                    n2 = name_of_product.substring(25);
                    cpclPrinter.printAndroidFont(null,true,n1, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,n2, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);


                }
            }
            else{
                if (name_of_product.length() < 25) {
                    cpclPrinter.printAndroidFont(null,true,name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);

                } else {
                    String n1;
                    String n2;
                    n1 = name_of_product.substring(0, 25);
                    n2 = name_of_product.substring(25);
                    cpclPrinter.printAndroidFont(null,true,n1, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,n2, nLineWidth, 20, y, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);


                }
            }

        }

        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, y + 30, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont(null,true," المجموع:  " + total, nLineWidth, 24, y + 60, CPCLConst.LK_CPCL_RIGHT);
        cpclPrinter.printAndroidFont(null,true," العدد:  " + details.size(), nLineWidth, 24, y + 60, CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont(null,true," ض . ق .م : مشمولة  ", nLineWidth, 24, y + 100, CPCLConst.LK_CPCL_RIGHT);
        cpclPrinter.printAndroidFont(null,true," المبلغ:  " + total, nLineWidth, 24, y + 140, CPCLConst.LK_CPCL_RIGHT);
        cpclPrinter.printAndroidFont(null,true," الملاحظة:  " + order.get(0).get("discount"), nLineWidth, 24, y + 180, CPCLConst.LK_CPCL_LEFT);

        // Print
        cpclPrinter.printForm();
    }
    }
    public void print_bill(int paperType,String id,String cust , String balance ,String note ,Context context)throws IOException
    {

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        ArrayList<HashMap<String, String>> shop=databaseAccess.getShopInformation();
        String name=cust;
        int h=0;
        String imageee=shop.get(0).get("tax");
        byte[] decodedString = Base64.decode(imageee.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

        int nLineWidth = 576;   //3-inch
        cpclPrinter.setForm(0, 200, 200, 1000, nLineWidth, 1);
        cpclPrinter.setMedia(paperType);

        cpclPrinter.printBitmap(decodedByte, 140, 0,0);
        h=h+160;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_name"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_address"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"  هاتف: "+shop.get(0).get("shop_contact"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," مشتغل مرخص :  "+shop.get(0).get("shop_email"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"سند قبض:" + id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"اصلية" , nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," وصلنا من السادة:  "+name, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," مبلغ وقدره:  "+balance+" شيكل ", nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+80;
        cpclPrinter.printAndroidFont(null,true," توقيع المستلم:........................  ", nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," بتاريخ :  "+currentDate, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," ملاحظة :  "+note, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," شكرا لكم " , nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;

        cpclPrinter.printForm();
        h=0;

        cpclPrinter.setForm(0, 200, 200, 1000, nLineWidth, 1);
        cpclPrinter.setMedia(paperType);

        cpclPrinter.printBitmap(decodedByte, 140, 0,0);
        h=h+160;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_name"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_address"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"  هاتف: "+shop.get(0).get("shop_contact"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," مشتغل مرخص :  "+shop.get(0).get("shop_email"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"سند قبض:" + id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"نسخة" , nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," وصلنا من السادة:  "+name, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," مبلغ وقدره:  "+balance+" شيكل ", nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+80;
        cpclPrinter.printAndroidFont(null,true," توقيع المستلم:........................  ", nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," بتاريخ :  "+currentDate, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," ملاحظة :  "+note, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," شكرا لكم " , nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;

        cpclPrinter.printForm();

    }

    public void Print_recipte_copy(int count, int paper_type , List<HashMap<String, String>>  details, Context context ,double total ,int bill_id ,String type) throws IOException
    {
        //int nLineWidth = 384;   //2-inch
        int nLineWidth = 576;   //3-inch
        //int nLineWidth = 832;   //4-inch
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        ArrayList<HashMap<String, String>> shop=databaseAccess.getShopInformation();
        if (details.size()<6) {
            cpclPrinter.setForm(0, 200, 200, 1200, nLineWidth, count);
        }
        else if (details.size()<14) {
            cpclPrinter.setForm(0, 200, 200, 1700, nLineWidth, count);

        }
        else if (details.size()<24) {
            cpclPrinter.setForm(0, 200, 200, 2200, nLineWidth, count);

        }
        else if (details.size()<32){
            cpclPrinter.setForm(0, 200, 200, 2700, nLineWidth, count);

        }
        else {
            cpclPrinter.setForm(0, 200, 200, 3200, nLineWidth, count);

        }
        cpclPrinter.setMedia(paper_type);


        databaseAccess.open();
        ArrayList<HashMap<String, String>> order=databaseAccess.getOrder_incoice(details.get(0).get("invoice_id"));
        databaseAccess.open();
        String name;
        name=databaseAccess.getCustomerNAme(order.get(0).get("customer_name"));

        int h=0;
        String imageee=shop.get(0).get("tax");
        byte[] decodedString = Base64.decode(imageee.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        cpclPrinter.printBitmap(decodedByte, 140, 0,0);
        h=h+160;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_name"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_address"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"  هاتف: "+shop.get(0).get("shop_contact"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," مشتغل مرخص :  "+shop.get(0).get("shop_email"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        if(type.equals("2")) {
            cpclPrinter.printAndroidFont(null,true," فاتورة ضريبية:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"نسخة ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
        }
        else if(type.equals("12")) {
            cpclPrinter.printAndroidFont(null,true," مردودات مبيعات:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"نسخة ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
        }
        else{
            cpclPrinter.printAndroidFont(null,true,"طلبية مبيعات " , nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;

        }
        cpclPrinter.printAndroidFont(null,true,order.get(0).get("order_payment_method"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," السادة:  "+name, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"الصنف                             السعر            الكمية            المبلغ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);

        int y=h;
        for(int i=1; i<details.size()+1;i++){
            double price = Double.parseDouble(details.get(i-1).get("product_price"));
            int quantity = Integer.parseInt(details.get(i-1).get("product_qty"));
            Double cost = quantity * price;
            String ccc=cost.toString();
            String quntity=details.get(i-1).get("product_qty");
            String pricee=details.get(i-1).get("product_price");
            databaseAccess.open();
            String unit=databaseAccess.getWeightUnitName(details.get(i-1).get("product_weight"));
            if(ccc.length()<4){
                ccc=".."+ccc+" ";
                quntity=".."+quntity;
                pricee=".."+pricee;
            }
            y=y+30;
            databaseAccess.open();
            String name_of_product=databaseAccess.getProductName(details.get(i-1).get("product_name"));
            cpclPrinter.printAndroidFont("  "+ccc+"               "+quntity+"                "+pricee, nLineWidth, 24, y+0, CPCLConst.LK_CPCL_LEFT);
            if(isProbablyArabic(name_of_product)) {
                if (name_of_product.length() < 25) {
                    cpclPrinter.printAndroidFont(null,true,name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);

                } else {
                    String n1;
                    String n2;
                    n1 = name_of_product.substring(0, 25);
                    n2 = name_of_product.substring(25);
                    cpclPrinter.printAndroidFont(null,true,n1, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,n2, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);


                }
            }
            else{
                if (name_of_product.length() < 25) {
                    cpclPrinter.printAndroidFont(null,true,name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);

                } else {
                    String n1;
                    String n2;
                    n1 = name_of_product.substring(0, 25);
                    n2 = name_of_product.substring(25);
                    cpclPrinter.printAndroidFont(null,true,n1, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,n2, nLineWidth, 20, y, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);


                }
            }


        }

        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, y+30, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont(null,true," المجموع:  "+total, nLineWidth, 24, y+60, CPCLConst.LK_CPCL_RIGHT);
        cpclPrinter.printAndroidFont(null,true," العدد:  "+details.size(), nLineWidth, 24, y+60, CPCLConst.LK_CPCL_LEFT);
        if (type.equals("2")) {
            cpclPrinter.printAndroidFont(null,true," ض . ق .م : مشمولة  ", nLineWidth, 24, y + 100, CPCLConst.LK_CPCL_RIGHT);
            cpclPrinter.printAndroidFont(null,true," المبلغ:  " + total, nLineWidth, 24, y + 140, CPCLConst.LK_CPCL_RIGHT);
        }
        cpclPrinter.printAndroidFont(null,true," الملاحظة:  "+order.get(0).get("discount"), nLineWidth, 24, y+180, CPCLConst.LK_CPCL_LEFT);

        // Print
        cpclPrinter.printForm();

    }



    public String Get_Status() throws UnsupportedEncodingException
    {
        String result = "";
        if(!(cpclPrinter.printerCheck() < 0))
        {
            int sts = cpclPrinter.status();
            if(sts == CPCLConst.LK_STS_CPCL_NORMAL)
                return "Normal";
            if((sts & CPCLConst.LK_STS_CPCL_BUSY) > 0)
                result = result + "Busy\r\n";
            if((sts & CPCLConst.LK_STS_CPCL_PAPER_EMPTY) > 0)
                result = result + "Paper empty\r\n";
            if((sts & CPCLConst.LK_STS_CPCL_COVER_OPEN) > 0)
                result = result + "Cover open\r\n";
            if((sts & CPCLConst.LK_STS_CPCL_BATTERY_LOW) > 0)
                result = result + "Battery low\r\n";
        }
        else
        {
            result = "Check the printer\r\nNo response";
        }
        return result;
    }

    public void Print_PDFFile(int paper_type)
    {
        int nLineWidth = 384;   //2-inch
        //int nLineWidth = 576;   //3-inch
        //int nLineWidth = 832;   //4-inch

        /* ********************************* Notice *********************************
        printPDFFile() is a function that can print a PDF file, and the quantity part is always fixed to 1 in setForm().
        If a value other than 1 is written, it will not be output normally.
        To use compress in printPDFFile(), you must add jniLibs folder referring to Sample.
        ***************************************************************************** */
        try {

            cpclPrinter.setForm(0, 200, 200, 406, nLineWidth, 1);   //always quantity should be written as 1.
            cpclPrinter.setMedia(paper_type);

            cpclPrinter.printPDFFile("//sdcard//temp//test//PDF_Sample.pdf", 0, 0, nLineWidth, 0);
            cpclPrinter.printForm();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isProbablyArabic(String s) {
        for (int i = 0; i < s.length();) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return true;
            i += Character.charCount(c);
        }
        return false;
    }


    public void Print_recipte2(int count, int paper_type , List<HashMap<String, String>>  details, Context context ,double total ,int bill_id ,String type) throws IOException
    {
        //int nLineWidth = 384;   //2-inch
        int nLineWidth = 576;   //3-inch
        //int nLineWidth = 832;   //4-inch
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        ArrayList<HashMap<String, String>> shop=databaseAccess.getShopInformation();
        if (details.size()<6) {
            cpclPrinter.setForm(0, 200, 200, 1200, nLineWidth, count);
        }
        else if (details.size()<14) {
            cpclPrinter.setForm(0, 200, 200, 1700, nLineWidth, count);

        }
        else if (details.size()<24) {
            cpclPrinter.setForm(0, 200, 200, 2200, nLineWidth, count);

        }
        else if (details.size()<32){
            cpclPrinter.setForm(0, 200, 200, 2700, nLineWidth, count);

        }
        else {
            cpclPrinter.setForm(0, 200, 200, 3200, nLineWidth, count);

        }
        cpclPrinter.setMedia(paper_type);


        databaseAccess.open();
        ArrayList<HashMap<String, String>> order=databaseAccess.getOrder_incoice(details.get(0).get("invoice_id"));
        databaseAccess.open();
        String name;
        name=databaseAccess.getCustomerNAme(order.get(0).get("customer_name"));

        int h=0;
        String imageee=shop.get(0).get("tax");
        byte[] decodedString = Base64.decode(imageee.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        cpclPrinter.printBitmap(decodedByte, 140, 0,0);
        h=h+160;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_name"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_address"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"  هاتف: "+shop.get(0).get("shop_contact"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," مشتغل مرخص :  "+shop.get(0).get("shop_email"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        if(type.equals("2")) {
            cpclPrinter.printAndroidFont(null,true," فاتورة ضريبية:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"اصلية ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
        }
        else  if(type.equals("12")){
            cpclPrinter.printAndroidFont(null,true,"مردودات مبيعات:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"اصلية ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
        }
        else{
            cpclPrinter.printAndroidFont(null,true,"طلبية مبيعات " , nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;

        }
        cpclPrinter.printAndroidFont(null,true,order.get(0).get("order_payment_method"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," السادة:  "+name, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+20;
        cpclPrinter.printAndroidFont(null,true,"الصنف", nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+30;
        cpclPrinter.printAndroidFont(null,true,"   السعر                             الكمية                             المبلغ ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);

        int y=h;
        for(int i=1; i<details.size()+1;i++){
            double price = Double.parseDouble(details.get(i-1).get("product_price"));
            int quantity = Integer.parseInt(details.get(i-1).get("product_qty"));
            Double cost = quantity * price;
            String ccc=cost.toString();
            String quntity=details.get(i-1).get("product_qty");
            String pricee=details.get(i-1).get("product_price");
            databaseAccess.open();
            String unit=databaseAccess.getWeightUnitName(details.get(i-1).get("product_weight"));
            if(ccc.length()<4){
                ccc=".."+ccc+" ";
                quntity=".."+quntity;
                pricee=".."+pricee;
            }

            databaseAccess.open();
            String name_of_product=databaseAccess.getProductName(details.get(i-1).get("product_name"));
            y=y+30;
            if(isProbablyArabic(name_of_product) ){
                cpclPrinter.printAndroidFont(null, true, name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
            }
            else{
                cpclPrinter.printAndroidFont(null, true, name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);


            }
            cpclPrinter.printAndroidFont(null,true,"     "+unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_RIGHT);
            y=y+30;
            cpclPrinter.printAndroidFont(ccc+"                                 "+quntity+"                              "+pricee, nLineWidth, 24, y+0, CPCLConst.LK_CPCL_CENTER);
            y=y+20;
            cpclPrinter.printAndroidFont(null,true,"--------------------------------------------------------------------------------------", nLineWidth, 24, y, CPCLConst.LK_CPCL_CENTER);

           /* if(isProbablyArabic(name_of_product)) {
                if (name_of_product.length() < 25) {
                    cpclPrinter.printAndroidFont(null,true,name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);

                } else {
                    String n1;
                    String n2;
                    n1 = name_of_product.substring(0, 25);
                    n2 = name_of_product.substring(25);
                    cpclPrinter.printAndroidFont(null,true,n1, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,n2, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);


                }
            }
            else{
                if (name_of_product.length() < 25) {
                    cpclPrinter.printAndroidFont(null,true,name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);

                } else {
                    String n1;
                    String n2;
                    n1 = name_of_product.substring(0, 25);
                    n2 = name_of_product.substring(25);
                    cpclPrinter.printAndroidFont(null,true,n1, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,n2, nLineWidth, 20, y, CPCLConst.LK_CPCL_RIGHT);
                    y = y + 20;
                    cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);


                }
            }*/


        }

        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, y+30, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont(null,true," المجموع:  "+total, nLineWidth, 24, y+60, CPCLConst.LK_CPCL_RIGHT);
        cpclPrinter.printAndroidFont(null,true," العدد:  "+details.size(), nLineWidth, 24, y+60, CPCLConst.LK_CPCL_LEFT);
        if (type.equals("2")) {
            cpclPrinter.printAndroidFont(null,true," ض . ق .م : مشمولة  ", nLineWidth, 24, y + 100, CPCLConst.LK_CPCL_RIGHT);
            cpclPrinter.printAndroidFont(null,true," المبلغ:  " + total, nLineWidth, 24, y + 140, CPCLConst.LK_CPCL_RIGHT);
        }
        cpclPrinter.printAndroidFont(null,true," الملاحظة:  "+order.get(0).get("discount"), nLineWidth, 24, y+180, CPCLConst.LK_CPCL_LEFT);

        // Print
        cpclPrinter.printForm();
        /*
        if (type.equals("2") || type.equals("12")){
            if (details.size()<6) {
                cpclPrinter.setForm(0, 200, 200, 1200, nLineWidth, count);
            }
            else if (details.size()<14) {
                cpclPrinter.setForm(0, 200, 200, 1700, nLineWidth, count);

            }
            else if (details.size()<24) {
                cpclPrinter.setForm(0, 200, 200, 2200, nLineWidth, count);

            }
            else if (details.size()<32){
                cpclPrinter.setForm(0, 200, 200, 2700, nLineWidth, count);

            }
            else {
                cpclPrinter.setForm(0, 200, 200, 3200, nLineWidth, count);

            }
            cpclPrinter.setMedia(paper_type);


            h=0;
            imageee=shop.get(0).get("tax");
            decodedString = Base64.decode(imageee.getBytes(), Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            cpclPrinter.printBitmap(decodedByte, 140, 0,0);
            h=h+160;
            cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_name"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_address"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"  هاتف: "+shop.get(0).get("shop_contact"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true," مشتغل مرخص :  "+shop.get(0).get("shop_email"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            if(type.equals("2")) {
                cpclPrinter.printAndroidFont(null,true," فاتورة ضريبية:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;
                cpclPrinter.printAndroidFont(null,true,"نسخة ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;
            }
            else  if(type.equals("12")) {
                cpclPrinter.printAndroidFont(null,true," مردودات مبيعات :" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;
                cpclPrinter.printAndroidFont(null,true,"نسخة ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;
            }
            else{
                cpclPrinter.printAndroidFont(null,true,"طلبية مبيعات " , nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;

            }
            cpclPrinter.printAndroidFont(null,true,order.get(0).get("order_payment_method"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true," السادة:  "+name, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"الصنف                             السعر            الكمية            المبلغ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);


            y = h;
            for (int i = 1; i < details.size() + 1; i++) {
                double price = Double.parseDouble(details.get(i - 1).get("product_price"));
                int quantity = Integer.parseInt(details.get(i - 1).get("product_qty"));
                Double cost = quantity * price;
                String ccc = cost.toString();
                String quntity = details.get(i - 1).get("product_qty");
                String pricee = details.get(i - 1).get("product_price");
                databaseAccess.open();
                String unit = databaseAccess.getWeightUnitName(details.get(i - 1).get("product_weight"));
                if (ccc.length() < 4) {
                    ccc = ".." + ccc + " ";
                    quntity = ".." + quntity;
                    pricee = ".." + pricee;
                }
                if (ccc.length() == 4) {
                    ccc = "0" + ccc;
                }
                y = y + 30;
                databaseAccess.open();
                String name_of_product = databaseAccess.getProductName(details.get(i - 1).get("product_name"));
                cpclPrinter.printAndroidFont("  " + ccc + "               " + quntity + "                " + pricee, nLineWidth, 24, y + 0, CPCLConst.LK_CPCL_LEFT);

                if(isProbablyArabic(name_of_product)) {
                    if (name_of_product.length() < 25) {
                        cpclPrinter.printAndroidFont(null,true,name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
                        y = y + 20;
                        cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);

                    } else {
                        String n1;
                        String n2;
                        n1 = name_of_product.substring(0, 25);
                        n2 = name_of_product.substring(25);
                        cpclPrinter.printAndroidFont(null,true,n1, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
                        y = y + 20;
                        cpclPrinter.printAndroidFont(null,true,n2, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);
                        y = y + 20;
                        cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);


                    }
                }
                else{
                    if (name_of_product.length() < 25) {
                        cpclPrinter.printAndroidFont(null,true,name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);
                        y = y + 20;
                        cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);

                    } else {
                        String n1;
                        String n2;
                        n1 = name_of_product.substring(0, 25);
                        n2 = name_of_product.substring(25);
                        cpclPrinter.printAndroidFont(null,true,n1, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);
                        y = y + 20;
                        cpclPrinter.printAndroidFont(null,true,n2, nLineWidth, 20, y, CPCLConst.LK_CPCL_RIGHT);
                        y = y + 20;
                        cpclPrinter.printAndroidFont(null,true,unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_LEFT);


                    }
                }

            }

            cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, y + 30, CPCLConst.LK_CPCL_CENTER);
            cpclPrinter.printAndroidFont(null,true," المجموع:  " + total, nLineWidth, 24, y + 60, CPCLConst.LK_CPCL_RIGHT);
            cpclPrinter.printAndroidFont(null,true," العدد:  " + details.size(), nLineWidth, 24, y + 60, CPCLConst.LK_CPCL_LEFT);
            cpclPrinter.printAndroidFont(null,true," ض . ق .م : مشمولة  ", nLineWidth, 24, y + 100, CPCLConst.LK_CPCL_RIGHT);
            cpclPrinter.printAndroidFont(null,true," المبلغ:  " + total, nLineWidth, 24, y + 140, CPCLConst.LK_CPCL_RIGHT);
            cpclPrinter.printAndroidFont(null,true," الملاحظة:  " + order.get(0).get("discount"), nLineWidth, 24, y + 180, CPCLConst.LK_CPCL_LEFT);

            // Print
            cpclPrinter.printForm();
        }

         */
    }
    public void Print_recipte3(int count, int paper_type , List<HashMap<String, String>>  details, Context context ,double total ,int bill_id ,String type) throws IOException
    {
        //int nLineWidth = 384;   //2-inch
        int nLineWidth = 576;   //3-inch
        //int nLineWidth = 832;   //4-inch
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        ArrayList<HashMap<String, String>> shop=databaseAccess.getShopInformation();
        if (details.size()<6) {
            cpclPrinter.setForm(0, 200, 200, 1200, nLineWidth, count);
        }
        else if (details.size()<14) {
            cpclPrinter.setForm(0, 200, 200, 1700, nLineWidth, count);

        }
        else if (details.size()<24) {
            cpclPrinter.setForm(0, 200, 200, 2200, nLineWidth, count);

        }
        else if (details.size()<32){
            cpclPrinter.setForm(0, 200, 200, 2700, nLineWidth, count);

        }
        else {
            cpclPrinter.setForm(0, 200, 200, 3200, nLineWidth, count);

        }
        cpclPrinter.setMedia(paper_type);


        databaseAccess.open();
        ArrayList<HashMap<String, String>> order=databaseAccess.getOrder_incoice(details.get(0).get("invoice_id"));
        databaseAccess.open();
        String name;
        name=databaseAccess.getCustomerNAme(order.get(0).get("customer_name"));

        int h=0;
        String imageee=shop.get(0).get("tax");
        byte[] decodedString = Base64.decode(imageee.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        cpclPrinter.printBitmap(decodedByte, 140, 0,0);
        h=h+160;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_name"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_address"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"  هاتف: "+shop.get(0).get("shop_contact"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," مشتغل مرخص :  "+shop.get(0).get("shop_email"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        if(type.equals("2")) {
            cpclPrinter.printAndroidFont(null,true," فاتورة ضريبية:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            if (bill_id>0) {
                cpclPrinter.printAndroidFont(null, true, "اصلية ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h = h + 40;
            }
            else {
                cpclPrinter.printAndroidFont(null, true, "نسخة ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h = h + 40;
            }
        }
        else  if(type.equals("12")){
            cpclPrinter.printAndroidFont(null,true,"مردودات مبيعات:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            if(bill_id>0) {
                cpclPrinter.printAndroidFont(null, true, "اصلية ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h = h + 40;
            }
            else {
                cpclPrinter.printAndroidFont(null, true, "نسخة ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h = h + 40;
            }
        }
        else{
            cpclPrinter.printAndroidFont(null,true,"طلبية مبيعات " , nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;

        }
        cpclPrinter.printAndroidFont(null,true,order.get(0).get("order_payment_method"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true," السادة:  "+name, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+20;
        cpclPrinter.printAndroidFont(null,true,"الصنف", nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
        h=h+30;
        cpclPrinter.printAndroidFont(null,true,"   السعر                             الكمية                             المبلغ ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
        h=h+40;
        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);

        int y=h;
        for(int i=1; i<details.size()+1;i++){
            double price = Double.parseDouble(details.get(i-1).get("product_price"));
            int quantity = Integer.parseInt(details.get(i-1).get("product_qty"));
            Double cost = quantity * price;
            String ccc=cost.toString();
            String quntity=details.get(i-1).get("product_qty");
            String pricee=details.get(i-1).get("product_price");
            databaseAccess.open();
            String unit=databaseAccess.getWeightUnitName(details.get(i-1).get("product_weight"));
            if(ccc.length()<4){
                ccc=".."+ccc+" ";
                quntity=".."+quntity;
                pricee=".."+pricee;
            }

            databaseAccess.open();
            String name_of_product=databaseAccess.getProductName(details.get(i-1).get("product_name"));
            y=y+30;
            if(isProbablyArabic(name_of_product)) {
                cpclPrinter.printAndroidFont(null, true, name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
            }
            else{
                cpclPrinter.printAndroidFont(null, true, name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);

            }            cpclPrinter.printAndroidFont(null,true,"     "+unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_RIGHT);
            y=y+30;
            cpclPrinter.printAndroidFont(ccc+"                                 "+quntity+"                              "+pricee, nLineWidth, 24, y+0, CPCLConst.LK_CPCL_CENTER);
            y=y+20;
            cpclPrinter.printAndroidFont(null,true,"--------------------------------------------------------------------------------------", nLineWidth, 24, y, CPCLConst.LK_CPCL_CENTER);


        }

        cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, y+30, CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.printAndroidFont(null,true," المجموع:  "+total, nLineWidth, 24, y+60, CPCLConst.LK_CPCL_RIGHT);
        cpclPrinter.printAndroidFont(null,true," العدد:  "+details.size(), nLineWidth, 24, y+60, CPCLConst.LK_CPCL_LEFT);
        if (type.equals("2")) {
            cpclPrinter.printAndroidFont(null,true," ض . ق .م : مشمولة  ", nLineWidth, 24, y + 100, CPCLConst.LK_CPCL_RIGHT);
            cpclPrinter.printAndroidFont(null,true," المبلغ:  " + total, nLineWidth, 24, y + 140, CPCLConst.LK_CPCL_RIGHT);
        }
        cpclPrinter.printAndroidFont(null,true," الملاحظة:  "+order.get(0).get("discount"), nLineWidth, 24, y+180, CPCLConst.LK_CPCL_LEFT);

        // Print
        cpclPrinter.printForm();
        if((type.equals("2") || type.equals("12") ) && bill_id>0){

            if (details.size()<6) {
                cpclPrinter.setForm(0, 200, 200, 1200, nLineWidth, count);
            }
            else if (details.size()<14) {
                cpclPrinter.setForm(0, 200, 200, 1700, nLineWidth, count);

            }
            else if (details.size()<24) {
                cpclPrinter.setForm(0, 200, 200, 2200, nLineWidth, count);

            }
            else if (details.size()<32){
                cpclPrinter.setForm(0, 200, 200, 2700, nLineWidth, count);

            }
            else {
                cpclPrinter.setForm(0, 200, 200, 3200, nLineWidth, count);

            }
            cpclPrinter.setMedia(paper_type);
             h=0;
            cpclPrinter.printBitmap(decodedByte, 140, 0,0);
            h=h+160;
            cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_name"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,shop.get(0).get("shop_address"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"  هاتف: "+shop.get(0).get("shop_contact"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true," مشتغل مرخص :  "+shop.get(0).get("shop_email"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            if(type.equals("2")) {
                cpclPrinter.printAndroidFont(null,true," فاتورة ضريبية:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;
                cpclPrinter.printAndroidFont(null, true, "نسخة ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h = h + 40;
            }
            else  if(type.equals("12")){
                cpclPrinter.printAndroidFont(null,true,"مردودات مبيعات:" + bill_id, nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;
                cpclPrinter.printAndroidFont(null, true, "نسخة ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h = h + 40;
            }
            else{
                cpclPrinter.printAndroidFont(null,true,"طلبية مبيعات " , nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
                h=h+40;

            }
            cpclPrinter.printAndroidFont(null,true,order.get(0).get("order_payment_method"), nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true," السادة:  "+name, nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+20;
            cpclPrinter.printAndroidFont(null,true,"الصنف", nLineWidth, 24, h, CPCLConst.LK_CPCL_LEFT);
            h=h+30;
            cpclPrinter.printAndroidFont(null,true,"   السعر                             الكمية                             المبلغ ", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);
            h=h+40;
            cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, h, CPCLConst.LK_CPCL_CENTER);

             y=h;
            for(int i=1; i<details.size()+1;i++){
                double price = Double.parseDouble(details.get(i-1).get("product_price"));
                int quantity = Integer.parseInt(details.get(i-1).get("product_qty"));
                Double cost = quantity * price;
                String ccc=cost.toString();
                String quntity=details.get(i-1).get("product_qty");
                String pricee=details.get(i-1).get("product_price");
                databaseAccess.open();
                String unit=databaseAccess.getWeightUnitName(details.get(i-1).get("product_weight"));
                if(ccc.length()<4){
                    ccc=".."+ccc+" ";
                    quntity=".."+quntity;
                    pricee=".."+pricee;
                }

                databaseAccess.open();
                String name_of_product=databaseAccess.getProductName(details.get(i-1).get("product_name"));
                y=y+30;
                if(isProbablyArabic(name_of_product)) {
                    cpclPrinter.printAndroidFont(null, true, name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_LEFT);
                }
                else{
                    cpclPrinter.printAndroidFont(null, true, name_of_product, nLineWidth, 20, y + 0, CPCLConst.LK_CPCL_RIGHT);

                }
                cpclPrinter.printAndroidFont(null,true,"     "+unit, nLineWidth, 20, y, CPCLConst.LK_CPCL_RIGHT);
                y=y+30;
                cpclPrinter.printAndroidFont(ccc+"                                 "+quntity+"                              "+pricee, nLineWidth, 24, y+0, CPCLConst.LK_CPCL_CENTER);
                y=y+20;
                cpclPrinter.printAndroidFont(null,true,"--------------------------------------------------------------------------------------", nLineWidth, 24, y, CPCLConst.LK_CPCL_CENTER);


            }

            cpclPrinter.printAndroidFont(null,true,"*******************************************************", nLineWidth, 24, y+30, CPCLConst.LK_CPCL_CENTER);
            cpclPrinter.printAndroidFont(null,true," المجموع:  "+total, nLineWidth, 24, y+60, CPCLConst.LK_CPCL_RIGHT);
            cpclPrinter.printAndroidFont(null,true," العدد:  "+details.size(), nLineWidth, 24, y+60, CPCLConst.LK_CPCL_LEFT);
            if (type.equals("2")) {
                cpclPrinter.printAndroidFont(null,true," ض . ق .م : مشمولة  ", nLineWidth, 24, y + 100, CPCLConst.LK_CPCL_RIGHT);
                cpclPrinter.printAndroidFont(null,true," المبلغ:  " + total, nLineWidth, 24, y + 140, CPCLConst.LK_CPCL_RIGHT);
            }
            cpclPrinter.printAndroidFont(null,true," الملاحظة:  "+order.get(0).get("discount"), nLineWidth, 24, y+180, CPCLConst.LK_CPCL_LEFT);

            // Print
            cpclPrinter.printForm();
        }

    }
}

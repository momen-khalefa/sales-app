package com.app.restaurantpos.cpcl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.app.restaurantpos.R;
import com.app.restaurantpos.assist.ResourceInstaller;
import com.app.restaurantpos.assist.Sample_Print;
import com.sewoo.jpos.command.CPCLConst;

import java.util.ArrayList;

public class Menu_Activity extends BaseActivity implements Button.OnClickListener{

    private Button button_profile;
    private Button button_bar_1d;
    private Button button_bar_2d;
    private Button button_image;
    private Button button_stamp;
    private Button button_font;
    private Button button_setmag;
    private Button button_multiline;
    private Button button_android_font;
    private Button button_multi;
    private Button button_status;
    private Button button_pdf;
    private Spinner spinner_media_type;

    private ArrayAdapter<String> adapter_media;

    Sample_Print sample;

    String con_type = "";
    int count = 1;
    int paperType = CPCLConst.LK_CPCL_CONTINUOUS;


    String[] str_media = {"Gap Paper","Black Mark Paper","Continuous Paper"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_layout);

        ResourceInstaller ri = new ResourceInstaller();
        ri.copyAssets(getAssets(), "temp");

        Intent in = getIntent();
        con_type = in.getStringExtra("Connection");

        con_type="BlueTooth";
        if(con_type.equals("BlueTooth"))
            activity_list.add(Menu_Activity.this);

        sample = new Sample_Print();

        button_profile = (Button)findViewById(R.id.ButtonProfile);
        button_bar_1d = (Button)findViewById(R.id.Button1DBarcode);
        button_bar_2d = (Button)findViewById(R.id.Button2DBarcode);
        button_image = (Button)findViewById(R.id.ButtonImage);
        button_stamp = (Button)findViewById(R.id.ButtonStamp);
        button_font = (Button)findViewById(R.id.ButtonFont);
        button_setmag = (Button)findViewById(R.id.ButtonSetMag);
        button_multiline = (Button)findViewById(R.id.ButtonMultiline);
        button_android_font = (Button)findViewById(R.id.ButtonAndroidFont);
        button_multi = (Button)findViewById(R.id.ButtonMultilingual);
        button_status = (Button)findViewById(R.id.ButtonStatus);
        button_pdf = (Button)findViewById(R.id.ButtonPDFPrint);
        spinner_media_type = (Spinner)findViewById(R.id.SpinnerMediaType);

        ArrayList<String> arr_media = new ArrayList<String>();

        for(int i=0; i<str_media.length; i++)
            arr_media.add(str_media[i]);

        adapter_media = new ArrayAdapter<String>(Menu_Activity.this, android.R.layout.simple_spinner_dropdown_item, arr_media);

        spinner_media_type.setAdapter(adapter_media);
        spinner_media_type.setSelection(2);

        spinner_media_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i)
                {
                    case 0 : paperType = CPCLConst.LK_CPCL_LABEL; break;
                    case 1 : paperType = CPCLConst.LK_CPCL_BLACKMARK; break;
                    case 2 : paperType = CPCLConst.LK_CPCL_CONTINUOUS; break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        button_profile.setOnClickListener(this);
        button_bar_1d.setOnClickListener(this);
        button_bar_2d.setOnClickListener(this);
        button_image.setOnClickListener(this);
        button_stamp.setOnClickListener(this);
        button_font.setOnClickListener(this);
        button_setmag.setOnClickListener(this);
        button_multiline.setOnClickListener(this);
        button_android_font.setOnClickListener(this);
        button_multi.setOnClickListener(this);
        button_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    String str_status = sample.Get_Status();

                    new AlertDialog.Builder(Menu_Activity.this)
                            .setTitle("Printer Status")
                            .setMessage(str_status)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                catch(Exception e)
                {
                    Log.e("NumberFormatException","Invalid Input Nubmer.",e);
                }
            }
        });

        button_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sample.Print_PDFFile(paperType);
            }
        });
    }

    public int check_data(String str)
    {
        int input_num;

        if(str.equals(""))
           input_num = 1;
        else
            input_num = Integer.parseInt(str);

        return input_num;
    }

    @Override
    public void onClick(View view)
    {
        int re_val = 0;
        int input_count = 1;

        final int view_num = view.getId();

        final LinearLayout linear_popup = (LinearLayout)View.inflate(Menu_Activity.this, R.layout.input_layout, null);
        final EditText edit_input = (EditText)linear_popup.findViewById(R.id.EditTextPopup);

        edit_input.setText(Integer.toString(count));

        AlertDialog.Builder alert_popup = new AlertDialog.Builder(Menu_Activity.this);

        alert_popup
                .setTitle("Input Number")
                .setView(linear_popup)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String input_data = edit_input.getText().toString();
                        count = check_data(input_data);

                        try {
                            switch (view_num) {
                                case R.id.ButtonProfile:
                                    sample.Print_Profile(count, paperType);
                                    break;
                                case R.id.Button1DBarcode:
                                    sample.Print_1DBarcode(count, paperType);
                                    break;
                                case R.id.Button2DBarcode:
                                    sample.Print_2DBarcode(count, paperType);
                                    break;
                                case R.id.ButtonImage:
                                    sample.Print_Image(count, paperType);
                                    break;
                                case R.id.ButtonStamp:
                                    sample.Print_Stamp(count, paperType);
                                    break;
                                case R.id.ButtonFont:
                                    sample.Print_Font(count, paperType);
                                    break;
                                case R.id.ButtonSetMag:
                                    sample.Print_SetMag(count, paperType);
                                    break;
                                case R.id.ButtonMultiline:
                                    sample.Print_Multiline(count, paperType);
                                    break;
                                case R.id.ButtonAndroidFont:
                                    sample.Print_AndroidFont(count, paperType);
                                    break;
                                case R.id.ButtonMultilingual:
                                    sample.Print_MultilingualFont(count, paperType);
                                    break;
                            }
                        }
                        catch(Exception e)
                        {
                            Log.e("NumberFormatException","Invalid Input Nubmer.",e);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        if (con_type.equals("BlueTooth")) {
            activity_list.remove(1);
            ((Bluetooth_Activity) activity_list.get(0)).ExcuteDisconnect();
        }
        else if(con_type.equals("WiFi")) {
            ((WiFi_Activity) activity_wifi).ExcuteDisconnect();
        }

        super.onDestroy();
    }
}

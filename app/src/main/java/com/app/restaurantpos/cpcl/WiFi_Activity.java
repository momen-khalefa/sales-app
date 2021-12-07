package com.app.restaurantpos.cpcl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.app.restaurantpos.R;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.request.android.RequestHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class WiFi_Activity extends BaseActivity {

    private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp";
    private static final String fileName = dir + "/WFPrinter";

    private EditText edit_input;
    private Button button_connect;
    private ListView list_printer;

    private ArrayAdapter<String> adapter;
    private Thread wfThread;
    private WiFiPort wifiPort;
    private Vector<String> ipAddrVector;

    private String lastConnAddr;

    private boolean disconnectflags;

    ExcuteDisconnectWF WFdiscon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wifi_layout);

        edit_input = (EditText)findViewById(R.id.EditTextAddressIP);
        button_connect = (Button)findViewById(R.id.ButtonConnectIP);
        list_printer = (ListView)findViewById(R.id.ipList);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        list_printer.setAdapter(adapter);

        ipAddrVector = new Vector<String>();

        DisplayDeviceList();

        disconnectflags = false;
        activity_wifi = WiFi_Activity.this;

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    if(wifiPort.isConnected()) {
                        wifiPort.disconnect();
                        wfThread.interrupt();
                    }

                    String input_ip = edit_input.getText().toString();

                    if(input_ip.equals(""))
                    {
                        alert
                                .setTitle("Error")
                                .setMessage("Input value is Null")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                    else
                    {
                        wifiSetup(input_ip);
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        list_printer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    if(wifiPort.isConnected()) {
                        wifiPort.disconnect();
                        wfThread.interrupt();
                    }

                    String ip = ipAddrVector.elementAt(i);

                    wifiSetup(ip);

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        list_printer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            String ip;

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ip = ipAddrVector.elementAt(i);

                new AlertDialog.Builder(WiFi_Activity.this)
                        .setTitle("Wi-Fi connection history")
                        .setMessage("Delete '"+ip+"' ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                deleteIpList(ip);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        })
                        .show();

                return true;
            }
        });
    }

    private void deleteIpList(String addr)
    {
        if(ipAddrVector.contains(addr))
        {
            ipAddrVector.remove(addr);
        }
        if(adapter.getPosition(addr) >= 0)
        {
            adapter.remove(addr);
        }

        adapter.notifyDataSetChanged();
        saveSettingFile();
    }

    public void DisplayDeviceList()
    {
        adapter.clear();
        // Initialize
        wifiPort = WiFiPort.getInstance();

        loadSettingFile();
    }

    private void loadSettingFile()
    {
        String line;
        BufferedReader fReader;
        try
        {
            fReader = new BufferedReader(new FileReader(fileName));
            while((line = fReader.readLine()) != null)
            {
                ipAddrVector.addElement(line);
                adapter.add(line);
            }
            fReader.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void wifiSetup(String ip)
    {
        try {
            wifiConn(ip);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void wifiConn(String ipAddr) throws IOException
    {
        new connWF().execute(ipAddr);
    }

    class connWF extends AsyncTask<String, Void, Integer>
    {
        private final ProgressDialog dialog = new ProgressDialog(WiFi_Activity.this);
        private final AlertDialog.Builder alertDialog = new AlertDialog.Builder(WiFi_Activity.this);

        @Override
        protected void onPreExecute()
        {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Connecting Device...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params)
        {
            Integer retVal = null;
            try
            {	// ip
                wifiPort.connect(params[0]);
                lastConnAddr = params[0];
                retVal = Integer.valueOf(0);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                retVal = Integer.valueOf(-1);
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            if(dialog.isShowing())
                dialog.dismiss();

            if(result.intValue() == 0)	//Connection success.
            {
                RequestHandler rh = new RequestHandler();
                wfThread = new Thread(rh);
                wfThread.start();

                if(!ipAddrVector.contains(lastConnAddr))
                {
                    ipAddrVector.addElement(lastConnAddr);
                    adapter.add(lastConnAddr);

                    saveSettingFile();
                }

                Intent in = new Intent(WiFi_Activity.this, Menu_Activity.class);
                in.putExtra("Connection", "WiFi");
                startActivity(in);
            }
            else	//Connection failed.
            {
                alertDialog
                        .setTitle("Error")
                        .setMessage("Failed to connect WiFi device.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

            super.onPostExecute(result);
        }
    }

    private void saveSettingFile()
    {
        try
        {
            File tempDir = new File(dir);
            if(!tempDir.exists())
            {
                tempDir.mkdir();
            }
            BufferedWriter fWriter = new BufferedWriter(new FileWriter(fileName));
            Iterator<String> iter = ipAddrVector.iterator();
            while(iter.hasNext())
            {
                fWriter.write(iter.next());
                fWriter.newLine();
            }
            fWriter.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void DisconnectDevice()
    {
        try {
            if(wifiPort.isConnected())
                wifiPort.disconnect();

            if((wfThread != null) && (wfThread.isAlive()))
            {
                wfThread.interrupt();
                wfThread = null;
            }

            disconnectflags = true;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void ExcuteDisconnect()
    {
        WFdiscon = new ExcuteDisconnectWF();
        WFdiscon.execute();
    }

    private class ExcuteDisconnectWF extends AsyncTask<Void, Void, Void>{

        ProgressDialog asyncDialog = new ProgressDialog(WiFi_Activity.this);

        @Override
        protected void onPreExecute(){
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("Disconnecting Device...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
            super.onPreExecute();
        };

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                DisconnectDevice();

                while(true)
                {
                    if(disconnectflags)
                        break;

                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            asyncDialog.dismiss();
            disconnectflags = false;
            super.onPostExecute(result);
        };
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        try {
            if(wifiPort.isConnected())
                wifiPort.disconnect();

            saveSettingFile();

            if((wfThread != null) && (wfThread.isAlive()))
            {
                wfThread.interrupt();
                wfThread = null;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


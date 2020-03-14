package com.journaldev.MapsInAction;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class EntData extends AppCompatActivity {
    EditText name;
    EditText latt;
    EditText longg;
    EditText messg;
    Button enter;
    public ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entdata);
        name = (EditText) findViewById(R.id.locnm);
        latt = (EditText) findViewById(R.id.loclt);
        longg = (EditText) findViewById(R.id.loclng);
        messg = (EditText) findViewById(R.id.loctg);
        enter = (Button) findViewById(R.id.enter);

    }
    public void OnLogin(View view){

        String st1 = name.getText().toString();
        String st2 = latt.getText().toString();
        String st3 = longg.getText().toString();
        String st4 = messg.getText().toString();
        String res="";
        pd = new ProgressDialog(EntData.this);
        pd.setMessage("Processing");
        pd.setTitle("Progress");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
        pd.setCancelable(false);
        res=asEnter(st1,st2,st3,st4);
        if(res.equals("succ"))
        {
            Toast.makeText(this,"Successfully added new Location!!",Toast.LENGTH_SHORT).show();
        }

    }
    public String asEnter(final String nm,final String lt,final String ln,final String mg) {
        class AsLogin extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String ... voids) {

                String result = "";
                String name = nm;
                String latt = lt;
                String lngg = ln;
                String mssg = mg;
                String connstr = "http://dronecontrol.000webhostapp.com/entdata.php";
                try {
                    URL url = new URL(connstr);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoInput(true);
                    http.setConnectTimeout(3000);
                    http.setDoOutput(true);
                    OutputStream ops = http.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&&" + URLEncoder.encode("latt", "UTF-8") + "=" + URLEncoder.encode(latt, "UTF-8")+ "&&" + URLEncoder.encode("lngg", "UTF-8") + "=" + URLEncoder.encode(lngg, "UTF-8")+ "&&" + URLEncoder.encode("mssg", "UTF-8") + "=" + URLEncoder.encode(mssg, "UTF-8");
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    ops.close();
                    InputStream ips = http.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ips, "ISO-8859-1"));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        result += line;

                    }
                    reader.close();
                    ips.close();
                    http.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    result = e.getMessage();
                } catch (IOException e) {
                    result = e.getMessage();
                }
                return result;
            }

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected void onPostExecute(String s) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                if(s.equals("failed"))
                {
                    android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(EntData.this);
                    alertDialogBuilder.setTitle("Query Status");
                    alertDialogBuilder.setMessage("Can't find Database!!");
                    alertDialogBuilder.setPositiveButton("OK",null);
                    alertDialogBuilder.show();
                }
                else if (!s.equals("succ"))
                {
                    android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(EntData.this);
                    alertDialogBuilder.setTitle("Entry Status");
                    alertDialogBuilder.setMessage("Network Error");
                    alertDialogBuilder.setPositiveButton("OK",null);
                    alertDialogBuilder.show();
                }
            }

        }
        AsLogin lg = new AsLogin();
        try {
            String locs = lg.execute().get();
            return locs;
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }
}

package com.journaldev.MapsInAction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class Login extends AppCompatActivity {
    EditText usern;
    EditText passw;
    TextView reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        usern = (EditText) findViewById(R.id.uname);
        passw = (EditText) findViewById(R.id.pass);
        reg=(TextView)findViewById(R.id.regbut);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this,"Not Available for now",Toast.LENGTH_LONG).show();
            }
        });
        TextView forpass=(TextView)findViewById(R.id.nopass);
        forpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this,"Not Available for now",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void OnLogin(View view){
        String st1 = usern.getText().toString();
        String st2 = passw.getText().toString();
        String res=" ";
        res=asLogin(st1,st2);
        if(res.equals("succ"))
        {
            Intent intent = new Intent(this,IPInput.class);
            startActivity(intent);
        }
    }
    public String asLogin(final String un,final String ps) {
        class AsLogin extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog;
            public AsLogin(){
                dialog = new ProgressDialog(Login.this);
            }
            @Override
            protected void onPreExecute() {
                this.dialog.setMessage("Loading");
                this.dialog.show();
            }
            @Override
            protected String doInBackground(String ... voids) {
                String result = "";
                String user = un;
                String pass = ps;
                String connstr = "http://dronecontrol.000webhostapp.com/login.php";
                try {
                    URL url = new URL(connstr);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoInput(true);
                    http.setConnectTimeout(3000);
                    http.setDoOutput(true);
                    OutputStream ops = http.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                    String data = URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8") + "&&" + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8");
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
            protected void onPostExecute(String s) {

                if(s.equals("failed"))
                {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(Login.this);
                    alertDialogBuilder.setTitle("Login Status");
                    alertDialogBuilder.setMessage("Incorrect Username or Password");
                    alertDialogBuilder.setPositiveButton("OK",null);
                    alertDialogBuilder.show();
                }
                else if (!s.equals("succ"))
                {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(Login.this);
                    alertDialogBuilder.setTitle("Login Status");
                    alertDialogBuilder.setMessage("Network Error");
                    alertDialogBuilder.setPositiveButton("OK",null);
                    alertDialogBuilder.show();
                }
                else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
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
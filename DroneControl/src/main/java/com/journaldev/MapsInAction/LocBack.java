package com.journaldev.MapsInAction;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
public class LocBack extends AsyncTask<String,Void,String> {
        AlertDialog alertDialog;
        Context context;
        public LocBack(Context context)
        {
            this.context = context;

        }
        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Connection Status");
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("failed"))
            {
                alertDialog.setMessage("Connection failed");
                alertDialog.show();

            }
            else
            {
                login(s);
                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Showing "+s+" Available Locations.");
                alertDialogBuilder.setPositiveButton("OK",null);
                alertDialogBuilder.show();
            }
        }
        private String login(String result)
        {
            return result;
        }

        @Override
        protected String doInBackground(String... voids) {
            String result = "";
            int i=0;
            String user = voids[0];
            String pass= voids[1];
            String connstr = "http://dronecontrol.000webhostapp.com/locback.php";
            try {
                URL url = new URL(connstr);
                String data = URLEncoder.encode("uname","UTF-8")+"="+URLEncoder.encode(user,"UTF-8")+"&&"+URLEncoder.encode("pass","UTF-8")+"="+URLEncoder.encode(pass,"UTF-8");
                URLConnection http= url.openConnection();
                http.setDoOutput(true);
                OutputStreamWriter ops = new OutputStreamWriter(http.getOutputStream());
                ops.write(data);
                ops.flush();
                ops.close();
                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));
                String line ="";
                while ((line = reader.readLine()) !=null)
                {
                    result += line;

                }
                reader.close();
                ips.close();
                return result;
            } catch (MalformedURLException e) {
                result = e.getMessage();
            } catch (IOException e){
                result = e.getMessage();
            }
            return result;
        }
    }

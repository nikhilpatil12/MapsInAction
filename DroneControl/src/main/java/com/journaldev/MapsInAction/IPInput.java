package com.journaldev.MapsInAction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPInput extends AppCompatActivity {
    Button btnSaveIp;
    EditText txtIpInput,txtUsername,txtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipinput);
        btnSaveIp = (Button)findViewById(R.id.saveIp);
        txtIpInput = (EditText)findViewById(R.id.ipInput);
        txtUsername = (EditText)findViewById(R.id.sshusername);
        txtPassword = (EditText)findViewById(R.id.sshpassword);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("RpiPref", 0);
        final String ipaddress = pref.getString("ipAddress","192.168.43.199");
        final String piUname = pref.getString("sshUsername","pi");
        final String piPassw = pref.getString("sshPassword","raspberry");
        txtIpInput.setText(ipaddress);
        txtUsername.setText(piUname);
        txtPassword.setText(piPassw);
        final String IPADDRESS_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        final Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        btnSaveIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isLocal=false;
                String sIpaddress = txtIpInput.getText().toString();
                String sUsername = txtUsername.getText().toString();
                String sPassword = txtPassword.getText().toString();
                Matcher matcher = pattern.matcher(sIpaddress);
                try{
                    String[] ips = sIpaddress.split("[.]",4);
                    if(ips[0].equals("192") && ips[1].equals("168"))
                        isLocal=true;
                    Log.d("MSG",ips[0]+"."+ips[1]);
                }
                catch (Exception e){
                    Toast.makeText(IPInput.this,"Enter Valid local IPv4 address",Toast.LENGTH_LONG).show();
                }
                if(isLocal && matcher.matches())
                {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("RpiPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("ipAddress", sIpaddress);
                    editor.putString("sshUsername", sUsername);
                    editor.putString("sshPassword", sPassword);
                    editor.commit(); // commit changes
                    Intent intent = new Intent(IPInput.this,MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(IPInput.this,"Enter Valid local IPv4 address",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

package com.journaldev.MapsInAction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class Takeoff extends AppCompatActivity {
    SeekBar seekBar;
    TextView progresstag,takeoffstatus;
    int succ_flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.takeoff);

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        takeoffstatus = (TextView)findViewById(R.id.takeoffstatus);
        progresstag = (TextView)findViewById(R.id.progresstag);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("RpiPref", 0);
        final String ipaddress = pref.getString("ipAddress","192.168.43.199");
        final String piUname = pref.getString("sshUsername","pi");
        final String piPassw = pref.getString("sshPassword","raspberry");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if(progress<48){
                    seekBar.setProgress(0);
                }
                if(progress>=48)
                {
                    seekBar.setProgress(0);
                    if(succ_flag==0)
                    {
                        String res;
                        res=execSSH(ipaddress,piUname,piPassw);
                        if(res.equals("succ"))
                        {
                            //Intent intent = new Intent(Takeoff.this, Monitor.class);
                            //startActivity(intent);
                            takeoffstatus.setText("Takeoff Successfull!!");
                            progresstag.setText("Slide right to start mission!!");
                            succ_flag=1;
                        }
                        else {
                            Log.d("MSG1",res);
                            //Toast.makeText(MainActivity.this,"Error establishing Connection to the drone!!!",Toast.LENGTH_LONG).show();
                        }
                    }
                    else if(succ_flag==1){
                        String res;
                        res=execSSH2(ipaddress,piUname,piPassw);
                        if(res.equals("succ"))
                        {
                            Intent intent = new Intent(Takeoff.this, Monitor.class);
                            startActivity(intent);
                        }
                        else {
                            Log.d("MSG","Failed");
                            //Toast.makeText(MainActivity.this,"Error establishing Connection to the drone!!!",Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(Takeoff.this,"Error establishing Connection to the drone!!!",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }
    public String execSSH(final String ip, final String uname, final String passw) {
        class SshAsync extends AsyncTask<String, String, String> {
            @Override
            protected String doInBackground(String... strings) {
                String user = uname;
                String password = passw;
                String host = ip;
                Log.d("MSG@@",user+" "+password+" "+ip);
                int port = 22;
                try {
                    JSch jsch = new JSch();
                    Session session = jsch.getSession(user, host, port);
                    session.setPassword(password);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.setTimeout(10000);
                    session.connect();
                    try {
                        ChannelShell channel2 = (ChannelShell) session.openChannel("shell");
                        final ByteArrayOutputStream os = new ByteArrayOutputStream();
                        OutputStream out = channel2.getOutputStream();
                        channel2.setOutputStream(os);
                        channel2.connect();
                        //out.write("cp wp.txt ardupilot/ardupilot/ArduCopter/ \n cd ardupilot/ardupilot/ArduCopter/\n".getBytes());
                        //out.write("sim_vehicle.py --console --map -L RMCET\n".getBytes());
                        out.write(("mavproxy.py --master=/dev/serial0 --baudrate 921600 --aircraft MyCopter \n").getBytes());
                        out.write(("mode guided \nwp load wp.txt \narm throttle \n").getBytes());
                        out.write(("takeoff 5 \n").getBytes());
                        out.flush();
                        channel2.connect();
                        try {
                            Thread.sleep(10000);
                        } catch (Exception e) {
                        }
                        String op = new String(os.toByteArray());
                        Log.d("MSGSUCC", op);
                        if (op.contains("Loaded")) {
                            channel2.disconnect();
                            session.disconnect();
                            return "succ";
                        } else {
                            channel2.disconnect();
                            session.disconnect();
                            Log.d("MSG1", "in else");
                            return "fail";
                        }
                    } catch (Exception e) {
                        Log.i("MSG1", e.getMessage());
                    }
                    return "fail";
                } catch (JSchException e) {
                    Log.d("MSG1", e.getMessage());
                    return "fail";
                }
            }
        }
        SshAsync sshAsync = new SshAsync();
        try {
            String re = sshAsync.execute().get();
            return re;
        }
        catch (Exception e)
        {
            return "fail";
        }
    }
    public String execSSH2(final String ip, final String uname, final String passw) {
        class SshAsync2 extends AsyncTask<String, String, String> {
            @Override
            protected String doInBackground(String... strings) {
                String user = uname;
                String password = passw;
                String host = ip;
                int port = 22;
                try {
                    JSch jsch = new JSch();
                    Session session = jsch.getSession(user, host, port);
                    session.setPassword(password);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.setTimeout(10000);
                    session.connect();
                    try {
                        ChannelShell channel2 = (ChannelShell) session.openChannel("shell");
                        final ByteArrayOutputStream os = new ByteArrayOutputStream();
                        OutputStream out = channel2.getOutputStream();
                        channel2.setOutputStream(os);
                        channel2.connect();
                        //out.write("cp wp.txt ardupilot/ardupilot/ArduCopter/ \n cd ardupilot/ardupilot/ArduCopter/\n".getBytes());
                        //out.write("sim_vehicle.py --console --map -L RMCET\n".getBytes());
                        out.write(("mavproxy.py --master=/dev/serial0 --baudrate 921600 --aircraft MyCopter \n").getBytes());
                        out.write(("\nmode auto \n").getBytes());
                        out.flush();
                        channel2.connect();
                        try {
                            Thread.sleep(15000);
                        } catch (Exception e) {
                        }
                        String op = new String(os.toByteArray());
                        Log.d("MSGSUCC", op);
                        if (op.contains("waypoint 1")) {
                            channel2.disconnect();
                            session.disconnect();
                            return "succ";
                        } else {
                            channel2.disconnect();
                            session.disconnect();
                            Log.d("MSG", "in else");
                        }
                    } catch (Exception e) {
                        Log.i("MSG", e.getMessage());
                    }
                    return "fail";
                } catch (JSchException e) {
                    Log.d("MSG", e.getMessage());
                    return "fail";
                }
            }
        }
        SshAsync2 sshAsync2 = new SshAsync2();
        try {
            String re = sshAsync2.execute().get();
            return re;
        }
        catch (Exception e)
        {
            return "fail";
        }
    }
}

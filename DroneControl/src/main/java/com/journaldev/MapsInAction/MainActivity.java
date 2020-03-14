package com.journaldev.MapsInAction;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private boolean fabExpanded = false;
    private FloatingActionButton fab;
    DatabaseHelper myDb;
    Button b1,b2;
    public boolean isSuccess = false;
    boolean isRotate = false;
    private LinearLayout layoutFabUpdate;
    private LinearLayout layoutFabRefresh;
    private LinearLayout layoutFabFocus;
    public ConstraintLayout conlayt;
    private FloatingActionButton fabRefresh;
    private FloatingActionButton fabUpdate;
    private FloatingActionButton fabFocus;
    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleMap mMap;
    MarkerOptions mo;
    Marker marker;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab =(FloatingActionButton) this.findViewById(R.id.fabSetting);
        layoutFabUpdate = (LinearLayout)this.findViewById(R.id.layoutFabUts);
        layoutFabRefresh = (LinearLayout)this.findViewById(R.id.layoutFabRefs);
        layoutFabFocus = (LinearLayout)this.findViewById(R.id.layoutFabFocs) ;
        fabRefresh = (FloatingActionButton) this.findViewById(R.id.fabRef);
        fabUpdate = (FloatingActionButton) this.findViewById(R.id.fabUpdt);
        fabFocus = (FloatingActionButton) this.findViewById(R.id.fabFocus);
        conlayt = (ConstraintLayout) this.findViewById(R.id.conlayt);
        b1 = (Button)findViewById(R.id.button2);
        b2 = (Button)findViewById(R.id.button3);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenSettings();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Continue();
            }
        });
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getJSON("http://dronecontrol.000webhostapp.com/alllocs.php");
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else requestLocation();
        if (!isLocationEnabled())
            showAlert(1);
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                myDb=new DatabaseHelper(MainActivity.this);
                try {
                    Cursor res = myDb.getAllData();
                    if(res.getCount()== 0)
                    {
                        Toast.makeText(MainActivity.this,"Empty",Toast.LENGTH_LONG).show();
                        return;
                    }
                    else {
                        String[][] locas = new String[res.getCount()][2];
                        Double[][] locasd = new Double[res.getCount()][2];
                        int i=0;
                        while (res.moveToNext()){
                            locas[i][0] = res.getString(0);
                            locasd[i][0] = res.getDouble(1);
                            locasd[i][1] = res.getDouble(2);
                            locas[i][1] = res.getString(3);
                            i++;
                        }
                        for (i = 0; i < res.getCount(); i++) {
                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(locasd[i][0], locasd[i][1]))
                                    .title(locas[i][0])
                                    .snippet(locas[i][1])
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(17.05, 73.57), 10));
            }
        });
    }
});

        fabUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb=new DatabaseHelper(MainActivity.this);
                final String addr = "http://dronecontrol.000webhostapp.com/alllocs.php";
                String str = getJSON(addr);
                try {
                    if(!str.equals("error")) {
                        Toast.makeText(MainActivity.this,"Successfully Updated!!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Network Error!!",Toast.LENGTH_SHORT).show();
                    }
                    JSONArray jsonArray = new JSONArray(str);
                    String[][] locas = new String[jsonArray.length()][2];
                    Double[][] locasd = new Double[jsonArray.length()][2];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        locas[i][0] = obj.getString("nm");
                        locasd[i][0] = obj.getDouble("lati");
                        locasd[i][1] = obj.getDouble("longi");
                        locas[i][1] = obj.getString("msg");
                    }
                    myDb.deleteTable();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        myDb.insertData(locas[i][0],locasd[i][0].toString(),locasd[i][1].toString(),locas[i][1]);
                    }
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this,"Network Error",Toast.LENGTH_SHORT).show();
                }

            }
        });
        fabFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, EntData.class);
                startActivity(i);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotate = ViewAnimtion.rotateFab(v,!isRotate);
                if (fabExpanded == true){
                    ViewAnimtion.showOut(layoutFabUpdate);
                    ViewAnimtion.showOut(layoutFabRefresh);
                    ViewAnimtion.showOut(layoutFabFocus);
                    closeSubMenusFab();
                } else {
                    ViewAnimtion.showIn(layoutFabUpdate);
                    ViewAnimtion.showIn(layoutFabRefresh);
                    ViewAnimtion.showIn(layoutFabFocus);
                    openSubMenusFab();
                }
            }
        });
        closeSubMenusFab();
        ViewAnimtion.init(layoutFabUpdate);
        ViewAnimtion.init(layoutFabRefresh);
        ViewAnimtion.init(layoutFabFocus);
        fab.setVisibility(View.INVISIBLE);
    }
    public void OpenSettings(){
        //Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        Intent intent = new Intent();
        intent.setClassName("com.android.settings","com.android.settings.TetherSettings");
        startActivity(intent);
    }
    public void Continue(){
        ViewAnimtion.showOut(conlayt);
        fab.setVisibility(View.VISIBLE);
    }
    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutFabRefresh.setVisibility(View.INVISIBLE);
        layoutFabUpdate.setVisibility(View.INVISIBLE);
        layoutFabFocus.setVisibility(View.INVISIBLE);
        //fab.setImageResource(R.drawable.locat);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabRefresh.setVisibility(View.VISIBLE);
        layoutFabUpdate.setVisibility(View.VISIBLE);
        layoutFabFocus.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        //fab.setImageResource(R.drawable.ic_close);
        fabExpanded = true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onTaskComplete(String reslt){
        Toast.makeText(this,"The result is "+reslt,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        String st1="";
        String st2="";
        googleMap.setMyLocationEnabled(true);
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers =lm.getProviders(true);
        Location myLoc =null;
        for (String provider:providers){
            try {
                Location l = lm.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (myLoc == null || l.getAccuracy() < myLoc.getAccuracy()) {
                    myLoc = l;
                }
            }
            catch (Exception e){
                Toast.makeText(MainActivity.this,"Can't get location. Please check permissions!!",Toast.LENGTH_LONG).show();
            }
        }
        final double myLat = myLoc.getLatitude();
        final double myLng = myLoc.getLongitude();
        Circle circle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(myLat,myLng))
                .radius(3000)
                .strokeColor(Color.argb(88,0,0,125))
                .fillColor(Color.argb(88,0,125,0)));
        circle.setVisible(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                LatLng cord = marker.getPosition();
                final double cordLat = cord.latitude;
                final double cordLng = cord.longitude;
                SharedPreferences pref = getApplicationContext().getSharedPreferences("RpiPref", 0);
                final String ipaddress = pref.getString("ipAddress","192.168.43.199");
                final String piUname = pref.getString("sshUsername","pi");
                final String piPassw = pref.getString("sshPassword","raspberry");
                try {
                    float[] results = new float[1];
                    Location.distanceBetween(cordLat, cordLng,
                            myLat, myLng, results);
                    final int distance = (int) results[0];
                    if (distance >= 3000) {
                        Toast t = Toast.makeText(getApplicationContext(), "The parcel can't be delivered to " + marker.getTitle() + " Distance: " + distance + " m", Toast.LENGTH_SHORT);
                        t.show();
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setMessage("Are you sure to Deliver to " + marker.getTitle() + " Distance: " + distance + " m");
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        new AsyncTask<Integer, Void, Void>(){
                                            @Override
                                            protected Void doInBackground(Integer... params) {
                                                try {
                                                    isSuccess = executeSSHcommand(cordLat,cordLng,ipaddress,piUname,piPassw);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                                if(isSuccess)
                                                {
                                                    Intent intent = new Intent(MainActivity.this, Takeoff.class);
                                                    startActivity(intent);
                                                }
                                                else {
                                                    Log.d("MSG","Failed");
                                                    //Toast.makeText(MainActivity.this,"Error establishing Connection to the drone!!!",Toast.LENGTH_LONG).show();
                                                }
                                                return null;
                                            }
                                        }.execute();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast t = Toast.makeText(MainActivity.this, "Select other Locations", Toast.LENGTH_SHORT);
                                t.show();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
                catch(Exception e)
                {
                    Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        myDb=new DatabaseHelper(this);
        try {
            Cursor res = myDb.getAllData();
            if(res.getCount()== 0)
            {
                Toast.makeText(this,"Empty",Toast.LENGTH_LONG).show();
                return;
            }
            else {
                String[][] locas = new String[res.getCount()][2];
                Double[][] locasd = new Double[res.getCount()][2];
                int i=0;
                while (res.moveToNext()){
                    locas[i][0] = res.getString(0);
                    locasd[i][0] = res.getDouble(1);
                    locasd[i][1] = res.getDouble(2);
                    locas[i][1] = res.getString(3);
                    i++;
                }
                for (i = 0; i < res.getCount(); i++) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(locasd[i][0], locasd[i][1]))
                            .title(locas[i][0])
                            .snippet(locas[i][1])
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(17.05, 73.57), 10));
    }
    public boolean executeSSHcommand(double cordlat,double cordlng,String ipaddress,String uname,String passw){
        String user = uname;
        String password = passw;
        String host = ipaddress;//43.98
        int port=22;
        try{
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(10000);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            try {
                String file = Environment.getExternalStorageDirectory()+"/test.txt";
                File f = new File(file);
                f.createNewFile();
                PrintWriter writer = new PrintWriter(f);
                writer.println("QGC WPL 110\n"+"0\t1\t0\t16\t0\t0\t0\t0\t"+cordlat+"\t"+cordlng+"\t0.07\t1"+"\n1\t0\t3\t21\t0\t0\t0\t1\t"+cordlat+"\t"+cordlng+"\t0\t1"+"\n2\t0\t0\t93\t10\t-1\t-1\t-1\t0\t0\t0\t1"+"\n3\t0\t3\t22\t0\t0\t0\t0\t0\t0\t5\t1"+"\n4\t0\t0\t20\t0\t0\t0\t0\t0\t0\t0\t1");
                writer.close();
                sftpChannel.put(new FileInputStream(f), "wp.txt");
                session.disconnect();
                channel.disconnect();
            }
            catch (Exception e){
                Log.d("MSG","Error writing file");
                return false;
            }
            sftpChannel.exit();
            return  true;
        }
        catch(JSchException e){
            Log.d("MSG",e.getMessage());
            return false;
        }
    }
    //Locations requests
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    private void requestLocation() {
        Criteria criteria = new Criteria();
        //criteria.setAccuracy(Criteria.ACCURACY_LOW);
        //criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 10000, 10, this);
    }
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isPermissionGranted() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("mylog", "Permission is granted");
            return true;
        } else {
            Log.v("mylog", "Permission not granted");
            return false;
        }
    }
    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }
    //Get Locations JSON
    public String getJSON(final String urlWebService) {
        class GetJSON extends AsyncTask<Void, Void, String> {
            private ProgressDialog dialog;
            public GetJSON(){
                dialog = new ProgressDialog(MainActivity.this);
            }
            @Override
            protected void onPreExecute() {
                dialog.setMessage("Please wait, Loading Locations!!");
                dialog.show();
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                super.onPostExecute(s);
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return "error";
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        try {
            String locs = getJSON.execute().get();
            return locs;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
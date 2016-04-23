package com.example.jeevan.splash;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class GroupTripMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_GROUP_NAME = "GroupName";
    private static final String PREF_GROUP_DEST = "GroupDestination";
    private static final String PREF_USERNAME = "username";
    SharedPreferences sp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_trip_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final TextView t1 = (TextView) findViewById(R.id.Groupname);
        final TextView t2 = (TextView) findViewById(R.id.Destination);
        String gName = "";
        String gDest = "";
        sp = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        if(sp != null)
        {
            gName = sp.getString(PREF_GROUP_NAME, PREF_GROUP_NAME);
            gDest = sp.getString(PREF_GROUP_DEST, PREF_GROUP_DEST);
        }
        t1.setText(gName);
        t2.setText(gDest);

        getData();
    }

    


    public void getData()
    {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // this code will be executed after 2 seconds  
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("op", "5");
                    obj.put("phone", getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString(PREF_USERNAME, null));
                    URL url = new URL("http://192.168.0.106:8000/test");
                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    Location location;
                    if (ActivityCompat.checkSelfPermission(GroupTripMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GroupTripMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Log.i("prelocation", "came here");
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    Log.i("location", latitude+":"+longitude);
                    obj.put("lat", latitude+"");
                    obj.put("lng", longitude+"");
                    new sendDataInitial().execute(url.toString(), obj.toString());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000);
        
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        // Add a marker in Sydney and move the camera
        LatLng banglore = new LatLng(12.93, 77.53);
        LatLng mysore = new LatLng(12.3,76.65);
        mMap.addMarker(new MarkerOptions().position(banglore).title("Marker in bangalore"));
        mMap.addMarker(new MarkerOptions().position(mysore).title("Marker in Mysore"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(mysore));
    }
    public void exit(View v)
    {
        try
        {
            JSONObject obj = new JSONObject();
            obj.put("op", "3");
            obj.put("phone", sp.getString(PREF_USERNAME, null));
            URL url = new URL("http://192.168.0.106:8000/test");
            new sendData().execute(url.toString(), obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        sp.edit().remove(PREF_GROUP_NAME).remove(PREF_GROUP_DEST).commit();
        finish();

    }
    public void chat(View v)
    {
        startActivity(new Intent(GroupTripMap.this, ChatActivity.class));
    }

    public void callMembers(View v)
    {
        startActivity(new Intent(GroupTripMap.this, MembersActivity.class));
    }

    class sendData extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                String send = params[1];
                os.write(send.getBytes());
                os.flush();
                os.close();
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                String data = sb.toString();
                br.close();
                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }

    class sendDataInitial extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                String send = params[1];
                os.write(send.getBytes());
                os.flush();
                os.close();
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                String data = sb.toString();
                br.close();
                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            try
            {
                JSONObject object = new JSONObject(result);
                // TODO: 23/4/16 dislay the output from backend to the map 
                Log.i("map_thing", result);
            }
            catch (Exception e) {
            }
        }
    }
}

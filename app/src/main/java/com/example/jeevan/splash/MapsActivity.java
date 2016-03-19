<<<<<<< HEAD
package com.example.jeevan.splash;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;


public class MapsActivity extends FragmentActivity {

    private String source = null;
    private String destination = null;
    private String json_dict=null;
    ArrayList<LatLng> bounds = new ArrayList<LatLng>();

    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    ArrayList<LatLng> locationPoints;
    private LatLng boundSouthWest;
    private LatLng boundNorthEast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle bundle = getIntent().getExtras();
        if ( (getIntent().getStringExtra("json_dict") != null)){
            json_dict = bundle.getString("json_dict");
        }
        Toast.makeText(MapsActivity.this, json_dict, Toast.LENGTH_LONG).show();
        // Initializing
        markerPoints = new ArrayList<LatLng>();
        locationPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting reference to Button
        Button btnDraw = (Button) findViewById(R.id.show);

        // Getting Map for the SupportMapFragment
        map = fm.getMap();


        // Enable MyLocation Button in the Map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);


        btnDraw.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //Start parsing and drawing JSON
                new ParserTask().execute(json_dict);
            }
        });
    }



    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            JSONArray jRoutes = null;
            List<List<HashMap<String, String>>> routes = null;


            try{
                jObject = new JSONObject(jsonData[0]);
                jRoutes = jObject.getJSONArray("route");
                JSONObject jsonBound = ((JSONObject)jRoutes.get(0)).getJSONObject("bounds");
                JSONObject jsonSouthWest = jsonBound.getJSONObject("southwest");
                JSONObject jsonNorthEast = jsonBound.getJSONObject("northeast");
                boundSouthWest = new LatLng(jsonSouthWest.getDouble("lat"),jsonSouthWest.getDouble("lng"));
                boundNorthEast = new LatLng(jsonNorthEast.getDouble("lat"),jsonNorthEast.getDouble("lng"));

                //Log.i
                DirectionJSONparser parser = new DirectionJSONparser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
                Log.i("________________________ERROREOEOEO-----------------","-------------------ERRORORORO------------------------");
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
           // builder.include(new LatLng(maxLat, maxLon));

           builder.include(boundNorthEast);
            builder.include(boundSouthWest);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 17));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
=======
package com.example.jeevan.splash;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;


public class MapsActivity extends FragmentActivity {

    private String source = null;
    private String destination = null;
    private String json_dict=null;
    ArrayList<LatLng> bounds = new ArrayList<LatLng>();

    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    ArrayList<LatLng> locationPoints;
    private LatLng boundSouthWest;
    private LatLng boundNorthEast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle bundle = getIntent().getExtras();
        if ( (getIntent().getStringExtra("json_dict") != null)){
            json_dict = bundle.getString("json_dict");
        }
        Toast.makeText(MapsActivity.this, json_dict, Toast.LENGTH_LONG).show();
        // Initializing
        markerPoints = new ArrayList<LatLng>();
        locationPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting reference to Button
        Button btnDraw = (Button) findViewById(R.id.show);

        // Getting Map for the SupportMapFragment
        map = fm.getMap();


        // Enable MyLocation Button in the Map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);


        btnDraw.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //Start parsing and drawing JSON
                new ParserTask().execute(json_dict);
            }
        });
    }



    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            JSONArray jRoutes = null;
            List<List<HashMap<String, String>>> routes = null;


            try{
                jObject = new JSONObject(jsonData[0]);
                jRoutes = jObject.getJSONArray("route");
                JSONObject jsonBound = ((JSONObject)jRoutes.get(0)).getJSONObject("bounds");
                JSONObject jsonSouthWest = jsonBound.getJSONObject("southwest");
                JSONObject jsonNorthEast = jsonBound.getJSONObject("northeast");
                boundSouthWest = new LatLng(jsonSouthWest.getDouble("lat"),jsonSouthWest.getDouble("lng"));
                boundNorthEast = new LatLng(jsonNorthEast.getDouble("lat"),jsonNorthEast.getDouble("lng"));

                map.addMarker(new MarkerOptions()
                        .position(boundSouthWest)
                        .title("Source")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                map.addMarker(new MarkerOptions()
                        .position(boundNorthEast)
                        .title("DESTINATION")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                //Log.i
                DirectionJSONparser parser = new DirectionJSONparser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
                Log.i("________________________ERROREOEOEO-----------------","-------------------ERRORORORO------------------------");
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
           // builder.include(new LatLng(maxLat, maxLon));

           builder.include(boundNorthEast);
            builder.include(boundSouthWest);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 17));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
>>>>>>> 9f2f9e4ce2bdf22b0c0235caed6fad8bc080d69a
}
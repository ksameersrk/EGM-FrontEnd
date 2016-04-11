package com.example.jeevan.splash;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TravelDiary extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LocationManager locMan;

    private Marker userMarker;
    private Marker[] placeMarkers;
    private final int MAX_PLACES = 20;
    private MarkerOptions[] places;

    private boolean haveStartedTrip = false;
    private Button checkIn;
    private TextView placesDesc;
    private Button tripButton;
    private TextView tripDesc;
    private Button viewTables;

    private PopupWindow pw;
    private PopupWindow pw2;
    private PopupWindow pw3;

    private String phNo = "5"; //// TODO: 08/04/16 get number from login page
    private int tripId = 0; //// TODO: 08/04/16 get from db
    private String placeName = "";
    private String op = "";

    private String startTime = "";
    private String review = "";
    private int count = 0;
    private HashMap<String, String> trips = null;
    private View currentInflatedLayout;



    //TravelDiaryParser parser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_diary);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        placeMarkers = new Marker[MAX_PLACES];
        //parser = new TravelDiaryParser();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkIn = (Button) findViewById(R.id.check);
        placesDesc = (TextView)findViewById(R.id.textView4);
        tripButton = (Button) findViewById(R.id.trip);
        tripDesc = (TextView)findViewById(R.id.textView5);
        viewTables = (Button) findViewById(R.id.view);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                // TODO Auto-generated method stub
                placesDesc.setText("");
                checkIn.setVisibility(View.INVISIBLE);
            }
        });
        updatePlaces();

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    private void updatePlaces() {
        locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();
        LatLng lastLatLng = new LatLng(lat, lng);

        if(userMarker!=null) userMarker.remove();
        userMarker = mMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("You are here")
                .snippet("Your last recorded location"));

        float zoomLevel = 16; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, zoomLevel));
        // mMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);

        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location="+lat+","+lng+
                "&radius=1000&sensor=true" +
                "&types=food|shopping_mall|store|museum|amusement_park|aquarium|art_gallery"+
                "&key=AIzaSyDYR8zC-Ve_brd5UvIJ5_8ocd88AaOQHCs";
        Log.v("DIARY URL", placesSearchStr);
        new GetPlaces().execute(placesSearchStr);
        //locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (long)30000, (float)100, (LocationListener)this);


    }
    private class GetPlaces extends AsyncTask<String, Void, String> {
        //fetch and parse place data
        @Override
        protected String doInBackground(String... placesURL) {
            //fetch places
            String data = "";
            StringBuilder placesBuilder = new StringBuilder();
            HttpURLConnection urlConnection = null;
            InputStream iStream = null;

            //process search parameter string(s)
            for (String placeSearchURL : placesURL) {
                //execute search
                try {
                    URL url = new URL(placeSearchURL);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();

                    iStream = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                    StringBuffer sb  = new StringBuffer();

                    String line = "";
                    while( ( line = br.readLine())  != null){
                        sb.append(line);
                    }
                    data = sb.toString();
                    br.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    urlConnection.disconnect();
                }
            }
            return data;
        }
        protected void onPostExecute(String result) {
            if(placeMarkers!=null){
                for(int pm=0; pm<placeMarkers.length; pm++){
                    if(placeMarkers[pm]!=null)
                        placeMarkers[pm].remove();
                }
            }
            try {
                //parse JSON
                JSONObject resultObject = new JSONObject(result);
                JSONArray placesArray = resultObject.getJSONArray("results");

                places = new MarkerOptions[placesArray.length()];

                boolean missingValue=false;
                LatLng placeLL=null;
                String placeName="";
                String vicinity="";

                //loop through places
                for (int p=0; p<placesArray.length(); p++) {
                    //parse each place
                    missingValue=false;
                    try{
                        //attempt to retrieve place data values
                        JSONObject placeObject = placesArray.getJSONObject(p);
                        JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
                        placeLL = new LatLng(
                                Double.valueOf(loc.getString("lat")),
                                Double.valueOf(loc.getString("lng")));
                        vicinity = placeObject.getString("vicinity");
                        placeName = placeObject.getString("name");
                    }
                    catch(Exception jse){
                        missingValue=true;
                        jse.printStackTrace();
                    }
                    if(missingValue)    places[p]=null;
                    else
                        places[p]=new MarkerOptions()
                                .position(placeLL)
                                .title(placeName)
                                .snippet(vicinity);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if(places!=null && placeMarkers!=null){
                for(int p=0; p<places.length && p<placeMarkers.length; p++){
                    //will be null if a value was missing
                    if(places[p]!=null)
                        placeMarkers[p]=mMap.addMarker(places[p]);
                }
            }
        }


    }
    @Override
    public void onLocationChanged(Location location) {
        Log.v("MyMapActivity", "location changed");
        updatePlaces();
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}
    @Override
    public boolean onMarkerClick(Marker arg0) {
        Log.v("MyMapActivity", "this is chill");
                /*Button checkIn = (Button) findViewById(R.id.check);
                TextView placesDesc = (TextView)findViewById(R.id.textView4);
                checkIn.setVisibility(View.VISIBLE);*/
        if(arg0 != null){
            Log.v("MyMapActivity", "sooo chill");
            if(!(arg0.getTitle().equals("You are here"))){
                placeName = arg0.getTitle();
                Button checkIn = (Button) findViewById(R.id.check);
                TextView placesDesc = (TextView)findViewById(R.id.textView4);
                if(haveStartedTrip){
                    checkIn.setVisibility(View.VISIBLE);
                    placesDesc.setText("Would you like to visit " +arg0.getTitle());
                }
                else{
                    placesDesc.setText("You can START TRIP and visit " +arg0.getTitle());
                }
            }
            else{
                placesDesc.setText("");
                checkIn.setVisibility(View.INVISIBLE);
            }
        }
        return false;
    }

    public void tripButtonAction(View view){
        //is START TRIP
        if(!(haveStartedTrip)){
            haveStartedTrip = true;
            op = "1";
            constructJSON();

            //// TODO: 09/04/16 MAKE CONNECTION
            try{
                JSONObject inputJSON = new JSONObject("{\"op\":1, \"trip_id\":5}");
                Log.v("CHECK JSON", inputJSON.get("op").toString());
                Log.v("JSON RESULT", inputJSON.toString());
                chooseParser(inputJSON);
            }catch(Exception e){
                Log.v("JSON RESULT", e.toString());
            }


            tripButton.setText("FINISH TRIP");
            placesDesc.setText("");
            displayTripDetails();
        }
        //IS FINISH TRIP
        else{
            Log.v("MyMapActivity", "it's hereee");
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) getBaseContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.diary_popup,
                    (ViewGroup) findViewById(R.id.popup_element));
            currentInflatedLayout = layout;
            // create a 300px width and 470px height PopupWindow
            pw = new PopupWindow(layout, 300, 470, true);
            TextView diaryText = (TextView) layout.findViewById(R.id.diary_text);
            // display the popup in the center


            op = "4";
            constructJSON();

            //// TODO: 09/04/16 MAKE CONNECTION
            try{
                JSONObject inputJSON = new JSONObject("{\"op\":4, \"trip_review\":\"blah blah\"}");
                Log.v("CHECK JSON", inputJSON.get("op").toString());
                Log.v("JSON RESULT", inputJSON.toString());
                chooseParser(inputJSON);
            }catch(Exception e){
                Log.v("JSON RESULT", e.toString());
            }
            diaryText.setText(review);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

        }
    }
    public void checkInButtonAction(View view){

        op = "2";
        constructJSON();

        placesDesc.setText("You have just checked in " + placeName + "");
        checkIn.setVisibility(View.INVISIBLE);
        displayTripDetails();
    }
    public void displayTripDetails(){

        op = "3";

        constructJSON();
        //// TODO: 09/04/16 MAKE CONNECTION
        try{
            JSONObject inputJSON = new JSONObject("{\"op\":\"3\", \"trip_start_datetime\":\"5\", \"count\":5}");
            Log.v("CHECK JSON", inputJSON.get("op").toString());
            Log.v("JSON RESULT", inputJSON.toString());
            chooseParser(inputJSON);
        }catch(Exception e){
            Log.v("JSON RESULT", e.toString());
        }
        //startTime = "07/08/16";
        tripDesc.setText("Trip " + tripId + " (started on: " + startTime + ")\nYou have visited " + count + " number of places");
    }

    /* close the diary pop-up window */
    public void cancelPopUpButtonAction(View view){
        pw.dismiss();
        //// TODO: 09/04/16 send diary back send(tripId, diary)
        haveStartedTrip = false;

        //// TODO: 08/04/16 remove
        tripButton.setText("START TRIP");
        tripDesc.setText("");
        checkIn.setVisibility(View.INVISIBLE);
        placesDesc.setText("");
        //// TODO: 07/04/16 go back to main screen
    }
    /* close the view trips pop-up window */
    public void cancelPopUpButtonAction2(View view){
        pw2.dismiss();

    }
    /* close the continue trip pop-up window */
    public void cancelPopUpButtonAction3(View view){
        pw3.dismiss();

    }




    public void viewTablesAction(View view){
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Inflate the view from a predefined XML layout
        View layout = inflater.inflate(R.layout.triptable_popup,
                (ViewGroup) findViewById(R.id.trips_diary));
        currentInflatedLayout = layout;
        // create a 300px width and 470px height PopupWindow
        pw2 = new PopupWindow(layout, 300, 470, true);
        TextView diaryText = (TextView) layout.findViewById(R.id.trips_list);


        op = "5";
        constructJSON();
        //// TODO: 09/04/16 MAKE CONNECTION
        try{
            JSONObject inputJSON = new JSONObject("{\"op\":5, \"trips\":{\"1\":\"hi\", \"2\":\"bye\"}}");
            Log.v("CHECK JSON", inputJSON.get("op").toString());
            Log.v("JSON RESULT", inputJSON.toString());
            chooseParser(inputJSON);
        }catch(Exception e){
            Log.v("JSON RESULT", e.toString());
        }
        String text = "";
        for (Map.Entry<String, String> entry : trips.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            text += "Trip Id: " + key +"\nDiary: " + value + "\n";

        }
        diaryText.setText(text);

        pw2.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }
    public void contTripAction(View view){
        LayoutInflater inflater = (LayoutInflater) getBaseContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Inflate the view from a predefined XML layout
        View layout = inflater.inflate(R.layout.cont_popup,
                (ViewGroup) findViewById(R.id.cont_diary));
        currentInflatedLayout = layout;
        // create a 300px width and 470px height PopupWindow
        pw3 = new PopupWindow(layout, 300, 470, true);
        pw3.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }
    /* send which trip to continue */
    public void continueTripAction(View view){

        EditText contTripId = (EditText)currentInflatedLayout.findViewById(R.id.editText);
        int oldTripId = tripId;
        int oldCount = count;
        String oldStartTime = startTime;
        tripId = Integer.parseInt(contTripId.getText().toString());


        op = "6";
        constructJSON();

        //// TODO: 09/04/16 MAKE CONNECTION
        try{
            JSONObject inputJSON = new JSONObject("{\"op\":\"6\", \"trip_start_datetime\":\"7\", \"count\":7}");
            Log.v("JSON RESULT", inputJSON.toString());
            chooseParser(inputJSON);
        }catch(Exception e){
            Log.v("JSON RESULT", e.toString());
        }


        if(count == -1){
            Toast.makeText(TravelDiary.this, "INVALID TRIP", Toast.LENGTH_LONG).show();
            tripId = oldTripId;
            count = oldCount;
            startTime = oldStartTime;
        }
        else {
            haveStartedTrip = true;
            tripButton.setText("FINISH TRIP");
            placesDesc.setText("");
            tripDesc.setText("Trip " + tripId + " (started on: " + startTime + ")\nYou have visited " + count + " number of places");


            pw3.dismiss();
        }

    }

    public void constructJSON(){
        try{
            JSONObject obj = new JSONObject();
            obj.put("Op", op);
            obj.put("phNo", phNo);
            obj.put("tripId", tripId);
            obj.put("placeName", placeName);
            Log.v("JSON OUTPUT", obj.toString());

        }catch(Exception e){
            Log.v("JSON OUTPUT", e.toString());
        }
    }
    public void chooseParser(JSONObject inputJSON){
        try{
            int op = Integer.parseInt(inputJSON.get("op").toString());
            switch(op){
                /*case 1:
                    tripId = parser.parseTripId(inputJSON);
                    break;
                case 3:
                    startTime = parser.parseStartTime(inputJSON);
                    count = parser.parseCount(inputJSON);
                    break;
                case 4:
                    review = parser.parseReview(inputJSON);
                    break;
                case 5:
                    trips = parser.parseTrips(inputJSON);
                    break;
                case 6:
                    startTime = parser.parseStartTime(inputJSON);
                    count = parser.parseCount(inputJSON);
                    break;
                default:
                    Log.v("JSON PARSE", "problem switching");*/

                case 1:
                    tripId = parseTripId(inputJSON);
                    break;
                case 3:
                    startTime = parseStartTime(inputJSON);
                    count = parseCount(inputJSON);
                    break;
                case 4:
                    review = parseReview(inputJSON);
                    break;
                case 5:
                    trips = parseTrips(inputJSON);
                    break;
                case 6:
                    startTime = parseStartTime(inputJSON);
                    count = parseCount(inputJSON);
                    break;
                default:
                    Log.v("JSON PARSE", "problem switching");


            }
        }catch(Exception e){
            Log.v("JSON PARSE", e.toString());
        }


    }
    public int parseTripId(JSONObject inputJSON){
        int trip_id = -1;
        try{
            trip_id = (Integer)inputJSON.get("trip_id");
        }catch(Exception e){
            Log.v("PARSER TRIP ID", e.toString());
        }
        return trip_id;
    }
    public String parseStartTime(JSONObject inputJSON){
        String startTime = "";
        try{
            startTime = (String)inputJSON.get("trip_start_datetime");
        }catch(Exception e){
            Log.v("PARSER START TIME", e.toString());
        }
        return startTime;
    }
    public int parseCount(JSONObject inputJSON){
        int count = -1;
        try{
            count = (Integer)inputJSON.get("count");
        }catch(Exception e){
            Log.v("PARSER COUNT", e.toString());
        }
        return count;
    }
    public String parseReview(JSONObject inputJSON){
        String review = "";
        try{
            review = (String)inputJSON.get("trip_review");
        }catch(Exception e){
            Log.v("PARSER REVIEW", e.toString());
        }
        return review;
    }
    public HashMap<String, String> parseTrips(JSONObject inputJSON){
        HashMap<String, String> trips = new HashMap<String, String>();
        JSONObject json;
        try{
            json = inputJSON.getJSONObject("trips");
            Iterator<String> keys = json.keys();
            while(keys.hasNext()){
                String key = keys.next();
                String value = (String)json.getString(key);
                trips.put(key, value);
            }
        }catch(Exception e){
            Log.v("PARSER TRIPS", e.toString());
        }
        return trips;
    }

}

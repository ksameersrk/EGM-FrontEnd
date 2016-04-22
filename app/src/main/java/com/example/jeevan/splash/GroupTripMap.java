package com.example.jeevan.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GroupTripMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_GROUP_NAME = "GroupName";
    private static final String PREF_GROUP_DEST = "GroupDestination";
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
        finish();
        sp.edit().remove(PREF_GROUP_NAME).remove(PREF_GROUP_DEST).commit();
    }
    public void chat(View v)
    {
        startActivity(new Intent(GroupTripMap.this, ChatActivity.class));
    }

    public void callMembers(View v)
    {
        startActivity(new Intent(GroupTripMap.this, MembersActivity.class));
    }
}

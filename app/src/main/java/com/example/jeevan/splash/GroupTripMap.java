package com.example.jeevan.splash;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GroupTripMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_trip_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

         final TextView t2 = (TextView) findViewById(R.id.Groupname);
        Bundle bundle = getIntent().getExtras();
        String grpname = bundle.getString("GroupName");
        t2.setText(grpname );

        final TextView t1 = (TextView) findViewById(R.id.Destination);
        Bundle bundle1 = getIntent().getExtras();
        String dest= bundle1.getString("GroupDest");
        t1.setText(dest);

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
    }
    public void chat(View v)
    {
        startActivity(new Intent(GroupTripMap.this, ChatActivity.class));
    }
}

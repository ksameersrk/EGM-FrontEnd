package com.example.jeevan.splash;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Bundle b = new Bundle();
    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_NAME = "name";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_GROUP_NAME = "GroupName";
    private static final String PREF_GROUP_DEST = "GroupDestination";

    String username ;
    String password ;
    String groupName;
    Boolean isSignedin = false;
    Boolean isPartOfGroup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        if(pref != null && pref.getString(PREF_NAME, null) != null)
        {
            username = pref.getString(PREF_NAME, null);
            password = pref.getString(PREF_PASSWORD, null);
            final TextView t1 = (TextView) findViewById(R.id.title_text);
            t1.setText("Hey "+username+"!");
            isSignedin = true;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        username = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString(PREF_NAME, null);
        if(username == null)
        {
            isSignedin = false;
        }
        else
        {
            isSignedin = true;
        }
        String s =  username == null ? "Anonymous" : username;
        final TextView t1 = (TextView) findViewById(R.id.title_text);
        t1.setText("Hey " + s + "!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isUserSignedIn()
    {
        SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        if(pref != null)
        {
            username = pref.getString(PREF_USERNAME, null);
            password = pref.getString(PREF_PASSWORD, null);
        }
        if(pref != null && username != null && password != null)
        {
            //Toast.makeText(MainActivity.this, "User signed In!", Toast.LENGTH_LONG).show();
            return true;
        }
        else
        {
            //Toast.makeText(MainActivity.this, "User Not signed In!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void isUserPartOfGroup()
    {
        SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        if(pref != null)
        {
            groupName = pref.getString(PREF_GROUP_NAME, null);
        }
        if(pref != null && groupName != null)
        {
            isPartOfGroup =  true;
        }
        else
        {
            URL a = null;
            JSONObject obj = new JSONObject();
            try {
                a = new URL("http://192.168.0.106:8000/test");

                obj.put("op", "4");
                obj.put("phone", username);
                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                Location location;
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
                Log.i("prelocation", "came here");
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Log.i("location", latitude+":"+longitude);
                obj.put("lat", latitude+"");
                obj.put("lng", longitude+"");



            } catch (Exception e) {
                e.printStackTrace();
            }
            new sendData().execute(a.toString(), obj.toString());
        }
    }

    public void callRoundAbout(View v){
        startActivity(new Intent(MainActivity.this, RoundAbout.class));
    }
    public void callFreeStyle(View v){
        startActivity(new Intent(MainActivity.this, FreeStyle.class));
    }
    public void callGroupTrip(View v){
        Intent i = null;
        if(! isUserSignedIn()) {
            i = new Intent(MainActivity.this, LoginActivity.class);
            b.putString("previousWindow", "MainActivity");
            //b.putString("nextWindow", true ? "GroupTrip" : "CreateGroup");
            i.putExtras(b);
        }
        else
        {
            isUserPartOfGroup();
            // TODO: 3/4/16 : call the intent of GroupTrip class

        }
        if(i != null){
            startActivity(i);
        }

    }
    public void callTravelDiary(View v){
        Intent i = null;
        if(!isUserSignedIn()) {
            i = new Intent(MainActivity.this, LoginActivity.class);
            b.putString("previousWindow", "GroupTrip");
            i.putExtras(b);
        }
        else
        {
            // TODO: 3/4/16 : call the intent of TravelDiary class
            i = new Intent(MainActivity.this, TravelDiary.class);
        }
        startActivity(i);
    }
    public void callLogin(View v){
        Intent i = null;
        if(! isSignedin) {
            i = new Intent(MainActivity.this, LoginActivity.class);
            b.putString("previousWindow", "MainActivity");
            i.putExtras(b);
        }
        else
        {
            i = new Intent(MainActivity.this, AlreadyLoggedInActivity.class);
            b.putString("username", username != null ? username : "");
            i.putExtras(b);

        }
        startActivity(i);
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
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                JSONObject object = new JSONObject(result);
                String no = object.getString("status");
                Log.i("sw32","HARRO");

                Log.i("status", no+"");
                if(Integer.parseInt(no) == 1)

                {
                    String gname = object.getString("name");
                    String gdest = object.getString("dest");
                    getSharedPreferences(PREF_FILE, MODE_PRIVATE)
                            .edit()
                            .putString(PREF_GROUP_DEST, gdest)
                            .putString(PREF_GROUP_NAME, gname)
                            .commit();
                    isPartOfGroup = true;
                }
                else
                {
                    isPartOfGroup = false;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Intent i;
            if(! isPartOfGroup)
            {
                i = new Intent(MainActivity.this, CreateGroup.class);
            }
            else
            {
                //TODO : uncomment GroupTripMap.class and comment CreateGroup.class
                i = new Intent(MainActivity.this, GroupTripMap.class);
                //i = new Intent(MainActivity.this, CreateGroup.class);
                // Toast.makeText(MainActivity.this, "Already part a group,"+
                //  " wait for our developers to create GroupActivity", Toast.LENGTH_LONG).show();
            }
            startActivity(i);
        }
    }
}

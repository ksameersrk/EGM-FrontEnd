package com.example.jeevan.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Bundle b = new Bundle();
    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";

    String username ;
    String password ;
    Boolean isSignedin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        if(pref != null && pref.getString(PREF_USERNAME, null) != null)
        {
            username = pref.getString(PREF_USERNAME, null);
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
        username = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString(PREF_USERNAME, null);
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
        t1.setText("Hey "+s+"!");
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
            Toast.makeText(MainActivity.this, "User signed In!", Toast.LENGTH_LONG).show();
            return true;
        }
        else
        {
            Toast.makeText(MainActivity.this, "User Not signed In!", Toast.LENGTH_LONG).show();
            return false;
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
        if(!isUserSignedIn()) {
            i = new Intent(MainActivity.this, LoginActivity.class);
            b.putString("previousWindow", "GroupTrip");
            i.putExtras(b);
        }
        else
        {
            // TODO: 3/4/16 : call the intent of GroupTrip class
            i = new Intent(MainActivity.this, FreeStyle.class);
        }
        startActivity(i);
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
            //i = new Intent(MainActivity.this, TravelDiary.class);
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


}

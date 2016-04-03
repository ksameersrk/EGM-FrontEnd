package com.example.jeevan.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AlreadyLoggedInActivity extends AppCompatActivity {

    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_logged_in);

        final TextView t2 = (TextView) findViewById(R.id.text_already_sigined_in);
        Bundle bundle = getIntent().getExtras();
        String username = bundle.getString("username");
        t2.setText("Hey " + username + ", you are already signed in. No need to sign in Again!");
    }

    public void callHome(View v)
    {
        //startActivity(new Intent(AlreadyLoggedInActivity.this, MainActivity.class));
        finish();
    }

    public void callHomeWithSignOut(View v)
    {
        getSharedPreferences(PREF_FILE, MODE_PRIVATE)
                .edit()
                .remove("username")
                .remove("password")
                .commit();
        //startActivity(new Intent(AlreadyLoggedInActivity.this, MainActivity.class));
        finish();
    }
}

package com.example.jeevan.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AlreadyLoggedInActivity extends AppCompatActivity {

    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_NAME = "name";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_GROUP_NAME = "GroupName";

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
                .remove(PREF_NAME)
                .remove(PREF_PASSWORD)
                .remove(PREF_GROUP_NAME)
                .commit();
        try
        {
            JSONObject obj = new JSONObject();
            obj.put("op", "2");
            obj.put("phone", getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString(PREF_USERNAME, null));
            URL url = new URL("http://192.168.0.106:8000/login");
            new sendData().execute(url.toString(), obj.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //startActivity(new Intent(AlreadyLoggedInActivity.this, MainActivity.class));
        finish();
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
}

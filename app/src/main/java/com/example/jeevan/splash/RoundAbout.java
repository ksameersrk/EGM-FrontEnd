package com.example.jeevan.splash;

/**
 * Created by jeevan on 3/3/16.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.jeevan.splash.R.layout.round_about;

/**
 * Created by jeevan on 3/3/16.
 */
public class RoundAbout extends Activity {

    Bundle b = new Bundle();
    Button getPlaces;
    String response;
    TextView seekBarValue;
    EditText location;
    SeekBar seekBar;
    String user_location;
    String radius;
    String localhost = "http://192.168.0.106:8000/test";
    private ProgressDialog progress=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(round_about);
        progress = new ProgressDialog(this);
        progress.setMessage("Posting data...");
        progress.setCancelable(false);
        location = (EditText) findViewById(R.id.user_location);

        getPlaces = (Button) findViewById(R.id.getPlaces);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBarValue = (TextView)findViewById(R.id.radius);
        seekBarValue.setText(String.valueOf(seekBar.getProgress()) + "km");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue.setText(String.valueOf(progress) + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        getPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_location = location.getText().toString();
                radius = seekBarValue.getText().toString();
                new postData().execute();

            }
        });

    }

    class postData extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            String post=null;
            try {
                URL url = new URL(localhost);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                String send = user_location + "::" +radius;
                os.write(send.getBytes());
                os.flush();
                os.close();
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb  = new StringBuilder();
                String line = "";
                while( ( line = br.readLine())  != null){
                    sb.append(line);
                }
                response = sb.toString();
                br.close();
                //int response = connection.getResponseCode();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if(connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            b.putString("json_places", s);
            Intent i = new Intent(RoundAbout.this, MapsActivity1.class);
            i.putExtras(b);
            startActivity(i);
        }
    }

}

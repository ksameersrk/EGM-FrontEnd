package com.example.jeevan.splash;

/**
 * Created by jeevan on 3/3/16.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.jeevan.splash.R.layout.free_style;


public class FreeStyle extends Activity implements View.OnClickListener {
    Context context;
    Button click;
    Bundle b = new Bundle();
    private String source;
    private String destination;
    // String data=null;
  //String source = null;
   // String destination = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(free_style);

        click = (Button) findViewById(R.id.go);

        click.setOnClickListener(this);

    }


    public void onClick(View view) {

        URL a = null;
        source = ((EditText) findViewById(R.id.user_location)).getText().toString();
        destination = ((EditText) findViewById(R.id.editText2)).getText().toString();

        try {


            a = new URL("http://192.168.0.106:8000/f1m2");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new sendData().execute(a.toString(), source, destination);
        new getData().execute(a.toString());

    }

    class sendData extends AsyncTask<String, String,String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
         try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                String send = params[1] + "::" +params[2];
                os.write(send.getBytes());
                os.flush();
                os.close();
             int response = connection.getResponseCode();
             return response+"";
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
           // Toast.makeText(FreeStyle.this,result,Toast.LENGTH_LONG).show();
        }
    }

    class getData extends AsyncTask<String, String,String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            String data = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb  = new StringBuilder();
                String line = "";
               while( ( line = br.readLine())  != null){
                    sb.append(line);
                }
                data = sb.toString();
                br.close();
                return data;
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Intent i = new Intent(FreeStyle.this, ListOfPlaces.class);
            b.putString("source", source);
            b.putString("destination", destination);
            b.putString("json_str",result);
            i.putExtras(b);
            startActivity(i);

        }
    }

}
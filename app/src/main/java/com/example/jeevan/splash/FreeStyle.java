package com.example.jeevan.splash;

/**
 * Created by jeevan on 3/3/16.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.jeevan.splash.R.layout.free_style;





public class FreeStyle extends Activity implements View.OnClickListener {
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

        AutoCompleteTextView editTextAddress = (AutoCompleteTextView)findViewById(R.id.auto_from);
        editTextAddress.setAdapter(new AutoCompleteAdapter(this));
        AutoCompleteTextView editTextAddress1 = (AutoCompleteTextView)findViewById(R.id.auto_to);
        editTextAddress1.setAdapter(new AutoCompleteAdapter(this));

        click = (Button) findViewById(R.id.go);

        click.setOnClickListener(this);

    }


    public void onClick(View view) {

        URL a = null;
        source = ((AutoCompleteTextView)findViewById(R.id.auto_from)).getText().toString();
        destination = ((AutoCompleteTextView)findViewById(R.id.auto_to)).getText().toString();

        try {


            a = new URL("http://192.168.0.106:8000/f1m2");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new sendData().execute(a.toString(), source, destination);

    }



    // And the corresponding Adapter
     private class AutoCompleteAdapter extends ArrayAdapter<Address> implements Filterable {

        private LayoutInflater mInflater;
        private Geocoder mGeocoder;
        private StringBuilder mSb = new StringBuilder();

        public AutoCompleteAdapter(final Context context) {
            super(context, -1);
            mInflater = LayoutInflater.from(context);
            mGeocoder = new Geocoder(context);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final TextView tv;
            if (convertView != null) {
                tv = (TextView) convertView;
            } else {
                tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            tv.setText(createFormattedAddressFromAddress(getItem(position)));
            return tv;
        }

        private String createFormattedAddressFromAddress(final Address address) {
            mSb.setLength(0);
            final int addressLineSize = address.getMaxAddressLineIndex();
            for (int i = 0; i < addressLineSize; i++) {
                mSb.append(address.getAddressLine(i));
                if (i != addressLineSize - 1) {
                    mSb.append(", ");
                }
            }
            return mSb.toString();
        }

        @Override
        public Filter getFilter() {
            Filter myFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(final CharSequence constraint) {
                    List<Address> addressList = null;
                    if (constraint != null) {
                        try {
                            addressList = mGeocoder.getFromLocationName((String) constraint, 5);
                        } catch (IOException e) {
                        }
                    }
                    if (addressList == null) {
                        addressList = new ArrayList<Address>();
                    }

                    final FilterResults filterResults = new FilterResults();
                    filterResults.values = addressList;
                    filterResults.count = addressList.size();

                    return filterResults;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(final CharSequence contraint, final Filter.FilterResults results) {
                    clear();
                    for (Address address : (List<Address>) results.values) {
                        add(address);
                    }
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }

                @Override
                public CharSequence convertResultToString(final Object resultValue) {
                    return resultValue == null ? "" : ((Address) resultValue).getAddressLine(0);
                }
            };
            return myFilter;
        }
    }


    class sendData extends AsyncTask<String, String,String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
         try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                OutputStream os = connection.getOutputStream();
                String send = params[1] + "::" +params[2];
                os.write(send.getBytes());
                os.flush();
                os.close();
             InputStream is = connection.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is));
             StringBuilder sb  = new StringBuilder();
             String line;
             while( ( line = br.readLine())  != null){
                 sb.append(line);
             }
             String data = sb.toString();
             br.close();

             return data;
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
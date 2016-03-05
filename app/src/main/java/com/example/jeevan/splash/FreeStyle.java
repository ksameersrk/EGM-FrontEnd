package com.example.jeevan.splash;

/**
 * Created by jeevan on 3/3/16.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.jeevan.splash.R.layout.free_style;

/**
 * Created by jeevan on 3/3/16.
 */
public class FreeStyle extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(free_style);

    }

    public void getListOfPlaces(View v){
        EditText src = (EditText) findViewById(R.id.editText);
        EditText dest = (EditText) findViewById(R.id.editText2);
        if(!src.getText().toString().equals("") && !dest.getText().toString().equals(""))
        {
            String tmp = src.getText().toString() + " to " + dest.getText().toString();
            Intent i = new Intent(FreeStyle.this, ListOfPlaces.class);
            Bundle b = new Bundle();
            b.putString("header", tmp);
            i.putExtras(b);
            startActivity(i);
        }
        else
        {
            final TextView t = (TextView) findViewById(R.id.textView3);
            if(src.getText().toString().equals(""))
            {
                t.append("Source field is empty!\n");
            }
            if(dest.getText().toString().equals(""))
            {
                t.append("Destination field is empty!");
            }
            //Log.v("myerror ", "Validation failed");
        }

    }
}

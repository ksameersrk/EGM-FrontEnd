package com.example.jeevan.splash;

/**
 * Created by jeevan on 3/3/16.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
        startActivity(new Intent(FreeStyle.this, ListOfPlaces.class));
    }
}

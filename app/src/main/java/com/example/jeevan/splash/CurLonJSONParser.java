package com.example.jeevan.splash;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CurLonJSONParser extends AppCompatActivity {
    
        public List<Double>  parse(JSONObject jObject) {

        //SHOW INPUT
        Log.d("SHARON Parser", "CHECK" + jObject); //{"lat":[1.1,2.2,3.3],"long":[4.4,5.5,6.6]}

        //SET UP DS
        List<Double> curLon = new ArrayList<>();

        JSONArray jcurLon = null;

        try {
            //get lon
            jcurLon = jObject.getJSONArray("users_long");
            for(int i = 0; i < jcurLon.length(); i++){
                curLon.add((Double)jcurLon.get(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return curLon;
    }
}
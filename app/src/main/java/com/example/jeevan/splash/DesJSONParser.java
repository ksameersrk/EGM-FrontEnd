package com.example.jeevan.splash;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DesJSONParser extends AppCompatActivity {
    
        public List<List<Object>> parse(JSONObject jObject) {

        //SHOW INPUT
        Log.d("SHARON Parser", "CHECK" + jObject); //{"lat":[1.1,2.2,3.3],"long":[4.4,5.5,6.6]}
        List<List<Object>> des = new ArrayList<>();
            List<Object> subArray = new ArrayList<>();
            JSONArray jDes = null;
            JSONArray jSubArray = null;

        try {

            jDes = jObject.getJSONArray("all_details");
            for(int i = 0; i < jDes.length(); i++){
                jSubArray = (JSONArray)jDes.get(i);
                subArray = new ArrayList<>();
                for(int j = 0; j < jSubArray.length(); j++){
                    //Log.d("SHARON Parser", "CHECK" + jSubArray);
                    subArray.add(jSubArray.get(j));
                }
                des.add(subArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return des;
    }
}
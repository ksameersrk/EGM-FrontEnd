package com.example.jeevan.splash;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TravelDiaryParser {

    public int parseTripId(JSONObject inputJSON){
        int trip_id = -1;
        try{
            trip_id = (Integer)inputJSON.get("trip_id");
        }catch(Exception e){
            Log.v("PARSER TRIP ID", e.toString());
        }
        return trip_id;
    }
    public String parseStartTime(JSONObject inputJSON){
        String startTime = "";
        try{
            startTime = (String)inputJSON.get("trip_start_datetime");
        }catch(Exception e){
            Log.v("PARSER START TIME", e.toString());
        }
        return startTime;
    }
    public int parseCount(JSONObject inputJSON){
        int count = -1;
        try{
            count = (Integer)inputJSON.get("count");
        }catch(Exception e){
            Log.v("PARSER COUNT", e.toString());
        }
        return count;
    }
    public String parseReview(JSONObject inputJSON){
        String review = "";
        try{
            review = (String)inputJSON.get("trip_review");
        }catch(Exception e){
            Log.v("PARSER REVIEW", e.toString());
        }
        return review;
    }
    public HashMap<String, String> parseTrips(JSONObject inputJSON){
        HashMap<String, String> trips = new HashMap<String, String>();
        JSONObject json;
        try{
            json = inputJSON.getJSONObject("trips");
            Iterator<String> keys = json.keys();
            while(keys.hasNext()){
                String key = keys.next();
                String value = (String)json.getString(key);
                trips.put(key, value);
            }
        }catch(Exception e){
            Log.v("PARSER TRIPS", e.toString());
        }
        return trips;
    }
}

package com.example.transit;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class customerHelper {

    public double getRoughCost(double rough_dist, int box_count) {
        if(box_count > 0){
            return ((rough_dist * 1.5) + ((box_count)*21))*1.11;
        }
        return -1.00;
    }

    public JSONArray getJSONFromString(JSONObject response) throws JSONException {
        JSONArray array = response.getJSONArray("result");
        return array;
    }
}

package com.example.transit.UserHelper;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Driver implements User{

    public String name;

    public Driver(String username){
        name = username;
    }

    public JSONObject getJobHistoryJSON(String response) throws JSONException {
        return new JSONObject((String) response);
    }




    // Driver Work Flow. Need to add more and test success
    public String getDistance(final JSONObject jsonObjectBOX, final int small, final int medium, final int large, final int heavy, final Context context, String ipaddress, final String jobID){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ipaddress + ":8080/driverPage/getFireBaseData";
        final String[] respon = {""};
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null && response.equals("NA")==false && response.equals("null")==false){
                            // distance
                            respon[0] =response;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        respon[0] =error.getMessage();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("search", "Distance");
                params.put("object", params.put("object", jobID));
                params.put("tableName", "requestinfo");
                return params;
            }
        };
        queue.add(postRequest);
        return respon[0];
    }

    public double getCost(int smallBoxCount,int mediumBoxCount,int largeBoxCount,int heavyWeightCount, double distance){
        int boxCost = (smallBoxCount*5) + (mediumBoxCount*10) + (largeBoxCount*15) + (heavyWeightCount*20);
        double fixedCost = distance*(7/10);
        double totalCost = (15+fixedCost) + ((distance/2)*boxCost);
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.valueOf(df.format(totalCost));
    }
}

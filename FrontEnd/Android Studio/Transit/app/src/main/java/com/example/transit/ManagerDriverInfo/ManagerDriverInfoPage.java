package com.example.transit.ManagerDriverInfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.transit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ManagerDriverInfoPage extends AppCompatActivity implements ManagerDriverInfoContract.MVPview{
    private TableLayout tableLayout;
    private JSONArray array;
    private ManagerDriverInfoPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_driver_info);
        presenter = new ManagerDriverInfoPresenter(this);
        tableLayout = findViewById(R.id.driver_table);
        array = new JSONArray();
        getDrivertList();
    }

    private void getDrivertList() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/ManagerPage/ManagerDriverInfoPage";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject send = new JSONObject(response);
                            array = send.getJSONArray("result");
                            fillTable();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest );
    }

    private void fillTable() {
        System.out.println(array.toString() + "second");
        LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(tableRowParams);

        /* create cell element - textview */
        TextView tv1 = new TextView(this);
        tv1.setBackgroundColor(0xff12dd12);
        tv1.setText("Name");
//
//        TextView tv2 = new TextView(this);
//        tv2.setBackgroundColor(0xff12dd12);
//        tv2.setText("CurrentLongtitude");

        TextView tv3 = new TextView(this);
        tv3.setBackgroundColor(0xff12dd12);
        tv3.setText("JobId");

        TextView tv4 = new TextView(this);
        tv4.setBackgroundColor(0xff12dd12);
        tv4.setText("JobSearch");

        TextView tv5 = new TextView(this);
        tv5.setBackgroundColor(0xff12dd12);
        tv5.setText("Phone");

        TextView tv6 = new TextView(this);
        tv6.setBackgroundColor(0xff12dd12);
        tv6.setText("Email");
//        TextView tv7 = new TextView(this);
//        tv7.setBackgroundColor(0xff12dd12);
//        tv7.setText("username");

        tableRow.addView(tv1);
//        tableRow.addView(tv2);
        tableRow.addView(tv3);
        tableRow.addView(tv4);
        tableRow.addView(tv5);
        tableRow.addView(tv6);
//        tableRow.addView(tv7);
        tableLayout.addView(tableRow, 0);

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject request = new JSONObject((String) array.get(i));

                LinearLayout.LayoutParams tableRowParams1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                /* create a table row */
                TableRow driverlist = new TableRow(this);
                driverlist.setLayoutParams(tableRowParams1);

                TextView name = new TextView(this);
                name.setBackgroundColor(0xff12dd12);
                //name.setText(request.get("Name").toString());
                if (request.has("Name")){
                    name.setText(request.get("Name").toString());
                }
                else {
                    name.setText("null");
                }
                /* create cell element - textview */
//                TextView latitude = new TextView(this);
//                latitude.setBackgroundColor(0xff12dd12);
//                latitude.setText(request.get("CurrentLatitude").toString());
//
//                TextView longtitude = new TextView(this);
//                longtitude.setBackgroundColor(0xff12dd12);
//                longtitude.setText(request.get("CurrentLongtitude").toString());

                TextView jobId = new TextView(this);
                jobId.setBackgroundColor(0xff12dd12);
                if (request.has("JobID")){
                    jobId.setText(request.get("JobID").toString());
                }
                else {
                    jobId.setText("null");
                }

                TextView jobSearch = new TextView(this);
                jobSearch.setBackgroundColor(0xff12dd12);
                if (request.has("JobSearch")){
                    jobSearch.setText(request.get("JobSearch").toString());
                }
                else {
                    jobSearch.setText("null");
                }

                TextView phone = new TextView(this);
                phone.setBackgroundColor(0xff12dd12);

                if (request.has("Phone")){
                    phone.setText(request.get("Phone").toString());
                }
                else {
                    phone.setText("null");
                }

                TextView email = new TextView(this);
                email.setBackgroundColor(0xff12dd12);

                if (request.has("Email")){
                    email.setText(request.get("Email").toString());
                }
                else {
                    email.setText("null");
                }

//                TextView username = new TextView(this);
//                username.setBackgroundColor(0xff12dd12);
//                username.setText(request.get("username").toString());
                /* create cell element - button */
//                Button btn = new Button(this);
//                btn.setText(request.get("username").toString());
//                btn.setBackgroundColor(0xff12dd12);
//
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent i = new Intent(ManagerRequstListPage.this, ManagerAssignJobToDriver.class);
//                        startActivity(i);
//                    }
//                });

//                /* set params for cell elements */
//                TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
//                cellParams.weight = 3;
//                tv.setLayoutParams(cellParams);
//                cellParams.weight = 2;
//                cellParams.rightMargin = 10;
//                btn.setLayoutParams(cellParams);

                /* add views to the row */
//                driverlist.addView(latitude);
//                driverlist.addView(longtitude);
                driverlist.addView(name);
                driverlist.addView(jobId);
                driverlist.addView(jobSearch);
                driverlist.addView(phone);
                driverlist.addView(email);

                //requestlist.addView(btn);

                /* add the row to the table */
                tableLayout.addView(driverlist,i + 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

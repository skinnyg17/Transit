package com.example.transit.ManagerRequestList;

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
import com.example.transit.ManagerAssignJob.ManagerAssignJobToDriver;
import com.example.transit.ManagerObservable;
import com.example.transit.ManagerObserver;
import com.example.transit.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ManagerRequstListPage extends AppCompatActivity implements ManagerRequestListContract.MVPview{
    private TableLayout tableLayout;
    private JSONArray array;
    private ManagerRequestListPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requst_list_page);
        presenter = new ManagerRequestListPresenter(this);
        tableLayout = findViewById(R.id.request_table);
        array = new JSONArray();
        getRequestList();
    }

    private void getRequestList() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/ManagerPage/ManagerRequestListPage";

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
        LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(tableRowParams);

        /* create cell element - textview */
        TextView tv1 = new TextView(this);
        tv1.setBackgroundColor(0xff12dd12);
        tv1.setText("cost");

//        TextView tv2 = new TextView(this);
//        tv2.setBackgroundColor(0xff12dd12);
//        tv2.setText("driver");

        TextView tv3 = new TextView(this);
        tv3.setBackgroundColor(0xff12dd12);
        tv3.setText("destination");

        TextView tv4 = new TextView(this);
        tv4.setBackgroundColor(0xff12dd12);
        tv4.setText("pickup");

        TextView tv5 = new TextView(this);
        tv5.setBackgroundColor(0xff12dd12);
        tv5.setText("box");
        TextView tv6 = new TextView(this);
        tv6.setBackgroundColor(0xff12dd12);
        tv6.setText("status");
        TextView tv7 = new TextView(this);
        tv7.setBackgroundColor(0xff12dd12);
        tv7.setText("username");

        tableRow.addView(tv1);
        //tableRow.addView(tv2);
        tableRow.addView(tv3);
        tableRow.addView(tv4);
        tableRow.addView(tv5);
        tableRow.addView(tv6);
        tableRow.addView(tv7);
        tableLayout.addView(tableRow, 0);

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject request = new JSONObject((String) array.get(i));

                LinearLayout.LayoutParams tableRowParams1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                /* create a table row */
                TableRow requestlist = new TableRow(this);
                requestlist.setLayoutParams(tableRowParams1);

                /* create cell element - textview */
                TextView cost = new TextView(this);
                cost.setBackgroundColor(0xff12dd12);
                cost.setText(request.get("cost").toString());

//                TextView driver = new TextView(this);
//                driver.setBackgroundColor(0xff12dd12);
//                if (request.has("driver")) {
//                    driver.setText(request.get("driver").toString());
//                }
//                else {
//                    driver.setText("null");
//                }

                TextView destination = new TextView(this);
                destination.setBackgroundColor(0xff12dd12);
                destination.setText(request.get("destination").toString());

                TextView pickup = new TextView(this);
                pickup.setBackgroundColor(0xff12dd12);
                pickup.setText(request.get("pickup").toString());

                TextView box = new TextView(this);
                box.setBackgroundColor(0xff12dd12);
                box.setText(request.get("box").toString());

                TextView status = new TextView(this);
                status.setBackgroundColor(0xff12dd12);
                status.setText(request.get("status").toString());

//                TextView username = new TextView(this);
//                username.setBackgroundColor(0xff12dd12);
//                username.setText(request.get("username").toString());
                /* create cell element - button */
                Button btn = new Button(this);
                final String getRequestbyUsername = request.get("username").toString();
                btn.setText(getRequestbyUsername);
                btn.setBackgroundColor(0xff12dd12);

                btn.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               ManagerObservable observable = new ManagerObservable();
                                               ManagerObserver observer = new ManagerObserver();
                                               observable.addObserver(observer);
                                               observable.setUsername(getRequestbyUsername);
                                               String specialNotify = observer.notifyManager();
                                               Intent i = new Intent(ManagerRequstListPage.this, ManagerAssignJobToDriver.class);
                                               i.putExtra("username", getRequestbyUsername);
                                               i.putExtra("notifyMassage", specialNotify);
                                               startActivity(i);
                                           }
                                       });

//                /* set params for cell elements */
//                TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
//                cellParams.weight = 3;
//                tv.setLayoutParams(cellParams);
//                cellParams.weight = 2;
//                cellParams.rightMargin = 10;
//                btn.setLayoutParams(cellParams);

                /* add views to the row */
                requestlist.addView(cost);
                //requestlist.addView(driver);
                requestlist.addView(destination);
                requestlist.addView(pickup);
                requestlist.addView(box);
                requestlist.addView(status);
                //requestlist.addView(username);
                requestlist.addView(btn);

                /* add the row to the table */
                tableLayout.addView(requestlist,i + 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}


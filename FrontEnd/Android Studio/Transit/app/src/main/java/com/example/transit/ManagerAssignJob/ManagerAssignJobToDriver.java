package com.example.transit.ManagerAssignJob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerAssignJobToDriver extends AppCompatActivity implements ManagerAssignJobContract.MVPview{
    private JSONArray array;
    private Spinner driverSpinner;
    private List driverlist;
    private TextView box;
    private TextView cost;
    private TextView destination;
    private TextView pickup;
    private TextView username;
    private TextView status;
    private TextView reciever;
    private TextView phone;
    private Button assign;
    private String jobId;
    private String assignDriver;
    private ManagerAssignJobPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_assign_job_to_driver);
        presenter = new ManagerAssignJobPresenter(this);
        array = new JSONArray();
        driverlist = new ArrayList<>();
        driverSpinner = findViewById(R.id.availableDriver);
        cost = findViewById(R.id.cost);
        destination = findViewById(R.id.destination);
        box = findViewById(R.id.box);
        username = findViewById(R.id.username);
        pickup = findViewById(R.id.pickup);
        status = findViewById(R.id.status);
        reciever = findViewById(R.id.reciever);
        phone = findViewById(R.id.phone);
        assign = findViewById(R.id.assign);
        Intent i = getIntent();
        String getRequestbyUsername = i.getStringExtra("username");
        final String observerNotify = i.getStringExtra("notifyMassage");
        getRequestInfo(getRequestbyUsername);
        //TODO
        // set sleep, sometimes jobid is slower at first time
        getAvailableDriver();
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                assignDriver = driverSpinner.getItemAtPosition(driverSpinner.getSelectedItemPosition()).toString();
                assignDriver = assignDriver.replace("\"", "");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
        Toast.makeText(ManagerAssignJobToDriver.this, observerNotify, Toast.LENGTH_SHORT).show();
        assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(assignDriver!=null){
                    updateDriverAndRequest(assignDriver, jobId);
                }
                else{
                    Toast.makeText(ManagerAssignJobToDriver.this, "Please select a driver first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDriverAndRequest(final String assigndriver, final String jobid) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/ManagerPage/ManagerUpdateDriverPage";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ManagerAssignJobToDriver.this, "Successfully update driver", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Driver",assigndriver);
                params.put("JobId", jobid);

                System.out.println("Put successfully");
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest );
    }

    //TODO
    //only return the one we need need to be modified
    private void getRequestInfo(final String getRequestbyUsername) {
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
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject request = new JSONObject((String) array.get(i));
                                if (request.get("username").toString().equals(getRequestbyUsername)) {
                                    box.setText(request.getString("box"));
                                    cost.setText(request.getString("cost"));
                                    destination.setText(request.getString("destination"));
                                    //phone.setText(request.getString("phone"));
                                    username.setText(request.getString("username"));
                                    status.setText(request.getString("status"));
                                    pickup.setText(request.getString("pickup"));
                                    jobId = request.getString("ID");
                                    //reciever.setText(request.getString("recievername"));
                                }
                            }

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

    private void getAvailableDriver() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/ManagerPage/ManagerAssignJobToDriverPage";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject send = new JSONObject(response);
                            array = send.getJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                String driver = array.getString(i);
                                driverlist.add(driver);
                            }
                        driverSpinner.setAdapter(new ArrayAdapter<String>(ManagerAssignJobToDriver.this, android.R.layout.simple_spinner_dropdown_item, driverlist));
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
}
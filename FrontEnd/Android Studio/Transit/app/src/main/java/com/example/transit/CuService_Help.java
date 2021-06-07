package com.example.transit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CuService_Help extends AppCompatActivity {
    private JSONArray array;
    private TableLayout tableLayout;
 private TextView name;
    private TextView phone;
    private TextView email;
    private String userName;
private String[]str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_help);
       tableLayout = findViewById(R.id.custinfo);
        array = new JSONArray();
        userName = getIntent().getStringExtra("username");
        CustomerList();
    }

    public void CustomerList() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/CusServicePage/customer";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject send = new JSONObject(response);
                            array = send.getJSONArray("result");
                            getList();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest );
    }

    private void getList() {

        final TableRow tableRow = new TableRow(this);
        tableLayout.addView(tableRow, 0);

        for (int i = 0; i < array.length(); i++) {
            try {
                final JSONObject request = new JSONObject((String) array.get(i));
                TableRow userList = new TableRow(this);


              name = new TextView(this);
                name.setBackgroundColor(0x000000);

                if (request.has("Name")) {
                    name.setText(request.get("Name").toString());
                }

                final TextView phone = new TextView(this);
                phone.setBackgroundColor(0x8157E2);
                if (request.has("Phone")) {
                    phone.setText(request.get("Phone").toString());
                }
             email = new TextView(this);
                email.setBackgroundColor(0x0000FF);

                if (request.has("Email")) {
                    email.setText(request.get("Email").toString());
                }
               email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent page = new Intent(CuService_Help.this, CusService_Handling_error.class);
                        page.putExtra("username",userName);
                        try {
                            page.putExtra("to", request.get("Email").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(page);
                    }
                });
                userList.addView(name);
                userList.addView(phone);
                userList.addView(email);

                tableLayout.addView(userList, i + 1);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }




    }

    }


package com.example.transit;

import android.Manifest;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomerPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String userName;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Button scan;
    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;
    private TableLayout tableLayout;
    private JSONArray array;
    private String scanResult;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page);

        userName = getIntent().getStringExtra("username");
        System.out.println(userName);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title=findViewById(R.id.tv_welcome);
        title.setText("Welcome, "+userName);
        drawer = findViewById(R.id.drawer_layout_Customer);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }

        scan = findViewById(R.id.customer_scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cus_scan = new Intent(CustomerPage.this, CustomerScan.class);
                cus_scan.putExtra("userName", userName);
                startActivity(cus_scan);
            }
        });

        tableLayout = findViewById(R.id.request_table);
        array = new JSONArray();
        setUserName();
        getRequestList();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_request){
            Intent customer_req = new Intent(this, CustomerRequest.class);
            customer_req.putExtra("username", userName);
            startActivity(customer_req);
        }
        else if(id == R.id.nav_chat){
            Intent intent = new Intent( CustomerPage.this, GeneralChatPage.class );
            intent.putExtra("userName",userName);
            intent.putExtra("type","customer");
            startActivityForResult(intent,1);
        }
        else if(id == R.id.nav_track){
            Intent intent = new Intent( CustomerPage.this, CustomerTrack.class );
            startActivity(intent);

        }
        else if(id == R.id.C_SignOut){
            Intent intent = new Intent( this, MainActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            this.startActivity( intent );
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                final Barcode barcode = data.getParcelableExtra("barcode");
                scanResult = barcode.displayValue;
            }
        }
    }

    public void getRequestList() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/customerPage/CustomerRequestList";
        final customerHelper c = new customerHelper();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject send = new JSONObject(response);
                            array = c.getJSONFromString(send);
                            System.out.println(array.toString() + "first");
                            fillTable();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.fillInStackTrace());
                        Toast.makeText(CustomerPage.this, "Failed to create request.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void fillTable(){
        System.out.println(array.toString() + "second");
        LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(tableRowParams);

        /* create cell element - textview */
        TextView tv1 = new TextView(this);
        tv1.setBackgroundColor(0xff12dd12);
        tv1.setText("cost");


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

        tableRow.addView(tv1);
        tableRow.addView(tv3);
        tableRow.addView(tv4);
        tableRow.addView(tv5);
        tableRow.addView(tv6);
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


                /* add views to the row */
                requestlist.addView(cost);
                requestlist.addView(destination);
                requestlist.addView(pickup);
                requestlist.addView(box);
                requestlist.addView(status);

                /* add the row to the table */
                tableLayout.addView(requestlist,i + 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void setUserName(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://"+getString(R.string.ip_address)+":8080/customerPage/setUserName";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("user set");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.out.println(error.fillInStackTrace());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName",userName);
                return params;
            }
        };
        queue.add(postRequest);
    }
}

package com.example.transit;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.L;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerPay extends AppCompatActivity {

    Button pay;
    TextView cost;
    EditText cardno;
    EditText name;
    EditText cvv;
    EditText exp;
    EditText postal;
    String box_count;
    double tot_cost;
    double rough_dist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_pay);

        box_count = getIntent().getStringExtra("box_count");
        cost = findViewById(R.id.cus_pay_amount);
        pay = findViewById(R.id.cus_pay_checkout);
        cardno = findViewById(R.id.cus_pay_cardno);
        name = findViewById(R.id.cus_pay_name);
        cvv = findViewById(R.id.cus_pay_cvv);
        exp = findViewById(R.id.cus_pay_expd);
        postal = findViewById(R.id.cus_pay_postal);

        double lat1;
        double lng1;
        double lat2;
        double lng2;

        String dest = getIntent().getStringExtra("destination");
        String loc = getIntent().getStringExtra("pickup");
        Geocoder geocoder = new Geocoder(this);
        List<Address> adrs;
        try {
            adrs = geocoder.getFromLocationName(dest,1);
            lat1 = adrs.get(0).getLatitude();
            lng1 = adrs.get(0).getLongitude();
            adrs = geocoder.getFromLocationName(loc,1);
            lat2 = adrs.get(0).getLatitude();
            lng2 = adrs.get(0).getLongitude();
            Location a = new Location("Current");
            a.setLatitude(lat2);
            a.setLongitude(lng2);
            Location b = new Location("Current");
            b.setLatitude(lat1);
            b.setLongitude(lng1);
            rough_dist = a.distanceTo(b)/1609.34;
            setCost();
        } catch (IOException e) {
            e.printStackTrace();
        }


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendPostRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String setCost() {
        customerHelper c = new customerHelper();
        if(c.getRoughCost(rough_dist,Integer.parseInt(box_count)) != -1.00){
            tot_cost = c.getRoughCost(rough_dist,Integer.parseInt(box_count));
            cost.setText("$"+Math.round(tot_cost * 100)/100);
            pay.setText("Pay  $"+Math.round(tot_cost * 100)/100);
            return "success";
        }
        cost.setText("-1");
        pay.setText("-1");
        return "failed";
    }

    private void sendPostRequest() throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/customerPage/addRequest";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(CustomerPay.this, "Successfully added request", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.out.println(error.fillInStackTrace());
                        Toast.makeText(CustomerPay.this, "Failed to create request.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName",getIntent().getStringExtra("user_name"));
                params.put("recieveName", getIntent().getStringExtra("recieve_name"));
                params.put("phone", getIntent().getStringExtra("phone"));
                params.put("boxCount", getIntent().getStringExtra("box_count"));
                params.put("destination", getIntent().getStringExtra("destination"));
                params.put("pickup", getIntent().getStringExtra("pickup"));
                params.put("cost", Double.toString(tot_cost));
                params.put("cardno", cardno.getText().toString());
                params.put("name", name.getText().toString());
                params.put("cvv", cvv.getText().toString());
                params.put("exp", exp.getText().toString());
                params.put("postal", postal.getText().toString());
                return params;
            }
        };
        queue.add(postRequest);
    }
}

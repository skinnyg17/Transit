package com.example.transit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CustomerRequest extends AppCompatActivity {

    private String userName;
    private EditText recieverName;
    private EditText pickup;
    private EditText destination;
    private EditText phone;
    private EditText boxCount;
    private Button checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_request);

        userName = getIntent().getStringExtra("username");

        recieverName = findViewById(R.id.CusReq_Recieve_name);
        phone = findViewById(R.id.CusReq_Phone);
        pickup = findViewById(R.id.CusReq_PickUp);
        destination = findViewById(R.id.CusReq_Destination);
        boxCount = findViewById(R.id.CusReq_boxCount);
        checkout = findViewById(R.id.CusReq_checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CustomerPay.class);
                intent.putExtra("user_name", userName);
                intent.putExtra("recieve_name", recieverName.getText().toString());
                intent.putExtra("phone", phone.getText().toString());
                intent.putExtra("pickup", pickup.getText().toString());
                intent.putExtra("destination", destination.getText().toString());
                intent.putExtra("box_count", boxCount.getText().toString());
                startActivity(intent);
            }
        });
    }
}

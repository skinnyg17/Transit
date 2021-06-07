package com.example.transit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomerScan extends AppCompatActivity {

    private TextView id;
    private ImageView image;
    private TextView name;
    private TextView desc;
    private Button scan;
    private String res;
    private JSONObject result;
    private boolean gotJSON = false;

    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_scan);

        id = findViewById(R.id.box_id);
        image = findViewById(R.id.box_image);
        name = findViewById(R.id.box_name);
        desc = findViewById(R.id.box_desc);
        scan = findViewById(R.id.box_scan);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cus_scan = new Intent(CustomerScan.this,GeneralScanPage.class);
                cus_scan.putExtra("role","driver");
                startActivityForResult(cus_scan,REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                final Barcode barcode = data.getParcelableExtra("barcode");
                res = barcode.displayValue;
                try {
                    getBoxDetails(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getBoxDetails(final String b_id) throws JSONException {
       if(!gotJSON) {
           RequestQueue queue = Volley.newRequestQueue(this);
           String url = "http://"+getString(R.string.ip_address)+":8080/customerPage/getBox";
           StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                   new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {
                           try {
                               result = new JSONObject(response);
                               Iterator<String> iter = result.keys();
                               while(iter.hasNext()){
                                   String key = iter.next();
                                   JSONObject check = new JSONObject((String) result.get(key));
                                   if(check.get("BoxID").equals(b_id)) {
                                       id.setText((CharSequence) check.get("BoxID"));
                                       ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                       byte[] im = Base64.decode((String)check.get("Image"), Base64.DEFAULT);
                                       Bitmap bmp = BitmapFactory.decodeByteArray(im, 0, im.length);
                                       image.setImageBitmap(bmp);
                                       name.setText((CharSequence) check.get("Title"));
                                       desc.setText((CharSequence) check.get("Description"));
                                   }
                               }
                           } catch (JSONException e) {
                               System.out.println("Unable to get Box Data");
                               e.printStackTrace();
                           }
                       }
                   },
                   new Response.ErrorListener() {
                       @Override
                       public void onErrorResponse(VolleyError error) {
                           // error
                           System.out.println("Unable to get Box Data");
                       }
                   }
           ) {
               @Override
               protected Map<String, String> getParams() {
                   Map<String, String> params = new HashMap<String, String>();
                   params.put("boxID",b_id);
                   return params;
               }
           };
           queue.add(postRequest);
       }
    }
}

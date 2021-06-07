package com.example.transit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CustomerTrack extends AppCompatActivity implements OnMapReadyCallback {

    private Button scanButton, updateButton;
    private TextView qrCode;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFrag;
    private JSONObject coordinates;
    private String JobID ="-1";
    private static final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_track);

        scanButton = findViewById(R.id.CustomerTrack_Scan);
        qrCode = findViewById(R.id.CustomerTrack_QRCode);
        updateButton = findViewById(R.id.CustomerTrack_Update);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.CustomerTrack_Map);
        mapFrag.getMapAsync(this);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(JobID.equals("-1")==false){
                    // update map
                    getCoordinates("requestinfo",JobID,"JourneyCoordinates");
                }
                else{
                    ToastHelper("Please scan the QR code first.");
                }
            }
        });

        scanButton = findViewById(R.id.CustomerTrack_Scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open up the scan page
                Intent intent = new Intent(CustomerTrack.this, GeneralScanPage.class);
                intent.putExtra("role", "customer");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void getCoordinates(final String tableName, final String object, final String search) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/getFireBaseData";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // get the coordinates from firebase and then update the json here
                        System.out.println(response);
                        if(response==null || response.equals("null")){
                            coordinates = new JSONObject();
                            ToastHelper("Invalid QR code");
                        }
                        else if(response.equals("NA")==false){
                            try {
                                coordinates = new JSONObject(response);
                                updateMap("");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastHelper("Invalid QR code");
                            }
                        }
                        else{
                            coordinates = new JSONObject();
                            ToastHelper("Invalid QR code");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastHelper("Failed to get coordinate data. Please check if QR Code is correct");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("search", search);
                params.put("object", object);
                params.put("tableName", tableName);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void ToastHelper(String message){
        Toast.makeText(CustomerTrack.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                final Barcode barcode = data.getParcelableExtra("barcode");
                JobID = barcode.displayValue;
                qrCode.setText(JobID);
            }
        }
    }

    private void updateMap(String flag) throws JSONException {
        mGoogleMap.clear();
        JSONObject startLocation = new JSONObject((String) coordinates.get("0"));
        mGoogleMap.addMarker(new MarkerOptions()
                .title("Starting Marker")
                .position(new LatLng(Double.valueOf((String) startLocation.get("Latitude")), Double.valueOf((String)startLocation.get("Longitude"))))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        Iterator<String> keys = coordinates.keys();
        List<LatLng> journey = new ArrayList<>();
        while(keys.hasNext()) {
            String key = keys.next();
            JSONObject tempLocation = new JSONObject((String) coordinates.get(key));
            LatLng newCoord = new LatLng(Double.valueOf((String) tempLocation.get("Latitude")), Double.valueOf((String)tempLocation.get("Longitude")));
            if(journey.size()>0){
                Location locationA = new Location("Prev");
                locationA.setLatitude(journey.get(journey.size()-1).latitude);
                locationA.setLongitude(journey.get(journey.size()-1).longitude);

                Location locationB = new Location("Cur");
                locationB.setLatitude(newCoord.latitude);
                locationB.setLongitude(newCoord.longitude);
            }
            journey.add(newCoord);
        }
        PolylineOptions opts = new PolylineOptions().addAll(journey).color(Color.CYAN).width(6);
        mGoogleMap.addPolyline(opts);

        mGoogleMap.addMarker(new MarkerOptions()
                .title("Current Marker")
                .position(new LatLng(Double.valueOf(journey.get(journey.size()-1).latitude), Double.valueOf(journey.get(journey.size()-1).longitude)))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }

}

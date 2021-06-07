package com.example.transit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DriverTransportPage extends AppCompatActivity implements OnMapReadyCallback {

    private Button startStopButton;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFrag;
    private JSONObject coordinates;
    private String userName,customer,jobID;
    private Intent serviceIntent;
    private Double distance;
    private int sendCount;
    private TextView distanceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_transport_page);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("JourneyLocationUpdates"));

        userName = getIntent().getStringExtra("username");
        customer = getIntent().getStringExtra("customer");
        jobID = getIntent().getStringExtra("jobID");
        distance=0.0;
        sendCount=0;

        startStopButton = findViewById(R.id.DriverTransport_Status);
        distanceView = findViewById(R.id.DriverTransport_Distance);

        getCoordinates("requestinfo",jobID,"JourneyCoordinates");

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTransportFlow();
            }
        });

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.DriverTransport_Map);
        mapFrag.getMapAsync(this);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String latitude = intent.getStringExtra("Latitude");
                String longitude = intent.getStringExtra("Longitude");
                JSONObject newJSON = new JSONObject();
                newJSON.put("Latitude", latitude);
                newJSON.put("Longitude", longitude);
                sendCount++;
                coordinates.put(String.valueOf(coordinates.length()),newJSON.toString());
                Toast.makeText(context, latitude + " : " + longitude, Toast.LENGTH_SHORT).show();
                if(distance==0.0) {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf((String)latitude), Double.valueOf((String)longitude)), 20));
                }
                if(sendCount%5==0){
                    updateCoordinates("requestinfo",jobID);
                }
                updateMap("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void startTransportFlow() {
        if(startStopButton.getText().equals("Start") || startStopButton.getText().equals("Continue")){
            startContinueAlertPopNotification(startStopButton.getText().toString(),"Are you sure you are ready to " + startStopButton.getText().toString() + "?","","Reached");
        }
        else{
            // button text = reached
            startContinueAlertPopNotification(startStopButton.getText().toString(),"By selecting \"confirm\" you are agreeing that you have reached the customer's drop off location.\nPlease be sure before proceeding.",startStopButton.getText().toString(),"");
        }
    }

    private void getCoordinates(final String tableName, final String object, final String search) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/getFireBaseData";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // get the coordinates from firebase and then update the json here
                        if(response==null || response.equals("null")){
                            coordinates = new JSONObject();
                        }
                        else if(response.equals("NA")==false){
                            try {
                                System.out.println(response);
                                coordinates = new JSONObject(response);
                                startStopButton.setText("Continue");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            coordinates = new JSONObject();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriverTransportPage.this, "Failed to get coordinate data", Toast.LENGTH_SHORT).show();
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

    private void updateCoordinates(final String tableName, final String object) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/updateFireBase";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // ??
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriverTransportPage.this, "Failed to get coordinate data", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    JSONObject toSend = new JSONObject();
                    toSend.put("JourneyCoordinates",coordinates.toString());
                    DecimalFormat df = new DecimalFormat("#.###");
                    toSend.put("Distance", String.valueOf(df.format(distance * 0.00062137)));

                    params.put("jsonString", toSend.toString());
                    params.put("object", object);
                    params.put("tableName", tableName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return params;
            }
        };
        queue.add(postRequest);
    }

    private boolean isLocationEnabled() {
        Context context = this;
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void locationAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Location not turned on");
        alertDialog.setMessage("Please turn on your Location. Do you want to go to your Location Settings Page?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    private boolean startJobLocationService(){
        checkPermissions();
        //if permission given
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if location enabled
            if(isLocationEnabled()){
                // start service
                serviceIntent = new Intent(this, JourneyLocationService.class);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        DriverTransportPage.this.startForegroundService(serviceIntent);
                    }
                    else {
                        startService(serviceIntent);
                    }
                    return true;
                }
            }
            else{
                // navigate to turn on location
                locationAlertDialog();
            }
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    private void startContinueAlertPopNotification(String title, String message, final String currentStatus, final String nextStatus){
        final AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        builder.show();
        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextStatus.equals("")){
                    updateCoordinates("requestinfo",jobID);
                    stopService(serviceIntent);
                    Intent intent = new Intent();
                    intent.putExtra("code", "12");
                    setResult(12, intent);
                    finish();
                }
                else {
                    if (startJobLocationService()) {
                        startStopButton.setText(nextStatus);
                    }
                }
                builder.dismiss();
            }
        });
        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
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
        distance=0.0;
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

                distance = (distance + (double) locationA.distanceTo(locationB));
            }
            journey.add(newCoord);
        }
        PolylineOptions opts = new PolylineOptions().addAll(journey).color(Color.CYAN).width(6);
        mGoogleMap.addPolyline(opts);

        mGoogleMap.addMarker(new MarkerOptions()
                .title("Current Marker")
                .position(new LatLng(Double.valueOf(journey.get(journey.size()-1).latitude), Double.valueOf(journey.get(journey.size()-1).longitude)))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        DecimalFormat df = new DecimalFormat("#.###");
        distanceView.setText("Distance : " + String.valueOf(df.format(distance * 0.00062137)) + " Miles");
    }
}

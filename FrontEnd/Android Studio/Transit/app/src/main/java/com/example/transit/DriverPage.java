package com.example.transit;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

public class DriverPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Switch jobSearch;
    private String userName,type;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Button startJob;
    private boolean doneLoading = false;
    private TextView jobTitle;
    private String customerName;
    private String recieverName;
    private String phone;
    private String pickUp;
    private String destination;
    private String boxCount;
    private String jobID;
    private String jobSearchVal;
    private String progress;
    private JSONObject toSendJsonObject= new JSONObject();
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_page);

        Toolbar toolbar = findViewById(R.id.toolbar_DriverPage);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout_DriverPage);
        serviceIntent = new Intent(DriverPage.this, JobLocationService.class);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        userName = getIntent().getStringExtra("username");
        type = getIntent().getStringExtra("type");

        startJob = findViewById(R.id.DriverMain_StartJob);
        startJob.setEnabled(false);
        startJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(progress.equals("assigned")) {
                    confirmStartJob();
                }
                else {
//                    Intent page = new Intent(DriverPage.this, DriverLoadPage.class);
//                    page.putExtra("username", userName);
//                    startActivity(page);
                    Intent page = new Intent(DriverPage.this, DriverWorkFlow.class);
                    if(toSendJsonObject.length()==0) {
                        try {
                            toSendJsonObject.put("Customer",customerName);
                            toSendJsonObject.put("Reciever", recieverName);
                            toSendJsonObject.put("Job ID", jobID);
                            toSendJsonObject.put("Pickup", pickUp);
                            toSendJsonObject.put("Destination", destination);
                            toSendJsonObject.put("Box Count", boxCount);
                            toSendJsonObject.put("Phone", phone);
                            toSendJsonObject.put("Progress", progress);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    page.putExtra("json", toSendJsonObject.toString());
                    page.putExtra("username", userName);
                    startActivityForResult(page, 1);
                }
            }
        });

        jobSearch = findViewById(R.id.DriverMain_switch);
        getUserSettings();

        jobSearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(doneLoading) {
                    if(isChecked){
                        if(!startJobLocationService()){
                            jobSearch.setChecked(false);
                        }
                        else{
                            sendPostJobSearchSettingsUpdate(isChecked);
                        }
                    }
                    else{
                        if(isLocationServiceRunning()) {
                            stopService(serviceIntent);
                            sendPostJobSearchSettingsUpdate(isChecked);
                        }
                    }
//                    sendPostJobSearchSettingsUpdate(isChecked);
                }
                else{
                    if(isChecked){
                        if(!startJobLocationService()){
                            jobSearch.setChecked(false);
                            // god. Remove to be nice to driver. Keep to be smart
                            sendPostJobSearchSettingsUpdate(false);
                        }
                    }
                }
            }
        });

        jobTitle = findViewById(R.id.DriverMain_AssignedJobTitle);

        navigationView = (NavigationView) findViewById(R.id.DP_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.example.transit.JobLocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void confirmStartJob() {
        final TextView input1 = new TextView(this);
        String toRender = ">.<*\n" +
                            "Receiver: " + recieverName + "\n"
                            + "Phone: " + phone + "\n"
                            + "Pick Up: " + pickUp + "\n"
                            + "Destination: " + destination + "\n"
                            + "Box Count: " + boxCount + "\n";
        input1.setText(toRender);
        input1.setGravity(Gravity.CENTER);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(input1);

        final AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle("Confirmation of Starting Job")
                .setCancelable(false)
                .setMessage("Please look at the information and then confirm starting of the Service.").setView(linearLayout).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
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
                progress="Started";
                startJob.setText("Continue");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("progressstatus",progress);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendPostUpdateDataBase("requestinfo",jobID,jsonObject.toString());
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
            if(isLocationServiceRunning()) {
                stopService(serviceIntent);
            }
    }

    private void sendPostJobSearchSettingsUpdate(final Boolean check) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/jobSearch";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DriverPage.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        jobSearch.setChecked(false);
                        Toast.makeText(DriverPage.this, "Failed to update Job Search Settings", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", userName);
                params.put("check", String.valueOf(check));
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void sendPostUpdateDataBase(final String tableName, final String object, final String jsonString) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/updateFireBase";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DriverPage.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriverPage.this, "Failed to update settings", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("jsonString", jsonString);
                params.put("object", object);
                params.put("tableName", tableName);
                System.out.println(object);
                System.out.println(jsonString);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void getUserSettings() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/getUserSettings";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject toRender = new JSONObject(response);
                            jobSearchVal = (String) toRender.get("JobSearch");
                            jobID = (String) toRender.get("JobID");
                            destination = (String) toRender.get("destination");
                            pickUp = (String) toRender.get("pickUp");
                            boxCount = (String) toRender.get("boxCount");
                            phone = (String) toRender.get("phone");
                            customerName = (String) toRender.get("customerName");
                            recieverName = (String) toRender.get("recieverName");
                            progress = (String) toRender.get("progress");

                            if(jobID.equals("NA")==false){
                                jobTitle.setText("Assigned Job \n "+jobID);
                                startJob.setEnabled(true);
                            }
                            else{
                                jobTitle.setText("No Assigned Job");
                            }
                            if(jobSearchVal.equals("true")){
                                jobSearch.setChecked(true);
                            }
                            else{
                                jobSearch.setChecked(false);
                            }
                            if(progress.equals("assigned")==false){
                                startJob.setText("Continue");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        doneLoading=true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        jobSearch.setChecked(false);
                        Toast.makeText(DriverPage.this, "Failed to get Job Search Settings", Toast.LENGTH_SHORT).show();
                        doneLoading=true;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", userName);
                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.DP_Chat){
            Toast.makeText(DriverPage.this, "Chat", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent( this, GeneralChatPage.class );
            intent.putExtra("userName",userName);
            intent.putExtra("type","driver");
            this.startActivityForResult(intent,1);
        }
        if(id == R.id.DP_Job_History){
            Toast.makeText(DriverPage.this, "History", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent( this, DriverJobHistory.class );
            intent.putExtra("userName",userName);
            this.startActivityForResult(intent,1);
        }
        if(id == R.id.DP_Emergency){
            Toast.makeText(DriverPage.this, "Please refer to the emergency contacts provided to you by the company, and the info.", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.DP_Help){
            Toast.makeText(DriverPage.this, "Please refer to the Help Guide provided to you by the company.", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.DP_SignOut){
            Intent intent = new Intent( this, MainActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            this.startActivity( intent );
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
//            String editTextString = data.getStringExtra("editText");
//            try {
//                toSendJsonObject = new JSONObject(data.getStringExtra("json"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            // Fix this
//            startJob.setEnabled(false);
//            getUserSettings();
        }
        startJob.setEnabled(false);
        getUserSettings();
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
                serviceIntent = new Intent(this, JobLocationService.class);
                serviceIntent.putExtra("userName", userName);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        DriverPage.this.startForegroundService(serviceIntent);
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


}
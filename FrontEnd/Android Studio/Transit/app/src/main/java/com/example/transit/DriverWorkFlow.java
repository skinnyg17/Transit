package com.example.transit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DriverWorkFlow extends AppCompatActivity {

    private JSONObject jsonObject;
    private String progress, completeMessage;
    private String userName;
    private Button navButton;
    private Bundle bundle;
    private double cost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_work_flow);

        navButton = findViewById(R.id.DriverWorkFlow_NavButton);

        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("json"));
            progress = jsonObject.getString("Progress");
            userName = getIntent().getStringExtra("username");
            updateTableView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (bundle != null) {
            try {
                jsonObject = new JSONObject((String) bundle.get("json"));
                userName = (String) bundle.get("userName");
                progress = (String) bundle.get("progress");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            bundle = new Bundle();
            onSaveInstanceState(bundle);
        }

        try {
            workFlowHelper(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    workFlowHelper(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void workFlowHelper(int type) throws JSONException {
        if(progress.equals("Started")) {
            if (type == 1) {
                alertPopNotification("Confirmation", "Confirm that you are ready to start driving to the customer's pickup location.", "Driving to Customer", "Reached Customer");
            }
        }
        if(progress.equals("Driving to Customer")){
            if(type==1){
                alertPopNotification("Confirmation", "Confirm that you have now reached the customer's pickup location.", "Reached Customer", "Pre Loading Verification");
            }
            else{
                navButton.setText("Reached Customer");
            }
        }
        if(progress.equals("Reached Customer")){
            if(type==1){
                verificationPopNotification("Permission","Please type your full name below and check the box to agree to our Terms and Conditions.","Pre Loading Verification","Load Truck");
            }
            else{
                navButton.setText("Pre Loading Verification");
            }
        }
        if(progress.equals("Pre Loading Verification")){
            if(type==1){
                Intent page = new Intent(DriverWorkFlow.this, DriverLoadPage.class);
                page.putExtra("username", userName);
                page.putExtra("customer", String.valueOf(jsonObject.get("Customer")));
                page.putExtra("jobID", String.valueOf(jsonObject.get("Job ID")));
                startActivityForResult(page,11);
            }
            else{
                navButton.setText("Load Truck");
            }
        }
        if(progress.equals("Load Truck")){
            if(type==1){
                verificationPopNotification("Permission to Transport","We have now loaded the Truck.\nPlease type your full name below and check the box to agree to our Terms and Conditions so that we can begin the Transportation.","Post Loading Verification","Transportation");
            }
            else{
                navButton.setText("Post Loading Verification");
            }
        }
        if(progress.equals("Post Loading Verification")){
            if(type==1){
                Intent page = new Intent(DriverWorkFlow.this, DriverTransportPage.class);
                page.putExtra("username", userName);
                page.putExtra("customer", String.valueOf(jsonObject.get("Customer")));
                page.putExtra("jobID", String.valueOf(jsonObject.get("Job ID")));
                startActivityForResult(page,12);
            }
            else{
                navButton.setText("Transportation");
            }
        }
        if(progress.equals("Transportation")){
            if(type==1){
                verificationPopNotification("Permission to Unload","We are ready to Unload!\nPlease type your full name below and check the box to agree to our Terms and Conditions so that we can begin the Unloading.","Pre Unloading Verification","Unload Truck");
            }
            else{
                navButton.setText("Pre Unloading Verification");
            }
        }
        if(progress.equals("Pre Unloading Verification")){
            if(type==1){
                Intent page = new Intent(DriverWorkFlow.this, DriverUnload.class);
                page.putExtra("username", userName);
                page.putExtra("customer", String.valueOf(jsonObject.get("Customer")));
                page.putExtra("jobID", String.valueOf(jsonObject.get("Job ID")));
                startActivityForResult(page,13);
            }
            else{
                navButton.setText("Unload Truck");
            }
        }
        if(progress.equals("Unload Truck")){
            if(type==1){
                verificationPopNotification("Confirmation of Completion of Service","We are done with the Service!\n" + completeMessage +"Please type your full name below and check the box to agree to our Terms and Conditions so that can get your confirmation to proceed with the payment.","Complete","Complete");
            }
            else{
                navButton.setText("Complete");
            }
        }
        if(progress.equals("Complete")){
            if(type==1){
                // remove job id from driver json, add driver name to request info
                // do the get user info again in main page
                JSONObject newObj = new JSONObject();
                newObj.put("JobID","NA");
                sendPostUpdateDataBase("users",userName, newObj.toString());
                newObj = new JSONObject();
                newObj.put("Driver",userName);
                newObj.put("status","done");
                newObj.put("FinalCost",cost);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
//                System.out.println(dateFormat.format(date));
                newObj.put("Date",String.valueOf(dateFormat.format(date)));
                sendPostUpdateDataBase("requestinfo",String.valueOf(jsonObject.get("Job ID")), newObj.toString());
                Toast.makeText(this, "Job Finished!",Toast.LENGTH_LONG).show();
                onBackPressed();
            }
            else{
                navButton.setText("Complete");
            }
        }
    }

    private double getCost(int smallBoxCount,int mediumBoxCount,int largeBoxCount,int heavyWeightCount, double distance){
        int boxCost = (smallBoxCount*5) + (mediumBoxCount*10) + (largeBoxCount*15) + (heavyWeightCount*20);
        double fixedCost = distance*(7/10);
        double totalCost = (15+fixedCost) + ((distance/2)*boxCost);
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.valueOf(df.format(totalCost));
    }

    private void updateTableView() throws JSONException {

        final TableLayout tableLayout = (TableLayout) findViewById(R.id.DriverWorkFlow_Table);

        int count = tableLayout.getChildCount();
        for (int i = 1; i < count; i++) {
            View child = tableLayout.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }

        TextView textView = findViewById(R.id.wtftest1);
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            String key = keys.next();

            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);

            TextView text1 = new TextView(this);
            text1.setText(key + " : " + jsonObject.getString(key));
            text1.setBackgroundColor(Color.parseColor("#ffffcc"));
            if(key.equals("Progress")){
                text1.setBackgroundColor(Color.parseColor("#ccccff"));
            }
            text1.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams params = textView.getLayoutParams();
            text1.setLayoutParams(params);

            tableRow.addView(text1, 0);

            tableLayout.addView(tableRow);
        }

    }

    private void alertPopNotification(String title, String message, final String currentStatus, final String nextStatus){
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
                progress=currentStatus;
                navButton.setText(nextStatus);
                try {
                    jsonObject.put("Progress",progress);
                    updateTableView();
                    JSONObject toSend = new JSONObject();
                    toSend.put("progressstatus",progress);
                    sendPostUpdateDataBase("requestinfo",jsonObject.getString("Job ID"),toSend.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void verificationPopNotification(String title, String message, final String currentStatus, final String nextStatus){
        final EditText input1 = new EditText(this);
        input1.setHint("Full Name");
        input1.setGravity(Gravity.CENTER);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(input1);

        final CheckBox input2 = new CheckBox(this);
        input2.setText("I agree to the Terms and Conditions.");
        linearLayout.addView(input2);

        final AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message).setView(linearLayout).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
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
                progress=currentStatus;
                navButton.setText(nextStatus);
                try {
                    jsonObject.put("Progress",progress);
                    updateTableView();
                    JSONObject toSend = new JSONObject();
                    toSend.put("progressstatus",progress);
                    sendPostUpdateDataBase("requestinfo",jsonObject.getString("Job ID"),toSend.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void sendPostUpdateDataBase(final String tableName, final String object, final String jsonString) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/updateFireBase";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DriverWorkFlow.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriverWorkFlow.this, "Failed to update settings", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("jsonString", jsonString);
                params.put("object", object);
                params.put("tableName", tableName);
                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent();
        i.putExtra("json",jsonObject.toString());
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 11 ){
            final String code = data.getStringExtra("code");
            if(code!=null) {
                if (code.equals("11")) {
                    try {
                        JSONObject jsonObjectToSend = new JSONObject();
                        jsonObjectToSend.put("progressstatus", "Load Truck");
                        sendPostUpdateDataBase("requestinfo", jsonObject.getString("Job ID"), jsonObjectToSend.toString());

                        progress = "Load Truck";
                        jsonObject.put("Progress", progress);
                        updateTableView();
                        navButton.setText("Post Loading Verification");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else if(requestCode == 12){
            final String code = data.getStringExtra("code");
            if(code!=null) {
                if(code.equals("12")){
                    try {
                        JSONObject jsonObjectToSend = new JSONObject();
                        jsonObjectToSend.put("progressstatus", "Transportation");
                        sendPostUpdateDataBase("requestinfo", jsonObject.getString("Job ID"), jsonObjectToSend.toString());

                        progress = "Transportation";
                        jsonObject.put("Progress", progress);
                        updateTableView();
                        navButton.setText("Pre Unloading Verification");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else if(requestCode == 13){
            final String code = data.getStringExtra("code");
            System.out.println(code);
            if(code!=null) {
                if(code.equals("13")){
                    try {
                        JSONObject jsonObjectToSend = new JSONObject();
                        jsonObjectToSend.put("progressstatus", "Unload Truck");
                        sendPostUpdateDataBase("requestinfo", jsonObject.getString("Job ID"), jsonObjectToSend.toString());
                        parseBoxData(data.getStringExtra("BoxData"));

                        progress = "Unload Truck";
                        jsonObject.put("Progress", progress);
                        updateTableView();
                        navButton.setText("Complete");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void parseBoxData(String boxData) throws JSONException {
        final JSONObject jsonObjectBOX = new JSONObject(boxData);
        Iterator<String> keys = jsonObjectBOX.keys();
        int small=0,medium=0,large=0,heavy =0;
        while(keys.hasNext()) {
            String key = keys.next();
            JSONObject curBox = (JSONObject) jsonObjectBOX.get(key);
            switch((String)curBox.get("Size")) {
                case "S" :
                    small++;
                    break;
                case "M" :
                    medium++;
                    break;
                case "L" :
                    large++;
                    break;
                case "Heavy" :
                    heavy++;
                    break;
            }
        }
        getDistance(jsonObjectBOX, small, medium, large, heavy);
    }

    private void getDistance(final JSONObject jsonObjectBOX, final int small, final int medium, final int large, final int heavy){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/getFireBaseData";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null && response.equals("NA")==false && response.equals("null")==false){
                            // distance
                            cost = getCost(small,medium,large,heavy,Double.valueOf(response));
                            completeMessage = "The total cost is $ " + cost
                                    + "\nDistance: " + response + " Miles"
                                    + "\nSmall Boxes: " + small
                                    + "\nMedium Boxes: " + medium
                                    + "\nLarge Boxes: " + large
                                    + "\nHeavy Weight Boxes: " + heavy +"\n";
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriverWorkFlow.this, "Failed to retrieve Distance", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("search", "Distance");
                try {
                    params.put("object", String.valueOf(jsonObject.get("Job ID")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("tableName", "requestinfo");
                return params;
            }
        };
        queue.add(postRequest);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("json", jsonObject.toString());
        bundle.putString("userName", userName);
        bundle.putString("progress", progress);
    }
}

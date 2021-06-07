package com.example.transit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DriverUnload extends AppCompatActivity {

    private String userName,customer,jobID;
    private JSONObject boxInfo,toSendBoxInfo;
    private Button scanButton;
    private int check=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_unload);

        userName = getIntent().getStringExtra("username");
        customer = getIntent().getStringExtra("customer");
        jobID = getIntent().getStringExtra("jobID");

        scanButton = findViewById(R.id.DriverUnload_Scan);

        getBoxInfoFireBase("requestinfo",jobID,"BoxData");

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverUnload.this, GeneralScanPage.class);
                intent.putExtra("role", "driver");
                startActivityForResult(intent, 100);
            }
        });
    }

    private void getBoxInfoFireBase(final String tableName, final String object, final String search) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/getFireBaseData";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null && response.equals("NA")==false && response.equals("null")==false){
                            try {
                                boxInfo = new JSONObject();
                                toSendBoxInfo = new JSONObject();
                                JSONObject toget =  new JSONObject(response);
                                Iterator<String> keys = toget.keys();
                                while(keys.hasNext()) {
                                    String key = keys.next();
                                    boxInfo.put(key,new JSONObject((String) toget.get(key)));
                                    toSendBoxInfo.put(key,new JSONObject((String) toget.get(key)));
                                }
                                populateTable();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriverUnload.this, "Failed to retrieve Box Data", Toast.LENGTH_SHORT).show();
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

    private void populateTable() throws JSONException {

        final TableLayout tableLayout = (TableLayout) findViewById(R.id.fixed_column);
        int count = tableLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = tableLayout.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
        TextView textView = findViewById(R.id.wtftest1);
        TextView remain = findViewById(R.id.DriverUnload_BoxRemaining);
        Iterator<String> keys = boxInfo.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            JSONObject cur = boxInfo.getJSONObject(key);

            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);

            TextView text1 = new TextView(this);
            text1.setText(key + " : " + cur.get("Title") + " : " + cur.get("Size"));
            text1.setBackgroundColor(Color.parseColor("#ffffcc"));
            text1.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams params = textView.getLayoutParams();
            text1.setLayoutParams(params);
            tableRow.addView(text1, 0);
            tableLayout.addView(tableRow);
        }
        remain.setText("Boxes Remaining : " + boxInfo.length());

        // if remain =0, clean finish, exit activity
        if(boxInfo.length()==0){
            Intent intent = new Intent();
            intent.putExtra("code", "13");
            intent.putExtra("BoxData", toSendBoxInfo.toString());
            setResult(13, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(check==1){
            check=0;
            super.onBackPressed();
        }
        else {
            final AlertDialog builder = new AlertDialog.Builder(this)
                    .setTitle("Cancel Unloading")
                    .setCancelable(false)
                    .setMessage("Are you sure you have to cancel Unloading?\nYou can done back and finish unloading").setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create();
            builder.show();
            ((AlertDialog) builder).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check = 1;
                    onBackPressed();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            if(data != null){
                final Barcode barcode = data.getParcelableExtra("barcode");
                System.out.println("|"+barcode.displayValue+"|");
                if(boxInfo.has(barcode.displayValue)){
                    boxInfo.remove(barcode.displayValue);
                    try {
                        populateTable();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

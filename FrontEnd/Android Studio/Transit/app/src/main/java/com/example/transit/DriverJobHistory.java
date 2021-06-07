package com.example.transit;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.transit.UserHelper.Driver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DriverJobHistory extends AppCompatActivity {

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_job_history);

        userName = getIntent().getStringExtra("userName");

        getJobHistory();
    }

    private void getJobHistory() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/driverPage/jobHistory";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DriverJobHistory.this, "Retrieved Info", Toast.LENGTH_SHORT).show();
                        try {
                            Driver d = new Driver("");
                            renderTable(d.getJobHistoryJSON(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DriverJobHistory.this, "Failed to update Job Search Settings", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", userName);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void renderTable(JSONObject toRender) throws JSONException {
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.history);
        int count = tableLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = tableLayout.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
        TextView textView = findViewById(R.id.wtftest1);
        Iterator<String> keys = toRender.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            JSONObject cur = new JSONObject((String) toRender.get(key));

            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);

            TextView text1 = new TextView(this);
            text1.setText(cur.get("Date") + " : " + cur.get("ID") + " : " + cur.get("pickup")
                    + " : " + cur.get("destination") + " : " + cur.get("Distance"));
            text1.setBackgroundColor(Color.parseColor("#ffffcc"));
            text1.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams params = textView.getLayoutParams();
            text1.setLayoutParams(params);
            tableRow.addView(text1, 0);
            tableLayout.addView(tableRow);
        }
    }
}

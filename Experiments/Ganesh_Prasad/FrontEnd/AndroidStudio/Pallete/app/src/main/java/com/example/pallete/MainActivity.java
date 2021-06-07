package com.example.pallete;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.net.MalformedURLException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button1,button2,button3,button4,button5,button6,random;
    HashMap<Integer,String> btn_data = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        random = findViewById(R.id.button_rand);

        button1.setBackgroundColor(Color.parseColor("#98F019"));
        button2.setBackgroundColor(Color.parseColor("#3CCEDA"));
        button3.setBackgroundColor(Color.parseColor("#6700CD"));
        button4.setBackgroundColor(Color.parseColor("#FF4982"));
        button5.setBackgroundColor(Color.parseColor("#8F6C7D"));
        button6.setBackgroundColor(Color.parseColor("#FFF68F"));

        btn_data.put(R.id.button1, String.format("#%06X", (0xFFFFFF & ((ColorDrawable)button1.getBackground()).getColor())));
        btn_data.put(R.id.button2, String.format("#%06X", (0xFFFFFF & ((ColorDrawable)button2.getBackground()).getColor())));
        btn_data.put(R.id.button3, String.format("#%06X", (0xFFFFFF & ((ColorDrawable)button3.getBackground()).getColor())));
        btn_data.put(R.id.button4, String.format("#%06X", (0xFFFFFF & ((ColorDrawable)button4.getBackground()).getColor())));
        btn_data.put(R.id.button5, String.format("#%06X", (0xFFFFFF & ((ColorDrawable)button5.getBackground()).getColor())));
        btn_data.put(R.id.button6, String.format("#%06X", (0xFFFFFF & ((ColorDrawable)button6.getBackground()).getColor())));


        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        random.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_rand:
                try {
                    String colors[] = getRandom();
                    button1.setBackgroundColor(Color.parseColor(colors[0]));
                    button2.setBackgroundColor(Color.parseColor(colors[1]));
                    button3.setBackgroundColor(Color.parseColor(colors[2]));
                    button4.setBackgroundColor(Color.parseColor(colors[3]));
                    button5.setBackgroundColor(Color.parseColor(colors[4]));
                    button6.setBackgroundColor(Color.parseColor(colors[5]));
                    btn_data.put(button1.getId(),colors[0]);
                    btn_data.put(button2.getId(),colors[1]);
                    btn_data.put(button3.getId(),colors[2]);
                    btn_data.put(button4.getId(),colors[3]);
                    btn_data.put(button5.getId(),colors[4]);
                    btn_data.put(button6.getId(),colors[5]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                Toast.makeText(this, btn_data.get(view.getId()),Toast.LENGTH_SHORT).show();
                break;
        }
    }


    public String[] getRandom() throws MalformedURLException {
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.start();
        String url = "http://10.26.52.154:8080/home";
        SharedPreferences a = PreferenceManager.getDefaultSharedPreferences(this);
        StringRequest getResult = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    sharedResponse(response);
                }
            },
        new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error:", error.toString());
                }
            }
        );
        queue.add(getResult);
        String reply = a.getString("Response", "");
        System.out.println(reply);
        String new_colors[] = reply.split(",");
        return new_colors;
    }

    private void sharedResponse(String response) {
        SharedPreferences a = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = a.edit();
        editor.putString("Response", response);
        editor.commit();
    }
}

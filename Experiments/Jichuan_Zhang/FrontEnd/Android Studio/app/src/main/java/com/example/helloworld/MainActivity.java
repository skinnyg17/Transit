package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    EditText input1;
    EditText input2;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input1 = findViewById(R.id.number1);
        input2 = findViewById(R.id.number2);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendPost();
                    //sendingGetRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

private void sendPost() throws MalformedURLException {
    RequestQueue queue = Volley.newRequestQueue(this);
    String url = "http://10.29.178.99:8080/home/MainActivity";
    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) {
                    // response
                    String result = response;
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                      //  Log.d("Response", response);
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // error
//                        Log.d("Error.Response", response);
                }
            }
    ) {
        @Override
        protected Map<String, String> getParams()
        {
            Map<String, String>  params = new HashMap<String, String>();
            params.put("input1", input1.getText().toString());
            params.put("input2", input2.getText().toString());
            return params;
        }
    };
    queue.add(postRequest);
}
//private void sendingGetRequest() throws Exception {
//    final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
//    emailExecutor.execute(new Runnable() {
//        @Override
//        public void run() {
//            try {
//                String urlString = "http://10.29.178.99:8080/home";
//                URL url = new URL(urlString);
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                // By default it is GET request
//                con.setRequestMethod("GET");
//                //add request header
//                int responseCode = con.getResponseCode();
//                System.out.println("Sending get request : "+ url);
//                System.out.println("Response code : "+ responseCode);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    });
//    emailExecutor.shutdown();
//}


}

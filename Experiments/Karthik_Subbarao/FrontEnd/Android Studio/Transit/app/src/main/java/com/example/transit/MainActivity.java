package com.example.transit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private Button signUp, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.usernameLoginPage);
        password = findViewById(R.id.passwordLoginPage);

        signUp = findViewById(R.id.signUpLoginPage);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToPage(SignUp.class, "na");
            }
        });

        login = findViewById(R.id.loginLoginPage);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendPostValidateLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void navigateToPage(Class pageToNavigate, String userName) {
        Intent page = new Intent(this, pageToNavigate);
        page.putExtra("username", userName);
        startActivity(page);
    }

    private void sendPostValidateLogin() throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        //The password's TextInputLayout
        final TextInputLayout passwordLayout = findViewById(R.id.passwordLayoutLoginPage);
        String url = "http://10.36.22.195:8080/home/login";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("driver")) {
                            navigateToPage(DriverPage.class, username.getText().toString());
                        } else if (response.equals("user")) {
                            navigateToPage(CustomerPage.class, username.getText().toString());
                        } else {
                            passwordLayout.setError("Incorrect username or password");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        passwordLayout.setError("Incorrect username or password");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", username.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };
        queue.add(postRequest);
    }
}

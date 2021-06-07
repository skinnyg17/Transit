package com.example.transit;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.transit.ManagerMain.ManagerPage;
import com.google.android.material.textfield.TextInputLayout;
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
    private TextInputLayout passwordLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordLayout = findViewById(R.id.passwordLayoutLoginPage);

        username = findViewById(R.id.usernameLoginPage);
        password = findViewById(R.id.passwordLoginPage);

        signUp = findViewById(R.id.signUpLoginPage);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToPage(SignUp.class, "na","na");
            }
        });

        login = findViewById(R.id.loginLoginPage);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                      sendPostValidateLogin();
//                    navigateToPage(CustomerPage.class, username.getText().toString());
       //             navigateToPage(ManagerPage.class, username.getText().toString());
//                    navigateToPage(DriverPage.class, username.getText().toString());
//                   navigateToPage(CusServicePage.class, username.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void navigateToPage(Class pageToNavigate, String userName, String type) {
        Intent page = new Intent(this, pageToNavigate);
        page.putExtra("username", userName);
        page.putExtra("type", type);
        startActivity(page);
        passwordLayout.setError(null);
        passwordLayout.clearFocus();
    }

    private void sendPostValidateLogin() throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        //The password's TextInputLayout
        String url = "http://" + getString(R.string.ip_address) + ":8080/loginSignUp/login";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("driver")) {
                            navigateToPage(DriverPage.class, username.getText().toString(), response);
                        } else if (response.equals("customer")) {
                            navigateToPage(CustomerPage.class, username.getText().toString(),response);
                        }else if (response.equals("manager")) {
                            navigateToPage(ManagerPage.class, username.getText().toString(),response);
                        }else if (response.equals("customerservice")) {
                            navigateToPage(CusServicePage.class, username.getText().toString(),response);
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

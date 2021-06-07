package com.example.transit;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class SignUp extends AppCompatActivity {
    private Button signUp;
    private EditText userName, userEmail, userPhone, userPassword, userVPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = findViewById(R.id.usernameSignUpPage);
        userEmail = findViewById(R.id.useremailSignUpPage);
        userPhone = findViewById(R.id.userPhoneNoSignUpPage);
        userPassword = findViewById(R.id.passwordSignUpPage);
        userVPassword = findViewById(R.id.vpasswordSignUpPage);

        signUp = findViewById(R.id.signUpButtonSignUpPage);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validationSuccess()) {
                    try {
                        String code = generateRandomNumber(6);
                        sendPostValidateUserName(code);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public boolean validationSuccess() {
        TextInputLayout textInputUserName = findViewById(R.id.input1SignUpPage);
        TextInputLayout textInputEmail = findViewById(R.id.input2SignUpPage);
        TextInputLayout textInputPhone = findViewById(R.id.input3SignUpPage);
        TextInputLayout textInputPassword = findViewById(R.id.input4SignUpPage);
        TextInputLayout textInputVPassword = findViewById(R.id.input5SignUpPage);
        boolean check = true;
        EmailValidator emailvalidator = EmailValidator.getInstance();
        if (TextUtils.isEmpty(userName.getText().toString())) {
            check = false;
            textInputUserName.setError("Field cannot be empty");
        } else {
            // specific username validation
        }

        if (TextUtils.isEmpty(userEmail.getText().toString())) {
            check = false;
            textInputEmail.setError("Field cannot be empty");
        } else {
            if (emailvalidator.isValid(userEmail.getText().toString()) == false) {
                check = false;
                textInputEmail.setError("Invalid Email");
            }
        }

        if (TextUtils.isEmpty(userPhone.getText().toString())) {
            check = false;
            textInputPhone.setError("Field cannot be empty");
        } else {
            // specific phone validation
        }

        if (TextUtils.isEmpty(userPassword.getText().toString())) {
            check = false;
            textInputPassword.setError("Field cannot be empty");
        } else {
            // specific password validation
        }

        if (TextUtils.isEmpty(userVPassword.getText().toString())) {
            check = false;
            textInputVPassword.setError("Field cannot be empty");
        } else {
            // specific verify password validation
        }

        if (userPassword.getText().toString().equals(userVPassword.getText().toString()) == false) {
            check = false;
            textInputVPassword.setError("Passwords do not match");
        }

        if (isOnline() == false) {
            check = false;
            Toast.makeText(SignUp.this, "Internet access is needed", Toast.LENGTH_SHORT).show();
        }
        return check;
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void alertPopUp(final String code) throws Exception {
        final EditText input1 = new EditText(this);
        input1.setHint("Code");
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(input1);

        final AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle("Email Verification")
                .setCancelable(false)
                .setMessage("enter the code sent to your email").setView(linearLayout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                if (input1.length() <= 0) {
                    input1.setError("Please enter the Code");
                } else if (input1.getText().toString().equals(code) == false) {
                    input1.setError("Invalid Code");

                } else {
                    try {
                        sendPostCreateUser();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    builder.dismiss();
                }
            }
        });
        ((AlertDialog) builder).getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
    }


    private String generateRandomNumber(int charLength) {
        return String.valueOf(charLength < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
                + (int) Math.pow(10, charLength - 1));
    }

    private void sendPostCreateUser() throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/loginSignUp/signUp";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("goodRequest")) {
                            Toast.makeText(SignUp.this, "Account successfully created. Please log in", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                        else if(response.equals("userNameExists")){
                            TextInputLayout textInputUserName = findViewById(R.id.input1SignUpPage);
                            textInputUserName.setError("Username already exists.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.out.println(error.fillInStackTrace());
                        Toast.makeText(SignUp.this, "Failed to create user.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                System.out.println(userName.getText().toString());
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", userName.getText().toString());
                params.put("email", userEmail.getText().toString());
                params.put("phone", userPhone.getText().toString());
                params.put("password", userPassword.getText().toString());
                return params;
            }
        };
        queue.add(postRequest);
        //queue.stop();
    }

    private void sendPostValidateUserName(final String code) throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/loginSignUp/validName";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("userNameExists")){
                            TextInputLayout textInputUserName = findViewById(R.id.input1SignUpPage);
                            textInputUserName.setError("Username already exists.");
                        }
                        else{
                            try {
                                alertPopUp(code);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        System.out.println(error.fillInStackTrace());
                        Toast.makeText(SignUp.this, "Failed to create user.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                System.out.println(userName.getText().toString());
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", userName.getText().toString());
                params.put("email", userEmail.getText().toString());
                params.put("code", code);
                return params;
            }
        };
        queue.add(postRequest);
        //queue.stop();
    }

    // This method is NOT being used. I only left it here because in case we decide to use regular HTTP instead of Volley
    // -we will have a working example.
    // HTTP GET request
    private void sendingGetRequest() throws Exception {

        String urlString = "http://10.27.133.26:8080/home";

        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("Sending get request : " + url);
        System.out.println("Response code : " + responseCode);

        // Reading response from input Stream
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        //printing result from response
        System.out.println(response.toString());
    }


}



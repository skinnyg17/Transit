package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class MainActivity extends AppCompatActivity {
    private EditText username, password, user, pass, firstName, lastName, email, phonNum, confirmemail;
    private  Button login, signUp, reg_register;
    private View view;
    private Toast toast;
    private LayoutInflater in;
    private  String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private String pattern = "^[0-9]$";
    private Pattern patern;
    private Matcher match;
    private static final String PASSWORD="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        phonNum=findViewById(R.id.phone_number);
        signUp = findViewById(R.id.signUp);
        ClickLogin();
        //SignUp Button for showing registration page
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickSignUp();
            }
        });


    }
    //login page: users enter their username and password to login to their account
    private void ClickLogin() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (username.getText().toString().trim().isEmpty()) {

                    toast = Toast.makeText(getApplicationContext(),"please enter a valid username",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
                if (password.getText().toString().trim().isEmpty()) {
                    toast = Toast.makeText(getApplicationContext(), "Please enter a valid Password",
                            Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    isValidPassword(pass.getText().toString().trim());
                }

            }

        });
    }
    //New users will register in this page
    private void ClickSignUp() {

        AlertDialog.Builder log = new AlertDialog.Builder(this);
        in = getLayoutInflater();
         view = in.inflate(R.layout.register,null);
        log.setView(view);
        user = view.findViewById(R.id.username);
        pass = view.findViewById(R.id.password);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        email =  view.findViewById(R.id.email);
        confirmemail = view.findViewById(R.id.confirmemail);
        reg_register = view.findViewById(R.id.register);
        phonNum=  view.findViewById(R.id.phone_number);
        reg_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getText().toString().trim().isEmpty()) {

                    user.setError("Please enter a valid username");
                }
                if (pass.getText().toString().trim().isEmpty()) {
                    pass.setError("Please enter a valid password");
                } else {
                    isValidPassword(pass.getText().toString().trim());
                }
                if (firstName.getText().toString().trim().isEmpty()&&(!firstName.getText().toString().trim().matches("[a-zA-Z]"))) {

                    firstName.setError("Please enter your First name");
                }
                if (lastName.getText().toString().trim().isEmpty()&&(!lastName.getText().toString().trim().matches("[a-zA-Z]"))) {

                    lastName.setError("Please enter your Last name");
                }
                if(phonNum.getText().toString().matches(pattern)) {

                    Toast.makeText(getApplicationContext(), "phone number is valid", Toast.LENGTH_SHORT).show();

                } else if(!(phonNum.getText().toString().matches(pattern))) {

                    Toast.makeText(getApplicationContext(), "Please enter valid 10 digit phone number", Toast.LENGTH_SHORT).show();
                }
                if (email.getText().toString().trim().isEmpty()) {

                    email.setError("Please enter your email address");
                } else {
                    if (email.getText().toString().trim().matches(emailPattern)) {
                        Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(),"Invalid email address", Toast.LENGTH_SHORT).show();
                    }
                }
                if (confirmemail.getText().toString().trim().isEmpty()) {

                    confirmemail.setError("Please confirm your email address");
                } else {
                    if (confirmemail.getText().toString().trim().matches(email.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                        confirmInfo();


                    }

                }
            }
        });

        log.show();
    }
    //password validator
    public boolean isValidPassword(final String password) {
        patern = Pattern.compile(PASSWORD);
        match = patern.matcher(password);

        return match.matches();

    }
    public boolean confirmInfo(){
        //TODO


        //default
        return false;

    }
    private void sendPost() throws MalformedURLException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.29.176.245:8080/student";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("name", "Alif");
                params.put("domain", "http://itsalif.info");

                return params;
            }
        };
        queue.add(postRequest);
    }


}


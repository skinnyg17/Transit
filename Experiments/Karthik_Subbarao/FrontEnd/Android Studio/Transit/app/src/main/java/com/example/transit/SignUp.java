package com.example.transit;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
                    final String code = GenerateRandomNumber(8);
                    ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
                    emailExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sendEmailVerfication(code);
                            } catch (AddressException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    emailExecutor.shutdown();
                    alertPopUp(code);
                }
            }
        });
    }

    private boolean validationSuccess() {
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

    private void alertPopUp(final String code) {

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
                        sendPostAddNewUser();
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


    private String GenerateRandomNumber(int charLength) {
        return String.valueOf(charLength < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
                + (int) Math.pow(10, charLength - 1));
    }


    private void sendEmailVerfication(String code) throws AddressException {
        final String username = "bigmovescompany.noreply@gmail.com";
        final String password = "bigmoves12345";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("bigmovescompany.noreply@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(userEmail.getText().toString()));
            message.setSubject("BMC Verification");
            message.setText("Dear " + userName.getText().toString() + ","
                    + "\n\nbigmovescompany here!\nVerfication code: " + code + "\nYou have 5 minutes before the code expires.\nIf this was sent by accident, that is too bad.\nThank you!\n\nRegards,\nBMC");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendPostAddNewUser() throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.36.22.195:8080/home";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SignUp.this, "Account successfully created. Please log in", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                System.out.println(userName.getText().toString());
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", userName.getText().toString());
                params.put("password", userPassword.getText().toString());
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



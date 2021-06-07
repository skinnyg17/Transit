package com.example.transit;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class CusService_Handling_error extends AppCompatActivity  {
    private EditText editTextEmail;
    private EditText editTextSubject;
    private EditText editTextMessage;
    private Button buttonSend;
    private String from,to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_error);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextSubject = (EditText) findViewById(R.id.editTextSubject);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        from = getIntent().getStringExtra("username");
        to="";
        if(getIntent().getStringExtra("to")!=null){
            to = getIntent().getStringExtra("to");
            editTextEmail.setText(to);
        }
        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendEmail();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
//    public void sendEmail() {
//        String email = editTextEmail.getText().toString().trim();
//        String subject = editTextSubject.getText().toString().trim();
//        String message = editTextMessage.getText().toString().trim();
//        Send sm = new Send(this, email, subject, message);
//        sm.execute();
//    }
//    @Override
//    public void onClick(View v) {
//        sendEmail();
//    }
//  private class Send extends AsyncTask<Void,Void,Void>{
//        private static final String EMAIL = "imasos982@gmail.com"; //enete the transit email here
//        private static final String PASSWORD = "Asrahulet"; //enter the password for the email
//        private Context context;
//        private Session session;
//        private String email;
//        private String subject;
//        private String message;
//        private ProgressDialog progressDialog;
//        private Send(Context context, String email, String subject, String message) {
//            this.context = context;
//            this.email = email;
//            this.subject = subject;
//            this.message = message;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = ProgressDialog.show(context, "Sending message", "Please wait...", false, false);
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            progressDialog.dismiss();
//            Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            Properties props = new Properties();
//
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.smtp.socketFactory.port", "465");
//            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.port", "465");
//            session = Session.getDefaultInstance(props,
//                    new javax.mail.Authenticator() {
//
//                        protected PasswordAuthentication getPasswordAuthentication() {
//                            return new PasswordAuthentication(EMAIL, PASSWORD);
//                        }
//                    });
//
//            try {
//                MimeMessage messag = new MimeMessage(session);
//                messag.setFrom(new InternetAddress(EMAIL));
//                messag.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
//                messag.setSubject(subject);
//                messag.setText(message);
//                Transport.send(messag);
//
//            } catch (MessagingException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//    }
    private void sendEmail() throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/CusServicePage/send";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(CusService_Handling_error.this,"Message Sent!",Toast.LENGTH_LONG);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CusService_Handling_error.this,"Failed to send message",Toast.LENGTH_LONG);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("to",editTextEmail.getText().toString());
                params.put("body",editTextMessage.getText().toString());
                params.put("subject",editTextSubject.getText().toString());
                params.put("from",from);
                return params;
            }
        };
        queue.add(request);
        //queue.stop();
    }
}

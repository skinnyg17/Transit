package com.example.demo;

import com.google.firebase.database.*;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * The type Customer service controller.
 */
@RestController
@RequestMapping("/CusServicePage")
public class CustomerServiceController {
    private FirebaseHelper firebaseHelper = new FirebaseHelper();

        @RequestMapping(value = "/customer", method = RequestMethod.GET)
        public String getList (HttpServletRequest request){
            String list = CustomerList().toString();
            return list;
        }

        private JSONObject CustomerList() {
            JSONArray array = new JSONArray();
            JSONObject jsonobject = new JSONObject();
            final CountDownLatch[] done = {new CountDownLatch(1)};
            FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = mFirebaseDatabase.getReference().child("users");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                        Object object = Snapshot.getValue(Object.class);
                        array.put(new Gson().toJson(object));
                    }
                    try {
                        jsonobject.put("result", array);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    done[0].countDown();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            try {
                done[0].await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return jsonobject;
        }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String send (HttpServletRequest request){
        String subject = request.getParameter("subject");
        String body = request.getParameter("body");
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        System.out.println(subject+body+from+to);
        body = body + "\n \n This message was sent by " + from + ".";
        EmailHelper emailHelper = new EmailHelper(to, subject, body);
        emailHelper.sendEmail();
        return body;
    }
}

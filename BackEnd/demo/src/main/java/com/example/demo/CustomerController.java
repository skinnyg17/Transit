package com.example.demo;

import com.google.api.client.util.Strings;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.CountDownLatch;


/**
 * The type Customer controller.
 */
@RestController
@RequestMapping("/customerPage")
public class CustomerController {

    private String username;
    private FirebaseHelper firebaseHelper = new FirebaseHelper();

    public boolean checkIfExist(String id) {
        final boolean[] isPresent = new boolean[1];
        final CountDownLatch[] done = {new CountDownLatch(1)};
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("requestinfo");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    if (Snapshot.child("ID").getValue().toString().equals("id")) {
                        isPresent[0] = true;
                    }
                }
                done[0].countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        try {
            done[0].await(); //it will wait till the response is received from firebase.
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        isPresent[0] = false;
        return isPresent[0];
    }


    private static int generateRandomDigits(int n) {
        int m = (int) Math.pow(10, n - 1);
        return m + new Random().nextInt(9 * m);
    }

    /**
     * Add customer request firebase string.
     * Returns "success" if it was successful in adding customer request
     * @param request the request
     * @return the string
     * @throws IOException the io exception
     */
    @RequestMapping(value = "/addRequest", method = RequestMethod.POST)
    public String addCustomerRequestFirebase(HttpServletRequest request) throws IOException {
        Random rand = new Random();
        String requestID = String.valueOf(generateRandomDigits(9));
        // check if requestID exists, if does not exist, then proceed, else try again
        while(checkIfExist(requestID) != false) {
            requestID = String.valueOf(generateRandomDigits(9));
        }

        username = request.getParameter("userName");
        String recieveName = request.getParameter("recieveName");
        String phone = request.getParameter("phone");
        String boxCount = request.getParameter("boxCount");
        String destination = request.getParameter("destination");
        String pickup = request.getParameter("pickup");
        String cost = request.getParameter("cost");
        String cardno = request.getParameter("cardno");
        String name = request.getParameter("name");
        String cvv = request.getParameter("cvv");
        String exp = request.getParameter("exp");
        String postal = request.getParameter("postal");

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"ID", requestID);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"username", username);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"recievername", recieveName);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"phone", phone);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"box", boxCount);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"progressstatus", "unassigned");
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"status", "ready");
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"destination", destination);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"pickup", pickup);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"cost", cost);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"cardno", cardno);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"name", name);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"cvv", cvv);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"exp", exp);
        firebaseHelper.updateFirebaseData("requestinfo",requestID,"postal", postal);
        return "Success";
    }
    @RequestMapping(value="/setUserName", method = RequestMethod.POST)
    private void setUserName(HttpServletRequest request) {
        String user = request.getParameter("userName");
        username = user;
    }

    /**
     * Gets request list. It is returned as a jsonString and more over it will only pick out requests that are in progress*
     * @param request the request
     * @return the request list
     */
    @RequestMapping(value = "/CustomerRequestList", method = RequestMethod.GET)
    public String getRequestList(HttpServletRequest request) {
        String s = getRequestList().toString();
        System.out.println("Returned: " + s);
        System.out.println("request list returned success");
        return s;
    }

    @RequestMapping(value = "/getBox", method = RequestMethod.POST)
    private String getBoxInfo(HttpServletRequest request) throws IOException, InterruptedException {
        String id = request.getParameter("boxID");
        String requestID = id.substring(0,9);
        String result = firebaseHelper.getFirebaseData("requestinfo", requestID, "BoxData");
        return result;
    }

    private JSONObject getRequestList(){
        JSONArray array = new JSONArray();
        JSONObject jsonobject = new JSONObject();
        final CountDownLatch[] done = {new CountDownLatch(1)};
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("requestinfo");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    if ((Snapshot.child("status").getValue().toString().equals("ready") ||
                            Snapshot.child("status").getValue().toString().equals("in progress")) &&
                            (Snapshot.child("username").getValue().toString().equals(username))) {
                        Object object = Snapshot.getValue(Object.class);
                        array.put(new Gson().toJson(object));
                    }
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
            done[0].await(); //it will wait till the response is received from firebase.
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        return jsonobject;
    }

    private String getUserName(String url){
        int idx = url.indexOf("=");
        String res = url.substring(idx+1,url.length());
        return res;
    }
}

package com.example.demo;

import com.google.firebase.database.*;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/ManagerPage")
public class ManagerController {

    /**
     * Returns the number of requests/jobs that are going on at the moment
     * @param request
     * @return
     */
    @RequestMapping(value = "/ManagerJobInProgressSituationPage", method = RequestMethod.GET)
    public int getJobInProgressSituation(HttpServletRequest request) {
        int result = getRequestInProgressNumber();
        return result;
    }

    /**
     * Returns the number of requests/jobs that are pending at the moment
     * @param request
     * @return
     */
    @RequestMapping(value = "/ManagerJobPendingSituationPage", method = RequestMethod.GET)
    public int getJobPendingSituation(HttpServletRequest request) {
        int result = getRequestPendingNumber();
        return result;
    }

    /**
     * Returns a json object that has the list of requests and all the info entailing it
     * @param request
     * @return
     */
    @RequestMapping(value = "/ManagerRequestListPage", method = RequestMethod.GET)
    public String getRequestList(HttpServletRequest request) {
        String requestlist = getRequestList().toString();
        return requestlist;
    }

    @RequestMapping(value = "/ManagerDriverInfoPage", method = RequestMethod.GET)
    public String getDriverList(HttpServletRequest request) {
        String driverlist = getDriverList().toString();
        return driverlist;
    }

    @RequestMapping(value = "/ManagerAssignJobToDriverPage", method = RequestMethod.GET)
    public String getAvailableDriver(HttpServletRequest request) {
        String driver = getAvailableDriver().toString();
        return driver;
    }

    @RequestMapping(value = "/ManagerUpdateDriverPage", method = RequestMethod.POST)
    public String updateDriverandRequest(HttpServletRequest request) {
        String driver = request.getParameter("Driver");
        String JobID = request.getParameter("JobId");
        updateDriverandRequest(driver, JobID);
        return "Update Driver Successfully";
    }

    private void updateDriverandRequest(String driver, String jobId) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.updateFirebaseData("users", driver, "JobID", jobId);
        firebaseHelper.updateFirebaseData("requestinfo", jobId, "status", "inprogress");
    }

    private int getRequestInProgressNumber(){
        final CountDownLatch[] done = {new CountDownLatch(1)};
        final int[] count = {0};
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("requestinfo");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    if (Snapshot.child("status").getValue().toString().equals("inprogress")) {
                        count[0]++;
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
        return count[0];
    }

    private int getRequestPendingNumber(){
        final CountDownLatch[] done = {new CountDownLatch(1)};
        final int[] count = {0};
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("requestinfo");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    if (Snapshot.child("status").getValue().toString().equals("ready")) {
                        count[0]++;
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
        return count[0];
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
                    if (Snapshot.child("status").getValue().toString().equals("ready")) {
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

    /**
     * Returns a json object that has the list of all avaivible drivers: job search is on and they are not currently driving someone
     * @return
     */
    private JSONObject getDriverList(){
        JSONArray array = new JSONArray();
        JSONObject jsonobject = new JSONObject();
        final CountDownLatch[] done = {new CountDownLatch(1)};
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    if (Snapshot.child("Type").getValue().toString().equals("driver")) {
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

    /**
     *
     * @return
     */
    private JSONObject getAvailableDriver(){
        JSONArray array = new JSONArray();
        JSONObject jsonobject = new JSONObject();
        final CountDownLatch[] done = {new CountDownLatch(1)};
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    if (Snapshot.child("Type").getValue().toString().equals("driver")) {
                        if (!Snapshot.hasChild("JobID") || Snapshot.child("JobID").getValue().toString().equals("NA")) {
                            if (!Snapshot.hasChild("JobSearch") || Snapshot.child("JobSearch").getValue().toString().equals("true")) {
                                Object object = Snapshot.getKey();
                                array.put(new Gson().toJson(object));
                            }
                        }
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
}

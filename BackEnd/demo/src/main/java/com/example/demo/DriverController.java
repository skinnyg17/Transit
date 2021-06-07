package com.example.demo;

import com.google.firebase.database.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

/**
 * The type Driver controller.
 */
@RestController
@RequestMapping("/driverPage")
public class DriverController {

    private FirebaseHelper firebaseHelper = new FirebaseHelper();

    /**
     * This method is used to update the Drivers Settings: Job Search in Firebase Real Time Database
     * We set a true / false value to JobSearch
     * When Job Search is true, GPS coordinates are collected and updated to firebase via another request mapping in this controller
     * request is the HttpServletRequest
     *
     * @param request the request
     * @return string string
     * @throws IOException the io exception
     */
    @RequestMapping(value = "/jobSearch", method = RequestMethod.POST)
    public String updateDriverJobSearch(HttpServletRequest request) throws IOException {
        String username = request.getParameter("name");
        String check = request.getParameter("check");
        firebaseHelper.updateFirebaseData("users",username,"JobSearch", check);
        if(check.equals("true")){
            return "Job Search is now enabled!";
        }
        else{
            return "Job Search is now disabled";
        }
    }

    /**
     * This is a very modular piece of code written for inserting anything into our firebase realtime database
     * What it does is the request has a table name, an object name and a jsonstring
     * When we convert the jsonstring into json object we can loop through it and add all objects into the database
     * returns "Successfully updated Settings" if no errors
     *
     * @param request the request
     * @return string string
     * @throws IOException   the io exception
     * @throws JSONException the json exception
     */
    @RequestMapping(value = "/updateFireBase", method = RequestMethod.POST)
    public String updateFireBaseRequestHandler(HttpServletRequest request) throws IOException, JSONException {
        String tableName = request.getParameter("tableName");
        String object = request.getParameter("object");
        String jsonString = request.getParameter("jsonString");
        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            firebaseHelper.updateFirebaseData(tableName,object,key, String.valueOf(jsonObject.get(key)));
        }
        return "Successfully updated Settings";
    }

    /**
     * Store image string.
     *
     * @param request the request
     * @return string string
     * @throws IOException the io exception
     */
    @RequestMapping(value = "/storeImage", method = RequestMethod.POST)
    public String storeImage(HttpServletRequest request) throws IOException {
        String username = request.getParameter("name");
        String image = request.getParameter("image");
        firebaseHelper.updateFirebaseData("users",username,"Image",image);
        return "Stored";
    }

    /**
     * Gets image.
     *
     * @param request the request
     * @return image image
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @RequestMapping(value = "/getImage", method = RequestMethod.POST)
    public String getImage(HttpServletRequest request) throws IOException, InterruptedException {
        String username = request.getParameter("name");
        String image = firebaseHelper.getFirebaseData("users",username,"Image");
        return image;
    }

    /**
     * Used to store
     *
     * @param request the request
     * @return journey coordinates
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @RequestMapping(value = "/getFireBaseData", method = RequestMethod.POST)
    public String getJourneyCoordinates(HttpServletRequest request) throws IOException, InterruptedException {
        String search = request.getParameter("search");
        String object = request.getParameter("object");
        String tableName = request.getParameter("tableName");
        String toReturn = firebaseHelper.getFirebaseData(tableName,object,search);
        return toReturn;
    }

    /**
     * Gets user settings.
     *
     * @param request the request
     * @return user settings
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     * @throws JSONException        the json exception
     */
    @RequestMapping(value = "/getUserSettings", method = RequestMethod.POST)
    public String getUserSettings(HttpServletRequest request) throws IOException, InterruptedException, JSONException {
        String username = request.getParameter("name");
        JSONObject toReturn = new JSONObject();
        String jobSearch = firebaseHelper.getFirebaseData("users",username,"JobSearch");
        String jobID = firebaseHelper.getFirebaseData("users",username,"JobID");
        String destination = firebaseHelper.getFirebaseData("requestinfo",jobID,"destination");
        String pickup = firebaseHelper.getFirebaseData("requestinfo",jobID,"pickup");
        String boxCount = firebaseHelper.getFirebaseData("requestinfo",jobID,"box");
        String phone = firebaseHelper.getFirebaseData("requestinfo",jobID,"phone");
        String customerName = firebaseHelper.getFirebaseData("requestinfo",jobID,"username");
        String recieverName = firebaseHelper.getFirebaseData("requestinfo",jobID,"recievername");
        String progress = firebaseHelper.getFirebaseData("requestinfo",jobID,"progressstatus");
        if(progress.equals("unassigned")){
            firebaseHelper.updateFirebaseData("requestinfo",jobID,"progressstatus","assigned");
            progress = "assigned";
        }

        toReturn.put("JobSearch", jobSearch);
        toReturn.put("JobID", jobID);
        toReturn.put("destination", destination);
        toReturn.put("pickUp", pickup);
        toReturn.put("boxCount", boxCount);
        toReturn.put("phone", phone);
        toReturn.put("customerName", customerName);
        toReturn.put("recieverName", recieverName);
        toReturn.put("progress", progress);

        return toReturn.toString();
    }

    @RequestMapping(value = "/jobHistory", method = RequestMethod.POST)
    public String getDriverJobHistory(HttpServletRequest request) throws IOException {
        String username = request.getParameter("username");
        System.out.println(username);
        final CountDownLatch[] done = {new CountDownLatch(1)};
        JSONObject toSend = new JSONObject();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("requestinfo");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    if (Snapshot.hasChild("Driver") && Snapshot.hasChild("progressstatus")) {
                        if (Snapshot.child("Driver").getValue().toString().equals(username) && Snapshot.child("progressstatus").getValue().toString().equals("Complete")) {
                            try {
                                JSONObject cur = new JSONObject();
                                cur.put("ID", Snapshot.child("ID").getValue().toString());
                                cur.put("Date", Snapshot.child("Date").getValue().toString());
                                cur.put("pickup", Snapshot.child("pickup").getValue().toString());
                                cur.put("destination", Snapshot.child("destination").getValue().toString());
                                cur.put("Distance", Snapshot.child("Distance").getValue().toString());
                                toSend.put(String.valueOf(toSend.length()), cur.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
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
        return toSend.toString();
    }
}

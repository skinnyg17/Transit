package com.example.demo;

import com.google.firebase.database.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * The type Login sign up controller.
 */
@RestController
@RequestMapping("/loginSignUp")
public class LoginSignUpController {

    /**
     * Gets home.
     *
     * @return the home
     * @throws IOException the io exception
     */
//General Page-Get Request
    @GetMapping
    public ResponseEntity<String> getHome() throws IOException {
        System.out.println("user logged");
        return new ResponseEntity<>(
                "LoginSignUp",
                HttpStatus.OK);
    }


    /**
     * Login string.
     *
     * @param request the request
     * @return the string
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpServletRequest request) throws IOException, InterruptedException {
        String userName = request.getParameter("name");
        String userPassword = request.getParameter("password");
        System.out.println("Attempt");
        if(!userNameExists(userName)){
            return "badRequest";
        }
        else{
            return validLoginCheck(userName,userPassword);
        }
    }

    /**
     * Sign up string.
     *
     * @param request the request
     * @return the string
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public String signUp(HttpServletRequest request) throws IOException, InterruptedException {
        String userName = request.getParameter("name");
        String userEmail = request.getParameter("email");
        String userPhone = request.getParameter("phone");
        String userPassword = request.getParameter("password");
        if(userNameExists(userName) == false) {
            addUserToFirebase(userName, userEmail, userPhone, userPassword);
            return "goodRequest";
        }
        else{
            return "userNameExists";
        }

    }

    /**
     * Valid name check string.
     *
     * @param request the request
     * @return the string
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    @RequestMapping(value = "/validName", method = RequestMethod.POST)
    public String validNameCheck(HttpServletRequest request) throws IOException, InterruptedException {
        String userName = request.getParameter("name");
        String userEmail = request.getParameter("email");
        String code = request.getParameter("code");
        if(userNameExists(userName) == false) {
            //sendEmailVerfication(code,userName,userEmail);
            sendEmail(code, userName, userEmail);
            return "goodRequest";
        }
        else{
            return "userNameExists";
        }

    }

    private void sendEmail(String code, String userName, String userEmail){
        String subject = "BMC Verification";
        String body = "Dear " + userName + ","
                + "\n\nbigmovescompany here!\nVerfication code: " + code + "\nYou have 5 minutes before the code expires.\nIf this was sent by accident, that is too bad.\nThank you!\n\nRegards,\nBMC";
        EmailHelper emailHelper = new EmailHelper(userEmail, subject, body);
        emailHelper.sendEmail();
    }

    private void addUserToFirebase(String userName, String userEmail, String userPhone, String userPassword) throws IOException {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference;
        mDatabaseReference = mDatabase.getReference().child("users");
        mDatabaseReference = mDatabaseReference.child(userName);
        mDatabaseReference.child("Type").setValue("customer",null);
        mDatabaseReference.child("Email").setValue(userEmail,null);
        mDatabaseReference.child("Phone").setValue(userPhone,null);
        mDatabaseReference.child("Password").setValue(userPassword,null);
        mDatabaseReference.child("Name").setValue(userName,null);

    }

    private boolean userNameExists(String userName) throws IOException, InterruptedException {
        CountDownLatch done = new CountDownLatch(1);
        final boolean[] check = new boolean[1];
        check[0] = false;
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(userName)) {
                    check[0] = true;
                    // it exists!
                }
                done.countDown();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        try {
            done.await(); //it will wait till the response is received from firebase.
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return check[0];
    }

    private String validLoginCheck(String userName, String password) throws IOException, InterruptedException {
        CountDownLatch done = new CountDownLatch(1);
        final String[] check = {"badRequest"};
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child(userName).child("Password").getValue().equals(password)){
                    check[0] = (String) snapshot.child(userName).child("Type").getValue();
                }
                done.countDown();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        try {
            done.await(); //it will wait till the response is received from firebase.
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return check[0];
    }

}

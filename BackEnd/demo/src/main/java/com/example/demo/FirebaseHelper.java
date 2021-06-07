package com.example.demo;

import com.google.firebase.database.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * The type Firebase helper.
 */
public class FirebaseHelper {
    /**
     * Gets firebase data.
     *
     * @param tableName the table name
     * @param object    the object
     * @param search    the search
     * @return the firebase data
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public String getFirebaseData(String tableName, String object, String search) throws IOException, InterruptedException {
        CountDownLatch done = new CountDownLatch(1);
        final String[] value = new String[1];
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child(tableName);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child(object).child(search) != null){
                    value[0] = String.valueOf(snapshot.child(object).child(search).getValue());
                }
                // it exists!
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
        if(value[0]==null){
            value[0] = "NA";
        }
        return value[0];
    }

    /**
     * Update firebase data.
     *
     * @param tableName the table name
     * @param object    the object
     * @param key       the key
     * @param value     the value
     */
    public void updateFirebaseData(String tableName, String object, String key, String value){
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference;
        mDatabaseReference = mDatabase.getReference().child(tableName);
        mDatabaseReference = mDatabaseReference.child(object);
        mDatabaseReference.child(key).setValue(value,null);
    }
}

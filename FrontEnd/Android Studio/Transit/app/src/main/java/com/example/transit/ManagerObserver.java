package com.example.transit;

import java.util.Observable;
import java.util.Observer;

public class ManagerObserver implements Observer {
    private String username;
    @Override
    public void update(Observable o, Object username) {
        this.username = (String) username;
    }

    public String notifyManager() {
        String message = "Special request from " + username + ":Hey, I need my packages to be expedited!";
        return message;
    }
}

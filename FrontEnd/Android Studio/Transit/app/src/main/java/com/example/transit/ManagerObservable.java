package com.example.transit;

import java.util.Observable;

public class ManagerObservable extends Observable {
    private String username;

    public void setUsername(String username) {
        this.username = username;
        setChanged();
        notifyObservers(username);
    }
}

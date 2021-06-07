package com.example.transit.ManagerMain;

public class ManagerMainPresenter implements ManagerMainContract.presenter {
    private ManagerMainContract.MVPview managerview;
    ManagerMainPresenter(ManagerMainContract.MVPview view) {
        managerview = view;
    }
}

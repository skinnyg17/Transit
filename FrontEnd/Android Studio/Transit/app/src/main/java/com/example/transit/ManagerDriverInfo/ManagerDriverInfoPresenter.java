package com.example.transit.ManagerDriverInfo;

public class ManagerDriverInfoPresenter implements ManagerDriverInfoContract.presenter{
    private ManagerDriverInfoContract.MVPview mview;
    ManagerDriverInfoPresenter(ManagerDriverInfoContract.MVPview view) {
        mview = view;
    }
}

package com.example.transit.ManagerRequestList;

public class ManagerRequestListPresenter implements ManagerRequestListContract.presenter {
    private ManagerRequestListContract.MVPview mview;
    ManagerRequestListPresenter(ManagerRequestListContract.MVPview view) {
        mview = view;
    }
}

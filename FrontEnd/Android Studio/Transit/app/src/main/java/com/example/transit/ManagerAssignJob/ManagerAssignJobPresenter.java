package com.example.transit.ManagerAssignJob;

public class ManagerAssignJobPresenter implements ManagerAssignJobContract.presenter{
    private ManagerAssignJobContract.MVPview mview;
    ManagerAssignJobPresenter(ManagerAssignJobContract.MVPview view) {
        mview = view;
    }
}

package com.eutectoid.dosomething;

import android.location.Location;

import java.util.List;

/**
 * Created by robbt on 3/20/16.
 */
public class User {
    private String facebookid;
    private String activity;
    private boolean isactive;


    public User() {
    }

    public String getFacebookid() {
        return facebookid;
    }
    public String getActivity() {
        return activity;
    }
    public Boolean getIsActive() {
        return isactive;
    }
}
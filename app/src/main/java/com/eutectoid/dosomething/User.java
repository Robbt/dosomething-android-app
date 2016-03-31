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
        // TODO - create use code that adds itself to FireBase ActiveUsers and stores itself between
        // activities in a Bundle
        // TODO add method to add this User to Firebase
        // TODO add method that removes this User from Firebase
        // TODO - build set method and get methods for this Object
    }

    public void setFacebookid(String id) { facebookid = id; }

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
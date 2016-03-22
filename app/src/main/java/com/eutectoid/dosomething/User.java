package com.eutectoid.dosomething;

import android.location.Location;

import java.util.List;

/**
 * Created by robbt on 3/20/16.
 */
public class User {
    private int mFacebookID;
    private String mFacebookName;
    private String mFacebookAuthToken;
    private boolean mIsActive;
    List<Integer> mFriends;
    private Location mUserLocation;
    private int mActivity;

    public User(String facebookAuthToken) {
        // need to write code to query facebook and set the facebookID & Name
        // and also pull a list of friends
    }

    public String getFacebookName() {
        return mFacebookName;
    }

    public int getFacebookID() {
        return mFacebookID;
    }
    public void setActive() {
        mIsActive = true;
    }
}
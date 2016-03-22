package com.eutectoid.dosomething;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by robbt on 3/20/16.
 */

public class FacebookLoginFragment extends Fragment {
    CallbackManager callbackManager;
    private LoginButton loginButton;
    private TextView mText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this.getContext());
        View v = inflater.inflate(R.layout.fragment_facebook_login, parent, false);
        Log.d("myTag", "onCreate Triggered");
        callbackManager = CallbackManager.Factory.create();
        super.onCreate(savedInstanceState);

        loginButton = (LoginButton) v.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        mText = (TextView) v.findViewById(R.id.mText);
        mText.setText("Please Log in to Facebook to use this App.");
        // Other app specific specialization
        loginButton.setFragment(this);
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("myTag", "Facebook Login Success");
                        // need to launch a new fragment to replace the login button with a user
                        mText.setText(
                                "User ID: "
                                        + loginResult.getAccessToken().getUserId()
                                        + "\n" +
                                        "Auth Token: "
                                        + loginResult.getAccessToken().getToken()
                        );
                    }

                    @Override
                    public void onCancel() {
                        Log.d("myTag", "Facebook Login Cancelled ");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("myTag", "Facebook Login Error Triggered");

                    }
                });
        // Inflate the layout for this fragment
        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

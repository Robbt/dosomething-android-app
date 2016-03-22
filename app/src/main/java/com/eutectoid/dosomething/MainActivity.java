package com.eutectoid.dosomething;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

/**
 * Created by robbt on 3/20/16.
 */
public class MainActivity extends FragmentActivity {
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dosomething);


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_facebook_login);
        if (fragment == null) {
            fragment = new FacebookLoginFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}

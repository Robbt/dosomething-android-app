package com.eutectoid.dosomething;
import android.content.Intent;
import com.facebook.FacebookSdk;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

import java.util.List;

/**
 * Created by robbt on 3/20/16.
 */
public class MainActivity extends FragmentActivity {


    private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";
    // these are the keys to the fragment array
    private static final int SPLASH = 0;
    private static final int SELECTION = 1;
    private static final int SETTINGS = 2;
    private static final int FRAGMENT_COUNT = SETTINGS + 1;
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private boolean isResumed = false;
    private boolean userSkippedLogin = false;
    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;


    public void AddActiveUser(String activity) {
        Bundle bundle = new Bundle();
        // call the active user, this is called from the button of whatdolist adapter
        ((DoSomethingApplication) this.getApplication())
                .addActiveUser(activity);
    FragmentManager manager= getSupportFragmentManager();
        // now i need to delete the whatdo list - using this idea http://stackoverflow.com/questions/22474584/remove-old-fragment-from-fragment-manager#22474821
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.somethingFragment).getChildFragmentManager().findFragmentByTag("WHATDOFRAGMENT");
        if(fragment != null) {
            getSupportFragmentManager().findFragmentById(R.id.somethingFragment).getChildFragmentManager().beginTransaction().remove(fragment).commit();
        }
        //getSupportFragmentManager().findFragmentById(R.id.somethingFragment).getChildFragmentManager().beginTransaction().add(R.id.whatdocontainer, new FriendsListFragment(), "FRIENDSLIST").commit();
    }
    public interface IMethodCaller{
        void AddActiveUser();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            userSkippedLogin = savedInstanceState.getBoolean(USER_SKIPPED_LOGIN_KEY);
        }

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (isResumed) {
                    FragmentManager manager = getSupportFragmentManager();
                    int backStackSize = manager.getBackStackEntryCount();
                    for (int i = 0; i < backStackSize; i++) {
                        manager.popBackStack();
                    }
                    if (currentAccessToken != null) {
                        showFragment(SELECTION, false);
                    } else {
                        showFragment(SPLASH, false);
                    }
                }
            }
        };

        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        SplashFragment splashFragment = (SplashFragment) fm.findFragmentById(R.id.splashFragment);
        // TODO add a friends list fragment to display underneath WHATDO,
        fragments[SPLASH] = splashFragment;
        fragments[SELECTION] = fm.findFragmentById(R.id.somethingFragment);
        fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }

        transaction.commit();

        splashFragment.setSkipLoginCallback(new SplashFragment.SkipLoginCallback() {
            @Override
            public void onSkipLoginPressed() {
                userSkippedLogin = true;
                showFragment(SELECTION, false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);

        if (AccessToken.getCurrentAccessToken() != null) {
            // if the user already logged in, try to show the selection fragment
            showFragment(SELECTION, false);
            userSkippedLogin = false;
        } else if (userSkippedLogin) {
            showFragment(SELECTION, false);
        } else {
            // otherwise present the splash screen and ask the user to login,
            // unless the user explicitly skipped.
            showFragment(SPLASH, false);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;

        //LoginManager.getInstance().logOut();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
    }

    public void showSettingsFragment() {
        showFragment(SETTINGS, true);
    }

    public void showSplashFragment() { showFragment(SPLASH, true); }

    public void showWhatDoFragment() {
        //showFragment(WHATDO, true);
        WhatdoListFragment whatdoFragment = new WhatdoListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.whatdocontainer, whatdoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }

        // TODO move to proper place
        if(fragmentIndex == SELECTION) {
            DoSomethingApplication app = (DoSomethingApplication) getApplication();
            app.getFacebookFriends();
        }

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}

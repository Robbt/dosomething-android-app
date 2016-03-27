package com.eutectoid.dosomething;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.json.JSONObject;

import java.util.List;


/**
 * Created by robbt on 3/26/16.
 * This custom application is used to pass state data between Activities
 */
public class DoSomethingApplication extends Application{

    private List<User> activeUsers;

    @Override
        public void onCreate() {
            super.onCreate();
            FacebookSdk.sdkInitialize(getApplicationContext());
            Firebase.setAndroidContext(this);

        }

    // This method queries the firebase and inserts all of the entries into the listener.
    public List<User> getActiveUsers() {
        // pull the value from the secrets.properties file so that it doesn't get shared via github
        String FIREBASE_DB = BuildConfig.FIREBASE_DB;
        Firebase refActive = new Firebase(FIREBASE_DB);
        refActive.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                activeUsers.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User activeUser = postSnapshot.getValue(User.class);
                    activeUsers.add(activeUser);

                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    return activeUsers;
    }




}

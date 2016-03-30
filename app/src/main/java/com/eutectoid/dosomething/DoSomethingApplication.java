package com.eutectoid.dosomething;

import android.app.Application;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by robbt on 3/26/16.
 * This custom application is used to pass state data between Activities
 */
public class DoSomethingApplication extends Application{
    private String userId;
    private List<User> activeUsers;
    private List<User> facebookFriends;

    @Override
        public void onCreate() {
            super.onCreate();
            FacebookSdk.sdkInitialize(getApplicationContext());
            Firebase.setAndroidContext(this);

        }

    // This method queries the firebase and inserts all of the entries into the listener.
    public List<User> getActiveUsers() {
        // TODO - need to fix the sample user
        // TODO - add fake facebook users as default ON
        // pull the value from the secrets.properties file so that it doesn't get shared via github
        String FIREBASE_DB = BuildConfig.FIREBASE_DB;
        Firebase refActive = new Firebase(FIREBASE_DB);
        final List<User> activeUsers = new ArrayList<User>();
        refActive.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

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
    public List<User> getFacebookFriends() {
        //TODO Create an List<Users> from Facebook
        // TODO Test this Method TJ
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String userID = AccessToken.USER_ID_KEY;

        GraphRequest request = GraphRequest.newMyFriendsRequest(
                accessToken,
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(
                            JSONArray object,
                            GraphResponse response) {


                    }
                });
        return facebookFriends;
    }
}

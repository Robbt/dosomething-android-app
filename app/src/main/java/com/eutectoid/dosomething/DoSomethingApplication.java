package com.eutectoid.dosomething;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

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
import org.json.JSONException;
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
    private ArrayList<User> facebookFriends = new ArrayList<>();

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
        //Log.d("myTag", "Made it into getFacebookFriends()");
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String userID = AccessToken.USER_ID_KEY;

        GraphRequest request = GraphRequest.newMyFriendsRequest(
                accessToken,
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray object, GraphResponse response) {
                        if(response.getError() != null) {
                            Log.d("myTag", "ResponseError:  " + response.getError().getErrorMessage());
                        }
                        else {
                            try {
                                JSONObject obj = new JSONObject(response.getRawResponse());
                                JSONArray data = obj.getJSONArray("data");
                                for(int i = 0; i < data.length(); ++i) {
                                    // TODO get more info from Facebook
                                    User user = new User();
                                    user.setFacebookid(data.getJSONObject(i).getString("id"));
                                    facebookFriends.add(user);
                                }
                            }
                            catch(JSONException e) {
                                Log.d("myTag", "JSONException: " + e.getMessage());
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();

        return facebookFriends;
    }
}

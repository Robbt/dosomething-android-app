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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by robbt on 3/26/16.
 * This custom application is used to pass state data between Activities
 */
public class DoSomethingApplication extends Application{
    private String userId;
    private List<User> activeUsers;
    private ArrayList<User> facebookFriends = new ArrayList<>();
    private Set<String> FriendsSet = new HashSet<String>();

    @Override
        public void onCreate() {
            super.onCreate();
            FacebookSdk.sdkInitialize(getApplicationContext());
            Firebase.setAndroidContext(this);

        }

    // This method queries the firebase and inserts all of the entries into the listener.
    public List<User> getActiveUsers(final Set<String> friends) {
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

                    Set<String> addedFriends = new HashSet<String>();
                    if ( !(addedFriends.contains(activeUser.getFacebookid())) && (friends.contains(activeUser.getFacebookid()))) {
                        addedFriends.add(activeUser.getFacebookid());
                        activeUsers.add(activeUser);
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        return activeUsers;
    }
    public void addActiveUser(String activity) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            String userID = accessToken.getUserId();
            String username = "Robbt E";
            // TODO encode lookup of user name from facebook
            User myUser = new User(userID);
            myUser.setUsername(username);
            myUser.setIsactive(true);
            myUser.setActivity(activity);
            String FIREBASE_DB = BuildConfig.FIREBASE_DB;
            Firebase refActive = new Firebase(FIREBASE_DB);
            Firebase newUser = refActive.push();
            Map<String, String> myUserDB = new HashMap<String, String>();
            myUserDB.put("facebookid", myUser.getFacebookid());
            myUserDB.put("username", myUser.getUsername());
            myUserDB.put("activity", myUser.getActivity());
            myUserDB.put("isactive", myUser.getIsActive().toString());
            refActive.push().setValue(myUserDB);
        }
    }
    public Set<String> getFacebookFriendsSet() {
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
                                    String FriendID = (data.getJSONObject(i).getString("id"));
                                    FriendsSet.add(FriendID);
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
        return FriendsSet;



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
                                    user.setUsername(data.getJSONObject(i).getString("name"));
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

        // Test Users
        User temp1 = new User();
        temp1.setFacebookid("104135979983213");
        temp1.setUsername("Rick Alaafjdccbdbb Occhinosen");
        temp1.setActivity("Do Anything");
        temp1.setIsactive(true);
        facebookFriends.add(temp1);

        User temp2 = new User();
        temp2.setFacebookid("110149799380373");
        temp2.setUsername("Margaret Alaaefjeiehih Chengstein");
        temp2.setActivity("Watch a Movie");
        temp2.setIsactive(true);
        facebookFriends.add(temp2);

        User temp3 = new User();
        temp3.setFacebookid("110530546008966");
        temp3.setUsername("Bill Alaaefjfbeffe Martinazzisen");
        temp3.setActivity("Eat Food");
        temp3.setIsactive(true);
        facebookFriends.add(temp3);

        return facebookFriends;
    }

    public User getUser(String facebookid) {
        for (User user : facebookFriends) {
            if (user.getFacebookid().equals(facebookid)) {
                return user;
            }
        }
        return null;
    }
}

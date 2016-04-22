package com.eutectoid.dosomething;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
    private static User myUserInfo;
    private List<User> activeUsers;
    private ArrayList<User> facebookFriends = new ArrayList<>();
    /* Used to store the set of facebook friends for determining which active users are inside of it. */
    private Set<String> FriendsSet = new HashSet<String>();
    /* This use is used to store the user profile from Facebook itself */
    private JSONObject user;

    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String PICTURE = "picture";

    private static final String FIELDS = "fields";

    private static final String REQUEST_FIELDS =
            TextUtils.join(",", new String[]{ID, NAME, PICTURE});


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
                Set<String> addedFriends = new HashSet<String>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User activeUser = postSnapshot.getValue(User.class);

                    if (!(addedFriends.contains(activeUser.getFacebookid())) && (friends.contains(activeUser.getFacebookid()))) {
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
    public List<User> getActiveUsers() {
        // pull the value from the secrets.properties file so that it doesn't get shared via github
        String FIREBASE_DB = BuildConfig.FIREBASE_DB;
        Firebase refActive = new Firebase(FIREBASE_DB);
        final List<User> activeUsers = new ArrayList<User>();
        refActive.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Set<String> addedFriends = new HashSet<String>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    User activeUser = postSnapshot.getValue(User.class);

                    if (!(addedFriends.contains(activeUser.getFacebookid()))) {
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


    public void addUserFireBase(final User myUser) {
        if (hasNetworkConnection() ) {
            //first check to see if the user exists in the Database
            String FIREBASE_DB = BuildConfig.FIREBASE_DB;
            final Firebase refActive = new Firebase(FIREBASE_DB);
            String key = new String();
            final Map<String, String> myUserDB = new HashMap<String, String>();
            myUserDB.put("facebookid", myUser.getFacebookid());
            myUserDB.put("username", myUser.getUsername());
            myUserDB.put("activity", myUser.getActivity());
            myUserDB.put("isactive", myUser.getIsActive().toString());
            myUserDB.put("facebookmessengerid", myUser.getFacebookmessengerid());
            refActive.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String key = new String();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        User activeUser = postSnapshot.getValue(User.class);
                        if (activeUser.getFacebookid().equals(myUser.getFacebookid())) {
                            key = postSnapshot.getKey();
                            break;
                        }
                    }
                    if (key.isEmpty()) {
                        Firebase newUser = refActive.push();
                        refActive.push().setValue(myUserDB);
                    } else {
                        Firebase updateUser = refActive.child(key);
                        updateUser.setValue(myUserDB);
                    }
                }


                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Your internet connection is down. We can't access the server.", Toast.LENGTH_SHORT).show();
        }

    }


    public void fetchUserIDaddUser(final String activity) {
        if (hasNetworkConnection()) {
            final AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken != null) {
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                user = me;
                                String userID = accessToken.getUserId();

                                String username = user.optString("name");
                                myUserInfo = new User(userID);
                                myUserInfo.setUsername(username);
                                myUserInfo.setIsactive(true);
                                myUserInfo.setActivity(activity);
                            /*
                            //TODO move this into another AsyncTask that checks for existing User or utilizes a cache
                            String FIREBASE_DB = BuildConfig.FIREBASE_DB;
                            Firebase refActive = new Firebase(FIREBASE_DB);
                            Firebase newUser = refActive.push();
                            Map<String, String> myUserDB = new HashMap<String, String>();
                            myUserDB.put("facebookid", myUser.getFacebookid());
                            myUserDB.put("username", myUser.getUsername());
                            myUserDB.put("activity", myUser.getActivity());
                            myUserDB.put("isactive", myUser.getIsActive().toString());
                            refActive.push().setValue(myUserDB); */
                                addUserFireBase(myUserInfo);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString(FIELDS, REQUEST_FIELDS);
                request.setParameters(parameters);
                GraphRequest.executeBatchAsync(request);
            } else {
                user = null;
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Your internet connection is down. We can't access the server.", Toast.LENGTH_SHORT).show();

        }
    }



    public void addActiveUser(String activity) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            fetchUserIDaddUser(activity);
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

    public boolean hasNetworkConnection(){
        ConnectivityManager
                connectivityManager
                =
                (
                        ConnectivityManager
                        )
                        getSystemService
                                (
                                        Context.CONNECTIVITY_SERVICE
                                );
        NetworkInfo
                networkInfo
                =
                connectivityManager.getNetworkInfo
                        (
                                ConnectivityManager.TYPE_WIFI
                        );
        boolean
                isConnected
                =    true;
        boolean
                isWifiAvailable
                =
                networkInfo.isAvailable
                        ();
        boolean
                isWifiConnected
                =
                networkInfo.isConnected
                        ();
        networkInfo
                =
                connectivityManager.getNetworkInfo
                        (
                                ConnectivityManager.TYPE_MOBILE
                        );
        boolean
                isMobileAvailable
                =
                networkInfo.isAvailable
                        ();
        boolean
                isMobileConnnected
                =
                networkInfo.isConnected
                        ();
        isConnected
                =    (
                isMobileAvailable
                        &&
                        isMobileConnnected
        )    ||
                (
                        isWifiAvailable
                                &&
                                isWifiConnected
                );
        return(
                isConnected
        );
    }

    public static User getMyUserInfo() { return myUserInfo; }
}

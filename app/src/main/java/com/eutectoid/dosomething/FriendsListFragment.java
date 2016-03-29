package com.eutectoid.dosomething;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.eutectoid.dosomething.R;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.AnalyticsEvents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by robbt on 3/26/16.
 */
public class FriendsListFragment extends Fragment {


    public enum FriendPickerType {
        FRIENDS("/friends"),
        TAGGABLE_FRIENDS("/taggable_friends"),
        INVITABLE_FRIENDS("/invitable_friends");

        private final String requestPath;

        FriendPickerType(String path) {
            this.requestPath = path;
        }

        String getRequestPath() {
            return requestPath;
        }
    }

    private static final String ID = "id";
    private static final String NAME = "name";
    private String userId;
    private ListView mListView;


    private FriendPickerType friendPickerType = FriendPickerType.FRIENDS;


    private GraphRequest createRequest() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String userID = AccessToken.USER_ID_KEY;

        GraphRequest request = GraphRequest.newMyFriendsRequest(
                accessToken,
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(
                            JSONArray object,
                            GraphResponse response) {
                        object.toString();
                    }
                });
        return request;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendlist_fragment, container, false);
        this.createRequest();
        mListView = (ListView) view.findViewById(R.id.friend_list_view);
        return view;
    }

}

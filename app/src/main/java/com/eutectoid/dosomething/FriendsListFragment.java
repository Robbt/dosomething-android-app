package com.eutectoid.dosomething;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    private RecyclerView mFacebookFriendsRecyclerView;

    private static final String ID = "id";
    private static final String NAME = "name";
    private String userId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendlist_fragment, container, false);
        mFacebookFriendsRecyclerView = (RecyclerView) view.findViewById(R.id.friend_recycler_view);
        mFacebookFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    private void updateUI() {
        //need to finish implementing recycler view as per page 184 of big nerd guide
    }

    private class FriendHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;

        public FriendHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView;
        }
    }

    private class FriendAdapter extends RecyclerView.Adapter<FriendHolder> {
        private List<User> mFriends;

        public FriendAdapter(List<User> friends) {
            mFriends = friends;
        }
        @Override
        public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1,parent, false);
            return new FriendHolder(view);
        }
        @Override
        public void onBindViewHolder(FriendHolder holder, int position) {
            User user = mFriends.get(position);
            holder.mTitleTextView.setText(user.getFacebookid());
        }
        @Override
        public int getItemCount() {
            return mFriends.size();
        }
    }

}

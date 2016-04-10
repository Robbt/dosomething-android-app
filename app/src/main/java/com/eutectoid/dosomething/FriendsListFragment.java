package com.eutectoid.dosomething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eutectoid.dosomething.R;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.share.widget.SendButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private FriendAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendlist_fragment, container, false);
        mFacebookFriendsRecyclerView = (RecyclerView) view.findViewById(R.id.friend_recycler_view);
        mFacebookFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }


public class fetchImage {
    public Bitmap get(String urlstr) {
        try {
            URL url;
            url = new URL(urlstr);

            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setDoInput(true);
            c.connect();
            InputStream is = c.getInputStream();
            Bitmap img;
            img = BitmapFactory.decodeStream(is);
            return img;
        } catch (MalformedURLException e) {
            Log.d("RemoteImageHandler", "fetchImage passed invalid URL: " + urlstr);
        } catch (IOException e) {
            Log.d("RemoteImageHandler", "fetchImage IO exception: " + e);
        }
        return null;
    }
}

    private void updateUI() {
        // TODO get it woking with getActiveUsers
        List<User> FacebookFriends = ((DoSomethingApplication) getActivity().getApplication())
                .getFacebookFriends();
                mAdapter = new FriendAdapter(FacebookFriends);
                mFacebookFriendsRecyclerView.setAdapter(mAdapter);

    }

    private FetchPhotoTask.OnTaskCompleted listener = new FetchPhotoTask.OnTaskCompleted() {
        public void onTaskCompleted() {
        }
    };
    private class FriendHolder extends RecyclerView.ViewHolder {
        private User mFriend;

        private TextView mFriendName;
        private TextView mFriendActivity;
        private ImageView mProfilePhoto;


        public FriendHolder(View itemView) {
            super(itemView);
            //SendButton button = (SendButton)itemView.findViewById(R.id.list_item_message_friend);
            mFriendName = (TextView) itemView.findViewById(R.id.list_item_friend_name_text_view);
            mFriendActivity = (TextView) itemView.findViewById(R.id.list_item_friend_activity_text_view);
            mProfilePhoto = (ImageView) itemView.findViewById(R.id.list_item_friend_photo);
        }
        public void bindBitmap(Bitmap bitmap) {

            mProfilePhoto.setImageBitmap(bitmap);
        }

        public void bindFriend(User user) {
            mFriend = user;
            mFriendName.setText(user.getUsername());
            mFriendActivity.setText(user.getActivity());
            class GetFacebookProfilePhoto extends AsyncTask<String, Integer, Bitmap> {
                protected Bitmap doInBackground(String... params) {
                    return new fetchImage().get(params[0]);
                }
                protected void onPostExecute(Bitmap response) {
                    mProfilePhoto.setImageBitmap(response);
                }

            }
            String mProfilePhotoURL = "https://graph.facebook.com/" + mFriend.getFacebookid() + "/picture?type=large";
            new GetFacebookProfilePhoto().execute(mProfilePhotoURL);
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
            View view = layoutInflater.inflate(R.layout.friend_list_item, parent, false);
            return new FriendHolder(view);
        }
        @Override
        public void onBindViewHolder(FriendHolder holder, int position) {
            User user = mFriends.get(position);
            holder.bindFriend(user);
        }
        @Override
        public int getItemCount() {
            return mFriends.size();
        }
    }

}

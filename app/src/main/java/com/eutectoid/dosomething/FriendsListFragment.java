package com.eutectoid.dosomething;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eutectoid.dosomething.R;
import com.eutectoid.dosomething.WhatdoListView.Adapter.WhatdoListAdapter;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.share.widget.SendButton;

import org.json.JSONArray;
import org.json.JSONObject;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.ShareToMessengerParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
    private Set<String> FriendsSet = new HashSet<String>();
    private List<User> FacebookFriends = new ArrayList<User>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendlist_fragment, container, false);
        mFacebookFriendsRecyclerView = (RecyclerView) view.findViewById(R.id.friend_recycler_view);

        mFacebookFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // need to combine these into a single method


        if (FriendsSet.isEmpty()) {
            FriendsSet = ((DoSomethingApplication) getActivity().getApplication()).getFacebookFriendsSet();
        }
        if (FacebookFriends.isEmpty()) {
            FacebookFriends = ((DoSomethingApplication) getActivity().getApplication())
                    .getActiveUsers();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        }, 1000);
        return view;
    }

    public Long idToLong(User u) {
        String id;

        if (u.getFacebookmessengerid() == null) {
            id = u.getFacebookid();
        }
         else {       id = u.getFacebookmessengerid();
            }
        return Long.parseLong(id);
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
            String path = Environment.getDownloadCacheDirectory().getAbsolutePath();
            FileOutputStream out = null;
            try {
                String filename = urlstr.substring(27, 42);
                //File file = new File(path, filename+".jpg");
                //file.createNewFile();
                out = getContext().openFileOutput(filename + ".jpg", Context.MODE_PRIVATE);
                img.compress(Bitmap.CompressFormat.JPEG, 85, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return img;
        } catch (MalformedURLException e) {
            Log.d("RemoteImageHandler", "fetchImage passed invalid URL: " + urlstr);
        } catch (IOException e) {
            Log.d("RemoteImageHandler", "fetchImage IO exception: " + e);
        }
        return null;
    }
}

        public void updateUI() {
            List<User> DisplayFriends = new ArrayList<>(FacebookFriends);
            User myUser = DoSomethingApplication.getMyUserInfo();
            String myActivity = myUser.getActivity();
            String myID = myUser.getFacebookid();
            for(User u: DisplayFriends) {
                if(u.getFacebookid().compareToIgnoreCase(myID) == 0) {
                    DisplayFriends.remove(u);
                    break;
                }
            }

            if(myActivity.compareToIgnoreCase("Do Anything") != 0) {
                for(int i = DisplayFriends.size() - 1; i >= 0; --i) {
                    String friendActivity = DisplayFriends.get(i).getActivity();
                    if(friendActivity.compareToIgnoreCase("Do Anything") != 0 && friendActivity.compareToIgnoreCase(myActivity) != 0) {
                        DisplayFriends.remove(i);
                    }
                }
            }
            mAdapter = new FriendAdapter(DisplayFriends);
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
                String filename = mProfilePhotoURL.substring(27, 42) + ".jpg";
                if (hasCachedProfilePhoto(filename)) {
                    String path = Environment.getDownloadCacheDirectory().getAbsolutePath();
                    Bitmap fbphoto = null;
                    try {
                        File filePath = getContext().getFileStreamPath(filename);
                        FileInputStream fi = new FileInputStream(filePath);
                        fbphoto = BitmapFactory.decodeStream(fi);
                        mProfilePhoto.setImageBitmap(fbphoto);
                    } catch (Exception e) {
                        Log.e("no get profile photo", e.getMessage());
                    }
                } else {
                    new GetFacebookProfilePhoto().execute(mProfilePhotoURL);
                }
            }

        }

        boolean hasCachedProfilePhoto (String profilephoto){
            File file = getContext().getFileStreamPath(profilephoto);
            return file.exists();
        }

        private class FriendAdapter extends RecyclerView.Adapter<FriendHolder> {
            private List<User> mFriends;
            private View view;

            public FriendAdapter(List<User> friends) {
                mFriends = friends;

            }

            @Override
            public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                view = layoutInflater.inflate(R.layout.friend_list_item, parent, false);
                return new FriendHolder(view);
            }

            @Override
            public void onBindViewHolder(FriendHolder holder, int position) {
                final User user = mFriends.get(position);
                holder.bindFriend(user);
                // TODO test messenger button
                final ImageButton b = (ImageButton) view.findViewById(R.id.messenger_send_button);

                b.setOnClickListener(new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("fb-messenger://user/");
                        uri = ContentUris.withAppendedId(uri, idToLong(user));
                        Log.d("myTag", uri.toString());

                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mFriends.size();
            }
        }
}

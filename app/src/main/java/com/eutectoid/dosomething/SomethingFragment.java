/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.eutectoid.dosomething;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;

import com.facebook.*;
import com.facebook.internal.Utility;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.SendButton;
import com.facebook.share.widget.ShareButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Fragment that represents the main selection screen for Scrumptious.
 */
public class SomethingFragment extends Fragment {

    private static final String TAG = "SomethingFragment";
    private static final String PENDING_ANNOUNCE_KEY = "pendingAnnounce";
    private static final int USER_GENERATED_MIN_SIZE = 480;
    private static final float MAX_TEXTURE_SIZE = 1024f;
    private static final String SHARE_ACTION_TYPE = "dosomething:action";

    private static final String PERMISSION = "publish_actions";


    public User myUser;

        private TextView announceButton;
    private ShareButton shareButton;
    private SendButton messageButton;
    private ProfilePictureView profilePictureView;
    private boolean pendingAnnounce;
    private MainActivity activity;
    private ProgressDialog announceProgressDialog;


    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

    private FacebookCallback<Sharer.Result> shareCallback =
            new FacebookCallback<Sharer.Result>() {
                @Override
                public void onCancel() {
                    processDialogResults(null, true);
                }

                @Override
                public void onError(FacebookException error) {
                    if (error instanceof FacebookGraphResponseException) {
                        FacebookGraphResponseException graphError =
                                (FacebookGraphResponseException) error;
                        if (graphError.getGraphResponse() != null) {
                            handleError(graphError.getGraphResponse());
                            return;
                        }
                    }
                    processDialogError(error);
                }

                @Override
                public void onSuccess(Sharer.Result result) {
                    processDialogResults(result.getPostId(), false);
                }
            };
    public void toggleWhatDoFragment() {
        Fragment whatdoListFragment = getChildFragmentManager().findFragmentByTag("WHATDOFRAGMENT");
        if (whatdoListFragment == null || !whatdoListFragment.isVisible()) {
            Log.d("myTag", "Fragment is null");
            whatdoListFragment = new WhatdoListFragment();

            FragmentTransaction newtransaction = getChildFragmentManager().beginTransaction();
            newtransaction.add(R.id.whatdocontainer, whatdoListFragment, "WHATDOFRAGMENT");
            newtransaction.commit();
            getFragmentManager().executePendingTransactions();
        }
        else {
            Log.d("myTag", "Fragment is NOT null");
            if (whatdoListFragment.isVisible()) {
                Log.d("myTag", "whatdoFragment removed");
                getFragmentManager().beginTransaction().remove(whatdoListFragment).commit();
                getFragmentManager().executePendingTransactions();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                updateWithToken(currentAccessToken);
            }
        };

    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {
            tokenUpdated(currentAccessToken);
            profilePictureView.setProfileId(currentAccessToken.getUserId());
            announceButton.setVisibility(View.VISIBLE);
        } else {
            profilePictureView.setProfileId(null);
            announceButton.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.something, container, false);


        profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        announceButton = (TextView) view.findViewById(R.id.announce_text);
        shareButton = (ShareButton) view.findViewById(R.id.share_button);
        messageButton = (SendButton) view.findViewById(R.id.message_button);


        announceProgressDialog = new ProgressDialog(getActivity());
        announceProgressDialog.setMessage(getString(R.string.progress_dialog_text));

        if (MessageDialog.canShow(ShareOpenGraphContent.class)) {
            messageButton.setVisibility(View.VISIBLE);
        }



            announceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAnnounce();
            }
        });

        messageButton.registerCallback(callbackManager, shareCallback);
        messageButton.setFragment(this);
        shareButton.registerCallback(callbackManager, shareCallback);
        shareButton.setFragment(this);

        /* Adding DoSomething Button */
        Button doButton = (Button) view.findViewById(R.id.do_button);
        doButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleWhatDoFragment();
            }
        });

        //TODO Add show FriendsList Function and Fragment FriendsListFragment



        profilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    activity.showSettingsFragment();
                } else {
                    activity.showSplashFragment();
                }
            }
        });

        init(savedInstanceState);
        updateWithToken(AccessToken.getCurrentAccessToken());

        return view;
    }

    @Override
    public void onActivityResult ( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
  /*       List<User> ActiveUsers =  ((DoSomethingApplication) getActivity().getApplication())
              .getActiveUsers(); */
         List<User> FacebookFriends = ((DoSomethingApplication) getActivity().getApplication())
                .getFacebookFriends();
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }


    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(PENDING_ANNOUNCE_KEY, pendingAnnounce);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        activity = null;
    }

    private void processDialogError(FacebookException error) {
        enableButtons();
        announceProgressDialog.dismiss();

        if (error != null) {
            new AlertDialog.Builder(getActivity())
                    .setPositiveButton(R.string.error_dialog_button_text, null)
                    .setTitle(R.string.error_dialog_title)
                    .setMessage(error.getLocalizedMessage())
                    .show();
        }
    }

    private void processDialogResults(String postId, boolean isCanceled) {
        enableButtons();
        announceProgressDialog.dismiss();

        boolean resetSelections = true;
        if (isCanceled) {
            // Leave selections alone if user canceled.
            resetSelections = false;
            showCancelResponse();
        } else {
            showSuccessResponse(postId);
        }

        if (resetSelections) {
            init(null);
        }
    }

    private void showRejectedPermissionError() {
        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.error_dialog_button_text, null)
                .setTitle(R.string.error_dialog_title)
                .setMessage(R.string.rejected_publish_permission)
                .show();
    }

    /**
     * Notifies that the token has been updated.
     */
    private void tokenUpdated(AccessToken currentAccessToken) {
        if (pendingAnnounce) {
            Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
            if (currentAccessToken == null
                    || !currentAccessToken.getPermissions().contains(PERMISSION)) {
                pendingAnnounce = false;
                showRejectedPermissionError();
                return;
            }
            handleAnnounce();
        }
    }

    private void updateShareContent() {
        ShareContent content = createOpenGraphContent();
        if (content != null) {
            enableButtons();
        } else {
            disableButtons();
        }

        shareButton.setShareContent(content);
        messageButton.setShareContent(content);
    }

    private void disableButtons() {
        announceButton.setEnabled(false);
        shareButton.setEnabled(false);
        messageButton.setEnabled(false);
    }

    private void enableButtons() {
        announceButton.setEnabled(true);
        shareButton.setEnabled(true);
        messageButton.setEnabled(true);
    }

    /**
     * Resets the view to the initial defaults.
     */
    private void init(Bundle savedInstanceState) {
        disableButtons();



        if (savedInstanceState != null) {
            pendingAnnounce = savedInstanceState.getBoolean(PENDING_ANNOUNCE_KEY, false);
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            profilePictureView.setProfileId(accessToken.getUserId());

        }
        if (accessToken != null) {
            myUser = new User(accessToken.getUserId());

        }

        updateShareContent();
    }

    private void handleAnnounce() {
        Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
        if (!permissions.contains(PERMISSION)) {
            pendingAnnounce = true;
            requestPublishPermissions();
            return;
        } else {
            pendingAnnounce = false;
        }

        disableButtons();
        announceProgressDialog.show();
        ShareApi.share(createOpenGraphContent(), shareCallback);
    }
    private ShareOpenGraphContent createOpenGraphContent() {
        ShareOpenGraphAction.Builder actionBuilder = createShareActionBuilder();

        boolean userGenerated = false;
        return new ShareOpenGraphContent.Builder()
                .setAction(actionBuilder.build())
                .setPreviewPropertyName("meal")
                .build();
    }

    private ShareOpenGraphAction.Builder createShareActionBuilder() {
        ShareOpenGraphAction.Builder builder = new ShareOpenGraphAction.Builder()
                .setActionType(SHARE_ACTION_TYPE);

        return builder;
    }


    private void requestPublishPermissions() {
        LoginManager.getInstance()
                .setDefaultAudience(DefaultAudience.FRIENDS)
                .logInWithPublishPermissions(this, Arrays.asList(PERMISSION));
    }

    private void showSuccessResponse(String postId) {
        String dialogBody;
        if (postId != null) {
            dialogBody = String.format(getString(R.string.result_dialog_text_with_id), postId);
        } else {
            dialogBody = getString(R.string.result_dialog_text_default);
        }
        showResultDialog(dialogBody);
    }

    private void showCancelResponse() {
        showResultDialog(getString(R.string.result_dialog_text_canceled));
    }

    private void showResultDialog(String dialogBody) {
        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.result_dialog_button_text, null)
                .setTitle(R.string.result_dialog_title)
                .setMessage(dialogBody)
                .show();
    }

    private void handleError(GraphResponse response) {
        FacebookRequestError error = response.getError();
        DialogInterface.OnClickListener listener = null;
        String dialogBody = null;

        if (error == null) {
            dialogBody = getString(R.string.error_dialog_default_text);
        } else {
            switch (error.getCategory()) {
                case LOGIN_RECOVERABLE:
                    // There is a login issue that can be resolved by the LoginManager.
                    LoginManager.getInstance().resolveError(this, response);
                    return;

                case TRANSIENT:
                    dialogBody = getString(R.string.error_transient);
                    break;

                case OTHER:
                default:
                    // an unknown issue occurred, this could be a code error, or
                    // a server side issue, log the issue, and either ask the
                    // user to retry, or file a bug
                    dialogBody = getString(R.string.error_unknown, error.getErrorMessage());
                    break;
            }
        }

        String title = error.getErrorUserTitle();
        String message = error.getErrorUserMessage();
        if (message == null) {
            message = dialogBody;
        }
        if (title == null) {
            title = getResources().getString(R.string.error_dialog_title);
        }

        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.error_dialog_button_text, listener)
                .setTitle(title)
                .setMessage(message)
                .show();
    }



    private class ActionListAdapter extends ArrayAdapter<BaseListElement> {
        private List<BaseListElement> listElements;

        public ActionListAdapter(
                Context context, int resourceId, List<BaseListElement> listElements) {
            super(context, resourceId, listElements);
            this.listElements = listElements;
            for (int i = 0; i < listElements.size(); i++) {
                listElements.get(i).setAdapter(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater =
                        (LayoutInflater) getActivity().getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem, null);
            }

            BaseListElement listElement = listElements.get(position);
            if (listElement != null) {
                view.setOnClickListener(listElement.getOnClickListener());
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                if (icon != null) {
                    icon.setImageDrawable(listElement.getIcon());
                }
                if (text1 != null) {
                    text1.setText(listElement.getText1());
                }
                if (text2 != null) {
                    if (listElement.getText2() != null) {
                        text2.setVisibility(View.VISIBLE);
                        text2.setText(listElement.getText2());
                    } else {
                        text2.setVisibility(View.GONE);
                    }
                }
            }
            return view;
        }
    }

}

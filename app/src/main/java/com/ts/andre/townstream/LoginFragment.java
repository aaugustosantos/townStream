package com.ts.andre.townstream;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends android.support.v4.app.Fragment {
    CallbackManager callbackManager;
    TextView info;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    ProfileTracker profileTracker;
    Profile userProfile;
    public final static String EXTRA_NAME = "com.ts.andre.townstream.NAME";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_login_fragment, container, false);
        info = (TextView)view.findViewById(R.id.info);

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker(){
            @Override
        protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken){
                accessToken = currentAccessToken;
            }

        };

        accessToken = AccessToken.getCurrentAccessToken();


        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                userProfile = currentProfile;

            }
        };

        userProfile = Profile.getCurrentProfile();

        if(accessToken!=null){
            sendUserInfo();
        }


        LoginButton loginButton = (LoginButton)view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends,email,user_birthday");


        loginButton.setFragment(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                sendUserInfo();

                }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled");

            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed");
            }
        });



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data){
      super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    public void sendUserInfo(){
        final Intent intent = new Intent(getActivity(), UserInfo.class);


        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        try {
                            intent.putExtra(EXTRA_NAME, jsonObject.getString("birthday"));
                            startActivity(intent);
                        }
                        catch (JSONException e) {

                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields","id,name,birthday");
        request.setParameters(parameters);
        request.executeAsync();


    }
}

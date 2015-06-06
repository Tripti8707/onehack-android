package com.arbrr.onehack.data.network;

import android.util.Log;

import com.arbrr.onehack.data.model.ModelObject;
import com.arbrr.onehack.data.model.User;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by boztalay on 6/4/15.
 */
public class NetworkManager {
    private static final String tag = "ONEHACK-NM";
    private static final String baseUrl = "http://onehack-mhacks.herokuapp.com/v1";

    private OneHackNetworkService networkSerivce;
    private String apiToken;

    public NetworkManager() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(baseUrl)
                .build();

        this.networkSerivce = restAdapter.create(OneHackNetworkService.class);
    }

    public void logUserIn(String email, String password, final OneHackCallback<User> callback) {
        // TODO get the apns and/or gcm token
        networkSerivce.logUserIn(new LoginParams(email, password, "apns_token"), new Callback<ModelObject>() {
            @Override
            public void success(ModelObject token, Response response) {
                // Now that we have the token and the user id, go get the user
                apiToken = token.token;

                networkSerivce.getCurrentUser(apiToken, new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        callback.success(user);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.d(tag, "Couldn't get the current user when logging in");
                        callback.failure(retrofitError);
                    }
                });
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(tag, "Couldn't create the session when logging in");
                callback.failure(retrofitError);
            }
        });
    }
}

package com.arbrr.onehack.data.network;

import android.util.Log;

import com.arbrr.onehack.data.model.Announcement;
import com.arbrr.onehack.data.model.Hackathon;
import com.arbrr.onehack.data.model.ModelObject;
import com.arbrr.onehack.data.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by boztalay on 6/4/15.
 */
public class NetworkManager {
    private static final String tag = "ONEHACK-NM";
    private static final String baseUrl = "http://onehack-mhacks.herokuapp.com/v1";

    private static final int DEFAULT_HACKATHON_ID = 1;
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private OneHackNetworkService networkSerivce;
    private String apiToken;
    private int currentHackathonId;

    private static NetworkManager instance;

    public NetworkManager getInstance() {
        if(instance == null) {
            instance = new NetworkManager();
        }

        return instance;
    }

    public NetworkManager() {
        currentHackathonId = getHackathonId();

        Gson gson = new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(baseUrl)
                .setConverter(new GsonConverter(gson))
                .build();

        this.networkSerivce = restAdapter.create(OneHackNetworkService.class);
    }

    public void logUserIn(String email, String password, final OneHackCallback<User> callback) {
        networkSerivce.logUserIn(new LoginParams(email, password, getGcmToken()), new Callback<ModelObject>() {
            @Override
            public void success(ModelObject token, Response response) {
                Log.d(tag, "Successfully logged in");
                apiToken = token.token;

                // Now that we have the token, go get the user
                networkSerivce.getCurrentUser(apiToken, new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        Log.d(tag, "Successfully got the current user");
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

    public void signUserUp(String email, String password, String firstName, String lastName, String company, final OneHackCallback<User> callback) {
        User user = new User();
        user.gcm_token = getGcmToken();
        user.email = email;
        user.password = password;
        user.firstName = firstName;
        user.lastName = lastName;
        user.company = company;

        networkSerivce.signUserUp(user, new Callback<ModelObject>() {
            @Override
            public void success(ModelObject token, Response response) {
                apiToken = token.token;

                // Now that we have the token, go get the user
                networkSerivce.getCurrentUser(apiToken, new Callback<User>() {
                    @Override
                    public void success(User user, Response response) {
                        Log.d(tag, "Successfully signed the user up");
                        callback.success(user);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.d(tag, "Couldn't get the current user after signing up");
                        callback.failure(retrofitError);
                    }
                });
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(tag, "Couldn't create the new user");
                callback.failure(retrofitError);
            }
        });
    }

    public void logUserOut(final OneHackCallback<Void> callback) {
        networkSerivce.logUserOut(new Callback<GenericResponse>() {
            @Override
            public void success(GenericResponse genericResponse, Response response) {
                Log.d(tag, "Successfully logged the user out");
                callback.success(null);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(tag, "Couldn't log the user out");
                callback.failure(retrofitError);
            }
        });
    }

    public void getHackathons(final OneHackCallback<List<Hackathon>> callback) {
        networkSerivce.getHackathons(apiToken, new Callback<List<Hackathon>>() {
            @Override
            public void success(List<Hackathon> hackathons, Response response) {
                Log.d(tag, "Successfully got " + hackathons.size() + " hackathons");
                callback.success(hackathons);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(tag, "Couldn't get hackathons");
                callback.failure(retrofitError);
            }
        });
    }

    public void getHackathon(int hackathonId, final OneHackCallback<Hackathon> callback) {
        networkSerivce.getHackathon(apiToken, hackathonId, new Callback<Hackathon>() {
            @Override
            public void success(Hackathon hackathon, Response response) {
                Log.d(tag, "Successfully got the hackathon " + hackathon.name);
                callback.success(hackathon);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(tag, "Couldn't get a hackathon");
                callback.failure(retrofitError);
            }
        });
    }

    public void createHackathon(Hackathon hackathon, final OneHackCallback<Hackathon> callback) {
        networkSerivce.createHackathon(apiToken, hackathon, new Callback<Hackathon>() {
            @Override
            public void success(Hackathon hackathon, Response response) {
                Log.d(tag, "Successfully created the hackathon " + hackathon.name);
                callback.success(hackathon);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(tag, "Couldn't create the hackathon");
                callback.failure(retrofitError);
            }
        });
    }

    public void getAnnouncements(final OneHackCallback<List<Announcement>> callback) {
        networkSerivce.getAnnouncements(currentHackathonId, new Callback<List<Announcement>>() {
            @Override
            public void success(List<Announcement> announcements, Response response) {
                Log.d(tag, "Successfully got " + announcements.size() + " announcements");
                callback.success(announcements);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(tag, "Couldn't get announcements");
                callback.failure(retrofitError);
            }
        });
    }

    //----- Helpers

    public void setCurrentHackathonId(int hackathonId) {
        currentHackathonId = hackathonId;
    }

    // TODO all dat GCM stuff
    private String getGcmToken() {
        return "";
    }

    private int getHackathonId() {
        return DEFAULT_HACKATHON_ID;
    }
}

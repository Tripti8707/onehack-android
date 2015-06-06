package com.arbrr.onehack.ui.announcements;

import android.app.Fragment;
import android.graphics.AvoidXfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.ModelObject;
import com.arbrr.onehack.data.model.User;
import com.arbrr.onehack.data.network.LoginParams;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AnnouncementsFragment extends Fragment {
    private static final String tag = "ONEHACK-AF";

    public AnnouncementsFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcements, container, false);

        // Instantiate any views in this layout here.

        NetworkManager networkManager = new NetworkManager();
        networkManager.logUserIn("tom_erdmann@mac.com", "test", new OneHackCallback<User>() {
            @Override
            public void success(User response) {
                Log.d(tag, "Logged in!");
            }

            @Override
            public void failure(Throwable error) {
                Log.d(tag, "Couldn't log in :(");
            }
        });

        return view;
    }
}

package com.arbrr.onehack.ui.announcements;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.*;
import com.arbrr.onehack.data.network.GenericResponse;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;

import java.util.Date;
import java.util.List;

public class AnnouncementsFragment extends Fragment {
    private static final String tag = "ONEHACK-AF";

    private NetworkManager networkManager;

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

        networkManager = new NetworkManager();
        networkManager.logUserIn("admin@admin.com", "admin", new OneHackCallback<User>() {
            @Override
            public void success(User response) {
                Log.d(tag, "Logged in!");
                getOtherData();
            }

            @Override
            public void failure(Throwable error) {
                Log.d(tag, "Couldn't log in :(");
            }
        });

        return view;
    }

    private void getOtherData() {
        HackerRole hackerRole = new HackerRole();
        hackerRole.id = 5;
        hackerRole.hackathon_id = 1;
        hackerRole.user_id = 7;
        hackerRole.role = 1;

        networkManager.updateHackerRole(hackerRole, new OneHackCallback<HackerRole>() {
            @Override
            public void success(HackerRole response) {

            }

            @Override
            public void failure(Throwable error) {

            }
        });
    }
}

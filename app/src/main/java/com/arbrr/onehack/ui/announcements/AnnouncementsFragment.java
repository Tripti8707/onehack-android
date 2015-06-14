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

/**
 * Created by Omkar Moghe on 5/27/15
 */
public class AnnouncementsFragment extends Fragment {

    public static final String TITLE = "Announcements";

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

        networkManager = NetworkManager.getInstance();
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
        networkManager.getAnnouncements(new OneHackCallback<List<Announcement>>() {
            @Override
            public void success(List<Announcement> announcements) {
                Log.d(tag, "Got " + announcements.size() + " announcements");
            }

            @Override
            public void failure(Throwable error) {
                Log.d(tag, ":(");
            }
        });
    }
}

package com.arbrr.onehack.ui.events;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.Event;
import com.arbrr.onehack.data.model.User;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omkar Moghe on 5/27/15
 */
public class EventsFragment extends Fragment {

    public static final String TITLE = "Events";
    public static final String TAG   = "EventsFragment";

    private NetworkManager networkManager;

    // ViewPager
    ViewPager mEventsViewPager;

    private ArrayList<ArrayList<Integer>> indexMap;

    public EventsFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        // ViewPager
        mEventsViewPager = (ViewPager) view.findViewById(R.id.events_pager);

        // Log in
        networkManager = NetworkManager.getInstance();
        networkManager.logUserIn("admin@admin.com", "admin", new OneHackCallback<User>() {
            @Override
            public void success(User response) {
                Log.d(TAG, "successfully logged in...");
                getEvents();
            }

            @Override
            public void failure(Throwable error) {

            }
        });

        return view;
    }

    public void getEvents() {
        networkManager.getEvents(new OneHackCallback<List<Event>>() {
            @Override
            public void success(List<Event> response) {
                updateEventsManager(response);
            }

            @Override
            public void failure(Throwable error) {

            }
        });
    }

    private void updateEventsManager (List<Event> update) {
        EventsManager.setEvents(update);
        indexMap = EventsManager.buildIndexes();
    }
}

package com.arbrr.onehack.ui.events;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
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

    // ViewPager stuff
    ViewPager mEventsViewPager;
    DayPagerAdapter mDayPagerAdapter;
    PagerTabStrip mDayPagerTabStrip;

    private ArrayList<ArrayList<Integer>> indexMap;

    // progress bar
    private ProgressDialog mProgressDialog;

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

        showProgressDialog("Loading", "Magical gnomes are fetching the latest schedule...");

        // Log in
        networkManager = NetworkManager.getInstance();
        networkManager.logUserIn("tom_erdmann@mac.com", "test", new OneHackCallback<User>() {
            @Override
            public void success(User response) {
                Log.d(TAG, "successfully logged in...");
                getEvents();
            }

            @Override
            public void failure(Throwable error) {

            }
        });

        // ViewPager
        mEventsViewPager = (ViewPager) view.findViewById(R.id.events_pager);
        mDayPagerAdapter = new DayPagerAdapter(getFragmentManager());
        mEventsViewPager.setAdapter(mDayPagerAdapter);
        mDayPagerTabStrip = (PagerTabStrip) view.findViewById(R.id.day_pager_tab_strip);

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
        mDayPagerAdapter.notifyDataSetChanged();
        mProgressDialog.dismiss();
    }

    private void showProgressDialog (String title, String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }
}

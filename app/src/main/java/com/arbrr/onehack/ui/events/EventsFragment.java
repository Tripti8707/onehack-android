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
import com.arbrr.onehack.data.model.Location;
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

        // Get data from the OneHack backend
        getLocations();
        getEvents();

        // Dismiss the dialog
        mProgressDialog.dismiss();

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
                EventsManager.setEvents(response);
                updateIndexesAndViews();
            }

            @Override
            public void failure(Throwable error) {

            }
        });
    }

    public void getLocations() {
        networkManager.getLocations(new OneHackCallback<List<Location>>() {
            @Override
            public void success(List<Location> response) {
                LocationsManager.setLocations(response);
            }

            @Override
            public void failure(Throwable error) {

            }
        });
    }

    private void updateIndexesAndViews() {
        indexMap = EventsManager.buildIndexes();
        mDayPagerAdapter.notifyDataSetChanged();
        mEventsViewPager.setAdapter(mDayPagerAdapter);
    }

    private void showProgressDialog (String title, String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }
}

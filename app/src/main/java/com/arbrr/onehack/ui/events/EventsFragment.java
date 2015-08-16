package com.arbrr.onehack.ui.events;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.Event;
import com.arbrr.onehack.data.model.Location;
import com.arbrr.onehack.data.network.GenericResponse;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;
import com.arbrr.onehack.ui.MainActivity;
import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omkar Moghe on 5/27/15
 */
public class EventsFragment extends Fragment implements DayFragment.EventActionListener,
                                                        EditEventFragment.OnEventUpdatedListener {

    public static final String TITLE = "Events";
    public static final String TAG   = "EventsFragment";

    private NetworkManager networkManager;

    // main frame layout

    // ViewPager stuff
    ViewPager mEventsViewPager;
    DayPagerAdapter mDayPagerAdapter;
    PagerTabStrip mDayPagerTabStrip;

    private ArrayList<ArrayList<Integer>> indexMap;

    // progress bar
    private ProgressDialog mProgressDialog;
    private ProgressBar mProgressBar;

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

        // Set title
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Events");
        }

        mProgressBar = (ProgressBar) view.findViewById(R.id.loading_spinner);
        mProgressBar.setVisibility(View.VISIBLE);

        // Log in
        networkManager = NetworkManager.getInstance();

        // Get data from the OneHack backend
        getLocations();
        getEvents();

        // Dismiss the dialog
        mProgressBar.setVisibility(View.GONE);

        // ViewPager
        mEventsViewPager = (ViewPager) view.findViewById(R.id.events_pager);
        mDayPagerAdapter = new DayPagerAdapter(getFragmentManager());
        mEventsViewPager.setAdapter(mDayPagerAdapter);
        mDayPagerTabStrip = (PagerTabStrip) view.findViewById(R.id.day_pager_tab_strip);

        // FAB
        AddFloatingActionButton newEventFAB = (AddFloatingActionButton) view.findViewById(R.id.new_event_fab);
        newEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEvent();
            }
        });

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

    private void newEvent () {
        EditEventFragment fragment = EditEventFragment.newInstance();
        fragment.setOnEventUpdatedListener(this);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);
        fragmentTransaction.addToBackStack("New Event"); // for back button navigation
        fragmentTransaction.commit();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////// INTERFACES, OVERRIDES, ETC. ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void editEvent (Event event) {
        EditEventFragment fragment = EditEventFragment.newInstance(event);
        fragment.setOnEventUpdatedListener(this);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);
        fragmentTransaction.addToBackStack("Edit Event"); // for back button navigation
        fragmentTransaction.commit();
    }

    @Override
    public void deleteEvent (final Event event) {
        showProgressDialog("Deleting " + event.getName(), "Say your goodbyes.");

        Log.d(TAG, "delete " + event.getName());

        networkManager.deleteEvent(event, new OneHackCallback<GenericResponse>() {
            @Override
            public void success(GenericResponse response) {
                Toast.makeText(getActivity(), event.getName() + " deleted.", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }

            @Override
            public void failure(Throwable error) {
                Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void OnEventUpdated(Event event) {
        getLocations();
        getEvents();
    }
}

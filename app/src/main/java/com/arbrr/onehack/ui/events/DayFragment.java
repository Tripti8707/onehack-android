package com.arbrr.onehack.ui.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.Event;

import java.util.ArrayList;

/**
 * Created by Omkar Moghe on 6/17/2015.
 */
public class DayFragment extends Fragment {

    public static final String TAG = "DayFragment";

    private RecyclerView eventsRecyclerView;

    private int mStart, mEnd;
    private ArrayList<Event> mEvents;

    public DayFragment() {
    }

    public static DayFragment newInstance(int start, int end) {
        DayFragment f = new DayFragment();

        Bundle args = new Bundle();
        args.putInt("start", start);
        args.putInt("end", end);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        setIndexes();
        mEvents = EventsManager.getEvents(mStart, mEnd);

        // Recycler view
        eventsRecyclerView = (RecyclerView) view.findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        EventsRecyclerAdapter eventsAdapter = new EventsRecyclerAdapter(mEvents);
        eventsRecyclerView.setAdapter(eventsAdapter);

        return view;
    }

    private void setIndexes() {
        Bundle args = getArguments();
        mStart = args.getInt("start");
        mEnd = args.getInt("end");
    }
}

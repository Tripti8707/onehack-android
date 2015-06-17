package com.arbrr.onehack.ui.events;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arbrr.onehack.data.model.Event;

import java.util.ArrayList;

/**
 * Created by Omkar Moghe on 6/17/2015.
 */
public class DayFragment extends Fragment {

    public static final String TAG = "DayFragment";

    public DayFragment() {

    }

    public static DayFragment newInstance(ArrayList<Event> events) {
        DayFragment f = new DayFragment();

        Bundle args = new Bundle();
        // TODO: put ArrayList of events into the bundle...
        // TODO: could use integer indexes of "master" sorted event List...
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
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

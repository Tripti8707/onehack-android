package com.arbrr.onehack.ui.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Omkar Moghe on 6/17/2015.
 */
public class DayFragment extends Fragment {

    public static final String TAG = "DayFragment";

    private int mStart, mEnd;

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
        setIndexes();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void setIndexes() {
        Bundle args = getArguments();
        mStart = args.getInt("start");
        mEnd = args.getInt("end");
    }
}

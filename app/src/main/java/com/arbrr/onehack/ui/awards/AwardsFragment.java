package com.arbrr.onehack.ui.awards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arbrr.onehack.R;

/**
 * Created by Omkar Moghe on 6/14/2015.
 */
public class AwardsFragment extends Fragment {

    public static final String TITLE = "Awards";

    public AwardsFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_awards, container, false);

        // Instantiate any views in this layout here.

        return view;
    }
}

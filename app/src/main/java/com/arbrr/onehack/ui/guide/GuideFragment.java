package com.arbrr.onehack.ui.guide;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arbrr.onehack.R;

/**
 * Created by Omkar Moghe on 5/27/15
 */
public class GuideFragment extends Fragment {

    public GuideFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);

        // Instantiate any views in this layout here.

        return view;
    }
}

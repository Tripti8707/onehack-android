package com.arbrr.onehack.ui.events;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Omkar Moghe on 7/12/2015.
 */
public class DayPagerAdapter extends FragmentStatePagerAdapter {

    public DayPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if (EventsManager.buildIndexes() != null) {
            ArrayList<Integer> indexes = EventsManager.buildIndexes().get(i);
            return DayFragment.newInstance(indexes.get(0), indexes.get(1));
        } else return new DayFragment();
    }

    @Override
    public int getCount() {
        if (EventsManager.buildIndexes() != null) return EventsManager.buildIndexes().size();
        else return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Day " + (position + 1);
    }
}

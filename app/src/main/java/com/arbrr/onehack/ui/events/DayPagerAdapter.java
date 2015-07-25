package com.arbrr.onehack.ui.events;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.arbrr.onehack.data.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
        String pageTitle = "Day " + (position + 1);

        if (EventsManager.buildIndexes() != null) {
            ArrayList<ArrayList<Integer>> indexes = EventsManager.buildIndexes();
            if (position >= 0 && position < indexes.size()) {
                int eventPosition = indexes.get(position).get(0);
                Event e = EventsManager.getEvents().get(eventPosition);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(e.getStartTime());

                String month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
                int date = calendar.get(Calendar.DATE);

                pageTitle = month + " " + date;
            }
        }

        return pageTitle;
    }
}

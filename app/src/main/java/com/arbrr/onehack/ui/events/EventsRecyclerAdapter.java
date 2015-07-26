package com.arbrr.onehack.ui.events;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.Event;
import com.arbrr.onehack.data.model.Location;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Omkar Moghe on 7/24/2015.
 */
public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.EventsHolder> {

    private ArrayList<Event> mEvents;

    private DayFragment mListener;

    public EventsRecyclerAdapter (ArrayList<Event> events, DayFragment listener) {
        mEvents = events;
        mListener = listener;
    }

    @Override
    public EventsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventsHolder(v);
    }

    @Override
    public void onBindViewHolder(EventsHolder holder, int position) {
        Event e = mEvents.get(position);

        // Getting the time in a human-readable format
        // EXAMPLE: "HH:MM - HH:MM"
        String timeFrame = ""; // prevents null string for TextView
        Calendar start = Calendar.getInstance();
        start.setTime(e.getStartTime());
        Calendar end = Calendar.getInstance();
        end.setTime(e.getEndTime());
        String startMin = (start.get(Calendar.MINUTE) == 0) ? "00" : "" + start.get(Calendar.MINUTE);
        String endMin = (end.get(Calendar.MINUTE) == 0) ? "00" : "" + end.get(Calendar.MINUTE);
        timeFrame += start.get(Calendar.HOUR_OF_DAY);
        timeFrame += (":" + startMin);
        timeFrame += (" - " + end.get(Calendar.HOUR_OF_DAY));
        timeFrame += (":" + endMin);

        // Getting location from LocationsManager
        Location location = LocationsManager.getLocation(e.getLocation_id());

        holder.eventName.setText(e.getName());
        holder.eventTime.setText(timeFrame);
        holder.eventInfo.setText(e.getInfo());
        // If location is null, no matching Location was found for the ID provided in the Event
        if (location == null) holder.eventLocation.setText("Location to be determined.");
        else holder.eventLocation.setText(location.getName());

        // Add click listeners
        holder.eventItemContainer.setOnClickListener(mListener);
        holder.eventItemContainer.setOnLongClickListener(mListener);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public static class EventsHolder extends RecyclerView.ViewHolder {
        protected TextView eventName, eventTime, eventInfo, eventLocation;
        protected FrameLayout eventItemContainer;

        public EventsHolder(View itemView) {
            super(itemView);
            this.eventName = (TextView) itemView.findViewById(R.id.event_name);
            this.eventTime = (TextView) itemView.findViewById(R.id.event_time);
            this.eventInfo = (TextView) itemView.findViewById(R.id.event_info);
            this.eventLocation = (TextView) itemView.findViewById(R.id.event_location);
            this.eventItemContainer = (FrameLayout) itemView.findViewById(R.id.event_item_container);
        }
    }
}

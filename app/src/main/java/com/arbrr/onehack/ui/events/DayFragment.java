package com.arbrr.onehack.ui.events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.Event;
import com.arbrr.onehack.data.model.Location;

import java.util.ArrayList;

/**
 * Created by Omkar Moghe on 6/17/2015.
 */
public class DayFragment extends Fragment implements View.OnClickListener,
                                                     View.OnLongClickListener {

    public static final String TAG = "DayFragment";

    private RecyclerView eventsRecyclerView;

    private int mStart, mEnd;
    private ArrayList<Event> mEvents; // local copy of events within [mStart, mEnd)

    public DayFragment () {}

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
        EventsRecyclerAdapter eventsAdapter = new EventsRecyclerAdapter(mEvents, this);
        eventsRecyclerView.setAdapter(eventsAdapter);

        return view;
    }

    private void setIndexes() {
        Bundle args = getArguments();
        mStart = args.getInt("start");
        mEnd = args.getInt("end");
    }

    private void showEventActionOptions (final Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(event.getName())
               .setItems(R.array.event_action_options, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int position) {
                       switch (position) {
                           case 0:
                               Location loc = LocationsManager.getLocation(event.getLocation_id());
                               if (loc != null) {
                                   Uri uri = Uri.parse("geo:0,0?q=" + loc.getLatitude() + "," + loc.getLongitude() + "(" + event.getName() + ")");
                                   Intent intent = new Intent(Intent.ACTION_VIEW);
                                   intent.setData(uri);
                                   if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                       startActivity(intent);
                                   }
                               } else Toast.makeText(getActivity(), "No location data for this event.", Toast.LENGTH_SHORT).show();
                               break;
                           case 1:
                               EventsFragment f = (EventsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("current_main_fragment");
                               if (f != null ) f.editEvent(event);
                               break;
                           case 2:
                               AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                               builder2.setTitle(R.string.confirmation_dialog)
                                       .setMessage(R.string.confirmation_dialog_info)
                                      .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                          public void onClick(DialogInterface dialog, int id) {
                                              EventsFragment f = (EventsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("current_main_fragment");
                                              if (f != null ) f.deleteEvent(event);
                                          }
                                      })
                                      .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                          public void onClick(DialogInterface dialog, int id) {
                                          }
                                      });
                               builder2.create().show();
                               break;
                       }
                   }
               });
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        int position = eventsRecyclerView.getChildPosition(v);
        Log.d(TAG, "position: " + position);
    }

    @Override
    public boolean onLongClick(View v) {
        int position = eventsRecyclerView.getChildPosition(v);
        showEventActionOptions(mEvents.get(position));

        return true;
    }

    public interface EventActionListener {
        public boolean deleteEvent (Event event);
        public boolean editEvent (Event event);
    }
}

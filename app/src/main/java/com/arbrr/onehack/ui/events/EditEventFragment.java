package com.arbrr.onehack.ui.events;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.arbrr.onehack.R;
import com.arbrr.onehack.data.model.Event;
import com.arbrr.onehack.data.model.Location;
import com.arbrr.onehack.data.network.NetworkManager;
import com.arbrr.onehack.data.network.OneHackCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditEventFragment extends Fragment implements AdapterView.OnItemSelectedListener,
                                                           DatePickerDialog.OnDateSetListener,
                                                           TimePickerDialog.OnTimeSetListener,
                                                           View.OnClickListener {

    // Event fields
    private String name, info;
    private Date startTime, endTime;
    private int role, hackathon_id, location_id, id;

    // Views
    private EditText editName, editInfo;
    private Button editStartDate, editStartTime, editEndDate, editEndTime;
    private Spinner editLocation;

    // Locations
    private ArrayList<Location> locationList = new ArrayList<>(LocationsManager.getLocations());
    private ArrayList<String> locationNamesList;

    // other shit
    private boolean newEvent = false;
    private int datePicker = 0; // 0 for start, 1 for end
    private int timePicker = 0; // 0 for start, 1 for end

    // Calendars
    private Calendar startTimeC = Calendar.getInstance();
    private Calendar endTimeC = Calendar.getInstance();

    public static EditEventFragment newInstance(Event event) {
        EditEventFragment f = new EditEventFragment();

        Bundle args = new Bundle();
        args.putBoolean("new", false);
        args.putString("name", event.getName());
        args.putString("info", event.getInfo());
        args.putLong("startTime", event.getStartTime().getTime());
        args.putLong("endTime", event.getEndTime().getTime());
        args.putInt("role", event.getRole());
        args.putInt("hackathon_id", event.getHackathon_id());
        args.putInt("location_id", event.getLocation_id());
        args.putInt("id", event.getId());
        f.setArguments(args);

        return f;
    }

    public static EditEventFragment newInstance() {
        EditEventFragment f = new EditEventFragment();

        Bundle args = new Bundle();
        args.putBoolean("new", true);
        f.setArguments(args);

        return f;
    }

    public EditEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if (!args.getBoolean("new")) {
            this.name = args.getString("name");
            this.info = args.getString("info");
            this.startTime = new Date(args.getLong("startTime"));
            this.endTime = new Date(args.getLong("endTime"));
            this.role = args.getInt("role");
            this.hackathon_id = args.getInt("hackathon_id");
            this.location_id = args.getInt("location_id");
            this.id = args.getInt("id");
        } else this.newEvent = true;

        locationNamesList = new ArrayList<>();
        for (Location l : locationList) {
            locationNamesList.add(l.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit_event, container, false);

        // Views
        editName = (EditText) view.findViewById(R.id.edit_event_name);
        editInfo = (EditText) view.findViewById(R.id.edit_event_info);
        editStartDate = (Button) view.findViewById(R.id.edit_event_startDate);
        editStartTime = (Button) view.findViewById(R.id.edit_event_startTime);
        editEndDate = (Button) view.findViewById(R.id.edit_event_endDate);
        editEndTime = (Button) view.findViewById(R.id.edit_event_endTime);

        // Locations spinner
        editLocation = (Spinner) view.findViewById(R.id.edit_event_location);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, locationNamesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editLocation.setAdapter(adapter);
        editLocation.setOnItemSelectedListener(this);

        // Date/time buttons
        Calendar today = Calendar.getInstance();
        String date = today.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) + " " + today.get(Calendar.DATE) + ", " + today.get(Calendar.YEAR);
        String time = today.get(Calendar.HOUR) + ":00";
        editStartDate.setText(date);
        editEndDate.setText(date);
        editStartTime.setText(time);
        editEndTime.setText(time);

        editStartDate.setOnClickListener(this);
        editStartTime.setOnClickListener(this);
        editEndDate.setOnClickListener(this);
        editEndTime.setOnClickListener(this);

        // Edit texts
        if (!newEvent) {
            editName.setText(this.name);
            editInfo.setText(this.info);
        }

        return view;
    }

    private void saveEvent (boolean newEvent) {
        NetworkManager mNetworkManager = NetworkManager.getInstance();

        // Get info from views, etc.
        this.startTime = new Date(startTimeC.getTimeInMillis());
        this.endTime = new Date(endTimeC.getTimeInMillis());
        if (editName.getText().toString().length() > 0) this.name = editName.getText().toString();
        else editName.setError("Require field.");
        this.info = editInfo.getText().toString();

        // Build event
        Event e = new Event();
        e.setName(this.name);
        e.setInfo(this.info);
        e.setStartTime(this.startTime);
        e.setEndTime(this.endTime);
        e.setRole(this.role);
        e.setHackathon_id(this.hackathon_id);
        e.setLocation_id(this.location_id);
        e.setId(this.id);

        if (newEvent) {
            mNetworkManager.createEvent(e, new OneHackCallback<Event>() {
                @Override
                public void success(Event response) {
                    // TODO: refresh the day page adapter
                }

                @Override
                public void failure(Throwable error) {
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mNetworkManager.updateEvent(e, new OneHackCallback<Event>() {
                @Override
                public void success(Event response) {
                    // TODO: refresh the day page adapter
                }

                @Override
                public void failure(Throwable error) {
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.location_id = locationList.get(position).getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        switch (datePicker) {
            case 0:
                startTimeC.set(Calendar.YEAR, year);
                startTimeC.set(Calendar.MONTH, monthOfYear);
                startTimeC.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                break;
            case 1:
                endTimeC.set(Calendar.YEAR, year);
                endTimeC.set(Calendar.MONTH, monthOfYear);
                endTimeC.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        switch (timePicker) {
            case 0:
                startTimeC.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTimeC.set(Calendar.MINUTE, minute);
                break;
            case 1:
                endTimeC.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endTimeC.set(Calendar.MINUTE, minute);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_event_startDate:
                datePicker = 0;
                DatePickerFragment sd = new DatePickerFragment();
                sd.setListener(this);
                sd.show(getActivity().getSupportFragmentManager(), "start date picker");
                break;
            case R.id.edit_event_startTime:
                timePicker = 0;
                TimePickerFragment st = new TimePickerFragment();
                st.setListener(this);
                st.show(getActivity().getSupportFragmentManager(), "start time picker");
                break;
            case R.id.edit_event_endDate:
                datePicker = 1;
                DatePickerFragment ed = new DatePickerFragment();
                ed.setListener(this);
                ed.show(getActivity().getSupportFragmentManager(), "end date picker");
                break;
            case R.id.edit_event_endTime:
                timePicker = 1;
                TimePickerFragment et = new TimePickerFragment();
                et.setListener(this);
                et.show(getActivity().getSupportFragmentManager(), "end time picker");
                break;
        }
    }

    public static class DatePickerFragment extends DialogFragment {
        private EditEventFragment listener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }

        public void setListener (EditEventFragment e) {
            this.listener = e;
        }

    }

    public static class TimePickerFragment extends DialogFragment {
        private EditEventFragment listener;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), listener, hour, minute,
                                        DateFormat.is24HourFormat(getActivity()));
        }

        public void setListener (EditEventFragment e) {
            this.listener = e;
        }

    }
}

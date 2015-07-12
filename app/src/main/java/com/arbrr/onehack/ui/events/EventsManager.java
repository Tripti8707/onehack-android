package com.arbrr.onehack.ui.events;

import com.arbrr.onehack.data.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omkar Moghe on 7/12/2015.
 */
public class EventsManager {
    private static ArrayList<Event> events;

    public static ArrayList<Event> getEvents() {
        return events;
    }

    public static ArrayList<ArrayList<Integer>> buildIndexes() {

        if (!isNull() && events.size() > 0) {

            ArrayList<ArrayList<Integer>> indexMap = new ArrayList<>();

            int start = 0;
            int end = 0;
            int lastDate = events.get(0).getStartTime().getDate();

            for (Event e : events) {
                if (e.getStartTime().getDate() == lastDate) {
                    ++end;
                } else {
                    ArrayList<Integer> arr = new ArrayList<>();
                    arr.add(start);
                    arr.add(end);
                    indexMap.add(arr);

                    start = end;
                    lastDate = e.getStartTime().getDate();
                }
            }

            return indexMap;
        }

        return null;
    }

    public static ArrayList<Event> getEvents(int start) {
        if (!isNull()) return new ArrayList<Event>(events.subList(start, events.size()));
        else return new ArrayList<Event>();
    }

    public static ArrayList<Event> getEvents(int start, int end) {
        if (!isNull()) return new ArrayList<Event>(events.subList(start, end));
        else return new ArrayList<Event>();
    }

    public static void setEvents(ArrayList<Event> events) {
        EventsManager.events = events;
    }

    public static void setEvents(List<Event> events) {
        EventsManager.events = new ArrayList<Event>(events);
    }

    public static boolean isNull() {
        return (events == null);
    }

    public static int getNumEvents() {
        if (!isNull()) return events.size();
        else return -1;
    }
}

package com.arbrr.onehack.ui.events;


import com.arbrr.onehack.data.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Omkar Moghe on 7/25/2015.
 */
public class LocationsManager {

    public static ArrayList<Location> locations;

    public static ArrayList<Location> getLocations() {
        return locations;
    }

    public static Location getLocation (int id) {
        if (!isNull() && locations.size() > 0) {
            for (Location l : locations) {
                if (l.getId() == id) return l;
            }
        }

        return null;
    }

    public static void setLocations(ArrayList<Location> locations) {
        LocationsManager.locations = locations;
    }

    public static void setLocations(List<Location> locations) {
        LocationsManager.locations = new ArrayList<Location>(locations);
    }

    public static boolean isNull () {
        return locations == null;
    }

    public static int getNumLocations () {
        if (!isNull()) return locations.size();
        else return -1;
    }

}

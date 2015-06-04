package com.arbrr.onehack.data.model;

import java.util.Date;

/**
 * Created by boztalay on 6/3/15.
 */
public class Event extends ModelObject {
    public String name;
    public String info;
    public Date startTime;
    public Date endTime;
    public int role;
    public int hackathon_id;
    public int location_id;
}

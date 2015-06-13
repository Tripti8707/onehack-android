package com.arbrr.onehack.ui.announcements;

import android.graphics.Bitmap;

import com.arbrr.onehack.data.model.Announcement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nilay on 6/3/15.
 */
public class OldAnnouncement{
    private String title;
    private String message;
    private String date;
    private Bitmap img;

    public static List<Announcement> dudList = new ArrayList<Announcement>();

    public OldAnnouncement(String someTitle, String someMessage, String someDate){
        title = someTitle;
        message = someMessage;
        date = someDate;
    }

    String getTitle(){
        return title;
    }

    String getMessage(){
        return message;
    }

    String getDate(){
        return date;
    }
}

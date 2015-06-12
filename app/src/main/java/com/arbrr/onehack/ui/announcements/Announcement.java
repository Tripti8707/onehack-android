package com.arbrr.onehack.ui.announcements;

import android.graphics.Bitmap;

/**
 * Created by Nilay on 6/3/15.
 */
public class Announcement{
    private String title;
    private String message;
    private String date;
    private Bitmap img;

    public Announcement(String someTitle, String someMessage, String someDate){
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

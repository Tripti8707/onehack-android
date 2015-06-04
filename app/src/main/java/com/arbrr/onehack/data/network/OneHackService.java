package com.arbrr.onehack.data.network;

import com.arbrr.onehack.data.model.Announcement;
import com.arbrr.onehack.data.model.ApiToken;
import com.arbrr.onehack.data.model.Event;
import com.arbrr.onehack.data.model.Hackathon;
import com.arbrr.onehack.data.model.HackerRole;
import com.arbrr.onehack.data.model.Location;
import com.arbrr.onehack.data.model.User;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by boztalay on 6/3/15.
 */
public interface OneHackService {
    @GET("/users/me")
    void getCurrentUser(Callback<User> callback);

    @POST("/users/")
    void signUserUp(@Body User user, Callback<ApiToken> callback);

    @POST("/sessions")
    void logUserIn(@Body LoginParams loginParams, Callback<ApiToken> callback);

    @DELETE("/sessions")
    void logUserOut(Callback<GenericResponse> callback);

    @GET("/hackathons")
    void getHackathons(Callback<List<Hackathon>> callback);

    @GET("/hackathons/{hackathon_id}")
    void getHackathon(@Path("hackathon_id") int hackathon_id, Callback<Hackathon> callback);

    @POST("/hackathons")
    void createHackathon(@Body Hackathon hackathon, Callback<Hackathon> callback);

    @GET("/hackathons/{hackathon_id}/announcements")
    void getAnnouncements(@Path("hackathon_id") int hackathon_id, Callback<List<Announcement>> callback);

    @GET("/hackathons/{hackathon_id}/announcements/{announcement_id}")
    void getAnnouncement(@Path("hackathon_id") int hackathon_id, @Path("announcement_id") int announcement_id, Callback<Announcement> callback);

    @POST("/hackathons/{hackathon_id}/announcements")
    void createAnnouncement(@Path("hackathon_id") int hackathon_id, @Body Announcement announcement, Callback<Announcement> callback);

    @DELETE("/hackathons/{hackathon_id}/announcements/{announcement_id}")
    void deleteAnnouncement(@Path("hackathon_id") int hackathon_id, @Path("announcement_id") int announcement_id, Callback<GenericResponse> callback);

    @GET("/hackathons/{hackathon_id}/events")
    void getEvents(@Path("hackathon_id") int hackathon_id, Callback<List<Event>> callback);

    @GET("/hackathons/{hackathon_id}/events/{event_id}")
    void getEvent(@Path("hackathon_id") int hackathon_id, @Path("event_id") int event_id, Callback<Event> callback);

    @POST("/hackathons/{hackathon_id}/events")
    void createEvent(@Path("hackathon_id") int hackathon_id, @Body Event event, Callback<Event> callback);

    @PUT("/hackathons/{hackathon_id}/events")
    void updateEvent(@Path("hackathon_id") int hackathon_id, @Body Event event, Callback<Event> callback);

    @DELETE("/hackathons/{hackathon_id}/events/{event_id}")
    void deleteEvent(@Path("hackathon_id") int hackathon_id, @Path("event_id") int event_id, Callback<GenericResponse> callback);

    @GET("/hackathons/{hackathon_id}/locations")
    void getLocations(@Path("hackathon_id") int hackathon_id, Callback<List<Location>> callback);

    @POST("/hackathons/{hackathon_id}/locations")
    void createLocation(@Path("hackathon_id") int hackathon_id, @Body Location location, Callback<Location> callback);

    @GET("/hackathons/{hackathon_id}/contacts")
    void getContacts(@Path("hackathon_id") int hackathon_id, Callback<List<User>> callback);

    @POST("/hacker_roles")
    void createHackerRole(@Body HackerRole hackerRole, Callback<HackerRole> callback);

    @PUT("/hacker_roles/{hacker_role_id}")
    void updateHackerRole(@Path("hacker_role_id") int hacker_role_id, @Body HackerRole hackerRole, Callback<HackerRole> callback);
}

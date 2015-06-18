package com.arbrr.onehack.data.model;

/**
 * Created by boztalay on 6/3/15.
 */
public class HackerRole extends ModelObject {
    public int user_id;
    public int hackathon_id;
    public int role;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getHackathon_id() {
        return hackathon_id;
    }

    public void setHackathon_id(int hackathon_id) {
        this.hackathon_id = hackathon_id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}

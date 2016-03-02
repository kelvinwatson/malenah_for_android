package com.watsonlogic.malenah.malenah3;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private long key;
    private String userId;
    private String email;
    private String name;
    private ArrayList<RowItem> favorites;

    public User() {
        //default constructor
    }

    public User(long key, String userId, String email, String name, ArrayList<RowItem> favorites) {
        this.key = key;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.favorites = favorites;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<RowItem> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<RowItem> favorites) {
        this.favorites = favorites;
    }
}

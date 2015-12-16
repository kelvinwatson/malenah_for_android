package com.watsonlogic.malenah.malenah3;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private ArrayList<RowItem> favorites;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<RowItem> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<RowItem> favorites) {
        this.favorites = favorites;
    }
}

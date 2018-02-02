package com.quadrivium.devs.bonvoyage;

/**
 * Created by vivek on 08-01-2018.
 */

public class User {
    private int id;
    private String name, email;

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

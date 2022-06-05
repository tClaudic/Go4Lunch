package com.example.go4lunch.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class User implements Serializable {

    public String uid;
    public String name;
    @SuppressWarnings("WeakerAccess")
    public String email;
    public String urlPicture;
    @Exclude
    public boolean isAuthenticated;
    @Exclude
    public boolean isNew, isCreated;

    public User() {
    }

    public User(String uid, String name, String email,String urlPicture) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.urlPicture = urlPicture;
    }
}

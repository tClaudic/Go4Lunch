package com.example.go4lunch.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    public String uid;
    public String name;
    @SuppressWarnings("WeakerAccess")
    public String email;
    public String urlPicture;
    public String restaurantChoice;
    public String restaurantChoiceName;
    public List<String> likes;
    @Exclude
    public boolean isAuthenticated;
    @Exclude
    public boolean isNew, isCreated;

    public User() {
    }

    public User(String uid, String name, String email,String urlPicture,String restaurantChoice,String restaurantChoiceName,List<String>likes) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.urlPicture = urlPicture;
        this.restaurantChoice = restaurantChoice;
        this.restaurantChoiceName =restaurantChoiceName;
        this.likes = likes;
    }
}

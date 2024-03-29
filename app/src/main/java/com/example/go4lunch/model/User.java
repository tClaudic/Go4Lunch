package com.example.go4lunch.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getRestaurantChoice() {
        return restaurantChoice;
    }

    public void setRestaurantChoice(String restaurantChoice) {
        this.restaurantChoice = restaurantChoice;
    }

    public String getRestaurantChoiceName() {
        return restaurantChoiceName;
    }

    public void setRestaurantChoiceName(String restaurantChoiceName) {
        this.restaurantChoiceName = restaurantChoiceName;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    @Exclude
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
    @Exclude
    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    @Exclude
    public boolean isNew() {
        return isNew;
    }
    @Exclude
    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    @Exclude
    public boolean isCreated() {
        return isCreated;
    }
    @Exclude
    public void setCreated(boolean created) {
        isCreated = created;
    }

    public User(String uid, String name, String email, String urlPicture, String restaurantChoice, String restaurantChoiceName, List<String> likes) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.urlPicture = urlPicture;
        this.restaurantChoice = restaurantChoice;
        this.restaurantChoiceName = restaurantChoiceName;
        this.likes = likes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isAuthenticated == user.isAuthenticated && isNew == user.isNew && isCreated == user.isCreated && Objects.equals(uid, user.uid) && Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(urlPicture, user.urlPicture) && Objects.equals(restaurantChoice, user.restaurantChoice) && Objects.equals(restaurantChoiceName, user.restaurantChoiceName) && Objects.equals(likes, user.likes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, name, email, urlPicture, restaurantChoice, restaurantChoiceName, likes, isAuthenticated, isNew, isCreated);
    }
}

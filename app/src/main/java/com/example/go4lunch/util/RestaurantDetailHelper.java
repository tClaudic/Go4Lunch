package com.example.go4lunch.util;

import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;

public class RestaurantDetailHelper {

    public static Boolean checkIfUserLikedThisRestaurant(User user, PlaceDetail placeDetail){
        return user.likes.contains(placeDetail.getResult().getPlaceId());
    }
}

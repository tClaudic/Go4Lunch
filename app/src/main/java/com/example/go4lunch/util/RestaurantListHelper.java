package com.example.go4lunch.util;

import android.location.Location;

import com.example.go4lunch.model.PlaceDetail.PlaceDetail;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

public class RestaurantListHelper {

    public static String sumDistanceBetweenTwoLocation(Location userLocation, PlaceDetail placeDetail) {
        Location restaurantLocation = new Location("");
        restaurantLocation.setLatitude(placeDetail.getResult().getGeometry().getLocation().getLat());
        restaurantLocation.setLongitude(placeDetail.getResult().getGeometry().getLocation().getLng());
        float distanceBetweenUserAndRestaurant = userLocation.distanceTo(restaurantLocation);
        return String.format("%sm", Math.round(distanceBetweenUserAndRestaurant));
    }


    private static int getGoodDayFormatForGetWeekdayText() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (day != 1) {
            return day - 2;
        } else return 6;

    }

    public static String formatCloseHour(PlaceDetail placeDetail) {
        String openStatus = "no data available";
        if (placeDetail.getResult().getOpeningHours() != null) {
            if (placeDetail.getResult().getOpeningHours().getOpenNow()) {
                if (differenceBetweenActualHourAndClosingRestaurantTime(placeDetail.getResult().getOpeningHours().getWeekdayText().get(getGoodDayFormatForGetWeekdayText()))) {
                    return "Closing soon";
                } else {
                    return getRestaurantClosingTime(placeDetail.getResult().getOpeningHours().getWeekdayText().get(getGoodDayFormatForGetWeekdayText()));
                }

            } else {
                return "Closed";
            }
        }
        return openStatus;
    }

    private static String getRestaurantClosingTime(String rawWeekDayText) {
        String weekDayText = StringUtils.substringAfterLast(rawWeekDayText, "–");
        return "open until" + weekDayText;
    }

    public static Boolean differenceBetweenActualHourAndClosingRestaurantTime(String rawWeekDayText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        String weekDayText = StringUtils.substringAfterLast(rawWeekDayText, "–");
        weekDayText = StringUtils.substringBefore(weekDayText, ":").trim();
        int restaurantClosedHour = Integer.parseInt(weekDayText);
        int result = restaurantClosedHour - hour;
        if (result <= 1) {
            return true;
        }
        return false;

    }

    public static float divideRatingResultBy3(PlaceDetail placeDetail){
        Double restaurantRating = placeDetail.getResult().getRating();
        if (restaurantRating != null){
            return (float) (restaurantRating/5 * 3);
        }
        else return 0;
    }



}
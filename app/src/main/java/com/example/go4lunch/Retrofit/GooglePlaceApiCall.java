package com.example.go4lunch.Retrofit;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.model.NearbySearch.NearbySearch;
import com.example.go4lunch.model.PlaceAutocomplete.PlaceAutocomplete;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlaceApiCall {

    String key = BuildConfig.MAPS_API_KEY;

    @GET("nearbysearch/json?key="+key)
    Observable<NearbySearch> getRestaurants(@Query("location") String location,
                                            @Query("radius") Integer radius,
                                            @Query("type") String type);
    @GET("details/json?key="+key)
    Observable<PlaceDetail> getPlaceDetails(@Query("place_id")String placeId);

    @GET("autocomplete/json?types=establishment&strictbounds&key="+key)
    Observable<PlaceAutocomplete> getPlaceAutoComplete(@Query("input")String input,
                                                       @Query("location")String location,
                                                       @Query("radius")Integer radius);
}

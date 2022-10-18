package com.example.go4lunch.Retrofit;

import com.example.go4lunch.model.NearbySearch.NearbySearch;
import com.example.go4lunch.model.NearbySearch.Result;
import com.example.go4lunch.model.PlaceAutocomplete.PlaceAutocomplete;
import com.example.go4lunch.model.PlaceAutocomplete.Prediction;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Go4LunchStreams {

    private final GooglePlaceApiCall googlePlaceApiCall;

    public Go4LunchStreams(GooglePlaceApiCall googlePlaceApiCall) {
        this.googlePlaceApiCall = googlePlaceApiCall;
    }


    public Observable<NearbySearch> streamFetchRestaurants(String location, int radius, String type) {
        return googlePlaceApiCall.getRestaurants(location, radius, type).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10, TimeUnit.SECONDS);
    }

    public Observable<PlaceDetail> streamFetchPlaceDetail(String placeId) {
        return googlePlaceApiCall.getPlaceDetails(placeId).
                observeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10, TimeUnit.SECONDS);
    }

    public Observable<PlaceAutocomplete> streamFetchAutocomplete(String input, String location, int radius) {
        return googlePlaceApiCall.getPlaceAutoComplete(input, location, radius).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10, TimeUnit.SECONDS);
    }

    public  Single<List<PlaceDetail>> streamFetchRestaurantsDetails(String location, int radius, String type) {
        return streamFetchRestaurants(location, radius, type)
                .flatMapIterable((Function<NearbySearch, List<Result>>) NearbySearch::getResults)
                .flatMap((Function<Result, Observable<PlaceDetail>>) result -> streamFetchPlaceDetail(result.getPlaceId()))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers
                        .mainThread());
    }

    public  Single<List<PlaceDetail>> streamFetchAutoCompleteRestaurantDetails(String input, String location, int radius) {
        return streamFetchAutocomplete(input, location, radius)
                .flatMapIterable((Function<PlaceAutocomplete, List<Prediction>>) PlaceAutocomplete::getPredictions)
                .flatMap((Function<Prediction, ObservableSource<PlaceDetail>>) prediction -> streamFetchPlaceDetail(prediction.getPlaceId()))
                .filter(placeDetail -> placeDetail.getResult().getTypes().contains("restaurant"))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers
                        .mainThread());
    }
}

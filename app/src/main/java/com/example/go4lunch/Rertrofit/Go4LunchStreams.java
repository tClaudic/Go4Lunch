package com.example.go4lunch.Rertrofit;

import com.example.go4lunch.model.NearbySearch.NearbySearch;
import com.example.go4lunch.model.NearbySearch.Result;
import com.example.go4lunch.model.PlaceAutocomplete.PlaceAutocomplete;
import com.example.go4lunch.model.PlaceAutocomplete.Prediction;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Go4LunchStreams {

    public static Observable<NearbySearch> streamFetchRestaurants(String location, int radius, String type) {
        ApiCall apicall = RetrofitService.retrofit.create(ApiCall.class);
        return apicall.getRestaurants(location, radius, type).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetail> streamFetchDetails(String placeId) {
        ApiCall apicall = RetrofitService.retrofit.create(ApiCall.class);
        return apicall.getPlaceDetails(placeId).
                observeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceAutocomplete> streamFetchAutocomplete(String input, String location, int radius) {
        ApiCall apicall = RetrofitService.retrofit.create(ApiCall.class);
        return apicall.getPlaceAutoComplete(input, location, radius).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10, TimeUnit.SECONDS);
    }

    public static Single<List<PlaceDetail>> streamFetchRestaurantsDetails(String location, int radius, String type) {
        return streamFetchRestaurants(location, radius, type)
                .flatMapIterable((Function<NearbySearch, List<Result>>) NearbySearch::getResults)
                .flatMap((Function<Result, Observable<PlaceDetail>>) result -> streamFetchDetails(result.getPlaceId()))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers
                        .mainThread());
    }

    public static Single<List<PlaceDetail>> streamFetchAutoCompleteRestaurantDetails(String input, String location, int radius){
        return streamFetchAutocomplete(input, location, radius)
                .flatMapIterable((Function<PlaceAutocomplete, List<Prediction>>) PlaceAutocomplete::getPredictions)
                .flatMap((Function<Prediction, ObservableSource<PlaceDetail>>) prediction -> streamFetchDetails(prediction.getPlaceId()))
                .filter(placeDetail -> placeDetail.getResult().getTypes().contains("restaurant"))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers
                        .mainThread());
    }
}

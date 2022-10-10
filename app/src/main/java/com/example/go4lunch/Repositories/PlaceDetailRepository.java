package com.example.go4lunch.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Retrofit.Go4LunchStreams;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class PlaceDetailRepository {

    private MutableLiveData<PlaceDetail> nearbySearchMutableLiveData;
    private final Go4LunchStreams go4LunchStreams;


    public PlaceDetailRepository(Go4LunchStreams go4LunchStreams) {
        this.go4LunchStreams = go4LunchStreams;
        this.nearbySearchMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<PlaceDetail> searchNearbyPlace(String placeID) {
        MutableLiveData<PlaceDetail> nearbySearchMutableLiveData = new MutableLiveData<>();
        go4LunchStreams.streamFetchDetails(placeID).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(@NonNull PlaceDetail placeDetail) {
                nearbySearchMutableLiveData.postValue(placeDetail);

            }

            @Override
            public void onError(@NonNull Throwable e) {
                nearbySearchMutableLiveData.postValue(null);

            }

            @Override
            public void onComplete() {

            }
        });
        return nearbySearchMutableLiveData;

    }

    public LiveData<PlaceDetail> getPlaceDetailResponse() {
        return nearbySearchMutableLiveData;
    }


    public MutableLiveData<List<PlaceDetail>> getNearbyRestaurantsLiveData(String location, int Radius, String type) {
        MutableLiveData<List<PlaceDetail>> nearbyRestaurantLiveData = new MutableLiveData<>();
        go4LunchStreams.streamFetchRestaurantsDetails(location, Radius, type).subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {
            @Override
            public void onSuccess(@NonNull List<PlaceDetail> placeDetails) {
                nearbyRestaurantLiveData.postValue(placeDetails);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                nearbyRestaurantLiveData.postValue(null);
            }
        });
        return nearbyRestaurantLiveData;
    }


    public MutableLiveData<List<PlaceDetail>> getNearbyRestaurantListWithAutoComplete(String query, String location, int Radius) {
        MutableLiveData<List<PlaceDetail>> nearbySearchAutocompleteLiveData = new MutableLiveData<>();
        go4LunchStreams.streamFetchAutoCompleteRestaurantDetails(query, location, Radius).subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {
            @Override
            public void onSuccess(@NonNull List<PlaceDetail> placeDetails) {
                nearbySearchAutocompleteLiveData.postValue(placeDetails);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                nearbySearchAutocompleteLiveData.postValue(null);
            }
        });
        return nearbySearchAutocompleteLiveData;
    }


}
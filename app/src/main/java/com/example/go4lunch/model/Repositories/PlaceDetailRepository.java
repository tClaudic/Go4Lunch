package com.example.go4lunch.model.Repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Rertrofit.ApiCall;
import com.example.go4lunch.Rertrofit.Go4LunchStreams;
import com.example.go4lunch.model.NearbySearch.NearbySearch;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class PlaceDetailRepository {

    private MutableLiveData<PlaceDetail> nearbySearchMutableLiveData;
    private MutableLiveData<List<PlaceDetail>> nearbyRestaurantsLiveData;

    public PlaceDetailRepository() {
        this.nearbySearchMutableLiveData = new MutableLiveData<>();
        this.nearbyRestaurantsLiveData = new MutableLiveData<>();
    }

    public void searchNearbyPlace(String placeID) {
        Go4LunchStreams.streamFetchDetails(placeID).subscribeWith(new DisposableObserver<PlaceDetail>() {
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

    }

    public LiveData<PlaceDetail> getPlaceDetailResponse(){
        return nearbySearchMutableLiveData;
    }

    public void searchNearbyRestaurants(String location,int Radius,String type){
        Go4LunchStreams.streamFetchRestaurantsDetails(location,Radius,type).subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {
            @Override
            public void onSuccess(@NonNull List<PlaceDetail> placeDetails) {
                nearbyRestaurantsLiveData.postValue(placeDetails);
                Log.e("placeDetails","nearbyRestaurantsSuccess")
;            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("nearbyRestaurantError",e.getMessage());
            }
        });

    }

    public LiveData<List<PlaceDetail>> getNearbyRestaurantsResponse(){return  nearbyRestaurantsLiveData;}
}
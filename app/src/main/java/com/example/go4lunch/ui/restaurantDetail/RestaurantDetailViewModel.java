package com.example.go4lunch.ui.restaurantDetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.Repositories.PlaceDetailRepository;

public class RestaurantDetailViewModel extends AndroidViewModel {

    private PlaceDetailRepository placeDetailRepository;
    public LiveData<PlaceDetail> restaurantDetail;

    public RestaurantDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        placeDetailRepository = new PlaceDetailRepository();
        restaurantDetail = placeDetailRepository.getPlaceDetailResponse();
    }

    public void searchRestaurantDetail(String placeId){
        placeDetailRepository.searchNearbyPlace(placeId);
    }
}

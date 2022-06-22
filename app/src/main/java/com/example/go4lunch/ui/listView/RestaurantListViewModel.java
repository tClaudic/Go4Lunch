package com.example.go4lunch.ui.listView;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.Repositories.PlaceDetailRepository;

import java.util.List;

public class RestaurantListViewModel extends AndroidViewModel {

    public LiveData<List<PlaceDetail>> nearbyRestaurantsLiveData;
    PlaceDetailRepository placeDetailRepository;

    public RestaurantListViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        placeDetailRepository = new PlaceDetailRepository();
        nearbyRestaurantsLiveData = placeDetailRepository.getNearbyRestaurantsResponse();
    }

    public void searchNearbyRestaurants(String location,int Radius,String type){
        placeDetailRepository.searchNearbyRestaurants(location, Radius, type);
    }
}

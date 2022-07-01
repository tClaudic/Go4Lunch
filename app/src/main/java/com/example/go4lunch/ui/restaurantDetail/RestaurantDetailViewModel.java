package com.example.go4lunch.ui.restaurantDetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.Repositories.PlaceDetailRepository;
import com.example.go4lunch.model.Repositories.UserRepository;
import com.example.go4lunch.model.User;

public class RestaurantDetailViewModel extends AndroidViewModel {

    private PlaceDetailRepository placeDetailRepository;
    private UserRepository userRepository;
    public LiveData<PlaceDetail> restaurantDetail;
    public MutableLiveData<User> authenticatedUser;

    public RestaurantDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        placeDetailRepository = new PlaceDetailRepository();
        userRepository = new UserRepository();
        authenticatedUser = userRepository.getAuthenticatedUserMutableLiveData();
        restaurantDetail = placeDetailRepository.getPlaceDetailResponse();
        test();
    }

    public void test(){
        userRepository.getUsersListMutableLiveData();
    }

    public void searchRestaurantDetail(String placeId){
        placeDetailRepository.searchNearbyPlace(placeId);
    }

    public void getAuthenticatedUser(){
        userRepository.getAuthenticatedUserMutableLiveData();
    }

    public void addUserRestaurantLike(String userId,String restaurandId){
        userRepository.addLike(userId,restaurandId);
    }
}

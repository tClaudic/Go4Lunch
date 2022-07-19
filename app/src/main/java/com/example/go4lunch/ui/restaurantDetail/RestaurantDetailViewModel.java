package com.example.go4lunch.ui.restaurantDetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.Repositories.PlaceDetailRepository;
import com.example.go4lunch.Repositories.UserRepository;
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

    public void setPlaceId(String userId, String placeId){
        userRepository.addRestaurant(userId, placeId);
    }


    public void setRestaurantChoiceName(String userId,String restaurantName){
        userRepository.addRestaurantChoiceName(userId,restaurantName);
    }

    public void removePlaceId(String userId){
        userRepository.removeRestaurantChoice(userId);
    }

    public void getAuthenticatedUser(){
        userRepository.getAuthenticatedUserMutableLiveData();
    }

    public void addUserRestaurantLike(String userId,String restaurantId){
        userRepository.addLike(userId,restaurantId);
    }


    public void removeUserRestaurantLike(String userId, String restaurantId){
        userRepository.removeUserLike(userId,restaurantId);
    }
}

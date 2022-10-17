package com.example.go4lunch.ui.restaurantDetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.Repositories.PlaceDetailRepository;
import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;

import java.util.List;

public class RestaurantDetailViewModel extends ViewModel {

    private final PlaceDetailRepository placeDetailRepository;
    private final UserRepository userRepository;

    public LiveData<PlaceDetail> restaurantDetail;
    public MutableLiveData<User> authenticatedUser;

    public RestaurantDetailViewModel(PlaceDetailRepository placeDetailRepository, UserRepository userRepository) {
        this.placeDetailRepository = placeDetailRepository;
        this.userRepository = userRepository;
        authenticatedUser = userRepository.getAuthenticatedUserMutableLiveData();
    }


    public LiveData<List<User>> getUsersListFilteredByRestaurantChoice(String placeId) {
        MutableLiveData<List<User>> usersListMutableLiveData = new MutableLiveData<>();
        usersListMutableLiveData = userRepository.getUsersFilteredListFromFirebase(placeId);
        return usersListMutableLiveData;
    }

    public LiveData<User> getAuthenticatedLiveDataUser() {
        authenticatedUser = userRepository.getAuthenticatedUserMutableLiveData();
        return authenticatedUser;
    }

    public LiveData<PlaceDetail> getRestaurantDetailByUserChoice(String placeId) {
        restaurantDetail = placeDetailRepository.searchNearbyPlace(placeId);
        return restaurantDetail;
    }


    public MutableLiveData<String> setPlaceId(String userId, String placeId) {
        MutableLiveData<String> result = new MutableLiveData<>();
        result = userRepository.addUserRestaurantChoiceInFirebase(userId, placeId);
        return result;
    }

    public MutableLiveData<String> setRestaurantChoiceName(String userId, String restaurantName) {
        MutableLiveData<String> result = new MutableLiveData<>();
        result = userRepository.addUserRestaurantChoiceNameInFirebase(userId, restaurantName);
        return result;
    }

    public MutableLiveData<String> removePlaceId(String userId) {
        MutableLiveData<String> result = new MutableLiveData<>();
        result = userRepository.removeRestaurantChoice(userId);
        return result;
    }


    public MutableLiveData<String> addUserRestaurantLike(String userId, String restaurantId) {
        MutableLiveData<String> result = new MutableLiveData<>();
        result = userRepository.addUserLikeInFirebase(userId, restaurantId);
        return result;
    }


    public MutableLiveData<String> removeUserRestaurantLike(String userId, String restaurantId) {
        MutableLiveData<String> result = new MutableLiveData<>();
        result = userRepository.removeUserLikeInFirebase(userId, restaurantId);
        return result;
    }
}

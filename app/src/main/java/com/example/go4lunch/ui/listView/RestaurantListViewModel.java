package com.example.go4lunch.ui.listView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.Repositories.PlaceDetailRepository;
import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;

import java.util.List;

public class RestaurantListViewModel extends ViewModel {

    public LiveData<List<PlaceDetail>> nearbyRestaurantsLiveData;
    private final PlaceDetailRepository placeDetailRepository;
    UserRepository userRepository;
    public MutableLiveData<List<User>> usersListMutableLiveData;
    private final MutableLiveData<PlaceDetail> selected = new MutableLiveData<PlaceDetail>();

    public RestaurantListViewModel(PlaceDetailRepository placeDetailRepository, UserRepository userRepository) {
        this.placeDetailRepository = placeDetailRepository;
        this.userRepository = userRepository;
        getUsersLists();
    }

    public LiveData<List<PlaceDetail>> getAllRestaurants(String location, int Radius, String type) {
        nearbyRestaurantsLiveData = placeDetailRepository.getNearbyRestaurantsLiveData(location, Radius, type);
        return nearbyRestaurantsLiveData;
    }


    public LiveData<List<User>> getUsersLists() {
        usersListMutableLiveData = userRepository.getUsersListFromFirebase();
        return usersListMutableLiveData;
    }

    public LiveData<List<PlaceDetail>> getAutoCompleteNearbyRestaurantList(String query, String location, int radius) {
        return placeDetailRepository.getNearbyRestaurantListWithAutoComplete(query, location, radius);
    }


    public void select(PlaceDetail placeDetail) {
        selected.setValue(placeDetail);
    }

    public LiveData<PlaceDetail> getSelected() {
        return selected;
    }
}

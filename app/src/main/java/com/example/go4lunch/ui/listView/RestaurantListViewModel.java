package com.example.go4lunch.ui.listView;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.Repositories.PlaceDetailRepository;
import com.example.go4lunch.model.Repositories.UserRepository;
import com.example.go4lunch.model.User;

import java.util.List;

public class RestaurantListViewModel extends AndroidViewModel {

    public LiveData<List<PlaceDetail>> nearbyRestaurantsLiveData;
    PlaceDetailRepository placeDetailRepository;
    UserRepository userRepository;
    MutableLiveData<List<User>> usersListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<PlaceDetail> selected = new MutableLiveData<PlaceDetail>();
    LiveData<List<PlaceDetail>> autoCompleteNearbyRestaurantList = new MutableLiveData<>();

    public RestaurantListViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        placeDetailRepository = new PlaceDetailRepository();
        userRepository = new UserRepository();
        nearbyRestaurantsLiveData = placeDetailRepository.getNearbyRestaurantsResponse();
        autoCompleteNearbyRestaurantList = placeDetailRepository.getNearbySearchAutocompleteLiveData();
        getUsersList();
    }

    public void getUsersList(){
        usersListMutableLiveData = userRepository.getUsersListMutableLiveData();
    }

    public void searchNearbyRestaurants(String location,int Radius,String type){
        placeDetailRepository.searchNearbyRestaurants(location, Radius, type);
    }

    public void searchNearbyRestaurantWithAutocomplete(String query, String location, int radius){
        placeDetailRepository.searchNearbyRestaurantWithAutoComplete(query, location, radius);
    }

    public void select(PlaceDetail placeDetail){
        selected.setValue(placeDetail);
    }

    public LiveData<PlaceDetail> getSelected(){
        return selected;
    }
}

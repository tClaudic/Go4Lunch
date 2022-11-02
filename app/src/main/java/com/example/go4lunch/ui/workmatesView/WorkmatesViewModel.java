package com.example.go4lunch.ui.workmatesView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.Repositories.PlaceDetailRepository;
import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.model.PlaceDetail.PlaceDetail;
import com.example.go4lunch.model.User;

import java.util.List;

public class WorkmatesViewModel extends ViewModel {
    public MutableLiveData<List<User>> userListMutableLiveData;
    public final UserRepository userRepository;
    private final PlaceDetailRepository placeDetailRepository;


    public WorkmatesViewModel(UserRepository userRepository, PlaceDetailRepository placeDetailRepository) {
        this.userRepository = userRepository;
        userListMutableLiveData = userRepository.getUsersListFromFirebase();
        this.placeDetailRepository = placeDetailRepository;
    }

    public void getUsersListFromFirebase(){
        userListMutableLiveData = userRepository.getUsersListFromFirebase();
    }

    public LiveData<PlaceDetail> getPlaceDetailByUserChoice(String placeId){
        return placeDetailRepository.getPlaceDetailByPlaceId(placeId);
    }


}

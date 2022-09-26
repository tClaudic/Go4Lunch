package com.example.go4lunch.ui.workmatesView;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.Repositories.UserRepositoryTest;
import com.example.go4lunch.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class WorkmatesViewModel extends AndroidViewModel {
    public MutableLiveData<List<User>> userListMutableLiveData = new MutableLiveData<>();
    public UserRepository userRepository;
    public UserRepositoryTest userRepositoryTest;
    public LiveData<List<User>> filteredUsersListLiveData;
    public MutableLiveData<List<User>> filteredUsersList = new MutableLiveData<>();
    public WorkmatesViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        userRepositoryTest = new UserRepositoryTest(FirebaseFirestore.getInstance());
        userRepository = new UserRepository();
        //userListMutableLiveData = userRepository.getUsersListMutableLiveData();
        userListMutableLiveData = userRepositoryTest.getFirebaseUsersList();
    }



    public void getFilteredUsersByRestaurantChoiceName(String restaurantChoice){
        filteredUsersList = userRepository.getUsersFilteredListMutableLiveData(restaurantChoice);
    }


    public LiveData<List<User>> getFilteredUsersListLiveData() {
        return filteredUsersList;
    }
}

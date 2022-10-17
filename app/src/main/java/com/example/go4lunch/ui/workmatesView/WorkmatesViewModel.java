package com.example.go4lunch.ui.workmatesView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.model.User;

import java.util.List;

public class WorkmatesViewModel extends ViewModel {
    public MutableLiveData<List<User>> userListMutableLiveData = new MutableLiveData<>();
    public final UserRepository userRepository;
    public WorkmatesViewModel(UserRepository userRepository) {

        this.userRepository = userRepository;
        userListMutableLiveData = userRepository.getUsersListFromFirebase();
    }


}

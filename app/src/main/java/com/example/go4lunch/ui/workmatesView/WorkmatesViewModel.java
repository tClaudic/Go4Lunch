package com.example.go4lunch.ui.workmatesView;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Repositories.UserRepository;
import com.example.go4lunch.model.User;

import java.util.List;

public class WorkmatesViewModel extends AndroidViewModel {
    MutableLiveData<List<User>> userListMutableLiveData = new MutableLiveData<>();
    UserRepository userRepository;
    public WorkmatesViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        userRepository = new UserRepository();
        userListMutableLiveData = userRepository.getUsersListMutableLiveData();
    }

}

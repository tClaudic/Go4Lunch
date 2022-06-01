package com.example.go4lunch.ui.authentication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.go4lunch.model.User;
import com.google.firebase.auth.AuthCredential;

public class AuthViewModel extends AndroidViewModel {

    private AuthRepository authRepository;
    LiveData<User> authenticatedUserLiveData;
    LiveData<User> createdUserLiveData;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
    }

    void signInWithGoogle(AuthCredential authCredential){
        authenticatedUserLiveData = authRepository.firebaseSignInWithGoogle(authCredential);
    }

    void createUser(User authenticatedUser){
        createdUserLiveData = authRepository.createUserInFirestoreIfNoExist(authenticatedUser);
    }
}

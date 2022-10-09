package com.example.go4lunch.ui.authentication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.Repositories.AuthRepository;
import com.example.go4lunch.model.User;
import com.google.firebase.auth.AuthCredential;

public class AuthViewModel extends ViewModel {

    private final AuthRepository authRepository;
    public LiveData<User> userLiveData;


    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }


    public LiveData<String> signInWithMailAndPassword(String email, String password){
        return authRepository.firebaseSignInWithMailAndPassword(email, password);
    }

    public LiveData<String> signUpWithEmailAndPassword(String email,String password, String username){
        return authRepository.firebaseSignUpWithMailAndPassword(email, password, username);
    }

    public LiveData<String> signInWithAuthCredential(AuthCredential authCredential) {
        return authRepository.firebaseSignInWithAuthCredential(authCredential);
    }


    public LiveData<String> checkIfUserIsAuthenticated() {
        return authRepository.checkIfUserIsAuthenticatedInFirebase();
    }

    public LiveData<User> getUserById(String uid) {
        userLiveData = authRepository.getUserById(uid);
        return userLiveData;
    }
}

package com.example.go4lunch.ui.authentication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.go4lunch.model.User;
import com.google.firebase.auth.AuthCredential;

import java.nio.file.attribute.UserPrincipalLookupService;

public class AuthViewModel extends AndroidViewModel {

    private AuthRepository authRepository;
    LiveData<User> authenticatedUserLiveData;
    LiveData<User> createdUserLiveData;
    public LiveData<User> isUserAuthenticatedLiveData;
    public LiveData<User> userLiveData;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
    }

    void signInWithGoogle(AuthCredential authCredential){
        authenticatedUserLiveData = authRepository.firebaseSignInWithGoogle(authCredential);
    }

    void signInWithFacebook(AuthCredential authCredential){
        authenticatedUserLiveData = authRepository.firebaseSignInWithFacebook(authCredential);
    }

    void createUser(User authenticatedUser){
        createdUserLiveData = authRepository.createUserInFirestoreIfNoExist(authenticatedUser);
    }

    public void checkIfUserIsAuthenticated(){
        isUserAuthenticatedLiveData = authRepository.checkIfUserIsAuthenticatedInFirebase();
    }

    public void setUid(String uid){
        userLiveData = authRepository.addUserToLiveData(uid);
    }
}

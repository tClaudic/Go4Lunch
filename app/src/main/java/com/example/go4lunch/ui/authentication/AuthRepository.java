package com.example.go4lunch.ui.authentication;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AuthRepository {
    private static final String USERS = "users";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(USERS);
    private User user = new User();


    MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential authCredential) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    String urlPicture = firebaseUser.getPhotoUrl().toString();
                    List<String> likes = new ArrayList<>();
                    likes.add("fdffsfsfs");
                    User user = new User(uid, name, email,urlPicture,likes);
                    user.isNew = isNewUser;
                    authenticatedUserMutableLiveData.setValue(user);
                }
            } else {
                Log.e("authrepo", authTask.getException().getMessage());
            }
        });
        return authenticatedUserMutableLiveData;
    }

    MutableLiveData<User> createUserInFirestoreIfNoExist(User authenticatedUser) {
        MutableLiveData<User> newUserMutableLiveData = new MutableLiveData<>();
        DocumentReference uidRef = usersRef.document(authenticatedUser.uid);
        uidRef.get().addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                DocumentSnapshot documentSnapshot = uidTask.getResult();
                if (!documentSnapshot.exists()) {
                    uidRef.set(authenticatedUser).addOnCompleteListener(userCreationTask -> {
                        if (userCreationTask.isSuccessful()) {
                            authenticatedUser.isCreated = true;
                            newUserMutableLiveData.setValue(authenticatedUser);
                        } else {
                            Log.e("creationUserLog", userCreationTask.getException().getMessage());
                        }
                    });
                } else {
                    newUserMutableLiveData.setValue(authenticatedUser);
                }
            } else {
                Log.e("uidTaskError", uidTask.getException().getMessage());
            }
        });
        return newUserMutableLiveData;
    }

    MutableLiveData<User> checkIfUserIsAuthenticatedInFirebase(){
        MutableLiveData<User> isUserAuthenticateInFirebaseMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            user.isAuthenticated = false;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
        }else {
            user.uid = firebaseUser.getUid();
            user.isAuthenticated = true;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
        }
        return isUserAuthenticateInFirebaseMutableLiveData;
    }

    MutableLiveData<User> addUserToLiveData(String uid){
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        usersRef.document(uid).get().addOnCompleteListener(userTask ->{
            if (userTask.isSuccessful()){
                DocumentSnapshot documentSnapshot = userTask.getResult();
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    userMutableLiveData.setValue(user);
                }else {
                    //Log.e("AddUserToLideData", userTask.getException().getMessage());
                }
            }
        });
        return userMutableLiveData;
    }
}

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

public class AuthRepository {
    private static final String USERS = "users";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(USERS);


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
                    User user = new User(uid, name, email);
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
}

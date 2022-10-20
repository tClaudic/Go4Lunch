package com.example.go4lunch.Repositories;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;

    private static final String USERS = "users";
    private static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public AuthRepository(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseFirestore = firebaseFirestore;
    }


    public MutableLiveData<String> firebaseSignInWithMailAndPassword(String email, String password) {
        MutableLiveData<String> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Task<AuthResult> task) -> {
            if (task.isSuccessful()) {
                authenticatedUserMutableLiveData.setValue(SUCCESS);
            } else {
                authenticatedUserMutableLiveData.setValue(ERROR);
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<String> firebaseSignUpWithMailAndPassword(String email, String password, String username) {
        MutableLiveData<String> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser()) {
                    createUserInFirestoreIfNoExist(Objects.requireNonNull(task.getResult().getUser()), username);
                }
                authenticatedUserMutableLiveData.setValue(SUCCESS);
            } else {
                authenticatedUserMutableLiveData.setValue(ERROR);
            }
        });
        return authenticatedUserMutableLiveData;
    }


    public MutableLiveData<String> firebaseSignInWithAuthCredential(AuthCredential authCredential) {
        MutableLiveData<String> authenticationResult = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                createUserInFirestoreIfNoExist(Objects.requireNonNull(task.getResult().getUser()), null);
                authenticationResult.setValue(SUCCESS);
            } else {
                authenticationResult.setValue(ERROR);
            }
        });
        return authenticationResult;
    }


    public MutableLiveData<String> createUserInFirestoreIfNoExist(FirebaseUser firebaseUser, @Nullable String username) {
        MutableLiveData<String> userCreationResult = new MutableLiveData<>();
        firebaseFirestore.collection(USERS).document(firebaseUser.getUid()).get().addOnCompleteListener(taskResult -> {
            if (taskResult.isSuccessful()) {
                if (taskResult.getResult().exists()) {
                    userCreationResult.setValue(SUCCESS);
                } else {
                    String uid = firebaseUser.getUid();
                    String name;
                    if (username != null) {
                        name = username;
                    } else {
                        name = firebaseUser.getDisplayName();
                    }
                    String email = firebaseUser.getEmail();
                    String urlPicture;
                    if (firebaseUser.getPhotoUrl() == null) {
                        urlPicture = "https://firebasestorage.googleapis.com/v0/b/go4lunch-10136.appspot.com/o/istockphoto-1223671392-170667a.jpg?alt=media&token=6d5a3228-24ad-4aad-8391-441e020ec9e2";
                    } else {
                        urlPicture = Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString();
                    }
                    String restaurantChoice = "";
                    String restaurantChoiceName = "";
                    List<String> likes = new ArrayList<>();
                    likes.add("fdffsfsfs");
                    User user = new User(uid, name, email, urlPicture, restaurantChoice, restaurantChoiceName, likes);
                    firebaseFirestore.collection(USERS).document(firebaseUser.getUid()).set(user).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            userCreationResult.setValue(SUCCESS);
                        } else {
                            userCreationResult.setValue(ERROR);
                        }
                    });
                }
            } else {
                userCreationResult.setValue(ERROR);
            }
        });

        return userCreationResult;
    }


    public MutableLiveData<String> getAuthenticatedUserId() {
        MutableLiveData<String> isUserAuthenticateInFirebaseMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            isUserAuthenticateInFirebaseMutableLiveData.setValue(firebaseUser.getUid());
        } else {
            isUserAuthenticateInFirebaseMutableLiveData.setValue(ERROR);
        }
        return isUserAuthenticateInFirebaseMutableLiveData;
    }

    public MutableLiveData<User> getUserByIdFromFirebase(String uid) {
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        firebaseFirestore.collection(USERS).document(uid).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot documentSnapshot = userTask.getResult();
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    userMutableLiveData.setValue(user);
                } else {
                    userMutableLiveData.setValue(null);
                }
            } else {
                userMutableLiveData.setValue(null);
            }
        });
        return userMutableLiveData;
    }
}

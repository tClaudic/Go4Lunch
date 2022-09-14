package com.example.go4lunch.Repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AuthRepository {
    private static final String USERS = "users";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(USERS);
    private User user = new User();
    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> userLogged = new MutableLiveData<Boolean>();
    public MutableLiveData<User> authenticatedUser;




    public AuthRepository() {
        this.authenticatedUser = new MutableLiveData<>();
    }

    public LiveData<User> getAuthenticatedUserResponse(){
        return authenticatedUser;
    }

    public void signInWithEmail(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null){
                        usersRef.document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()){
                                        User user = documentSnapshot.toObject(User.class);
                                        user.isAuthenticated = true;
                                        authenticatedUser.postValue(user);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData(){
        return firebaseUserMutableLiveData;
    }

    public MutableLiveData<Boolean> getUserLogged(){
        return userLogged;
    }



    public MutableLiveData<User> firebaseSignInWithEmailAndPassword(String email, String password) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        usersRef.document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        User user = documentSnapshot.toObject(User.class);
                                        authenticatedUserMutableLiveData.setValue(user);

                                    }
                                }
                            }
                        });
                    }
                    userLogged.postValue(true);
                }
            }
        });
        return authenticatedUserMutableLiveData;
    }


    public MutableLiveData<User> firebaseSignUpWithEmailAndPassword(String email, String password, String username) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.e("sucessfull", "sucess");
                    boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
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
                        User user = new User(uid, username, email, urlPicture, restaurantChoice, restaurantChoiceName, likes);
                        user.isNew = isNewUser;
                        authenticatedUserMutableLiveData.setValue(user);
                        createUserInFirestoreIfNoExist(user);
                        Log.e("test", "login with email");
                    } else {
                        Log.e("emailAuth", task.getException().getMessage());
                    }
                    firebaseUserMutableLiveData.postValue(firebaseUser);
                }
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<User> firebaseSignInWithTwitter(AuthCredential authCredential) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.e("twitter", task.getResult().getUser().getDisplayName().toString());
                    boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                    Log.e("suerbool", String.valueOf(isNewUser));
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        String name = firebaseUser.getDisplayName();
                        String email = firebaseUser.getEmail();
                        String urlPicture = firebaseUser.getPhotoUrl().toString();
                        String restaurantChoice = "";
                        String restaurantChoiceName = "";
                        List<String> likes = new ArrayList<>();
                        likes.add("fdffsfsfs");
                        User user = new User(uid, name, email, urlPicture, restaurantChoice, restaurantChoiceName, likes);
                        user.isNew = isNewUser;
                        user.isAuthenticated = true;
                        authenticatedUserMutableLiveData.setValue(user);
                        createUserInFirestoreIfNoExist(user);

                    }
                    userLogged.postValue(true);
                } else {
                    Log.e("authrepo", task.getException().getMessage());
                    userLogged.postValue(false);
                }


            }
        });
        return authenticatedUserMutableLiveData;
    }


    public MutableLiveData<User> firebaseSignInWithFacebook(AuthCredential authCredential) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.e("fbtest", task.getResult().getUser().getDisplayName().toString());
                    boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                    Log.e("suerbool", String.valueOf(isNewUser));
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        Log.e("user != null", "!=null");
                        String uid = firebaseUser.getUid();
                        String name = firebaseUser.getDisplayName();
                        String email = firebaseUser.getEmail();
                        String urlPicture = firebaseUser.getPhotoUrl().toString();
                        String restaurantChoice = "";
                        String restaurantChoiceName = "";
                        List<String> likes = new ArrayList<>();
                        likes.add("fdffsfsfs");
                        User user = new User(uid, name, email, urlPicture, restaurantChoice, restaurantChoiceName, likes);
                        user.isNew = isNewUser;
                        authenticatedUserMutableLiveData.setValue(user);
                        createUserInFirestoreIfNoExist(user);

                    }
                    firebaseUserMutableLiveData.postValue(firebaseUser);
                } else {
                    Log.e("authrepo", task.getException().getMessage());
                }


            }
        });
        return authenticatedUserMutableLiveData;
    }


    public MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential authCredential) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                Log.e("GoogleSuccess","GoogleSuccess");
                boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    String urlPicture = firebaseUser.getPhotoUrl().toString();
                    String restaurantChoice = "";
                    String restaurantChoiceName = "";
                    List<String> likes = new ArrayList<>();
                    likes.add("fdffsfsfs");
                    User user = new User(uid, name, email, urlPicture, restaurantChoice, restaurantChoiceName, likes);
                    user.isNew = isNewUser;
                    authenticatedUserMutableLiveData.setValue(user);
                    createUserInFirestoreIfNoExist(user);
                }
                firebaseUserMutableLiveData.postValue(firebaseUser);
            } else {
                Log.e("authrepo", authTask.getException().getMessage());
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<User> createUserInFirestoreIfNoExist(User authenticatedUser) {
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

    public MutableLiveData<User> checkIfUserIsAuthenticatedInFirebase() {
        MutableLiveData<User> isUserAuthenticateInFirebaseMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            user.isAuthenticated = false;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
        } else {
            user.uid = firebaseUser.getUid();
            user.isAuthenticated = true;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
        }
        return isUserAuthenticateInFirebaseMutableLiveData;
    }

    public MutableLiveData<User> addUserToLiveData(String uid) {
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        usersRef.document(uid).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot documentSnapshot = userTask.getResult();
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    userMutableLiveData.setValue(user);
                } else {
                    //Log.e("AddUserToLideData", userTask.getException().getMessage());
                }
            }
        });
        return userMutableLiveData;
    }
}

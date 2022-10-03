package com.example.go4lunch.Repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryTest {

    @NonNull
    private final FirebaseFirestore firebaseFirestore;

    public UserRepositoryTest(@NonNull FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }


    public void getUsers() {
        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

            }
        });
    }

    public List<User> transformFirestoreData(@NonNull Task<QuerySnapshot> task) {
        List<User> userList = new ArrayList<>();


        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
            userList.add(documentSnapshot.toObject(User.class));

        }
        return userList;
    }

    public MutableLiveData<List<User>> getFirebaseUsersList() {
        MutableLiveData<List<User>> usersListMutableLiveData = new MutableLiveData<List<User>>();
        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        userList.add(documentSnapshot.toObject(User.class));
                    }
                    usersListMutableLiveData.postValue(userList);
                }
                if (task.isCanceled()) {
                    usersListMutableLiveData.postValue(null);
                }
            }
        });
        return usersListMutableLiveData;
    }

    public MutableLiveData<List<User>> getAllUsersList() {
        MutableLiveData<List<User>> usersList = new MutableLiveData<List<User>>();
        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> userList = new ArrayList<>();
                Log.e("test", String.valueOf(task.getResult().getClass()));
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    userList.add(documentSnapshot.toObject(User.class));
                }
                Log.e("userListgfggrgwrgwrwg", String.valueOf(userList.size()));
                usersList.postValue(userList);
            }
        });
        return usersList;
    }
}

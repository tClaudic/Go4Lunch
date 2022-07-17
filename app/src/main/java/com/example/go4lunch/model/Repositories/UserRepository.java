package com.example.go4lunch.model.Repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepository {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String COLLECTION_NAME = "users";
    MutableLiveData<List<User>> usersListMutableLiveData;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference usersRef = firebaseFirestore.collection(COLLECTION_NAME);
    MutableLiveData<List<User>> usersFilteredListMutableLiveData = new MutableLiveData<>();


    public UserRepository() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        usersListMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<User>> getUsersListMutableLiveData() {
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.e("getAllUsersTest", String.valueOf(task.getResult()));
                List<User> userList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    userList.add(documentSnapshot.toObject(User.class));
                }
                usersListMutableLiveData.postValue(userList);
            }

        });
        return usersListMutableLiveData;
    }

    public MutableLiveData<List<User>> getUsersFilteredListMutableLiveData(String restaurantChoice) {
        Log.e("testrestochoice",restaurantChoice);
        usersRef.whereEqualTo("restaurantChoice", restaurantChoice).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.e("getAllUsersTest", String.valueOf(task.isSuccessful()));
                List<User> userList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    userList.add(documentSnapshot.toObject(User.class));
                }
                Log.e("usersFilterTestoutside", String.valueOf(userList.size()));
                if (!userList.isEmpty()) {
                    usersFilteredListMutableLiveData.setValue(userList);
                    Log.e("usersFilterTest", String.valueOf(userList.size()));
                }

            }
        });

        return usersFilteredListMutableLiveData;
    }

    public void removeUserLike(String userId, String restaurantID) {

    }


    public Task<QuerySnapshot> getUsersByRestaurantChoice(String restaurantId) {
        return usersRef.whereEqualTo("restaurantChoice", restaurantId).get();
    }

    public void addLike(String userID, String restaurantId) {
        usersRef.document(userID).update("likes", FieldValue.arrayUnion(restaurantId)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("succesLike", String.valueOf(task.isSuccessful()));
            }
        });
    }

    public void addRestaurant(String userId, String restaurantID) {
        usersRef.document(userId).update("restaurantChoice", restaurantID).addOnCompleteListener(task -> Log.e("addrestaurant", String.valueOf(task.isSuccessful())));
    }

    public void addRestaurantChoiceName(String userId, String restaurantChoiceName) {
        usersRef.document(userId).update("restaurantChoiceName", restaurantChoiceName).addOnCompleteListener(task -> Log.e("addRestaurantChoiceName", String.valueOf(task.isSuccessful())));
    }

    public void removeRestaurantChoice(String uid) {
        usersRef.document(uid).update("placeId", "").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public Task<DocumentSnapshot> getAuthenticatedUser() {

        DocumentReference documentReference = usersRef.document(firebaseAuth.getUid());
        return documentReference.get();

    }


    public MutableLiveData<User> getAuthenticatedUserMutableLiveData() {
        MutableLiveData<User> authenticatedUser = new MutableLiveData<>();
        if (firebaseAuth.getCurrentUser() != null) {
            DocumentReference documentReference = usersRef.document(firebaseAuth.getUid());
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    authenticatedUser.setValue(user);
                } else {
                    Log.e("getAuthenticatedUser", Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        }
        return authenticatedUser;
    }


}

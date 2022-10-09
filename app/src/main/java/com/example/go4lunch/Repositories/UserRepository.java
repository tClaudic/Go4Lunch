package com.example.go4lunch.Repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

    private final FirebaseFirestore firebaseFirestore;

    private static final String COLLECTION_NAME = "users";
    MutableLiveData<List<User>> usersListMutableLiveData;
    private final FirebaseAuth firebaseAuth;
    MutableLiveData<List<User>> usersFilteredListMutableLiveData = new MutableLiveData<>();

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String RESTAURANT_CHOICE = "restaurantChoice";
    private static final String PLACE_ID = "placeId";


    public UserRepository(FirebaseFirestore firebaseFirestore, FirebaseAuth firebaseAuth) {
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
        usersListMutableLiveData = new MutableLiveData<>();
    }


    public MutableLiveData<List<User>> getUsersListMutableLiveData() {
        firebaseFirestore.collection(COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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

        firebaseFirestore.collection(COLLECTION_NAME).whereEqualTo("restaurantChoice", restaurantChoice).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> userList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    userList.add(documentSnapshot.toObject(User.class));
                }
                usersFilteredListMutableLiveData.setValue(userList);
            }
        });

        return usersFilteredListMutableLiveData;
    }


    public MutableLiveData<String> removeUserLike(String userId, String restaurantID) {
        MutableLiveData<String> result = new MutableLiveData<>();
        firebaseFirestore.collection(COLLECTION_NAME).document(userId).update("likes", FieldValue.arrayRemove(restaurantID)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.setValue(SUCCESS);
            }
            if (task.isCanceled()) {
                result.setValue(ERROR);
            }
        });
        return result;
    }


    public Task<QuerySnapshot> getUsersByRestaurantChoice(String restaurantId) {
        return firebaseFirestore.collection(COLLECTION_NAME).whereEqualTo(RESTAURANT_CHOICE, restaurantId).get();
    }

    public MutableLiveData<String> addLike(String userID, String restaurantId) {
        MutableLiveData<String> result = new MutableLiveData<>();
        firebaseFirestore.collection(COLLECTION_NAME).document(userID).update("likes", FieldValue.arrayUnion(restaurantId)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.setValue(SUCCESS);
            }
            if (task.isCanceled()) {
                result.setValue(ERROR);
            }
        });
        return result;
    }

    public MutableLiveData<String> addRestaurant(String userId, String restaurantID) {
        MutableLiveData<String> result = new MutableLiveData<>();
        String restaurantChoice = "restaurantChoice";

        firebaseFirestore.collection(COLLECTION_NAME).document(userId).update(RESTAURANT_CHOICE, restaurantID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    result.setValue(SUCCESS);
                }
                if (task.isCanceled()) {
                    result.setValue(ERROR);
                }
            }
        });
        return result;
    }


    public MutableLiveData<String> addRestaurantChoiceName(String userId, String restaurantChoiceName) {
        MutableLiveData<String> result = new MutableLiveData<>();
        firebaseFirestore.collection(COLLECTION_NAME).document(userId).update("restaurantChoiceName", restaurantChoiceName).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.setValue(SUCCESS);
            }
            if (task.isCanceled()) {
                result.setValue(ERROR);
            }
        });
        return result;
    }

    public MutableLiveData<String> removeRestaurantChoice(String uid) {
        MutableLiveData<String> result = new MutableLiveData<>();
        String string = "";
        firebaseFirestore.collection(COLLECTION_NAME).document(uid).update(PLACE_ID, string).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    result.setValue(SUCCESS);
                }
                if (task.isCanceled()) {
                    result.setValue(ERROR);
                }
            }
        });
        return result;
    }

    public Task<DocumentSnapshot> getAuthenticatedUser() {

        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(Objects.requireNonNull(firebaseAuth.getUid()));
        return documentReference.get();

    }


    public MutableLiveData<User> getAuthenticatedUserMutableLiveData() {
        MutableLiveData<User> authenticatedUser = new MutableLiveData<>();
        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            firebaseFirestore.collection(COLLECTION_NAME).document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    authenticatedUser.setValue(user);
                }
                if (task.isCanceled()) {
                    authenticatedUser.setValue(null);
                }

            });
        } else {
            authenticatedUser.setValue(null);
        }
        return authenticatedUser;
    }


}

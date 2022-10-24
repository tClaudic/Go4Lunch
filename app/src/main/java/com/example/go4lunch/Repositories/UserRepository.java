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
    private final FirebaseAuth firebaseAuth;


    private static final String COLLECTION_NAME = "users";
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String RESTAURANT_CHOICE = "restaurantChoice";
    private static final String RESTAURANT_CHOICE_NAME = "restaurantChoiceName";
    private static final String LIKES = "likes";


    public UserRepository(FirebaseFirestore firebaseFirestore, FirebaseAuth firebaseAuth) {
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseAuth = firebaseAuth;
    }


    public MutableLiveData<List<User>> getUsersListFromFirebase() {
        MutableLiveData<List<User>> usersListMutableLiveData = new MutableLiveData<>();
        firebaseFirestore.collection(COLLECTION_NAME).get().addOnCompleteListener(task -> {
            List<User> userList = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                userList.add(documentSnapshot.toObject(User.class));
            }
            usersListMutableLiveData.setValue(userList);
        });
        return usersListMutableLiveData;
    }


    public MutableLiveData<List<User>> getUsersFilteredListFromFirebase(String restaurantChoice) {
        MutableLiveData<List<User>> usersFilteredListMutableLiveData = new MutableLiveData<>();
        firebaseFirestore.collection(COLLECTION_NAME).whereEqualTo(RESTAURANT_CHOICE, restaurantChoice).get().addOnCompleteListener(task -> {
            List<User> userList = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                userList.add(documentSnapshot.toObject(User.class));
            }
            usersFilteredListMutableLiveData.setValue(userList);
        });
        return usersFilteredListMutableLiveData;
    }


    public MutableLiveData<String> removeUserLikeInFirebase(String userId, String restaurantID) {
        MutableLiveData<String> result = new MutableLiveData<>();
        firebaseFirestore.collection(COLLECTION_NAME).document(userId).update(LIKES, FieldValue.arrayRemove(restaurantID)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.setValue(SUCCESS);
            }
            if (task.isCanceled()) {
                result.setValue(ERROR);
            }
        });
        return result;
    }


    public MutableLiveData<String> addUserLikeInFirebase(String userID, String restaurantId) {
        MutableLiveData<String> result = new MutableLiveData<>();
        firebaseFirestore.collection(COLLECTION_NAME).document(userID).update(LIKES, FieldValue.arrayUnion(restaurantId)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.setValue(SUCCESS);
            }
            if (task.isCanceled()) {
                result.setValue(ERROR);
            }
        });
        return result;
    }

    public MutableLiveData<String> addUserRestaurantChoiceInFirebase(String userId, String restaurantID) {
        MutableLiveData<String> result = new MutableLiveData<>();
        firebaseFirestore.collection(COLLECTION_NAME).document(userId).update(RESTAURANT_CHOICE, restaurantID).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.setValue(SUCCESS);
            }
            if (task.isCanceled()) {
                result.setValue(ERROR);
            }
        });
        return result;
    }

    public MutableLiveData<String> addUserRestaurantChoiceNameInFirebase(String userId, String restaurantChoiceName) {
        MutableLiveData<String> result = new MutableLiveData<>();
        firebaseFirestore.collection(COLLECTION_NAME).document(userId).update(RESTAURANT_CHOICE_NAME, restaurantChoiceName).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.setValue(SUCCESS);
            }
            if (task.isCanceled()) {
                result.setValue(ERROR);
            }
        });
        return result;
    }

    public MutableLiveData<String> removeUserRestaurantChoiceNameInFirebase(String userId) {
        MutableLiveData<String> result = new MutableLiveData<>();
        String string = "";
        firebaseFirestore.collection(COLLECTION_NAME).document(userId).update(RESTAURANT_CHOICE_NAME, string).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.setValue(SUCCESS);
            }
            if (task.isCanceled()) {
                result.setValue(ERROR);
            }
        });
        return result;
    }

    public MutableLiveData<String> removeRestaurantChoiceInFirebase(String uid) {
        MutableLiveData<String> result = new MutableLiveData<>();
        String string = "";
        firebaseFirestore.collection(COLLECTION_NAME).document(uid).update(RESTAURANT_CHOICE, string).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.setValue(SUCCESS);
            }
            if (task.isCanceled()) {
                result.setValue(ERROR);
            }
        });
        return result;
    }

    public MutableLiveData<User> getAuthenticatedUserFromFirebase() {
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


    public Task<DocumentSnapshot> getTaskAuthenticatedUserFromFirebase() {
        DocumentReference documentReference = firebaseFirestore.collection(COLLECTION_NAME).document(Objects.requireNonNull(firebaseAuth.getUid()));
        return documentReference.get();
    }

    public Task<QuerySnapshot> getTaskUsersByRestaurantChoiceFromFirebase(String restaurantId) {
        return firebaseFirestore.collection(COLLECTION_NAME).whereEqualTo(RESTAURANT_CHOICE, restaurantId).get();
    }


}

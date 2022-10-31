package com.example.go4lunch.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AlertReceiver extends BroadcastReceiver {

    UserRepository userRepository = new UserRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
    User currentAuthenticatedUser = new User();
    List<User> usersList = new ArrayList<>();
    String usersListString = "";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notificationPreference = sharedPreferences.getBoolean("notification", false);
        if (notificationPreference) {
            getAuthenticatedUser();
            Log.e("Notification", "Notification Happened");
        }
    }

    public void getAuthenticatedUser() {
        userRepository.getTaskAuthenticatedUserFromFirebase().addOnCompleteListener(task -> {
            currentAuthenticatedUser = task.getResult().toObject(User.class);
            if (currentAuthenticatedUser != null) {
                userRepository.getTaskUsersByRestaurantChoiceFromFirebase(currentAuthenticatedUser.restaurantChoice).addOnCompleteListener(task1 -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task1.getResult()) {
                        User user = queryDocumentSnapshot.toObject(User.class);
                        if (!Objects.equals(user.uid, currentAuthenticatedUser.uid)) {
                            usersList.add(user);
                        }
                    }
                    Log.e("userListSizeNotificatio", String.valueOf(usersList.size()));
                    setupUsersStringForNotification();
                    sendNotification();
                });
            }
        });
    }


    public void sendNotification() {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder notificationBuilder = notificationHelper.getChannelNotification(currentAuthenticatedUser, usersListString);
        notificationHelper.getNotificationManager().notify(1, notificationBuilder.build());
    }

    public void setupUsersStringForNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            usersListString = usersList.stream().map(User::getName).collect(Collectors.joining(" , "));
        } else usersListString = usersList.toString();
    }


}

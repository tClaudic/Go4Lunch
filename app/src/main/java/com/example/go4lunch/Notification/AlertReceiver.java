package com.example.go4lunch.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.go4lunch.Repositories.UserRepository;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlertReceiver extends BroadcastReceiver {

    UserRepository userRepository = new UserRepository(FirebaseFirestore.getInstance());
    User currentAuthenticatedUser = new User();
    List<User> usersList = new ArrayList<>();
    String usersListString = "";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        getAuthenticatedUser();
        Log.e("Notification", "Notification Happened");
    }

    public void getAuthenticatedUser() {
        userRepository.getAuthenticatedUser().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                currentAuthenticatedUser = task.getResult().toObject(User.class);
                if (currentAuthenticatedUser != null) {
                    userRepository.getUsersByRestaurantChoice(currentAuthenticatedUser.restaurantChoice).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                usersList.add(queryDocumentSnapshot.toObject(User.class));
                            }
                            Log.e("userListSizeNotificatio", String.valueOf(usersList.size()));
                            setupUsersStringForNotification();
                            sendNotification();
                        }
                    });
                }
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
            usersListString = usersList.stream().map(User::getName).collect(Collectors.joining(","));
        } else usersListString = usersList.toString();
    }


}

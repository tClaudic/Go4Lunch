package com.example.go4lunch.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;

public class NotificationHelper extends ContextWrapper {


    public static final String channelID = "channelID";
    public static final String channelName = "channelName";
    NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    private void createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelID,channelName, NotificationManager.IMPORTANCE_HIGH);
           getNotificationManager().createNotificationChannel(notificationChannel);
        }
    }

    public NotificationManager getNotificationManager(){
        if (notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public NotificationCompat.Builder getChannelNotification(User user, String usersList){
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle(getString(R.string.notification_title) + " " +  user.restaurantChoiceName)
                .addLine(getString(R.string.notification_line) + " " + usersList);
        return new NotificationCompat.Builder(getApplicationContext(),channelID)
                .setContentTitle(getString(R.string.app_name))
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_go4lunch_launcher_foreground);
    }
}

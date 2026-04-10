package com.alertify.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "alertify_emergency_channel";
    private static final String CHANNEL_NAME = "Emergency Alerts";

    public static void showNearbyAlertNotification(Context context, String title, String message, String locationLink) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for nearby emergencies");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Action to open the Google Maps link when the notification is tapped
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationLink));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mapIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // using default OS alert icon
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message + "\nTap to open location."))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            // Trigger the notification
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}

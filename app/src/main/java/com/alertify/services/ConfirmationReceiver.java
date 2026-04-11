package com.alertify.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import android.app.PendingIntent;
import com.alertify.R;
import com.alertify.emergency.EmergencyManager;

public class ConfirmationReceiver extends BroadcastReceiver {
    public static final String ACTION_YES = "com.alertify.ACTION_YES";
    public static final String ACTION_NO = "com.alertify.ACTION_NO";
    public static final int NOTIFICATION_ID = 100;

    private static Handler autoTriggerHandler;
    private static Runnable autoTriggerRunnable;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        cancelAutoTrigger(); // Cancel the 60s timer if any button is pressed

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);

        if (ACTION_YES.equals(action)) {
            EmergencyManager.triggerEmergency(context);
        } else if (ACTION_NO.equals(action)) {
            // Do nothing, alert cancelled
        }
    }

    public static void showConfirmationNotification(Context context) {
        Intent yesIntent = new Intent(context, ConfirmationReceiver.class);
        yesIntent.setAction(ACTION_YES);
        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(context, 0, yesIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent noIntent = new Intent(context, ConfirmationReceiver.class);
        noIntent.setAction(ACTION_NO);
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(context, 1, noIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "AlertifyBackgroundChannel")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Emergency Triggered")
                .setContentText("Are you in danger?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVibrate(new long[]{0, 500, 200, 500})
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setOngoing(true)
                .addAction(0, "YES", yesPendingIntent)
                .addAction(0, "NO", noPendingIntent);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, builder.build());

        startAutoTriggerTimer(context);
    }

    public static void startAutoTriggerTimer(Context context) {
        cancelAutoTrigger(); // Reset existing
        autoTriggerHandler = new Handler(Looper.getMainLooper());
        autoTriggerRunnable = () -> {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(NOTIFICATION_ID);
            EmergencyManager.triggerEmergency(context);
        };
        autoTriggerHandler.postDelayed(autoTriggerRunnable, 30000); // 30 seconds
    }

    public static void cancelAutoTrigger() {
        if (autoTriggerHandler != null && autoTriggerRunnable != null) {
            autoTriggerHandler.removeCallbacks(autoTriggerRunnable);
            autoTriggerHandler = null;
            autoTriggerRunnable = null;
        }
    }
}

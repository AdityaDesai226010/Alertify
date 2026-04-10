package com.alertify.triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.alertify.emergency.EmergencyManager;

public class VolumeButtonReceiver extends BroadcastReceiver {

    private static final String TAG = "VolumeButtonReceiver";
    private static int count = 0;
    private static long lastPressTime = 0;
    private static final int PRESS_THRESHOLD = 3;
    private static final int TIME_WINDOW = 2000; // 2 seconds

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction()) || Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            // This is a common trick to detect volume buttons when screen is off, 
            // but for real implementation, an AccessibilityService is preferred.
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPressTime < TIME_WINDOW) {
            count++;
        } else {
            count = 1;
        }

        lastPressTime = currentTime;

        if (count >= PRESS_THRESHOLD) {
            Log.d(TAG, "Volume Button Triggered!");
            EmergencyManager.triggerEmergency(context);
            count = 0;
        }
    }
}

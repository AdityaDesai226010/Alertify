package com.alertify.emergency;

import android.content.Context;
import com.alertify.location.LocationHelper;
import com.alertify.utils.Logger;

/**
 * The central orchestrator for emergency situations.
 * This class is called by various triggers (Member 1) and executes
 * the necessary actions (Member 2 and Member 3).
 */
public class EmergencyManager {
    private static final String TAG = "EmergencyManager";

    public static void triggerEmergency(Context context) {
        Logger.info(TAG, "!!! EMERGENCY TRIGGERED !!!");

        // 0. Provide immediate physical/visual feedback
        vibrate(context);
        com.alertify.utils.NotificationHelper.showNearbyAlertNotification(context, 
            "EMERGENCY ACTIVATED", 
            "Alertify is sending alerts and calling your contact.", 
            "https://maps.google.com");

        // 1. Send an immediate "Signal Received" SMS (Pre-location)
        SmsSender.sendSMS(context, null);

        // 2. Initiate call immediately
        CallHandler.makeCall(context);

        // 3. Fetch location and proceed with detailed alerts
        LocationHelper.fetchLocation(context, new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(double lat, double lng, String locationLink) {
                Logger.info(TAG, "Location acquired: " + locationLink);
                executeAlerts(context, lat, lng, locationLink);
            }

            @Override
            public void onLocationError(String error) {
                Logger.error(TAG, "Location acquisition failed: " + error);
                // Fallback: Alert without location or with a placeholder
                executeAlerts(context, 0.0, 0.0, "Location unavailable");
            }
        });
    }

    /**
     * Executes individual alert actions that require location.
     */
    private static void executeAlerts(Context context, double lat, double lng, String locationLink) {
        // 3. Send SMS to all emergency contacts (Member 2)
        SmsSender.sendSMS(context, locationLink);

    // 4. Notify Nearby Users / Firebase (Member 3)
        Logger.info(TAG, "Notifying nearby users and updating Firebase synchronized state...");
        com.alertify.nearby.NearbyAlertService.sendAlert(context, lat, lng, locationLink);
    }

    private static void vibrate(Context context) {
        android.os.Vibrator v = (android.os.Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Strong double vibration
                v.vibrate(android.os.VibrationEffect.createWaveform(new long[]{0, 500, 200, 500}, -1));
            } else {
                // Deprecated in API 26
                v.vibrate(new long[]{0, 500, 200, 500}, -1);
            }
        }
    }
}

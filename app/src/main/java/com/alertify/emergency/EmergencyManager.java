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

    /**
     * Triggers the full emergency response sequence.
     * @param context Application context
     */
    public static void triggerEmergency(Context context) {
        Logger.info(TAG, "!!! EMERGENCY TRIGGERED !!!");

        // 1. Fetch location and proceed with alerts
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
     * Executes individual alert actions.
     */
    private static void executeAlerts(Context context, double lat, double lng, String locationLink) {
        // 2. Send SMS to all emergency contacts (Member 2)
        SmsSender.sendSMS(context, locationLink);

        // 3. Initiate a call to the primary contact (Member 2)
        CallHandler.makeCall(context);

        // 4. Notify Nearby Users / Firebase (Member 3)
        Logger.info(TAG, "Notifying nearby users and updating Firebase synchronized state...");
        com.alertify.nearby.NearbyAlertService.sendAlert(context, lat, lng, locationLink);
    }
}

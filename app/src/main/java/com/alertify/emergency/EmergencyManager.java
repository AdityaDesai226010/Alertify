package com.alertify.emergency;

import android.content.Context;
import android.util.Log;
import com.alertify.location.LocationHelper;

public class EmergencyManager {

    private static final String TAG = "EmergencyManager";

    public static void triggerEmergency(Context context) {
        Log.d(TAG, "Emergency Triggered!");

        // 1. Fetch Location
        LocationHelper.fetchLocation(context, new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(String locationLink) {
                Log.d(TAG, "Location Link: " + locationLink);

                // 2. Send SMS to all emergency contacts
                SmsSender.sendSMS(context, locationLink);

                // 3. Make Automated Call to primary contact
                CallHandler.makeCall(context);

                // 4. Notify Nearby Users (Implementation depends on Firebase)
                Log.d(TAG, "Notifying nearby users...");
            }

            @Override
            public void onLocationError(String error) {
                Log.e(TAG, "Location Error: " + error);
                
                // Fallback: Send emergency alert without location or with last known location
                SmsSender.sendSMS(context, "Location unavailable");
                CallHandler.makeCall(context);
            }
        });
    }
}

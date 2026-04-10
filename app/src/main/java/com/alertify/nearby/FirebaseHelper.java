package com.alertify.nearby;

import android.util.Log;
import com.alertify.models.AlertModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.UUID;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    // Call this in Application class onCreate or MainActivity to persist data offline
    public static void initializeFirebase() {
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Log.d(TAG, "Firebase Offline Persistence Enabled.");
        } catch (Exception e) {
            Log.e(TAG, "Firebase Persistence already enabled or failed: " + e.getMessage());
        }
    }

    public static void sendAlert(double lat, double lng, String locationLink) {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("alerts");
            
            // Generate a unique ID
            String alertId = ref.push().getKey();
            if (alertId == null) alertId = UUID.randomUUID().toString();

            // Note: Replace "currentUser" with real user ID (from FirebaseAuth) when auth is implemented
            AlertModel alert = new AlertModel("currentUser", lat, lng, locationLink, System.currentTimeMillis());

            ref.child(alertId).setValue(alert)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Alert sent successfully to Firebase: " + alertId))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to send alert: " + e.getMessage()));

        } catch (Exception e) {
            Log.e(TAG, "Firebase Database error: " + e.getMessage());
        }
    }
}

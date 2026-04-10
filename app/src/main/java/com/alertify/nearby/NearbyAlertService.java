package com.alertify.nearby;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import androidx.annotation.NonNull;
import com.alertify.models.AlertModel;
import com.alertify.utils.NotificationHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NearbyAlertService {

    private static final String TAG = "NearbyAlertService";
    private static final double ALERT_RADIUS_METERS = 2000.0; // 2km radius
    
    // Prevent showing duplicate alerts (timestamp tracking)
    private static long lastProcessedTimestamp = 0;

    public static void sendAlert(Context context, double lat, double lng, String locationLink) {
        FirebaseHelper.sendAlert(lat, lng, locationLink);
    }

    // Start listening for nearby alerts. You can call this in BackgroundService or MainActivity
    public static void listenForNearbyAlerts(Context context, double currentLat, double currentLng) {
        DatabaseReference alertsRef = FirebaseDatabase.getInstance().getReference("alerts");

        alertsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Optimization: Track the latest processed alert timestamp
                long maxTimestampProcessedInThisBatch = lastProcessedTimestamp;

                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        AlertModel alert = data.getValue(AlertModel.class);
                        if (alert == null) continue;

                        // Ignore our own alerts (if userId matches) or old alerts
                        if ("currentUser".equals(alert.getUserId())) continue;
                        if (alert.getTimestamp() <= lastProcessedTimestamp) continue; // skip already processed

                        // Calculate distance using Android Location.distanceBetween
                        float[] results = new float[1];
                        Location.distanceBetween(currentLat, currentLng, alert.getLatitude(), alert.getLongitude(), results);
                        float distanceInMeters = results[0];

                        Log.d(TAG, "Incoming alert distance: " + distanceInMeters + " meters");

                        if (distanceInMeters <= ALERT_RADIUS_METERS) {
                            // Trigger Notification
                            NotificationHelper.showNearbyAlertNotification(
                                context,
                                "Nearby Emergency Alert!",
                                "Someone needs help " + Math.round(distanceInMeters) + "m away!",
                                alert.getLocationLink()
                            );
                        }

                        // Keep track of the highest timestamp we've seen this batch
                        if (alert.getTimestamp() > maxTimestampProcessedInThisBatch) {
                            maxTimestampProcessedInThisBatch = alert.getTimestamp();
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing alert data: " + e.getMessage());
                    }
                }
                
                // Update our global timestamp so we don't process old alerts again
                lastProcessedTimestamp = maxTimestampProcessedInThisBatch;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error listening for alerts: " + error.getMessage());
            }
        });
    }
}

package com.alertify.location;

import android.annotation.SuppressLint;
import android.content.Context;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationHelper {

    public interface LocationCallback {
        void onLocationResult(double lat, double lng, String locationLink);
        void onLocationError(String error);
    }

    @SuppressLint("MissingPermission")
    public static void fetchLocation(Context context, LocationCallback callback) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener(location -> {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    String link = "https://maps.google.com/?q=" + lat + "," + lng;
                    callback.onLocationResult(lat, lng, link);
                } else {
                    callback.onLocationError("Location is null");
                }
            })
            .addOnFailureListener(e -> callback.onLocationError(e.getMessage()));
    }
}

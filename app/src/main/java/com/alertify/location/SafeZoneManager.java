package com.alertify.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import com.alertify.utils.Constants;
import com.alertify.utils.Logger;

/**
 * Manages the Safe Zone feature.
 * Handles saving/retrieving home location and checking distance.
 */
public class SafeZoneManager {
    private static final String TAG = "SafeZoneManager";

    public static void setHomeLocation(Context context, double lat, double lng) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_SAFE_ZONE, Context.MODE_PRIVATE);
        prefs.edit()
                .putFloat(Constants.KEY_HOME_LAT, (float) lat)
                .putFloat(Constants.KEY_HOME_LNG, (float) lng)
                .putBoolean(Constants.KEY_SAFE_ZONE_ENABLED, true)
                .apply();
        Logger.info(TAG, "Home location set: " + lat + ", " + lng);
    }

    public static boolean isSafeZoneEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_SAFE_ZONE, Context.MODE_PRIVATE);
        return prefs.getBoolean(Constants.KEY_SAFE_ZONE_ENABLED, false);
    }

    public static void setSafeZoneEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_SAFE_ZONE, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Constants.KEY_SAFE_ZONE_ENABLED, enabled).apply();
    }

    /**
     * Checks if the given location is within the safe zone radius and updates the cache.
     */
    public static boolean updateSafeStatus(Context context, double currentLat, double currentLng) {
        boolean inZone = false;
        if (isSafeZoneEnabled(context)) {
            SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_SAFE_ZONE, Context.MODE_PRIVATE);
            float homeLat = prefs.getFloat(Constants.KEY_HOME_LAT, 0f);
            float homeLng = prefs.getFloat(Constants.KEY_HOME_LNG, 0f);

            if (homeLat != 0f || homeLng != 0f) {
                float[] results = new float[1];
                Location.distanceBetween(currentLat, currentLng, homeLat, homeLng, results);
                float distance = results[0];
                inZone = distance <= Constants.DEFAULT_SAFE_RADIUS;
            }
        }

        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_SAFE_ZONE, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Constants.KEY_IS_CURRENTLY_SAFE, inZone).apply();
        Logger.d("Safe Zone Status Updated: InZone=" + inZone);
        return inZone;
    }

    /**
     * Returns the last known safe status without calculating.
     */
    public static boolean isCurrentlySafe(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_SAFE_ZONE, Context.MODE_PRIVATE);
        return prefs.getBoolean(Constants.KEY_IS_CURRENTLY_SAFE, false);
    }

    public static double[] getHomeLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_SAFE_ZONE, Context.MODE_PRIVATE);
        return new double[]{
                prefs.getFloat(Constants.KEY_HOME_LAT, 0f),
                prefs.getFloat(Constants.KEY_HOME_LNG, 0f)
        };
    }
}

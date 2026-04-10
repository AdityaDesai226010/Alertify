package com.alertify.utils;

public class Constants {
    public static final String SECRET_DIAL_CODE = "5678";
    public static final String SECRET_CALC_PIN = "1234";
    public static final float SHAKE_THRESHOLD = 2.5f;

    // SAFE ZONE CONSTANTS
    public static final String PREF_SAFE_ZONE = "SafeZonePrefs";
    public static final String KEY_HOME_LAT = "home_lat";
    public static final String KEY_HOME_LNG = "home_lng";
    public static final String KEY_SAFE_ZONE_ENABLED = "safe_zone_enabled";
    public static final String KEY_IS_CURRENTLY_SAFE = "is_currently_safe";
    public static final float DEFAULT_SAFE_RADIUS = 500.0f; // meters
}

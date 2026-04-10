package com.alertify.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling runtime permissions.
 */
public class PermissionUtils {
    
    public static final int REQUEST_CODE_ALL_PERMISSIONS = 101;

    public static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.FOREGROUND_SERVICE
    };

    /**
     * Checks if all required permissions are granted.
     * @param context Application context
     * @return true if all permissions are granted, false otherwise
     */
    public static boolean hasAllPermissions(Context context) {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Requests all required permissions.
     * @param activity The activity from which to request permissions
     */
    public static void requestAllPermissions(Activity activity) {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[0]), REQUEST_CODE_ALL_PERMISSIONS);
        }
    }

    /**
     * Checks if a specific permission is granted.
     * @param context Application context
     * @param permission Permission string
     * @return true if granted
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}

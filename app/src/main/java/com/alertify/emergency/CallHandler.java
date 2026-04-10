package com.alertify.emergency;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.alertify.contacts.ContactManager;
import com.alertify.utils.Logger;

/**
 * Handles initiating emergency calls to the primary contact.
 */
public class CallHandler {
    private static final String TAG = "CallHandler";

    /**
     * Makes a call to the primary emergency contact.
     * @param context Application context
     */
    public static void makeCall(Context context) {
        String primaryNumber = ContactManager.getPrimaryContact(context);

        if (primaryNumber == null || primaryNumber.isEmpty()) {
            Logger.warn(TAG, "No primary contact set. Cannot initiate call.");
            // Optional: fallback to national emergency number if needed
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + primaryNumber));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            // Check if there is an app that can handle this intent
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                Logger.info(TAG, "Initiating emergency call to: " + primaryNumber);
            } else {
                Logger.error(TAG, "No activity found to handle ACTION_CALL");
            }
        } catch (SecurityException e) {
            Logger.error(TAG, "CALL_PHONE permission not granted: " + e.getMessage());
        } catch (Exception e) {
            Logger.error(TAG, "Failed to initiate call: " + e.getMessage());
        }
    }
}

package com.alertify.emergency;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
        String primaryNumber = "9579397876";

        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + primaryNumber));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            // On newer Android versions, resolveActivity may return null in background 
            // due to package visibility rules. We will attempt to start the activity directly.
            context.startActivity(intent);
            Logger.info(TAG, "Initiating emergency call to: " + primaryNumber);
        } catch (SecurityException e) {
            Logger.error(TAG, "CALL_PHONE permission not granted: " + e.getMessage());
        } catch (Exception e) {
            Logger.error(TAG, "Failed to initiate call: " + e.getMessage());
        }
    }
}

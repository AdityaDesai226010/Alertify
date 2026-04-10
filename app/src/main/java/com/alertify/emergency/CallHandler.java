package com.alertify.emergency;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class CallHandler {

    private static final String TAG = "CallHandler";

    public static void makeCall(Context context) {
        try {
            // TODO: Fetch primary contact from database/preferences
            String primaryNumber = "911"; // Default emergency number for demo

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + primaryNumber));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.d(TAG, "Initiating call to: " + primaryNumber);
        } catch (SecurityException e) {
            Log.e(TAG, "Call permission not granted", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initiate call", e);
        }
    }
}

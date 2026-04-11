package com.alertify.triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.alertify.emergency.EmergencyManager;
import com.alertify.location.SafeZoneManager;
import com.alertify.utils.Constants;

public class DialCodeReceiver extends BroadcastReceiver {

    private static final String TAG = "DialCodeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String dialedNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            if (Constants.SECRET_DIAL_CODE.equals(dialedNumber)) {
                Log.d(TAG, "Secret Dial Code Triggered!");
                
                // Cancel the actual call
                setResultData(null);
                
                if (SafeZoneManager.isCurrentlySafe(context)) {
                    Log.d(TAG, "Dial Code blocked by Safe Zone!");
                    return;
                }

                // Trigger Emergency
                EmergencyManager.triggerEmergency(context);
            }
        }
    }
}

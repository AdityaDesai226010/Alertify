package com.alertify.triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.alertify.emergency.EmergencyManager;

public class DialCodeReceiver extends BroadcastReceiver {

    private static final String TAG = "DialCodeReceiver";
    private static final String SECRET_CODE = "*123#";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String dialedNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            if (SECRET_CODE.equals(dialedNumber)) {
                Log.d(TAG, "Secret Dial Code Triggered!");
                
                // Cancel the actual call
                setResultData(null);
                
                // Trigger Emergency
                EmergencyManager.triggerEmergency(context);
            }
        }
    }
}

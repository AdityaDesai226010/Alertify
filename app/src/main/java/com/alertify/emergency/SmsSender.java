package com.alertify.emergency;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class SmsSender {

    private static final String TAG = "SmsSender";

    public static void sendSMS(Context context, String locationLink) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "I am in danger! Help me!\nLive Location: " + locationLink;

            // TODO: Fetch real contacts from database/preferences
            List<String> contacts = getEmergencyContacts();

            for (String number : contacts) {
                smsManager.sendTextMessage(number, null, message, null, null);
                Log.d(TAG, "SMS sent to: " + number);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS", e);
        }
    }

    private static List<String> getEmergencyContacts(Context context) {
        return com.alertify.contacts.ContactManager.getContacts(context);
    }
}

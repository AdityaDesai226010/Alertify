package com.alertify.emergency;

import android.content.Context;
import android.telephony.SmsManager;
import com.alertify.contacts.ContactManager;
import com.alertify.utils.Logger;
import java.util.List;

/**
 * Handles sending SMS alerts to emergency contacts.
 */
public class SmsSender {
    private static final String TAG = "SmsSender";

    /**
     * Sends emergency SMS with location to all contacts.
     * @param context Application context
     * @param locationLink Google Maps link with current location
     */
    public static void sendSMS(Context context, String locationLink) {
        List<String> contacts = ContactManager.getContacts(context);
        
        if (contacts.isEmpty()) {
            Logger.warn(TAG, "No emergency contacts found to send SMS.");
            return;
        }

        String message = "EMERGENCY! I need help. My current location: " + locationLink;
        
        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String number : contacts) {
                if (number != null && !number.isEmpty()) {
                    smsManager.sendTextMessage(number, null, message, null, null);
                    Logger.info(TAG, "SMS sent to: " + number);
                }
            }
        } catch (Exception e) {
            Logger.error(TAG, "Failed to send SMS: " + e.getMessage());
        }
    }
}

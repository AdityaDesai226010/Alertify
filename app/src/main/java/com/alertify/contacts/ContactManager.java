package com.alertify.contacts;

import android.content.Context;
import android.content.SharedPreferences;
import com.alertify.utils.Logger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages emergency contacts for the application.
 * Uses SharedPreferences for persistence.
 */
public class ContactManager {
    private static final String TAG = "ContactManager";
    private static final String PREF_NAME = "EmergencyContactsPref";
    private static final String KEY_CONTACTS = "contacts_list";
    private static final String KEY_PRIMARY_CONTACT = "primary_contact";

    /**
     * Adds an emergency contact.
     * @param context Application context
     * @param phoneNumber The phone number to add
     */
    public static void addContact(Context context, String phoneNumber) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> contacts = new HashSet<>(prefs.getStringSet(KEY_CONTACTS, new HashSet<>()));
        contacts.add(phoneNumber);
        prefs.edit().putStringSet(KEY_CONTACTS, contacts).apply();
        
        // If no primary contact exists, set this as primary
        if (getPrimaryContact(context) == null || getPrimaryContact(context).equals("9356467029")) {
            setPrimaryContact(context, phoneNumber);
        }
        Logger.info(TAG, "Added contact: " + phoneNumber);
    }

    /**
     * Removes an emergency contact.
     * @param context Application context
     * @param phoneNumber The phone number to remove
     */
    public static void removeContact(Context context, String phoneNumber) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> contacts = new HashSet<>(prefs.getStringSet(KEY_CONTACTS, new HashSet<>()));
        contacts.remove(phoneNumber);
        prefs.edit().putStringSet(KEY_CONTACTS, contacts).apply();

        // If removed was primary, pick another if available
        if (phoneNumber.equals(getPrimaryContact(context))) {
            if (!contacts.isEmpty()) {
                setPrimaryContact(context, contacts.iterator().next());
            } else {
                setPrimaryContact(context, "9356467029");
            }
        }
        Logger.info(TAG, "Removed contact: " + phoneNumber);
    }

    /**
     * Retrieves all emergency contacts.
     * @param context Application context
     * @return List of phone numbers
     */
    public static List<String> getContacts(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> contacts = prefs.getStringSet(KEY_CONTACTS, new HashSet<>());
        return new ArrayList<>(contacts);
    }

    /**
     * Sets the primary emergency contact for calls.
     * @param context Application context
     * @param phoneNumber The phone number to set as primary
     */
    public static void setPrimaryContact(Context context, String phoneNumber) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PRIMARY_CONTACT, phoneNumber).apply();
        Logger.info(TAG, "Primary contact set to: " + phoneNumber);
    }

    /**
     * Retrieves the primary contact for making calls.
     * @param context Application context
     * @return Primary contact phone number or '9356467029' if none
     */
    public static String getPrimaryContact(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PRIMARY_CONTACT, "9356467029");
    }
}

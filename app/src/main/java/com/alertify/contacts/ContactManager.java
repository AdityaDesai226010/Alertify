package com.alertify.contacts;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactManager {

    private static final String PREF_NAME = "AlertifyContacts";
    private static final String KEY_CONTACTS = "emergency_contacts";
    private static final String KEY_PRIMARY = "primary_contact";

    public static void addContact(Context context, String phoneNumber) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> contacts = prefs.getStringSet(KEY_CONTACTS, new HashSet<>());
        contacts.add(phoneNumber);
        prefs.edit().putStringSet(KEY_CONTACTS, contacts).apply();
    }

    public static List<String> getContacts(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> contacts = prefs.getStringSet(KEY_CONTACTS, new HashSet<>());
        return new ArrayList<>(contacts);
    }

    public static void setPrimaryContact(Context context, String phoneNumber) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PRIMARY, phoneNumber).apply();
    }

    public static String getPrimaryContact(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PRIMARY, "911"); // Fallback to 911
    }
}

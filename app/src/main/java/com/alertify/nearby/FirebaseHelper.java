package com.alertify.nearby;

import android.util.Log;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    public static void sendAlert(String locationLink) {
        // TODO: Initialize Firebase Database
        // DatabaseReference ref = FirebaseDatabase.getInstance().getReference("alerts");
        // String id = ref.push().getKey();
        // ref.child(id).setValue(new AlertModel(locationLink, System.currentTimeMillis()));
        
        Log.d(TAG, "Simulating Firebase Alert Send: " + locationLink);
    }
}

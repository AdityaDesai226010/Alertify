package com.alertify.utils;

import android.util.Log;

public class Logger {
    private static final String TAG = "Alertify";

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void e(String message, Throwable t) {
        Log.e(TAG, message, t);
    }
}

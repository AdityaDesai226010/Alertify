package com.alertify.utils;

import android.util.Log;

public class Logger {
    private static final String TAG = "Alertify";

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void info(String tag, String message) {
        Log.i(tag, message);
    }

    public static void warn(String tag, String message) {
        Log.w(tag, message);
    }

    public static void error(String tag, String message) {
        Log.e(tag, message);
    }

    public static void e(String message, Throwable t) {
        Log.e(TAG, message, t);
    }
}

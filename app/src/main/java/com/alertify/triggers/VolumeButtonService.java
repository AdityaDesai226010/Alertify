package com.alertify.triggers;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import com.alertify.emergency.EmergencyManager;

public class VolumeButtonService extends AccessibilityService {

    private static final String TAG = "VolumeButtonService";
    private int count = 0;
    private long lastPressTime = 0;

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (action == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                long currentTime = System.currentTimeMillis();

                // If window expired, reset count
                if (currentTime - lastPressTime > com.alertify.utils.Constants.VOLUME_TIME_WINDOW) {
                    count = 0;
                }

                count++;
                lastPressTime = currentTime;

                // Provide haptic feedback for each press so the user knows it's registered
                vibrate(100);

                Log.d(TAG, "Volume press count: " + count + " (Key: " + (keyCode == KeyEvent.KEYCODE_VOLUME_UP ? "UP" : "DOWN") + ")");

                if (count >= com.alertify.utils.Constants.VOLUME_PRESS_THRESHOLD) {
                    Log.d(TAG, "Volume Trigger DETECTED! Threshold reached.");
                    EmergencyManager.triggerEmergency(this);
                    count = 0; // Reset after trigger
                    // Extra long vibration to confirm trigger
                    vibrate(500);
                }
            }
            // Return true to consume the event so volume doesn't actually change
            // This makes it "silent" and prevents the volume dialog from popping up constantly
            return true;
        }
        return super.onKeyEvent(event);
    }

    private void vibrate(long duration) {
        android.os.Vibrator v = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (v != null && v.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                v.vibrate(android.os.VibrationEffect.createOneShot(duration, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(duration);
            }
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Not used
    }

    @Override
    public void onInterrupt() {
        // Not used
    }
}

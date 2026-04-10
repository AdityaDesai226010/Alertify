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
    private static final int PRESS_THRESHOLD = 3;
    private static final int TIME_WINDOW = 2000;

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastPressTime < TIME_WINDOW) {
                    count++;
                } else {
                    count = 1;
                }

                lastPressTime = currentTime;

                if (count >= PRESS_THRESHOLD) {
                    Log.d(TAG, "Volume Trigger Detected via Accessibility!");
                    EmergencyManager.triggerEmergency(this);
                    count = 0;
                }
            }
            // Return false to allow the system to still handle the volume change
            return false;
        }
        return super.onKeyEvent(event);
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

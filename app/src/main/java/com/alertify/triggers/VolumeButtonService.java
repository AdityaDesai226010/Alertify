package com.alertify.triggers;

import android.accessibilityservice.AccessibilityService;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import com.alertify.emergency.EmergencyManager;
import com.alertify.location.SafeZoneManager;

public class VolumeButtonService extends AccessibilityService {

    private long lastVolumeUpTime = 0;
    private int volumeUpCount = 0;
    private static final long RESET_INTERVAL = 2000; // 2 seconds to press 3 times

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Not used for key events
    }

    @Override
    public void onInterrupt() {
        // Required method
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && action == KeyEvent.ACTION_DOWN) {
            long currentTime = System.currentTimeMillis();
            
            if (currentTime - lastVolumeUpTime < RESET_INTERVAL) {
                volumeUpCount++;
            } else {
                volumeUpCount = 1;
            }
            
            lastVolumeUpTime = currentTime;

            if (volumeUpCount >= 3) {
                triggerEmergency();
                volumeUpCount = 0; // Reset
            }
            // We return false to allow the volume to actually change as well, 
            // or true to consume it. For safety apps, allowing it to change is fine.
            return false;
        }

        return super.onKeyEvent(event);
    }

    private void triggerEmergency() {
        if (SafeZoneManager.isCurrentlySafe(this)) {
            Toast.makeText(this, "Emergency trigger blocked: Currently in Safe Zone", Toast.LENGTH_SHORT).show();
            return;
        }
        EmergencyManager.triggerEmergency(this);
    }
}

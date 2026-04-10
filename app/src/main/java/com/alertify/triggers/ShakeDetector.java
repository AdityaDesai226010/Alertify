package com.alertify.triggers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.alertify.emergency.EmergencyManager;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 12.0f;
    private static final int SHAKE_TIME_LAPSE = 1000;
    private long lastShakeTime = 0;
    private Context context;

    public ShakeDetector(Context context) {
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration = (float) Math.sqrt(x*x + y*y + z*z);

        if (acceleration > SHAKE_THRESHOLD) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastShakeTime > SHAKE_TIME_LAPSE) {
                lastShakeTime = currentTime;

                // Trigger Emergency
                EmergencyManager.triggerEmergency(context);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}

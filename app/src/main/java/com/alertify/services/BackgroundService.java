package com.alertify.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.alertify.triggers.ShakeDetector;
import com.alertify.emergency.EmergencyManager;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.SpeechService;
import org.vosk.android.StorageService;
import org.vosk.android.RecognitionListener;
import android.os.Vibrator;
import android.os.VibrationEffect;

public class BackgroundService extends Service implements RecognitionListener {

    public static final String ACTION_VOSK_STATUS = "com.alertify.VOSK_STATUS";
    public static final String EXTRA_STATUS = "status";
    
    public static final int STATUS_LOADING = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_ERROR = 2;

    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;
    private Model model;
    private SpeechService speechService;
    private Vibrator vibrator;
    private static final String CHANNEL_ID = "AlertifyBackgroundChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        
        // DEBUG: 1 long vibration to confirm Service is STARTING
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500); 
        }

        createNotificationChannel();
        
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alertify Active")
                .setContentText("Monitoring for emergency triggers...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        startForeground(1, notification);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(this);
        
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        // Delay initialization by 1.5s to give MainActivity time to register its UI receiver
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::initializeVosk, 1500);
    }

    private void initializeVosk() {
        android.util.Log.d("Vosk", "Attempting model load...");
        sendStatus(STATUS_LOADING);
        
        // This is the most compatible way to load for 0.3.32+
        StorageService.unpack(this, "model", "model",
            (m) -> {
                this.model = m;
                android.util.Log.d("Vosk", "SUCCESS: Model ready.");
                new android.os.Handler(android.os.Looper.getMainLooper()).post(this::startRecognition);
            },
            (exception) -> {
                android.util.Log.e("Vosk", "ERROR: " + exception.getMessage());
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> sendStatus(STATUS_ERROR));
            }
        );
    }

    private void sendStatus(int status) {
        Intent intent = new Intent(ACTION_VOSK_STATUS);
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
    }

    private void startRecognition() {
        try {
            Recognizer rec = new Recognizer(model, 16000.0f);
            speechService = new SpeechService(rec, 16000.0f);
            speechService.startListening(this);
            android.util.Log.d("Vosk", "Recognition started. Listening...");
            
            sendStatus(STATUS_READY);
            
            // DIAGNOSTIC VIBRATION: 2 short pulses when ready
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 100, 100, 100}, -1));
                } else {
                    vibrator.vibrate(new long[]{0, 100, 100, 100}, -1);
                }
            }
            
            updateNotification("Voice Monitoring Active");
        } catch (Exception e) {
            android.util.Log.e("Vosk", "Error starting recognition: " + e.getMessage());
            sendStatus(STATUS_ERROR);
        }
    }

    private void updateNotification(String text) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alertify Protection On")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1, notification);
        }
    }

    @Override
    public void onResult(String hypothesis) {
        checkHypothesis(hypothesis);
    }

    @Override
    public void onFinalResult(String hypothesis) {
        checkHypothesis(hypothesis);
    }

    @Override
    public void onPartialResult(String hypothesis) {
        checkHypothesis(hypothesis);
    }

    private void checkHypothesis(String json) {
        if (json == null) return;
        try {
            // Vosk returns JSON like {"text": "help me"}
            org.json.JSONObject obj = new org.json.JSONObject(json);
            String text = obj.optString("text", "").toLowerCase();
            String partial = obj.optString("partial", "").toLowerCase();
            
            if (text.contains("help me") || partial.contains("help me")) {
                android.util.Log.d("Vosk", "Emergency Triggered by Voice!");
                EmergencyManager.triggerEmergency(this);
            }
        } catch (org.json.JSONException e) {
            // Fallback for raw string check
            if (json.toLowerCase().contains("help me")) {
                EmergencyManager.triggerEmergency(this);
            }
        }
    }

    @Override
    public void onError(Exception exception) {
        // Handle error
    }

    @Override
    public void onTimeout() {
        // Handle timeout
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alertify Background Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(restartServiceIntent);
        } else {
            startService(restartServiceIntent);
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null && shakeDetector != null) {
            sensorManager.unregisterListener(shakeDetector);
        }
        if (speechService != null) {
            speechService.stop();
            speechService.shutdown();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

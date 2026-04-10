package com.alertify.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.alertify.utils.Constants;
import android.provider.Settings;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import android.widget.ProgressBar;
import com.alertify.R;
import com.alertify.emergency.EmergencyManager;
import com.alertify.services.BackgroundService;
import com.alertify.contacts.ContactManager;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private TextView statusTextView;
    private TextView contactsListTextView;
    private EditText contactEditText;
    private TextView modelStatusTextView;
    private ProgressBar modelProgressBar;

    private final BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BackgroundService.EXTRA_STATUS, -1);
            updateModelUI(status);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        contactsListTextView = findViewById(R.id.contactsListTextView);
        contactEditText = findViewById(R.id.contactEditText);
        modelStatusTextView = findViewById(R.id.modelStatusTextView);
        modelProgressBar = findViewById(R.id.modelProgressBar);

        Button addContactButton = findViewById(R.id.addContactButton);
        Button testButton = findViewById(R.id.testTriggerButton);
        Button btnSafeZone = findViewById(R.id.btnSafeZone);
        Button btnOverlayPermission = findViewById(R.id.btnOverlayPermission);

        btnOverlayPermission.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    Toast.makeText(this, "Enable 'Draw over other apps' to allow background calls", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Overlay permission already granted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshContactsList();

        btnSafeZone.setOnClickListener(v -> {
            Intent intent = new Intent(this, SafeZoneActivity.class);
            startActivity(intent);
        });

        addContactButton.setOnClickListener(v -> {
            String phone = contactEditText.getText().toString().trim();
            if (!phone.isEmpty()) {
                ContactManager.addContact(this, phone);
                contactEditText.setText("");
                refreshContactsList();
                Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();
            }
        });

        testButton.setOnClickListener(v -> {
            EmergencyManager.triggerEmergency(this);
            Toast.makeText(this, "Manual Trigger Activated", Toast.LENGTH_SHORT).show();
        });

        if (checkPermissions()) {
            startService();
        } else {
            requestPermissions();
        }

        updateSafeZoneStatusDisplay();
    }

    private void updateSafeZoneStatusDisplay() {
        TextView safeZoneStatusText = findViewById(R.id.safeZoneStatusText);
        if (com.alertify.location.SafeZoneManager.isCurrentlySafe(this)) {
            safeZoneStatusText.setVisibility(View.VISIBLE);
            safeZoneStatusText.setText("📍 Safe Zone: ACTIVE (Home)");
            safeZoneStatusText.setTextColor(ContextCompat.getColor(this, R.color.success_green));
        } else if (com.alertify.location.SafeZoneManager.isSafeZoneEnabled(this)) {
            safeZoneStatusText.setVisibility(View.VISIBLE);
            safeZoneStatusText.setText("📍 Safe Zone: Enabled (Away)");
            safeZoneStatusText.setTextColor(ContextCompat.getColor(this, R.color.gray_light));
        } else {
            safeZoneStatusText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSafeZoneStatusDisplay();
    }

    private boolean checkPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECORD_AUDIO
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private void requestPermissions() {
        java.util.List<String> permissions = new java.util.ArrayList<>();
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.CALL_PHONE);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        java.util.List<String> missingPermissions = new java.util.ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(p);
            }
        }

        if (!missingPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            // Foreground granted, check background
            requestBackgroundLocationPermission();
        }
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Background location must be requested AFTER foreground location
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    Toast.makeText(this, "Safe Zone requires 'Allow all the time' location permission. Please enable it in settings.", Toast.LENGTH_LONG).show();
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_CODE + 1);
            } else {
                startService();
            }
        } else {
            startService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int res : grantResults) {
                if (res != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                requestBackgroundLocationPermission();
            } else {
                Toast.makeText(this, "Permissions are required for the app to function.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE + 1) {
            // Background location result
            startService();
        }
    }

    private void startService() {
        Intent backgroundIntent = new Intent(this, BackgroundService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(backgroundIntent);
        } else {
            startService(backgroundIntent);
        }
        statusTextView.setText(R.string.emergency_status_idle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BackgroundService.ACTION_VOSK_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(statusReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(statusReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(statusReceiver);
    }

    private void updateModelUI(int status) {
        switch (status) {
            case BackgroundService.STATUS_LOADING:
                modelProgressBar.setVisibility(View.VISIBLE);
                modelStatusTextView.setText("Voice Module: Loading...");
                modelStatusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                break;
            case BackgroundService.STATUS_READY:
                modelProgressBar.setVisibility(View.GONE);
                modelStatusTextView.setText("Voice Module: Ready ✅");
                modelStatusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
                break;
            case BackgroundService.STATUS_ERROR:
                modelProgressBar.setVisibility(View.GONE);
                modelStatusTextView.setText("Voice Module: Error ❌");
                modelStatusTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                break;
        }
    }

    private void refreshContactsList() {
        java.util.List<String> contacts = ContactManager.getContacts(this);
        if (contacts.isEmpty()) {
            contactsListTextView.setText("No contacts added yet.");
        } else {
            StringBuilder sb = new StringBuilder("Active Project Contacts:\n");
            for (String c : contacts) {
                sb.append("• ").append(c).append("\n");
            }
            contactsListTextView.setText(sb.toString());
        }
    }
}

package com.alertify.ui.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alertify.R;
import com.alertify.contacts.ContactManager;
import com.alertify.emergency.EmergencyManager;
import com.alertify.services.BackgroundService;
import com.google.android.material.button.MaterialButton;

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
            updateVoskStatusUI(status);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // New Design System Button (Splash/Entry point)
        MaterialButton btnGetStarted = findViewById(R.id.btn_get_started);
        if (btnGetStarted != null) {
            btnGetStarted.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
                startActivity(intent);
            });
        }

        // Functional UI Elements (Dashboard Hub)
        statusTextView = findViewById(R.id.statusTextView);
        contactsListTextView = findViewById(R.id.contactsListTextView);
        contactEditText = findViewById(R.id.contactEditText);
        modelStatusTextView = findViewById(R.id.modelStatusTextView);
        modelProgressBar = findViewById(R.id.modelProgressBar);

        Button addContactButton = findViewById(R.id.addContactButton);
        Button testButton = findViewById(R.id.testTriggerButton);
        Button btnSafeZone = findViewById(R.id.btnSafeZone);
        Button btnOverlayPermission = findViewById(R.id.btnOverlayPermission);

        if (btnOverlayPermission != null) {
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
        }

        if (btnSafeZone != null) {
            btnSafeZone.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.alertify.ui.activities.SafeZoneActivity.class);
                startActivity(intent);
            });
        }

        if (addContactButton != null) {
            addContactButton.setOnClickListener(v -> {
                String phone = contactEditText.getText().toString().trim();
                if (!phone.isEmpty()) {
                    ContactManager.addContact(this, phone);
                    contactEditText.setText("");
                    refreshContactsList();
                    Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (testButton != null) {
            testButton.setOnClickListener(v -> {
                EmergencyManager.triggerEmergency(this);
                Toast.makeText(this, "Manual Trigger Activated", Toast.LENGTH_SHORT).show();
            });
        }

        refreshContactsList();

        if (checkPermissions()) {
            startService();
        } else {
            requestPermissions();
        }

        updateSafeZoneStatusDisplay();
    }

    private void updateVoskStatusUI(int status) {
        if (modelStatusTextView == null || modelProgressBar == null) return;
        
        switch (status) {
            case BackgroundService.STATUS_LOADING:
                modelStatusTextView.setText("Voice Module: Loading...");
                modelProgressBar.setVisibility(View.VISIBLE);
                break;
            case BackgroundService.STATUS_READY:
                modelStatusTextView.setText("Voice Module: Listening ('Help me')");
                modelProgressBar.setVisibility(View.GONE);
                break;
            case BackgroundService.STATUS_ERROR:
                modelStatusTextView.setText("Voice Module: Error loading");
                modelProgressBar.setVisibility(View.GONE);
                break;
        }
    }

    private void refreshContactsList() {
        if (contactsListTextView != null) {
            String contacts = ContactManager.getContactsDisplayString(this);
            contactsListTextView.setText(contacts.isEmpty() ? "No contacts added." : contacts);
        }
    }

    private void updateSafeZoneStatusDisplay() {
        TextView safeZoneStatusText = findViewById(R.id.safeZoneStatusText);
        if (safeZoneStatusText == null) return;

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
        refreshContactsList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(BackgroundService.ACTION_VOSK_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(statusReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(statusReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(statusReceiver);
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
            requestBackgroundLocationPermission();
        }
    }

    private void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                Toast.makeText(this, "Permissions required", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE + 1) {
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
        if (statusTextView != null) {
            statusTextView.setText(R.string.emergency_status_idle);
        }
    }
}

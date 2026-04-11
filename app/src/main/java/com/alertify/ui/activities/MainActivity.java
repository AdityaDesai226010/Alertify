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
import android.widget.LinearLayout;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private TextView statusTextView;
    private TextView contactsListTextView;
    private EditText contactEditText;
    private TextView modelStatusTextView;
    private ProgressBar modelProgressBar;
    private View voiceTriggerDot;
    private LinearLayout contactsContainer;

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

        // Hidden Get Started button (kept for onboarding flow compatibility)
        MaterialButton btnGetStarted = findViewById(R.id.btn_get_started);
        if (btnGetStarted != null) {
            btnGetStarted.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
                startActivity(intent);
            });
        }

        // Bind Views
        statusTextView = findViewById(R.id.statusTextView);
        contactsListTextView = findViewById(R.id.contactsListTextView);
        contactEditText = findViewById(R.id.contactEditText);
        modelStatusTextView = findViewById(R.id.modelStatusTextView);
        modelProgressBar = findViewById(R.id.modelProgressBar);
        voiceTriggerDot = findViewById(R.id.voiceTriggerDot);
        contactsContainer = findViewById(R.id.contactsContainer);

        Button addContactButton = findViewById(R.id.addContactButton);
        Button testButton = findViewById(R.id.testTriggerButton);
        Button btnSafeZone = findViewById(R.id.btnSafeZone);
        Button btnOverlayPermission = findViewById(R.id.btnOverlayPermission);

        // Controls: Enable Background Calls (Overlay)
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

        // Controls: Safe Zone Settings
        if (btnSafeZone != null) {
            btnSafeZone.setOnClickListener(v -> {
                Intent intent = new Intent(this, SafeZoneActivity.class);
                startActivity(intent);
            });
        }

        // Controls: Add Contact
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

        // Test Emergency Trigger
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

    /**
     * Updates the Vosk voice recognition status across the trigger card.
     */
    private void updateVoskStatusUI(int status) {
        if (modelStatusTextView == null || modelProgressBar == null) return;

        switch (status) {
            case BackgroundService.STATUS_LOADING:
                modelStatusTextView.setText("Loading…");
                modelProgressBar.setVisibility(View.VISIBLE);
                if (voiceTriggerDot != null) {
                    voiceTriggerDot.setBackgroundResource(R.drawable.bg_status_dot_ready);
                }
                break;
            case BackgroundService.STATUS_READY:
                modelStatusTextView.setText("Ready — Listening for 'Help me'");
                modelProgressBar.setVisibility(View.GONE);
                if (voiceTriggerDot != null) {
                    voiceTriggerDot.setBackgroundResource(R.drawable.bg_status_dot_active);
                }
                break;
            case BackgroundService.STATUS_ERROR:
                modelStatusTextView.setText("Error loading model");
                modelProgressBar.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Rebuilds the contacts list section with individual styled cards.
     * Shows each contact with a phone icon and highlights the primary contact.
     */
    private void refreshContactsList() {
        if (contactsContainer == null) return;

        // Remove all views except the hidden contactsListTextView
        List<String> contacts = ContactManager.getContacts(this);
        String primary = ContactManager.getPrimaryContact(this);

        // Clear previous dynamic cards (keep the first child which is contactsListTextView)
        if (contactsContainer.getChildCount() > 1) {
            contactsContainer.removeViews(1, contactsContainer.getChildCount() - 1);
        }

        if (contacts.isEmpty()) {
            if (contactsListTextView != null) {
                contactsListTextView.setVisibility(View.VISIBLE);
                contactsListTextView.setText("No emergency contacts added yet.");
            }
        } else {
            if (contactsListTextView != null) {
                contactsListTextView.setVisibility(View.GONE);
            }
            for (String phone : contacts) {
                boolean isPrimary = phone.equals(primary);
                contactsContainer.addView(buildContactCard(phone, isPrimary));
            }
        }
    }

    /**
     * Builds a single contact card view with icon, phone number, and primary label.
     */
    private View buildContactCard(String phone, boolean isPrimary) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(android.view.Gravity.CENTER_VERTICAL);
        card.setBackgroundResource(R.drawable.bg_contact_card);
        int dp14 = dpToPx(14);
        card.setPadding(dpToPx(16), dp14, dpToPx(16), dp14);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dpToPx(8);
        card.setLayoutParams(lp);

        // Phone icon
        TextView icon = new TextView(this);
        icon.setText("📞");
        icon.setTextSize(18);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        iconLp.rightMargin = dpToPx(12);
        icon.setLayoutParams(iconLp);
        card.addView(icon);

        // Phone number
        TextView num = new TextView(this);
        num.setText(phone);
        num.setTextColor(ContextCompat.getColor(this, R.color.on_surface));
        num.setTextSize(15);
        LinearLayout.LayoutParams numLp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        num.setLayoutParams(numLp);
        card.addView(num);

        // Primary badge
        if (isPrimary) {
            TextView badge = new TextView(this);
            badge.setText("★ PRIMARY");
            badge.setTextColor(ContextCompat.getColor(this, R.color.primary));
            badge.setTextSize(11);
            badge.setTypeface(null, android.graphics.Typeface.BOLD);
            card.addView(badge);
        }

        return card;
    }

    /**
     * Updates the Safe Zone status indicator.
     */
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

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}

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
import com.alertify.triggers.VolumeButtonService;
import android.provider.Settings;
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
        Button btnEnableAccessibility = findViewById(R.id.btnEnableAccessibility);

        refreshContactsList();

        btnEnableAccessibility.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Scroll down to 'Downloaded apps' or 'Installed services' to find 'Alertify Volume Trigger' and turn it ON", Toast.LENGTH_LONG).show();
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

        updateAccessibilityStatus();
    }

    private void updateAccessibilityStatus() {
        Button btnEnableAccessibility = findViewById(R.id.btnEnableAccessibility);
        if (isAccessibilityServiceEnabled(this, VolumeButtonService.class)) {
            btnEnableAccessibility.setText("Volume Trigger: ENABLED ✅");
            btnEnableAccessibility.setEnabled(false); // Already enabled
            btnEnableAccessibility.setAlpha(0.6f);
        } else {
            btnEnableAccessibility.setText("Enable Volume Button Trigger");
            btnEnableAccessibility.setEnabled(true);
            btnEnableAccessibility.setAlpha(1.0f);
        }
    }

    private boolean isAccessibilityServiceEnabled(Context context, Class<?> service) {
        String prefString = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (prefString != null) {
            return prefString.contains(context.getPackageName() + "/" + service.getName());
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAccessibilityStatus(); // Re-check when returning from settings
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

        return true;
    }

    private void requestPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.RECORD_AUDIO
            };
        }
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService();
            } else {
                Toast.makeText(this, "Permissions are required for the app to function.", Toast.LENGTH_LONG).show();
            }
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

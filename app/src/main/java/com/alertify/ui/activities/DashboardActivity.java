package com.alertify.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.alertify.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class DashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        ExtendedFloatingActionButton btnSos = findViewById(R.id.fab_sos);
        btnSos.setOnClickListener(v -> {
            // SOS action
        });

        findViewById(R.id.cv_guide).setOnClickListener(v -> {
            Intent intent = new Intent(this, LegalGuideActivity.class);
            startActivity(intent);
        });
    }
}

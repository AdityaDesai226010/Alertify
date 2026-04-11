package com.alertify.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.alertify.R;

public class TransitionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition);
        
        // Simulating transition delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(TransitionActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}

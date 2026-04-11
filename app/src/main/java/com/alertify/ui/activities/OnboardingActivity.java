package com.alertify.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.alertify.R;
import com.google.android.material.button.MaterialButton;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        MaterialButton btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

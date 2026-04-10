package com.alertify.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.alertify.R;
import com.alertify.stealth.FakeCalculatorActivity;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> {
            // After onboarding, go to the Fake Calculator (Stealth Entry)
            Intent intent = new Intent(OnboardingActivity.this, FakeCalculatorActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

package com.alertify.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.alertify.R;
import com.google.android.material.button.MaterialButton;

public class SetupCompleteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_complete);
        
        MaterialButton btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransitionActivity.class);
            startActivity(intent);
        });
    }
}

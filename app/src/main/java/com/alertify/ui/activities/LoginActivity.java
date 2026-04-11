package com.alertify.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.alertify.R;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, GooglePermissionsActivity.class);
            startActivity(intent);
        });
    }
}

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
<!-- slide -->
package com.alertify.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.alertify.R;
import com.google.android.material.button.MaterialButton;

public class GooglePermissionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_access);
        
        MaterialButton btnAllow = findViewById(R.id.btn_allow);
        btnAllow.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChooseAccountActivity.class);
            startActivity(intent);
        });
    }
}
<!-- slide -->
package com.alertify.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.alertify.R;

public class ChooseAccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account);
        
        findViewById(R.id.cv_account_1).setOnClickListener(v -> {
            Intent intent = new Intent(this, SetupCompleteActivity.class);
            startActivity(intent);
        });
    }
}

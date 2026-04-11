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

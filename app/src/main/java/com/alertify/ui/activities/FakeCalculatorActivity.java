package com.alertify.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.alertify.R;

public class FakeCalculatorActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private StringBuilder inputBuffer = new StringBuilder();
    private static final String SECRET_CODE = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_calculator);

        tvDisplay = findViewById(R.id.tvDisplay);
        
        // Note: In a real implementation, we would set listeners for all buttons.
        // For this demo, we'll implement a general click handler for numbers.
    }

    public void onNumberClick(View view) {
        Button b = (Button) view;
        String text = b.getText().toString();
        
        inputBuffer.append(text);
        tvDisplay.setText(inputBuffer.toString());

        // Check for secret code
        if (inputBuffer.toString().equals(SECRET_CODE)) {
            unlockApp();
        }
    }

    public void onClearClick(View view) {
        inputBuffer.setLength(0);
        tvDisplay.setText("0");
    }

    private void unlockApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

package com.alertify.stealth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.alertify.R;
import com.alertify.ui.activities.MainActivity;

public class FakeCalculatorActivity extends AppCompatActivity {

    private TextView resultDisplay;
    private StringBuilder inputBuffer = new StringBuilder();
    private static final String SECRET_PIN = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_calculator);

        resultDisplay = findViewById(R.id.calcDisplay);

        // Bind numeric buttons (mapping IDs in XML)
        int[] buttonIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, 
                           R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(v -> {
                Button b = (Button) v;
                inputBuffer.append(b.getText());
                resultDisplay.setText(inputBuffer.toString());
                checkSecretCode();
            });
        }

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            inputBuffer.setLength(0);
            resultDisplay.setText("");
        });
    }

    private void checkSecretCode() {
        if (inputBuffer.toString().equals(SECRET_PIN)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

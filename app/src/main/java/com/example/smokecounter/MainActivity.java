package com.example.smokecounter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextView countText;
    private SmokeManager smokeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countText = findViewById(R.id.text_count);
        Button addBtn = findViewById(R.id.btn_add);
        Button monthlyLogBtn = findViewById(R.id.btn_monthly_log);
        Button logoutBtn = findViewById(R.id.btn_logout);

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "default_user";

        smokeManager = SmokeManager.getInstance(this, userId);

        updateCountDisplay();

        // Add cigarette
        addBtn.setOnClickListener(v -> {
            smokeManager.increment();
            updateCountDisplay();
        });

        // Monthly log (open new activity)
        monthlyLogBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MonthlyLogActivity.class);
            startActivity(intent);
        });

        // Logout
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void updateCountDisplay() {
        int count = smokeManager.getCount();
        countText.setText("Today's cigarettes: " + count);
    }
}

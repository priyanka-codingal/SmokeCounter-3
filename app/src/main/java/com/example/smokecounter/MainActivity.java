package com.example.smokecounter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : "default_user";

        smokeManager = SmokeManager.getInstance(this, userId);

        updateCountDisplay();

        addBtn.setOnClickListener(v -> {
            smokeManager.increment();
            updateCountDisplay();
        });

        monthlyLogBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MonthlyLogActivity.class));
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });
    }

    private void updateCountDisplay() {
        int count = smokeManager.getCount();
        countText.setText("Today's cigarettes: " + count);
    }
}
package com.example.smokecounter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MonthlyLogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LogAdapter logAdapter;
    private SmokeManager smokeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_log);

        recyclerView = findViewById(R.id.recycler_monthly_log);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : "default_user";

        smokeManager = SmokeManager.getInstance(this, userId);

        List<SmokeManager.DailyLog> logs = smokeManager.getMonthlyLogs();

        logAdapter = new LogAdapter(logs);
        recyclerView.setAdapter(logAdapter);
    }
}
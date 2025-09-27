package com.example.smokecounter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        String userId = smokeManager != null ? smokeManager.getUserId() : "default_user";
        smokeManager = SmokeManager.getInstance(this, userId);

        // Fetch monthly logs
        List<SmokeManager.DailyLog> logs = smokeManager.getMonthlyLogs();

        logAdapter = new LogAdapter(logs);
        recyclerView.setAdapter(logAdapter);
    }
}

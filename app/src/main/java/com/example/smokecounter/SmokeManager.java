package com.example.smokecounter;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class SmokeManager {

    private static SmokeManager instance;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String userId;

    private static final String PREFS_NAME = "SmokePrefs";
    private static final String KEY_COUNT = "dailyCount";
    private static final String KEY_LOG = "dailyLog";

    private SmokeManager(Context context, String userId) {
        this.userId = userId;
        prefs = context.getSharedPreferences(PREFS_NAME + "_" + userId, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Singleton
    public static synchronized SmokeManager getInstance(Context context, String userId) {
        if (instance == null || !userId.equals(instance.userId)) {
            instance = new SmokeManager(context.getApplicationContext(), userId);
        }
        return instance;
    }

    // Increment daily count
    public void increment() {
        int count = prefs.getInt(KEY_COUNT, 0);
        count++;
        editor.putInt(KEY_COUNT, count);
        editor.apply();
    }

    // Get current count
    public int getCount() {
        return prefs.getInt(KEY_COUNT, 0);
    }

    // Reset daily count
    public void resetDailyCount() {
        editor.putInt(KEY_COUNT, 0);
        editor.apply();
    }

    // Log daily count for a date
    public void logDaily(@NonNull String date, int count) {
        // Save per-date count for monthly log
        prefs.edit().putInt("log_" + date, count).apply();
    }

    // Convert Map to String
    private String dailyLogToString(Map<String, Integer> log) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : log.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }

    // Get daily log as Map
    public Map<String, Integer> getDailyLog() {
        Map<String, Integer> log = new HashMap<>();
        String logString = prefs.getString(KEY_LOG, "");
        if (!logString.isEmpty()) {
            String[] entries = logString.split(";");
            for (String entry : entries) {
                if (entry.contains(":")) {
                    String[] parts = entry.split(":");
                    log.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        }
        return log;
    }
    public static class DailyLog {
        public String date;
        public int count;

        public DailyLog(String date, int count) {
            this.date = date;
            this.count = count;
        }
    }
    public List<DailyLog> getMonthlyLogs() {
        Map<String, ?> allPrefs = prefs.getAll();
        List<DailyLog> logs = new ArrayList<>();

        for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
            if (entry.getKey().startsWith("log_")) {
                String date = entry.getKey().replace("log_", "");
                int count = (int) entry.getValue();
                logs.add(new DailyLog(date, count));
            }
        }
        return logs;
    }
    // Get the current userId
    public String getUserId() {
        return userId;
    }
}
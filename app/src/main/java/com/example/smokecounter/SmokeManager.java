package com.example.smokecounter;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SmokeManager {

    private static SmokeManager instance;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String userId;

    // Dynamic prefs name per user
    private String PREFS_NAME;

    private SmokeManager(Context context, String userId) {
        this.userId = userId;
        this.PREFS_NAME = "SmokePrefs_" + userId;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static synchronized SmokeManager getInstance(Context context, String userId) {
        if (instance == null || !instance.userId.equals(userId)) {
            instance = new SmokeManager(context.getApplicationContext(), userId);
        }
        return instance;
    }

    // Increment today's count
    public void increment() {
        String todayKey = getTodayKey();
        int count = prefs.getInt(todayKey, 0);
        count++;
        editor.putInt(todayKey, count);
        editor.apply();
    }

    // Get today's count
    public int getCount() {
        return prefs.getInt(getTodayKey(), 0);
    }

    // Reset today
    public void resetDailyCount() {
        editor.putInt(getTodayKey(), 0);
        editor.apply();
    }

    // Monthly logs (all log_ keys)
    public List<DailyLog> getMonthlyLogs() {
        List<DailyLog> logs = new ArrayList<>();
        Map<String, ?> all = prefs.getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (entry.getKey().startsWith("log_")) {
                String date = entry.getKey().replace("log_", "");
                int count = (int) entry.getValue();
                logs.add(new DailyLog(date, count));
            }
        }
        return logs;
    }

    private String getTodayKey() {
        LocalDate today = LocalDate.now();
        return "log_" + today.toString();
    }

    public String getUserId() {
        return userId;
    }

    // Data class
    public static class DailyLog {
        public String date;
        public int count;
        public DailyLog(String date, int count) {
            this.date = date;
            this.count = count;
        }
    }
}
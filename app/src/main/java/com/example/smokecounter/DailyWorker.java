package com.example.smokecounter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyWorker extends Worker {

    public DailyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.e("DailyWorker", "No user logged in, cannot log daily.");
            return Result.failure();
        }

        String userId = user.getUid();
        SmokeManager smokeManager = SmokeManager.getInstance(getApplicationContext(), userId);

        // Get today’s date (optional, for logging)
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Get today's count
        int todayCount = smokeManager.getCount();

        // We don’t need logDaily(); the daily count is already stored under "log_YYYY-MM-DD"
        // Just ensure it’s persisted (increment already saves it in SharedPreferences)
        Log.i("DailyWorker", "User: " + userId + ", Date: " + today + ", Count: " + todayCount);

        return Result.success();
    }
}
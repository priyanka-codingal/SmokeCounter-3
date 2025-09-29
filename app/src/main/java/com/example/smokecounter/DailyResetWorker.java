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

public class DailyResetWorker extends Worker {

    public DailyResetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.e("DailyResetWorker", "No user logged in, cannot reset count.");
            return Result.failure();
        }

        String userId = user.getUid();
        SmokeManager smokeManager = SmokeManager.getInstance(getApplicationContext(), userId);

        // Save today's count before resetting (already stored in log_key)
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int todayCount = smokeManager.getCount();
        Log.i("DailyResetWorker", "User: " + userId + ", Date: " + today + ", Count saved: " + todayCount);

        // Reset today's count for a fresh start tomorrow
        smokeManager.resetDailyCount();

        return Result.success();
    }
}

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

        // Example: get today’s date
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // For now, assume we tracked a count somewhere (can be passed via Data or SharedPrefs)
        int count = getInputData().getInt("dailyCount", 0);

        smokeManager.logDaily(today, count);

        return Result.success();
    }
}

package com.example.smokecounter;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyResetWorker extends Worker {

    private static final String TAG = "DailyResetWorker";

    public DailyResetWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get userId passed via Data
        String userId = getInputData().getString("userId");
        if (userId == null) {
            Log.e(TAG, "UserId not provided");
            return Result.failure();
        }

        SmokeManager smokeManager = SmokeManager.getInstance(getApplicationContext(), userId);

        int count = smokeManager.getCount();

        // Send SMS based on range
        sendSmsBasedOnRange(count);

        // Log today count
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        smokeManager.logDaily(today, count);

        // Reset daily count
        smokeManager.resetDailyCount();

        return Result.success();
    }

    private void sendSmsBasedOnRange(int count) {
        // Replace with user's phone number
        String phoneNumber = "USER_PHONE_NUMBER";

        String message;
        if (count >= 1 && count <= 5) {
            message = "You smoked " + count + " cigarettes today. Try to reduce.";
        } else if (count >= 6 && count <= 10) {
            message = "You smoked " + count + " cigarettes today. Consider stopping soon!";
        } else if (count > 10) {
            message = "High alert! You smoked " + count + " cigarettes today.";
        } else {
            message = "No cigarettes today. Great job!";
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d(TAG, "SMS sent to " + phoneNumber + ": " + message);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS", e);
        }
    }
}
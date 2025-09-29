package com.example.smokecounter;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is already logged in
            SmokeManager.getInstance(getApplicationContext(), user.getUid());
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // No user, go to login
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
package com.example.smokecounter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText phoneInput, otpInput;
    private Button sendOtpButton, verifyOtpButton;
    private LinearLayout otpLayout;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Already logged in
            SmokeManager.getInstance(getApplicationContext(), user.getUid());
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        phoneInput = findViewById(R.id.editTextPhone);
        otpInput = findViewById(R.id.editTextOtp);
        sendOtpButton = findViewById(R.id.buttonSendOtp);
        verifyOtpButton = findViewById(R.id.buttonVerifyOtp);
        otpLayout = findViewById(R.id.layoutOtp);
        SignInButton googleLoginBtn = findViewById(R.id.googleSignInButton);

        // Google Sign-In setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleLoginBtn.setOnClickListener(v -> {
            mGoogleSignInClient.signOut(); // ensures user can pick a different account
            googleSignInLauncher.launch(mGoogleSignInClient.getSignInIntent());
        });

        // Send OTP
        sendOtpButton.setOnClickListener(v -> {
            String phone = phoneInput.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            otpLayout.setVisibility(LinearLayout.VISIBLE);
            sendVerificationCode(phone);
        });

        // Verify OTP
        verifyOtpButton.setOnClickListener(v -> {
            String code = otpInput.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyCode(code);
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(LoginActivity.this, "OTP failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                verificationId = verId;
                                Toast.makeText(LoginActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String code) {
        if (verificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
        } else {
            Toast.makeText(this, "Please request OTP first", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) openMainActivity(user.getUid());
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openMainActivity(String userId) {
        SmokeManager.getInstance(getApplicationContext(), userId);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult();
                    if (account != null) {
                        String idToken = account.getIdToken();
                        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                                .addOnCompleteListener(this, task1 -> {
                                    if (task1.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) openMainActivity(user.getUid());
                                    } else {
                                        Toast.makeText(this, "Google login failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                }
            });
}

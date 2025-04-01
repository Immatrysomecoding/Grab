package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneInputActivity extends AppCompatActivity {

    private EditText etPhoneNumber;
    private MaterialButton btnContinue;
    private ImageButton btnBack;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_input);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo các view
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);

        // Ban đầu nút Next bị mờ
        btnContinue.setEnabled(false);
        btnContinue.setAlpha(0.5f);

        // Thiết lập listeners
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        btnContinue.setOnClickListener(v -> {
            // Hiển thị loading và vô hiệu hóa nút
            btnContinue.setEnabled(false);
            btnContinue.setText("Sending code...");

            // Gửi mã xác thực đến số điện thoại
            String phoneNumber = "+84" + etPhoneNumber.getText().toString().trim();
            sendVerificationCode(phoneNumber);
        });

        // Theo dõi thay đổi trong trường nhập số điện thoại
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Kích hoạt nút Next khi có số điện thoại
                boolean hasText = s.length() > 0;
                btnContinue.setEnabled(hasText);
                btnContinue.setAlpha(hasText ? 1.0f : 0.5f);
                btnContinue.setText("Next");
            }
        });

        // Khởi tạo callbacks cho việc xác thực số điện thoại
        setupPhoneAuthCallbacks();
    }

    private void setupPhoneAuthCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // Tự động xác thực khi SMS được phát hiện tự động
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // Xác thực thất bại
                btnContinue.setEnabled(true);
                btnContinue.setText("Next");
                Toast.makeText(PhoneInputActivity.this,
                        "Verification failed: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Mã xác thực đã được gửi, chuyển tới màn hình nhập OTP
                btnContinue.setEnabled(true);
                btnContinue.setText("Next");

                Intent intent = new Intent(PhoneInputActivity.this, SmsVerificationActivity.class);
                intent.putExtra("PHONE_NUMBER", "+84 " + etPhoneNumber.getText().toString().trim());
                intent.putExtra("VERIFICATION_ID", verificationId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        };
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công, chuyển đến màn hình nhập tên
                        Intent intent = new Intent(PhoneInputActivity.this, NameInputActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        // Đăng nhập thất bại
                        Toast.makeText(PhoneInputActivity.this,
                                "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
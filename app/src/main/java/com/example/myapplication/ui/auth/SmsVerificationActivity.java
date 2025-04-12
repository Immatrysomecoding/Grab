package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class SmsVerificationActivity extends AppCompatActivity {

    private EditText etOtpCode;
    private TextView tvInstructions, tvErrorMessage;
    private ImageButton btnBack;
    private String phoneNumber;
    private String verificationId;
    private FirebaseAuth mAuth;
    private String authMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);


        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Lấy dữ liệu từ intent
        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        verificationId = getIntent().getStringExtra("VERIFICATION_ID");
        authMode = getIntent().getStringExtra("AUTH_MODE");

        // Khởi tạo các view
        etOtpCode = findViewById(R.id.etOtpCode);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnBack = findViewById(R.id.btnBack);

        if (authMode == null) {
            authMode = "SIGNUP"; // Mặc định là đăng ký nếu không có
        }

        // Cập nhật mô tả với số điện thoại
        tvInstructions.setText("Enter the 6-digit code sent to " + phoneNumber + " by SMS.");

        // Thiết lập listeners
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        etOtpCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ẩn thông báo lỗi khi người dùng thay đổi mã
                if (tvErrorMessage.getVisibility() == View.VISIBLE) {
                    tvErrorMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nếu đã nhập đủ 6 số, thực hiện xác thực
                if (s.length() == 6) {
                    verifyCode(s.toString());
                }
            }
        });
    }

    private void verifyCode(String code) {
        if (verificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Xác thực thành công
                        if (authMode.equals("LOGIN")) {
                            // Nếu là đăng nhập, chuyển đến màn hình chính
                            Intent intent = new Intent(SmsVerificationActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Nếu là đăng ký, chuyển đến màn hình nhập tên
                            Intent intent = new Intent(SmsVerificationActivity.this, NameInputActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    } else {
                        // Đăng nhập thất bại, hiển thị thông báo lỗi
                        tvErrorMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void redirectToMainScreen() {
        // Chuyển đến màn hình chính
        Intent intent = new Intent(SmsVerificationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
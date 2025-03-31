package com.example.myapplication.ui.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import java.util.Locale;

public class SmsVerificationActivity extends AppCompatActivity {

    private EditText etOtpCode;
    private TextView tvInstructions, tvResendCode;
    private ImageButton btnBack;
    private CountDownTimer countDownTimer;
    private String phoneNumber;
    private final long COUNT_DOWN_TIME = 30000; // 30 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);

        // Lấy số điện thoại từ intent
        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");

        // Khởi tạo các view
        etOtpCode = findViewById(R.id.etOtpCode);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvResendCode = findViewById(R.id.tvResendCode);
        btnBack = findViewById(R.id.btnBack);

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
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Nếu đã nhập đủ 6 số, thực hiện xác thực
                if (s.length() == 6) {
                    verifyCode(s.toString());
                }
            }
        });

        // Bắt đầu đếm ngược
        startCountdownTimer();
    }

    private void startCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(COUNT_DOWN_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvResendCode.setText(String.format(Locale.getDefault(),
                        "Get new code or send by WhatsApp in %02d:%02d", seconds / 60, seconds % 60));
            }

            @Override
            public void onFinish() {
                tvResendCode.setText("Get new code or send by WhatsApp");
                tvResendCode.setTextColor(getResources().getColor(android.R.color.black));
            }
        }.start();
    }

    private void verifyCode(String code) {
        // TODO: Implement OTP verification logic here
        // This is where you would typically make a call to Firebase
        // or your backend to verify the OTP

        // For now, we'll just simulate a success
        // In the future, this will integrate with Firebase phone auth
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
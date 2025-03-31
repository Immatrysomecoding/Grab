package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;

public class PhoneInputActivity extends AppCompatActivity {

    private EditText etPhoneNumber;
    private MaterialButton btnContinue;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_input);

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
            String phoneNumber = etPhoneNumber.getText().toString().trim();

            Intent intent = new Intent(PhoneInputActivity.this, SmsVerificationActivity.class);
            intent.putExtra("PHONE_NUMBER", "+84 " + phoneNumber);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
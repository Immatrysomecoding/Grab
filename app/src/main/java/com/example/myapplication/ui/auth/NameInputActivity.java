package com.example.myapplication.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;

public class NameInputActivity extends AppCompatActivity {

    private EditText etName;
    private MaterialButton btnNext;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_input);

        // Khởi tạo views
        etName = findViewById(R.id.etName);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        // Ban đầu nút Next bị mờ
        btnNext.setEnabled(false);
        btnNext.setAlpha(0.5f);

        // Thiết lập listeners
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        btnNext.setOnClickListener(v -> {
            // TODO: Xử lý lưu tên người dùng và chuyển đến màn hình tiếp theo
            // Ví dụ:
            // Intent intent = new Intent(NameInputActivity.this, NextActivity.class);
            // startActivity(intent);
            // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            // Hoặc có thể kết thúc quy trình đăng ký và chuyển đến màn hình chính
            // finishAffinity();
            // Intent intent = new Intent(NameInputActivity.this, MainActivity.class);
            // startActivity(intent);
        });

        // Theo dõi thay đổi trong trường nhập tên
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Kích hoạt nút Next khi có tên
                boolean hasText = s.length() > 0;
                btnNext.setEnabled(hasText);
                btnNext.setAlpha(hasText ? 1.0f : 0.5f);

                // Đổi màu nút Next thành xanh lá đậm khi có tên
                if (hasText) {
                    btnNext.setBackgroundTintList(getColorStateList(R.color.signin_background));
                } else {
                    btnNext.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
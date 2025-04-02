package com.example.myapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NameInputActivity extends AppCompatActivity {

    private static final String TAG = "NameInputActivity";
    private EditText etName;
    private MaterialButton btnNext;
    private ImageButton btnBack;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_input);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://grab-741f2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        // Kiểm tra kết nối database
        try {
            FirebaseDatabase.getInstance("https://grab-741f2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference(".info/connected")
                    .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                            boolean connected = snapshot.getValue(Boolean.class);
                            Log.d(TAG, "Firebase Database connected: " + connected);
                        }

                        @Override
                        public void onCancelled(com.google.firebase.database.DatabaseError error) {
                            Log.e(TAG, "Firebase Database connection error: " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error checking database connection: " + e.getMessage());
        }

        // Khởi tạo views
        etName = findViewById(R.id.etName);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        // Ban đầu nút Next bị mờ
        btnNext.setEnabled(false);
        btnNext.setAlpha(0.5f);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Code xử lý khi nhấn nút back
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Kích hoạt sự kiện back
            getOnBackPressedDispatcher().onBackPressed();
        });

        btnNext.setOnClickListener(v -> {
            // Hiển thị trạng thái loading
            btnNext.setEnabled(false);
            btnNext.setText("Saving...");

            // Lưu thông tin người dùng
            saveUserProfile();
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
            }
        });
    }

    private void saveUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, "Saving user profile, current user: " + (user != null ? user.getUid() : "null"));

        if (user != null) {
            String userId = user.getUid();
            String name = etName.getText().toString().trim();
            String phone = user.getPhoneNumber();

            Log.d(TAG, "User data - Name: " + name + ", Phone: " + phone);

            // Tạo object người dùng
            Map<String, Object> userValues = new HashMap<>();
            userValues.put("name", name);
            userValues.put("phone", phone);
            userValues.put("createdAt", System.currentTimeMillis());

            // Lưu vào Firebase Database
            mDatabase.child("users").child(userId).setValue(userValues)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User data saved successfully");

                        // Chuyển đến màn hình chính
                        Intent intent = new Intent(NameInputActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save user data: " + e.getMessage());
                        btnNext.setEnabled(true);
                        btnNext.setText("Next");
                        Toast.makeText(NameInputActivity.this,
                                "Failed to save user data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Không có người dùng đăng nhập, quay lại màn hình đăng nhập
            Log.e(TAG, "No authenticated user found");
            Toast.makeText(this, "Authentication error. Please try again.", Toast.LENGTH_SHORT).show();
            btnNext.setEnabled(true);
            btnNext.setText("Next");
            navigateToLogin();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, PhoneInputActivity.class);
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
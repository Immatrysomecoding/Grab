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
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneInputActivity extends AppCompatActivity {

    private EditText etPhoneNumber;
    private MaterialButton btnContinue;
    private ImageButton btnBack;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String authMode;
    private TextView tvPhoneError;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_input);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://grab-741f2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        // Lấy chế độ xác thực từ intent
        authMode = getIntent().getStringExtra("AUTH_MODE");
        if (authMode == null) {
            authMode = "SIGNUP"; // Mặc định là đăng ký nếu không có
        }

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);
        tvPhoneError = findViewById(R.id.tvPhoneError);

        TextView tvTitle = findViewById(R.id.tvTitle);
        if (authMode.equals("LOGIN")) {
            tvTitle.setText("Log In");
        } else {
            tvTitle.setText("Sign Up");
        }

        // Ban đầu nút Next bị mờ
        btnContinue.setEnabled(false);
        btnContinue.setAlpha(0.5f);

        // Thiết lập listeners
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        btnContinue.setOnClickListener(v -> {
            // Ẩn thông báo lỗi nếu có
            tvPhoneError.setVisibility(View.GONE);

            // Hiển thị loading và vô hiệu hóa nút
            btnContinue.setEnabled(false);
            btnContinue.setText("Processing...");

            // Lấy số điện thoại
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String fullPhoneNumber = "+84" + phoneNumber;

            checkPhoneNumberExists(fullPhoneNumber);
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
                intent.putExtra("AUTH_MODE", authMode); // Truyền chế độ xác thực sang màn hình OTP
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        };
    }

    private void checkPhoneNumberExists(String phoneNumber) {
        // Kiểm tra số điện thoại trong database
        mDatabase.child("users").orderByChild("phone").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Số điện thoại đã tồn tại
                            if (authMode.equals("SIGNUP")) {
                                // Nếu đang đăng ký, hiển thị lỗi
                                btnContinue.setEnabled(true);
                                btnContinue.setText("Next");
                                tvPhoneError.setText("This phone number is already registered. Please log in instead.");
                                tvPhoneError.setVisibility(View.VISIBLE);
                            } else {
                                // Nếu đang đăng nhập, tiếp tục gửi OTP
                                sendVerificationCode(phoneNumber);
                            }
                        } else {
                            // Số điện thoại chưa tồn tại
                            if (authMode.equals("LOGIN")) {
                                // Nếu đang đăng nhập, hiển thị lỗi
                                btnContinue.setEnabled(true);
                                btnContinue.setText("Next");
                                tvPhoneError.setText("This phone number is not registered. Please sign up first.");
                                tvPhoneError.setVisibility(View.VISIBLE);
                            } else {
                                // Nếu đang đăng ký, tiếp tục gửi OTP
                                sendVerificationCode(phoneNumber);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xảy ra lỗi khi truy vấn database
                        btnContinue.setEnabled(true);
                        btnContinue.setText("Next");
                        Toast.makeText(PhoneInputActivity.this,
                                "Database error: " + databaseError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
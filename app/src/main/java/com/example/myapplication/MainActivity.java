package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.example.myapplication.ui.auth.PhoneInputActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo các view
        Button btnSignUp = findViewById(R.id.btnSignUp);
        Button btnLogIn = findViewById(R.id.btnLogIn);
        ImageView ivArrowRight = findViewById(R.id.ivArrowRight);

        // Thiết lập listeners
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PhoneInputActivity.class);
            intent.putExtra("AUTH_MODE", "SIGNUP"); // Đánh dấu là đăng ký
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PhoneInputActivity.class);
            intent.putExtra("AUTH_MODE", "LOGIN"); // Đánh dấu là đăng nhập
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        if (ivArrowRight != null) {
            ivArrowRight.setOnClickListener(v -> {
                navigateToPhoneInput();
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Kiểm tra nếu người dùng đã đăng nhập
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Người dùng đã đăng nhập, chuyển đến màn hình chính của ứng dụng
            // Ví dụ: startActivity(new Intent(this, HomeActivity.class));
            // Trong trường hợp này, ta đã ở màn hình chính rồi nên có thể ẩn các nút đăng nhập/đăng ký
            //hideLoginButtons();
        }
    }

    private void navigateToPhoneInput() {
        Intent intent = new Intent(MainActivity.this, PhoneInputActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

//    private void hideLoginButtons() {
//        // Ẩn các nút đăng nhập/đăng ký
//        Button btnSignUp = findViewById(R.id.btnSignUp);
//        Button btnLogIn = findViewById(R.id.btnLogIn);
//
//        if (btnSignUp != null) btnSignUp.setVisibility(View.GONE);
//        if (btnLogIn != null) btnLogIn.setVisibility(View.GONE);
//    }
}
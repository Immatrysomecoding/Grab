package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private CardView cardSearch;
    private LinearLayout layoutBike, layoutCar;
    private Button btnSignUp, btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các view
        cardSearch = findViewById(R.id.cardSearch);
        layoutBike = findViewById(R.id.layoutBike);
        layoutCar = findViewById(R.id.layoutCar);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogIn = findViewById(R.id.btnLogIn);

        // Set listeners
        cardSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sẽ điều hướng đến trang tìm kiếm sau khi hoàn thành auth
                // TODO: Implement search functionality
            }
        });

        layoutBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement bike booking functionality
            }
        });

        layoutCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement car booking functionality
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to sign up screen
                // Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                // startActivity(intent);
            }
        });

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Navigate to login screen
                // Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                // startActivity(intent);
            }
        });
    }
}
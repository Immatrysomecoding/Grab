package com.example.myapplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.myapplication.R;
import com.example.myapplication.ui.subscription.SubscriptionsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfileActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton btnBack;
    private ImageButton btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Khởi tạo views
        btnBack = findViewById(R.id.btnBack);
        btnSettings = findViewById(R.id.btnSettings);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Thiết lập ViewPager và TabLayout
        ProfilePagerAdapter pagerAdapter = new ProfilePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Profile");
                    break;
                case 1:
                    tab.setText("Activity");
                    break;
            }
        }).attach();

        // Xử lý sự kiện click
        btnBack.setOnClickListener(v -> finish());

        btnSettings.setOnClickListener(v -> {
            // TODO: Mở màn hình cài đặt
        });

        // Set click listener for Member Benefits card
        CardView cardMemberBenefits = findViewById(R.id.cardMemberBenefits);
        cardMemberBenefits.setOnClickListener(v -> {
            // Open SubscriptionsActivity
            Intent intent = new Intent(ProfileActivity.this, SubscriptionsActivity.class);
            startActivity(intent);
            // Apply slide from right to left animation
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    public void finish() {
        super.finish();
        // Animation khi đóng activity
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
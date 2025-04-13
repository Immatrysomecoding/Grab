package com.example.myapplication.ui.subscription;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SubscriptionsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SubscriptionPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);

        initViews();
        setupViewPager();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void setupViewPager() {
        pagerAdapter = new SubscriptionPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.browse);
                    break;
                case 1:
                    tab.setText(R.string.my_subscriptions);
                    break;
            }
        }).attach();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    public void switchToMySubscriptionsTab() {
        if (viewPager != null) {
            viewPager.setCurrentItem(1);
        }
    }

    public void switchToBrowseTab() {
        if (viewPager != null) {
            viewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply custom animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        // Apply custom animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
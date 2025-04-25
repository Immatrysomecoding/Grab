package com.example.myapplication.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ActivitiesActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView rvRecentTrips;
    private CardView cardOngoingTrip;
    private TextView tvOngoingHeader;
    private ViewPager2 insightPager;
    private LinearLayout indicatorLayout;
    private BottomNavigationView bottomNavigation;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    // Data
    private List<TripItem> tripItems;
    private ActivityAdapter adapter;

    // Insight cards
    private List<InsightCard> insightCards;
    private InsightPagerAdapter insightAdapter;
    private ImageView[] indicators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://grab-741f2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        initViews();
        setupBottomNavigation();
        setupInsightCards();
        setupOngoingTrip();
        setupRecentTrips();
    }

    private void initViews() {
        rvRecentTrips = findViewById(R.id.rvRecentTrips);
        cardOngoingTrip = findViewById(R.id.cardOngoingTrip);
        tvOngoingHeader = findViewById(R.id.tvOngoingHeader);
        insightPager = findViewById(R.id.insightPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        CardView btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ActivitiesActivity.this, HistoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        ImageView btnCloseInsights = findViewById(R.id.btnCloseInsights);
        btnCloseInsights.setOnClickListener(v -> {
            // Hide the insights section
            findViewById(R.id.tvInsightsHeader).setVisibility(View.GONE);
            btnCloseInsights.setVisibility(View.GONE);
            insightPager.setVisibility(View.GONE);
            indicatorLayout.setVisibility(View.GONE);
        });
    }

    private void setupBottomNavigation() {
        // Vì đây là màn hình Activity nên chúng ta chọn tab Activity
        bottomNavigation.setSelectedItemId(R.id.navigation_activity);

        // Set listener for bottom navigation
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Lưu trạng thái được chọn vào SharedPreferences
                SharedPreferences prefs = getSharedPreferences("nav_state", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("selected_tab", R.id.navigation_home);
                editor.apply();

                // Đơn giản là finish() để quay lại Home activity với animation mượt mà
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            } else if (itemId == R.id.navigation_activity) {
                // Đã ở Activity rồi, không làm gì
                return true;
            } else if (itemId == R.id.navigation_payment) {
                // TODO: Chuyển đến Payment screen
                return false;
            } else if (itemId == R.id.navigation_messages) {
                // TODO: Chuyển đến Messages screen
                return false;
            }
            return false;
        });
    }

    private void setupInsightCards() {
        // Create insight cards
        insightCards = new ArrayList<>();

        // Green card (riding back to 2024)
        InsightCard greenCard = new InsightCard();
        greenCard.setTitle("We're riding back to 2024");
        greenCard.setSubtitle("To revisit shared moments with you!");
        greenCard.setActionText("Hop on");
        greenCard.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green_background));

        // Brown card
        InsightCard brownCard = new InsightCard();
        brownCard.setTitle("Ready for a new journey?");
        brownCard.setSubtitle("Discover new places with Grab!");
        brownCard.setActionText("Explore");
        brownCard.setBackgroundColor(ContextCompat.getColor(this, R.color.light_brown_background));

        insightCards.add(greenCard);
        insightCards.add(brownCard);

        // Setup ViewPager
        insightAdapter = new InsightPagerAdapter(insightCards);
        insightPager.setAdapter(insightAdapter);

        // Setup page indicator
        setupPageIndicator();

        // Listen for page changes
        insightPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updatePageIndicator(position);
            }
        });
    }

    private void setupPageIndicator() {
        indicators = new ImageView[insightCards.size()];

        // Clear indicator layout
        indicatorLayout.removeAllViews();

        // Create indicators
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);

            // Set size and margin
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            indicators[i].setLayoutParams(params);

            // Set indicator drawable
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    i == 0 ? R.drawable.indicator_active : R.drawable.indicator_inactive));

            // Add to layout
            indicatorLayout.addView(indicators[i]);
        }
    }

    private void updatePageIndicator(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    i == position ? R.drawable.indicator_active : R.drawable.indicator_inactive));
        }
    }

    private void setupOngoingTrip() {
        // By default, hide the ongoing trip section
        cardOngoingTrip.setVisibility(View.GONE);
        tvOngoingHeader.setVisibility(View.GONE);

        // TODO: Check if there is an ongoing trip in the database
        // For now, we'll just show a mock ongoing delivery
        cardOngoingTrip.setVisibility(View.VISIBLE);
        tvOngoingHeader.setVisibility(View.VISIBLE);

        ImageView ivOngoingIcon = findViewById(R.id.ivOngoingIcon);
        TextView tvOngoingStatus = findViewById(R.id.tvOngoingStatus);
        TextView tvOngoingTitle = findViewById(R.id.tvOngoingTitle);
        TextView tvOngoingTime = findViewById(R.id.tvOngoingTime);

        ivOngoingIcon.setImageResource(R.drawable.ic_bike);
        tvOngoingStatus.setText("Delivery placed");
        tvOngoingStatus.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        tvOngoingTitle.setText("Delivery for Thinh Thinh");
        tvOngoingTime.setText("24 Apr 2025, 06:52 PM");
    }

    private void setupRecentTrips() {
        tripItems = new ArrayList<>();
        adapter = new ActivityAdapter(tripItems);

        rvRecentTrips.setLayoutManager(new LinearLayoutManager(this));
        rvRecentTrips.setAdapter(adapter);

        // Load trips from Firebase
        loadTripHistory();
    }

    private void loadTripHistory() {
        if (currentUser == null) {
            return;
        }

        String userId = currentUser.getUid();

        // Load trip history from Firebase
        mDatabase.child("trips").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tripItems.clear();

                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    TripItem item = tripSnapshot.getValue(TripItem.class);
                    if (item != null) {
                        tripItems.add(item);
                    }
                }

                // Sort by timestamp (newest first)
                Collections.sort(tripItems, new Comparator<TripItem>() {
                    @Override
                    public int compare(TripItem t1, TripItem t2) {
                        return t2.getTimestampValue().compareTo(t1.getTimestampValue());
                    }
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Lưu trạng thái được chọn vào SharedPreferences
        SharedPreferences prefs = getSharedPreferences("nav_state", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("selected_tab", R.id.navigation_home);
        editor.apply();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
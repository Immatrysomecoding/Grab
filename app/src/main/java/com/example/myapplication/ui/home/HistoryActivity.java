package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
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

public class HistoryActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView rvHistoryTrips;
    private View emptyStateLayout;
    private TextView tvFilterAll, tvFilterCar, tvFilterBike;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    // Data
    private List<TripItem> allTripItems;
    private List<TripItem> filteredTripItems;
    private ActivityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://grab-741f2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        initViews();
        setupListeners();
        setupRecyclerView();
        loadTripHistory();
    }

    private void initViews() {
        rvHistoryTrips = findViewById(R.id.rvHistoryTrips);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tvFilterAll = findViewById(R.id.tvFilterAll);
        tvFilterCar = findViewById(R.id.tvFilterCar);
        tvFilterBike = findViewById(R.id.tvFilterBike);

        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        tvFilterAll.setOnClickListener(v -> {
            setFilterSelected(tvFilterAll, true);
            setFilterSelected(tvFilterCar, false);
            setFilterSelected(tvFilterBike, false);
            filterTrips("all");
        });

        tvFilterCar.setOnClickListener(v -> {
            setFilterSelected(tvFilterAll, false);
            setFilterSelected(tvFilterCar, true);
            setFilterSelected(tvFilterBike, false);
            filterTrips("car");
        });

        tvFilterBike.setOnClickListener(v -> {
            setFilterSelected(tvFilterAll, false);
            setFilterSelected(tvFilterCar, false);
            setFilterSelected(tvFilterBike, true);
            filterTrips("bike");
        });
    }

    private void setupRecyclerView() {
        allTripItems = new ArrayList<>();
        filteredTripItems = new ArrayList<>();
        adapter = new ActivityAdapter(filteredTripItems);
        rvHistoryTrips.setLayoutManager(new LinearLayoutManager(this));
        rvHistoryTrips.setAdapter(adapter);
    }

    private void loadTripHistory() {
        if (currentUser == null) {
            showEmptyState();
            return;
        }

        String userId = currentUser.getUid();

        // Load trip history from Firebase
        mDatabase.child("trips").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allTripItems.clear();

                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    TripItem item = tripSnapshot.getValue(TripItem.class);
                    if (item != null) {
                        allTripItems.add(item);
                    }
                }

                // Sort by timestamp (newest first)
                Collections.sort(allTripItems, new Comparator<TripItem>() {
                    @Override
                    public int compare(TripItem t1, TripItem t2) {
                        return t2.getTimestampValue().compareTo(t1.getTimestampValue());
                    }
                });

                if (allTripItems.isEmpty()) {
                    showEmptyState();
                } else {
                    showTrips();
                    // Default filter is "all"
                    filterTrips("all");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showEmptyState();
            }
        });
    }

    private void filterTrips(String vehicleType) {
        filteredTripItems.clear();

        if ("all".equals(vehicleType)) {
            filteredTripItems.addAll(allTripItems);
        } else {
            for (TripItem item : allTripItems) {
                if (vehicleType.equals(item.getVehicleType())) {
                    filteredTripItems.add(item);
                }
            }
        }

        if (filteredTripItems.isEmpty()) {
            showEmptyState();
        } else {
            showTrips();
        }

        adapter.notifyDataSetChanged();
    }

    private void setFilterSelected(TextView textView, boolean isSelected) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.filter_bg_selected);
            textView.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            textView.setBackgroundResource(R.drawable.filter_bg_unselected);
            textView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void showEmptyState() {
        emptyStateLayout.setVisibility(View.VISIBLE);
        rvHistoryTrips.setVisibility(View.GONE);
    }

    private void showTrips() {
        emptyStateLayout.setVisibility(View.GONE);
        rvHistoryTrips.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
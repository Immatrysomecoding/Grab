package com.example.myapplication.ui.transport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.home.HotspotAdapter;
import com.example.myapplication.ui.home.HotspotItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransportActivity extends AppCompatActivity {

    private RecyclerView rvRecentLocations, rvHotspots, rvPreBook, rvDiscover, rvMoreWays;
    private CardView cardMap;
    private ImageButton btnBack;
    private LinearLayout layoutSearchBar;

    private RecentLocationAdapter recentLocationAdapter;
    private HotspotAdapter hotspotAdapter;
    private FeatureCardAdapter preBookAdapter;
    private FeatureCardAdapter discoverAdapter;
    private FeatureCardAdapter moreWaysAdapter;

    private List<RecentLocation> recentLocations = new ArrayList<>();
    private List<HotspotItem> hotspotItems = new ArrayList<>();
    private List<FeatureCard> preBookItems = new ArrayList<>();
    private List<FeatureCard> discoverItems = new ArrayList<>();
    private List<FeatureCard> moreWaysItems = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private String transportType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        // Get transport type from intent (car or bike)
        transportType = getIntent().getStringExtra("TRANSPORT_TYPE");
        if (transportType == null) {
            transportType = "car"; // Default to car
        }

        // Set status bar color to match the green background
        getWindow().setStatusBarColor(getResources().getColor(R.color.light_green_background));

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://grab-741f2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        initViews();
        setupClickListeners();
        setupAdapters();
        loadRecentLocations();
        setupMockData();
    }


    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        cardMap = findViewById(R.id.cardMap);
        layoutSearchBar = findViewById(R.id.layoutSearchBar);

        rvRecentLocations = findViewById(R.id.rvRecentLocations);
        rvHotspots = findViewById(R.id.rvHotspots);
        rvPreBook = findViewById(R.id.rvPreBook);
        rvDiscover = findViewById(R.id.rvDiscover);
        rvMoreWays = findViewById(R.id.rvMoreWays);

        // Setup click listeners for service options
        findViewById(R.id.cardAdvanceBooking).setOnClickListener(v -> {
            Toast.makeText(this, "Advance Booking", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.cardBookForFamily).setOnClickListener(v -> {
            Toast.makeText(this, "Book for Family", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.card7Seater).setOnClickListener(v -> {
            Toast.makeText(this, "7 seater car", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.cardCarPlus).setOnClickListener(v -> {
            Toast.makeText(this, "Car Plus", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.cardAirportPickup).setOnClickListener(v -> {
            Toast.makeText(this, "Airport pickup", Toast.LENGTH_SHORT).show();
        });

        // Setup RecyclerViews
        rvRecentLocations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvHotspots.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPreBook.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDiscover.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvMoreWays.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        cardMap.setOnClickListener(v -> {
            // Will implement map activity later
            // Intent intent = new Intent(TransportActivity.this, MapActivity.class);
            // startActivity(intent);
        });

        layoutSearchBar.setOnClickListener(v -> {
            // Will implement search destination activity later
            // Intent intent = new Intent(TransportActivity.this, SearchDestinationActivity.class);
            // intent.putExtra("TRANSPORT_TYPE", transportType);
            // startActivity(intent);
        });
    }

    private void setupAdapters() {
        // Recent locations adapter
        recentLocationAdapter = new RecentLocationAdapter(recentLocations, location -> {
            // Handle location selection - will be implemented later
        });
        rvRecentLocations.setAdapter(recentLocationAdapter);

        // Hotspots adapter
        hotspotAdapter = new HotspotAdapter(hotspotItems);
        rvHotspots.setAdapter(hotspotAdapter);

        // Feature cards adapters
        preBookAdapter = new FeatureCardAdapter(preBookItems, (card, position) -> {
            // Handle pre-book card click - will be implemented later
        });
        rvPreBook.setAdapter(preBookAdapter);

        discoverAdapter = new FeatureCardAdapter(discoverItems, (card, position) -> {
            // Handle discover card click - will be implemented later
        });
        rvDiscover.setAdapter(discoverAdapter);

        moreWaysAdapter = new FeatureCardAdapter(moreWaysItems, (card, position) -> {
            // Handle more ways card click - will be implemented later
        });
        rvMoreWays.setAdapter(moreWaysAdapter);
    }
    private void loadRecentLocations() {
        if (currentUser == null) {
            rvRecentLocations.setVisibility(View.GONE);
            return;
        }

        String userId = currentUser.getUid();
        android.util.Log.d("TRANSPORT_DEBUG", "User ID: " + userId);

        mDatabase.child("trips").child(userId)
                .limitToLast(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            android.util.Log.d("TRANSPORT_DEBUG", "Tìm thấy dữ liệu trips của user");

                            recentLocations.clear();
                            Set<String> uniqueLocations = new HashSet<>();

                            for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                                String tripId = tripSnapshot.getKey();
                                String endLocation = tripSnapshot.child("endLocation").getValue(String.class);

                                android.util.Log.d("TRANSPORT_DEBUG", "Trip ID: " + tripId +
                                        ", End Location: " + endLocation);

                                if (endLocation != null && !uniqueLocations.contains(endLocation)) {
                                    uniqueLocations.add(endLocation);

                                    RecentLocation location = new RecentLocation();
                                    location.setName(endLocation);
                                    location.setFullAddress(endLocation);
                                    location.setType("history");

                                    recentLocations.add(location);

                                    if (recentLocations.size() >= 3) {
                                        break;
                                    }
                                }
                            }

                            if (recentLocations.isEmpty()) {
                                rvRecentLocations.setVisibility(View.GONE);
                                android.util.Log.d("TRANSPORT_DEBUG", "Không tìm thấy endLocation nào");
                            } else {
                                rvRecentLocations.setVisibility(View.VISIBLE);
                                recentLocationAdapter.notifyDataSetChanged();
                                android.util.Log.d("TRANSPORT_DEBUG", "Tìm thấy " + recentLocations.size() + " địa điểm");
                            }
                        } else {
                            rvRecentLocations.setVisibility(View.GONE);
                            android.util.Log.d("TRANSPORT_DEBUG", "Không tìm thấy trips nào cho user này");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        rvRecentLocations.setVisibility(View.GONE);
                        android.util.Log.e("TRANSPORT_DEBUG", "Database Error: " + databaseError.getMessage());
                    }
                });
    }
    private void setupMockData() {
        // Setup HotSpot data (reuse from HomeActivity)
        hotspotItems.clear();
        hotspotItems.add(new HotspotItem(R.drawable.ic_car, "AEON MALL BÌNH TÂN", "8.3", "25% off"));
        hotspotItems.add(new HotspotItem(R.drawable.ic_car, "The Gangs", "8.4", "20% off"));
        hotspotItems.add(new HotspotItem(R.drawable.ic_car, "Làm Tí", "8.2", "20% off"));
        hotspotAdapter.notifyDataSetChanged();

        // Pre-book items
        preBookItems.clear();
        preBookItems.add(new FeatureCard("Advance Booking", "Book ahead, never be late",
                "Your driver will arrive on time or early", R.drawable.ic_car));
        preBookItems.add(new FeatureCard("Airport Transfer", "Breeze past last-minute stress",
                "Enjoy extended wait time at no extra cost", R.drawable.ic_car));
        preBookItems.add(new FeatureCard("Hourly Booking", "Keep your driver for longer",
                "Perfect for multiple stops", R.drawable.ic_car));
        preBookAdapter.notifyDataSetChanged();

        // Discover items
        discoverItems.clear();
        discoverItems.add(new FeatureCard("Rent a GrabCar by the hour", "Enjoy endless trips with a dedicated car",
                "", R.drawable.ic_car));
        discoverItems.add(new FeatureCard("Book GrabCar Plus", "Safe, convenient and high-quality rides",
                "", R.drawable.ic_car));
        discoverItems.add(new FeatureCard("Try fixed fare rides", "Know your fare before you ride",
                "", R.drawable.ic_car));
        discoverAdapter.notifyDataSetChanged();

        // More ways items
        moreWaysItems.clear();
        moreWaysItems.add(new FeatureCard("Make an Advance Booking", "Breeze past last-minute stress",
                "", R.drawable.ic_car));
        moreWaysItems.add(new FeatureCard("Try a Saver ride", "Wait a while longer for a lower fare",
                "", R.drawable.ic_car));
        moreWaysItems.add(new FeatureCard("Book a GrabCar Plus", "Premium rides at affordable prices",
                "", R.drawable.ic_car));
        moreWaysAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
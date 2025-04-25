package com.example.myapplication.ui.transport;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.map.MapActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class SearchDestinationActivity extends AppCompatActivity {

    private ImageButton btnBack, btnClear;
    private EditText etSearch;
    private TextView tvNoResults, tvChooseOnMap;
    private RecyclerView rvSearchResults;
    private TabLayout tabLayout;
    private FrameLayout bottomSection;

    private LocationAdapter locationAdapter;
    private List<LocationItem> locations = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private PlacesClient placesClient;

    private static final String TAG = "SearchDestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_destination);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);

        // Khởi tạo Google Places API
        Places.initialize(getApplicationContext(), "AIzaSyAM4QRcESnHhJGq7K4virXYtmzZh2rW-N0"); // 🔁 Đổi API Key nếu cần
        placesClient = Places.createClient(this);

        setupFirebase();
        initViews();
        setupRecyclerView();
        setupListeners();
        loadLocations();
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnClear = findViewById(R.id.btnClear);
        etSearch = findViewById(R.id.etSearch);
        tvNoResults = findViewById(R.id.tvNoResults);
        tvChooseOnMap = findViewById(R.id.tvChooseOnMap);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        tabLayout = findViewById(R.id.tabLayout);
        bottomSection = findViewById(R.id.bottomSection);
    }

    private void setupRecyclerView() {
        locationAdapter = new LocationAdapter(locations, location -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("LOCATION_TITLE", location.getTitle());
            resultIntent.putExtra("LOCATION_ADDRESS", location.getAddress());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(locationAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            btnClear.setVisibility(View.GONE);
        });

        bottomSection.setOnClickListener(v -> {
            startActivity(new Intent(this, MapActivity.class));
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                btnClear.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);

                if (query.isEmpty()) {
                    tabLayout.setVisibility(View.VISIBLE);
                    bottomSection.setVisibility(View.VISIBLE);
                    loadLocations();
                } else {
                    tabLayout.setVisibility(View.GONE);
                    bottomSection.setVisibility(View.GONE);
                    searchPlacesWithAPI(query);
                }
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { loadLocations(); }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadLocations() {
        locations.clear();
        locations.add(new LocationItem("Home", "143/9 An Binh St., P.6, Q.5, Hồ Chí Minh", "0.0km", LocationItem.TYPE_HOME));

        if (tabLayout.getSelectedTabPosition() == 0 && currentUser != null) {
            loadRecentTrips();
        }

        locationAdapter.notifyDataSetChanged();
        tvNoResults.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.VISIBLE);
    }

    private void loadRecentTrips() {
        String userId = currentUser.getUid();
        mDatabase.child("trips").child(userId).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot trip : dataSnapshot.getChildren()) {
                        String endLocation = trip.child("endLocation").getValue(String.class);
                        if (endLocation != null && !isLocationAlreadyAdded(endLocation)) {
                            locations.add(new LocationItem(endLocation, endLocation, randomDistance(), LocationItem.TYPE_RECENT));
                        }
                    }
                    locationAdapter.notifyDataSetChanged();
                }
            }
            @Override public void onCancelled(DatabaseError error) {
                Toast.makeText(SearchDestinationActivity.this, "Failed to load trips", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isLocationAlreadyAdded(String locationName) {
        for (LocationItem item : locations) {
            if (item.getTitle().equals(locationName)) return true;
        }
        return false;
    }

    private String randomDistance() {
        double[] distances = {0.0, 0.7, 1.6, 2.4};
        return distances[(int)(Math.random() * distances.length)] + "km";
    }

    private void searchPlacesWithAPI(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(10.7, 106.6),
                new LatLng(10.8, 106.8)
        );

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setOrigin(new LatLng(10.76, 106.70))
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(response -> {
                    List<LocationItem> searchResults = new ArrayList<>();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        String title = prediction.getPrimaryText(null).toString();
                        String address = prediction.getSecondaryText(null).toString();
                        String distance = String.format("%.1fkm", Math.random() * 10);
                        searchResults.add(new LocationItem(title, address, distance, LocationItem.TYPE_RECENT));
                    }
                    updateSearchResults(searchResults);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Place search error: ", exception);
                    Toast.makeText(this, "Error loading search results", Toast.LENGTH_SHORT).show();
                    tvNoResults.setVisibility(View.VISIBLE);
                    rvSearchResults.setVisibility(View.GONE);
                });
    }

    private void updateSearchResults(List<LocationItem> results) {
        if (results.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);
            locationAdapter = new LocationAdapter(results, location -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("LOCATION_TITLE", location.getTitle());
                resultIntent.putExtra("LOCATION_ADDRESS", location.getAddress());
                setResult(RESULT_OK, resultIntent);
                finish();
            });
            rvSearchResults.setAdapter(locationAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
}

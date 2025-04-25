package com.example.myapplication.ui.transport;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.map.MapActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchDestinationActivity extends AppCompatActivity {

    private ImageButton btnBack, btnClear;
    private EditText etSearch;
    private TextView tvNoResults, tvChooseOnMap;
    private RecyclerView rvSearchResults;
    private TabLayout tabLayout;

    private LocationAdapter locationAdapter;
    private List<LocationItem> locations = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_destination);

        // Cài đặt animation slide up khi mở activity
        overridePendingTransition(R.anim.slide_up, R.anim.stay);

        setupFirebase();
        initViews();
        setupRecyclerView();
        setupListeners();
        loadLocations();
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://grab-741f2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnClear = findViewById(R.id.btnClear);
        etSearch = findViewById(R.id.etSearch);
        tvNoResults = findViewById(R.id.tvNoResults);
        tvChooseOnMap = findViewById(R.id.tvChooseOnMap);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void setupRecyclerView() {
        locationAdapter = new LocationAdapter(locations, location -> {
            // Xử lý khi chọn một địa điểm
            Toast.makeText(SearchDestinationActivity.this, "Selected: " + location.getTitle(), Toast.LENGTH_SHORT).show();

            // Trả về địa điểm được chọn
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

        // Sử dụng View.OnClickListener cho LinearLayout
        findViewById(R.id.bottomSection).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        });

        // Theo dõi thay đổi trong ô tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                btnClear.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);

                // Lọc kết quả tìm kiếm
                filterLocations(query);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Khi chọn tab, load dữ liệu phù hợp
                loadLocations();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });
    }

    private void filterLocations(String query) {
        if (query.isEmpty()) {
            // Nếu không có query, hiển thị tất cả
            loadLocations();
            return;
        }

        // Lọc danh sách dựa trên query
        List<LocationItem> filteredList = new ArrayList<>();

        for (LocationItem item : locations) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    item.getAddress().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        // Cập nhật UI
        if (filteredList.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);
            locationAdapter = new LocationAdapter(filteredList, location -> {
                // Xử lý khi chọn một địa điểm từ kết quả lọc
                Intent resultIntent = new Intent();
                resultIntent.putExtra("LOCATION_TITLE", location.getTitle());
                resultIntent.putExtra("LOCATION_ADDRESS", location.getAddress());
                setResult(RESULT_OK, resultIntent);
                finish();
            });
            rvSearchResults.setAdapter(locationAdapter);
        }
    }

    private void loadLocations() {
        locations.clear();

        // Luôn thêm địa chỉ Home đầu tiên
        locations.add(new LocationItem(
                "Home",
                "143/9 An Binh St., P.6, Q.5, Hồ Chí Minh, 70000, Vietnam",
                "0.0km",
                LocationItem.TYPE_HOME));

        // Nếu đang ở tab Recent, load lịch sử chuyến đi
        if (tabLayout.getSelectedTabPosition() == 0) {
            if (currentUser != null) {
                loadRecentTrips();
            }
        }

        // Cập nhật adapter
        locationAdapter.notifyDataSetChanged();

        // Ẩn thông báo không có kết quả
        tvNoResults.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.VISIBLE);
    }

    private void loadRecentTrips() {
        String userId = currentUser.getUid();

        mDatabase.child("trips").child(userId).limitToLast(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                                String endLocation = tripSnapshot.child("endLocation").getValue(String.class);
                                String distance = calculateTripDistance(tripSnapshot);

                                if (endLocation != null && !isLocationAlreadyAdded(endLocation)) {
                                    locations.add(new LocationItem(
                                            endLocation,
                                            endLocation,
                                            distance,
                                            LocationItem.TYPE_RECENT
                                    ));
                                }
                            }
                            locationAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(SearchDestinationActivity.this,
                                "Failed to load trip data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isLocationAlreadyAdded(String locationName) {
        // Kiểm tra xem địa điểm đã có trong danh sách chưa
        for (LocationItem item : locations) {
            if (item.getTitle().equals(locationName)) {
                return true;
            }
        }
        return false;
    }

    private String calculateTripDistance(DataSnapshot tripSnapshot) {
        // Trong thực tế sẽ tính khoảng cách từ vị trí hiện tại đến điểm đến
        // Ở đây chúng ta sẽ lấy một khoảng cách ngẫu nhiên để demo
        double[] distances = {0.0, 0.1, 0.7, 1.6, 2.4};
        int randomIndex = (int) (Math.random() * distances.length);
        return distances[randomIndex] + "km";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Animation khi đóng
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
}
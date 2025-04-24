package com.example.myapplication.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.ui.map.NearbyLocation;
import com.example.myapplication.ui.map.NearbyLocationAdapter;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class SearchDestinationActivity extends AppCompatActivity {
    private ImageButton btnBack, btnClear;
    private EditText etSearch;
    private TextView tvNoResults;
    private RecyclerView rvSearchResults;

    private NearbyLocationAdapter locationAdapter;
    private List<NearbyLocation> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_destination);

        initViews();
        setupSearchResults();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnClear = findViewById(R.id.btnClear);
        etSearch = findViewById(R.id.etSearch);
        tvNoResults = findViewById(R.id.tvNoResults);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        // Ban đầu ẩn nút clear và thông báo không có kết quả
        btnClear.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);
    }

    private void setupSearchResults() {
        searchResults = new ArrayList<>();

        // Thêm một số kết quả mẫu (trong ứng dụng thực tế, bạn sẽ tìm kiếm dựa trên API)
        searchResults.add(new NearbyLocation(
                "142/43 An Binh St.",
                "An Binh, P.6, Q.5, Hồ Chí Minh, 70000, Vietnam",
                "0.0",
                new LatLng(10.7769, 106.7009)));

        searchResults.add(new NearbyLocation(
                "143/12 An Binh St.",
                "An Binh, P.6, Q.5, Hồ Chí Minh, 70000, Vietnam",
                "0.0",
                new LatLng(10.7767, 106.7007)));

        // Setup adapter
        locationAdapter = new NearbyLocationAdapter(searchResults, location -> {
            // Khi chọn một địa điểm, trả về kết quả cho MapActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("LOCATION_NAME", location.getName());
            resultIntent.putExtra("LOCATION_ADDRESS", location.getAddress());
            resultIntent.putExtra("LATITUDE", location.getLatLng().latitude);
            resultIntent.putExtra("LONGITUDE", location.getLatLng().longitude);
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
                String query = s.toString().trim().toLowerCase();

                // Hiển thị/ẩn nút clear
                btnClear.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);

                // Lọc kết quả tìm kiếm
                filterResults(query);
            }
        });
    }

    private void filterResults(String query) {
        List<NearbyLocation> filteredList = new ArrayList<>();

        // Trong ứng dụng thực tế, bạn sẽ gọi API tìm kiếm
        // Ở đây chỉ là ví dụ đơn giản
        if (query.isEmpty()) {
            filteredList.addAll(searchResults);
        } else {
            for (NearbyLocation location : searchResults) {
                if (location.getName().toLowerCase().contains(query) ||
                        location.getAddress().toLowerCase().contains(query)) {
                    filteredList.add(location);
                }
            }
        }

        // Cập nhật UI
        if (filteredList.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);

            // Cập nhật adapter
            locationAdapter = new NearbyLocationAdapter(filteredList, location -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("LOCATION_NAME", location.getName());
                resultIntent.putExtra("LOCATION_ADDRESS", location.getAddress());
                resultIntent.putExtra("LATITUDE", location.getLatLng().latitude);
                resultIntent.putExtra("LONGITUDE", location.getLatLng().longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            });

            rvSearchResults.setAdapter(locationAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
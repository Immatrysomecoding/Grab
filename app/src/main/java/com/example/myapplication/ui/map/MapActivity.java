package com.example.myapplication.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng selectedLocation;
    private boolean isMapMoving = false;

    // UI Components
    private ImageButton btnBack, btnFocusLocation;
    private Button btnChooseDestination;
    private TextView tvLocationName, tvLocationAddress, tvDistanceInfo;
    private RecyclerView rvNearbyLocations;
    private ImageView centerMarker;
    private View bottomSheet;
    private View chooseDestinationContainer;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    // Data
    private NearbyLocation currentLocation;
    private List<NearbyLocation> predefinedLocations;
    private NearbyLocationAdapter locationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initViews();
        initializePredefinedLocations();
        setupBottomSheet();
        setupNearbyLocations();
        setupClickListeners();

        // Get the SupportMapFragment and request notification when the map is ready
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnFocusLocation = findViewById(R.id.btnFocusLocation);
        btnChooseDestination = findViewById(R.id.btnChooseDestination);
        tvLocationName = findViewById(R.id.tvLocationName);
        tvLocationAddress = findViewById(R.id.tvLocationAddress);
        tvDistanceInfo = findViewById(R.id.tvDistanceInfo);
        rvNearbyLocations = findViewById(R.id.rvNearbyLocations);
        centerMarker = findViewById(R.id.centerMarker);
        bottomSheet = findViewById(R.id.bottomSheet);
        chooseDestinationContainer = findViewById(R.id.chooseDestinationContainer);
    }

    private void initializePredefinedLocations() {
        predefinedLocations = new ArrayList<>();

        // Thêm các địa điểm mẫu
        predefinedLocations.add(new NearbyLocation(
                "142/43 An Binh St.",
                "An Binh, P.6, Q.5, Hồ Chí Minh, 70000, Vietnam",
                "0.0",
                new LatLng(10.7769, 106.7009)));

        predefinedLocations.add(new NearbyLocation(
                "143/12 An Binh St.",
                "An Binh, P.6, Q.5, Hồ Chí Minh, 70000, Vietnam",
                "0.0",
                new LatLng(10.7767, 106.7007)));

        predefinedLocations.add(new NearbyLocation(
                "131/10 An Binh St.",
                "131/10 An Binh St., P.6, Q.5, Hồ Chí Minh, 70000, Vietnam",
                "0.0",
                new LatLng(10.7765, 106.7005)));

        predefinedLocations.add(new NearbyLocation(
                "OverArt",
                "142 Đường An Binh, P.6, Q.5, Hồ Chí Minh, 70000, Vietnam",
                "0.0",
                new LatLng(10.7763, 106.7003)));
    }

    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_sheet_peek_height));
        bottomSheetBehavior.setHideable(false);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // Khi mở rộng hoàn toàn, ẩn nút
                    chooseDestinationContainer.setVisibility(View.GONE);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // Khi thu gọn, hiện nút
                    chooseDestinationContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Khi bottom sheet trượt, dịch chuyển container button theo cùng hướng
                float translateY = slideOffset * chooseDestinationContainer.getHeight();
                chooseDestinationContainer.setTranslationY(translateY);

                // Ẩn khi trượt gần hết
                if (slideOffset > 0.8) {
                    chooseDestinationContainer.setVisibility(View.GONE);
                } else {
                    chooseDestinationContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupNearbyLocations() {
        // Đảm bảo predefinedLocations đã khởi tạo
        if (predefinedLocations == null) {
            predefinedLocations = new ArrayList<>();
        }

        // Khởi tạo adapter với callback khi chọn vị trí
        locationAdapter = new NearbyLocationAdapter(predefinedLocations, location -> {
            // Khi chọn địa điểm từ danh sách, di chuyển map đến vị trí đó
            if (mMap != null && location.getLatLng() != null) {
                // Di chuyển camera đến vị trí đã chọn
                mMap.animateCamera(CameraUpdateFactory.newLatLng(location.getLatLng()));

                // Cập nhật thông tin vị trí hiện tại
                updateLocationInfo(location.getLatLng());

                // Thu gọn bottom sheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        rvNearbyLocations.setLayoutManager(new LinearLayoutManager(this));
        rvNearbyLocations.setAdapter(locationAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnFocusLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
                return;
            }

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f));
                }
            });
        });

        btnChooseDestination.setOnClickListener(v -> {
            if (selectedLocation != null) {
                // Return selected location to previous activity
                Toast.makeText(this, "Destination selected: " + tvLocationName.getText(),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set default location (Ho Chi Minh City)
        LatLng defaultLocation = new LatLng(10.7769, 106.7009);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 18f));

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            requestLocationPermission();
        }

        // Set up map UI
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Setup các listeners cho map
        setupMapListeners();

        // Khởi tạo vị trí ban đầu
        selectedLocation = mMap.getCameraPosition().target;
        updateLocationInfo(selectedLocation);
    }

    private void setupMapListeners() {
        // Khi camera bắt đầu di chuyển
        mMap.setOnCameraMoveStartedListener(reason -> {
            isMapMoving = true;
            startMarkerBounceAnimation();
        });

        // Khi camera đang di chuyển
        mMap.setOnCameraMoveListener(() -> {
            // Cập nhật vị trí trung tâm mới
            selectedLocation = mMap.getCameraPosition().target;
        });

        // Khi camera dừng lại
        mMap.setOnCameraIdleListener(() -> {
            isMapMoving = false;
            stopMarkerBounceAnimation();
            updateLocationInfo(selectedLocation);
        });
    }

    private void startMarkerBounceAnimation() {
        // Bắt đầu animation nảy lên xuống
        centerMarker.animate()
                .translationY(-20f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    if (isMapMoving) {
                        centerMarker.animate()
                                .translationY(0f)
                                .setDuration(300)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .withEndAction(() -> {
                                    if (isMapMoving) {
                                        startMarkerBounceAnimation();
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    private void stopMarkerBounceAnimation() {
        // Dừng animation và quay trở lại vị trí ban đầu
        centerMarker.animate().cancel();
        centerMarker.animate()
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void updateLocationInfo(LatLng position) {
        // Lưu vị trí được chọn
        selectedLocation = position;

        // Tạo thông tin cho vị trí hiện tại (dùng fake data vì Geocoding bị lỗi)
        boolean locationFound = false;

        // Tìm trong danh sách có vị trí nào gần với vị trí đã chọn không
        for (NearbyLocation location : predefinedLocations) {
            double distance = calculateDistance(position, location.getLatLng());

            // Nếu khoảng cách nhỏ hơn 50m (0.05km), coi là cùng vị trí
            if (distance < 0.05) {
                // Cập nhật thông tin từ vị trí có sẵn
                tvLocationName.setText(location.getName());
                tvLocationAddress.setText(location.getAddress());
                tvDistanceInfo.setText("0.0km");

                // Lưu thông tin vào currentLocation
                currentLocation = new NearbyLocation(
                        location.getName(),
                        location.getAddress(),
                        "0.0",
                        position);

                locationFound = true;
                break;
            }
        }

        // Nếu không tìm thấy vị trí tương ứng, tạo thông tin mới
        if (!locationFound) {
            String name = "Unknown Location";
            String address = "Hồ Chí Minh, Vietnam";

            // Tạo thông tin có tọa độ tương đối
            tvLocationName.setText(name);
            tvLocationAddress.setText(address);
            tvDistanceInfo.setText("0.0km");

            // Lưu thông tin vào currentLocation
            currentLocation = new NearbyLocation(name, address, "0.0", position);
        }

        // Cập nhật khoảng cách của các địa điểm gần đây so với vị trí hiện tại
        updateDistanceToNearbyLocations(position);
    }

    private void getAddressFromLocation(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                // Lấy thông tin địa chỉ
                String addressLine = address.getMaxAddressLineIndex() > 0 ?
                        address.getAddressLine(0) : "Unknown Location";

                String city = address.getLocality();
                String state = address.getAdminArea();
                String country = address.getCountryName();
                String postalCode = address.getPostalCode();

                StringBuilder fullAddress = new StringBuilder();
                if (city != null) fullAddress.append(city).append(", ");
                if (state != null) fullAddress.append(state).append(", ");
                if (country != null) fullAddress.append(country);
                if (postalCode != null) fullAddress.append(", ").append(postalCode);

                // Cập nhật UI
                tvLocationName.setText(addressLine);
                tvLocationAddress.setText(fullAddress.toString());
                tvDistanceInfo.setText("0.0km");

                // Lưu thông tin vào currentLocation
                currentLocation = new NearbyLocation(
                        addressLine,
                        fullAddress.toString(),
                        "0.0",
                        latLng
                );
            } else {
                // Fallback nếu không tìm thấy địa chỉ
                String name = "Unknown Location";
                tvLocationName.setText(name);
                tvLocationAddress.setText("Hồ Chí Minh, Vietnam");
                tvDistanceInfo.setText("0.0km");

                currentLocation = new NearbyLocation(name, "Hồ Chí Minh, Vietnam", "0.0", latLng);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý lỗi
            String name = "Unknown Location";
            tvLocationName.setText(name);
            tvLocationAddress.setText("Hồ Chí Minh, Vietnam");
            tvDistanceInfo.setText("0.0km");

            currentLocation = new NearbyLocation(name, "Hồ Chí Minh, Vietnam", "0.0", latLng);
        }
    }

    private void updateDistanceToNearbyLocations(LatLng currentPosition) {
        // Đảm bảo danh sách không null
        if (predefinedLocations == null) {
            predefinedLocations = new ArrayList<>();
        }

        // Cập nhật khoảng cách cho từng địa điểm
        for (NearbyLocation location : predefinedLocations) {
            if (location.getLatLng() != null) {
                double distance = calculateDistance(currentPosition, location.getLatLng());
                // Chỉ hiển thị 1 số thập phân
                location.setDistance(String.format(Locale.getDefault(), "%.1f", distance));
            }
        }

        // Sắp xếp danh sách theo khoảng cách
        Collections.sort(predefinedLocations, (loc1, loc2) -> {
            try {
                double dist1 = Double.parseDouble(loc1.getDistance());
                double dist2 = Double.parseDouble(loc2.getDistance());
                return Double.compare(dist1, dist2);
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        // Cập nhật adapter
        locationAdapter.notifyDataSetChanged();
    }

    private double calculateDistance(LatLng point1, LatLng point2) {
        // Tính khoảng cách giữa hai điểm
        double lat1 = point1.latitude;
        double lon1 = point1.longitude;
        double lat2 = point2.latitude;
        double lon2 = point2.longitude;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return 6371 * c; // Đơn vị km (6371 là bán kính Trái Đất)
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        // Get the last known location and move the camera
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f));
            }
        });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
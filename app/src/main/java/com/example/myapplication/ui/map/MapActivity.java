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

        // Thêm các địa điểm mẫu - hãy để tọa độ chính xác nếu bạn biết
        predefinedLocations.add(new NearbyLocation(
                "Lý Tự Trọng",
                "63 Lý Tự Trọng, Bến Nghé, Quận 1, Hồ Chí Minh, Vietnam",
                "0.0",
                new LatLng(10.7734, 106.7030)));

        predefinedLocations.add(new NearbyLocation(
                "OverArt",
                "142 Đường An Binh, P.6, Q.5, Hồ Chí Minh, 70000, Vietnam",
                "0.0",
                new LatLng(10.7563, 106.6693)));

        predefinedLocations.add(new NearbyLocation(
                "131/10 An Binh St.",
                "131/10 An Binh St., P.6, Q.5, Hồ Chí Minh, 70000, Vietnam",
                "0.0",
                new LatLng(10.7565, 106.6695)));

        predefinedLocations.add(new NearbyLocation(
                "143/12 An Binh St.",
                "143/12 An Binh St., P.6, Q.5, Hồ Chí Minh, 70000, Vietnam",
                "0.0",
                new LatLng(10.7567, 106.6697)));
    }

    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_sheet_peek_height));
        bottomSheetBehavior.setHideable(false);

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        bottomSheetBehavior.setMaxHeight((int)(screenHeight * 0.8));

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
                // Di chuyển camera đến vị trí đã chọn - dùng animateCamera để có hiệu ứng mượt mà
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location.getLatLng(), 18f));

                // QUAN TRỌNG: Cập nhật thông tin khi click vào card
                selectedLocation = location.getLatLng();

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

                    // Cập nhật selectedLocation và thông tin vị trí
                    selectedLocation = currentLocation;
                    updateLocationInfo(selectedLocation);
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

        // Set default location (An Binh, District 5)
        LatLng defaultLocation = new LatLng(10.7568, 106.6691);
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
            selectedLocation = mMap.getCameraPosition().target;
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

        // Sử dụng Geocoder để lấy thông tin địa chỉ
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Lấy tên địa điểm
                String name = address.getFeatureName();
                if (name == null || name.isEmpty() || name.matches("\\d+")) {
                    // Nếu tên chỉ là số, thử dùng thoroughfare (tên đường)
                    name = address.getThoroughfare();
                    if (name == null || name.isEmpty()) {
                        name = address.getSubAdminArea();
                        if (name == null || name.isEmpty()) {
                            name = "Vị trí đã chọn";
                        }
                    }
                }

                // Tạo địa chỉ đầy đủ
                StringBuilder fullAddress = new StringBuilder();
                // Thêm số nhà và tên đường
                String addressLine = address.getAddressLine(0);
                if (addressLine != null && !addressLine.isEmpty()) {
                    fullAddress.append(addressLine);
                } else {
                    // Tự tạo địa chỉ từ các thành phần
                    String street = address.getThoroughfare();
                    String subArea = address.getSubLocality();
                    String city = address.getLocality();
                    String state = address.getAdminArea();
                    String postalCode = address.getPostalCode();
                    String country = address.getCountryName();

                    if (street != null) fullAddress.append(street).append(", ");
                    if (subArea != null) fullAddress.append(subArea).append(", ");
                    if (city != null) fullAddress.append(city).append(", ");
                    if (state != null) fullAddress.append(state).append(", ");
                    if (country != null) fullAddress.append(country);
                    if (postalCode != null) fullAddress.append(" ").append(postalCode);
                }

                // Cập nhật UI
                tvLocationName.setText(name);
                tvLocationAddress.setText(fullAddress.toString());
                tvDistanceInfo.setText("0.0km");

                // Lưu thông tin vào currentLocation
                currentLocation = new NearbyLocation(
                        name,
                        fullAddress.toString(),
                        "0.0",
                        position
                );
            } else {
                // Fallback khi không có kết quả
                setDefaultLocationInfo(position);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý lỗi
            setDefaultLocationInfo(position);
        }

        // Cập nhật khoảng cách của các địa điểm gần đây so với vị trí hiện tại
        updateDistanceToNearbyLocations(position);
    }

    // Helper method khi geocoding thất bại
    private void setDefaultLocationInfo(LatLng position) {
        String name = "Vị trí đã chọn";
        String address = String.format(Locale.getDefault(),
                "%.6f, %.6f", position.latitude, position.longitude);

        tvLocationName.setText(name);
        tvLocationAddress.setText(address);
        tvDistanceInfo.setText("0.0km");

        currentLocation = new NearbyLocation(name, address, "0.0", position);
    }

    private void updateDistanceToNearbyLocations(LatLng currentPosition) {
        // Đảm bảo danh sách không null
        if (predefinedLocations == null) {
            initializePredefinedLocations(); // Đảm bảo rằng predefinedLocations đã được khởi tạo
        }

        // Tạo bản sao của danh sách để không ảnh hưởng đến danh sách gốc
        List<NearbyLocation> updatedLocations = new ArrayList<>();

        // Cập nhật khoảng cách cho từng địa điểm
        for (NearbyLocation location : predefinedLocations) {
            if (location.getLatLng() != null) {
                // Tạo bản sao mới của location để tránh thay đổi đối tượng gốc
                NearbyLocation updatedLocation = new NearbyLocation(
                        location.getName(),
                        location.getAddress(),
                        "0.0", // Sẽ cập nhật khoảng cách ở dưới
                        location.getLatLng()
                );

                // Tính và cập nhật khoảng cách
                double distance = calculateDistance(currentPosition, updatedLocation.getLatLng());
                updatedLocation.setDistance(String.format(Locale.getDefault(), "%.1f", distance));

                // Thêm vào danh sách mới
                updatedLocations.add(updatedLocation);
            }
        }

        // Sắp xếp danh sách theo khoảng cách
        Collections.sort(updatedLocations, (loc1, loc2) -> {
            try {
                double dist1 = Double.parseDouble(loc1.getDistance());
                double dist2 = Double.parseDouble(loc2.getDistance());
                return Double.compare(dist1, dist2);
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        // Debug log - kiểm tra xem danh sách có dữ liệu không
        android.util.Log.d("MapActivity", "Updated locations count: " + updatedLocations.size());
        for (NearbyLocation loc : updatedLocations) {
            android.util.Log.d("MapActivity", "Location: " + loc.getName() + ", Distance: " + loc.getDistance());
        }

        // Cập nhật adapter với danh sách mới
        if (locationAdapter != null) {
            locationAdapter.updateLocations(updatedLocations);
        }
    }

    private double calculateDistance(LatLng point1, LatLng point2) {
        // Tính khoảng cách giữa hai điểm sử dụng công thức Haversine
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
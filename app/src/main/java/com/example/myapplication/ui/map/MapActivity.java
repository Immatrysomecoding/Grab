package com.example.myapplication.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker currentLocationMarker;
    private LatLng selectedLocation;

    private ImageButton btnBack, btnFocusLocation, btnZoomIn, btnMoreOptions;
    private Button btnChooseDestination;
    private TextView tvLocationName, tvLocationAddress, tvDistanceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        btnFocusLocation = findViewById(R.id.btnFocusLocation);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnMoreOptions = findViewById(R.id.btnMoreOptions);
        btnChooseDestination = findViewById(R.id.btnChooseDestination);
        tvLocationName = findViewById(R.id.tvLocationName);
        tvLocationAddress = findViewById(R.id.tvLocationAddress);
        tvDistanceInfo = findViewById(R.id.tvDistanceInfo);

        // Get the SupportMapFragment and request notification when the map is ready
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set click listeners
        setupClickListeners();
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

        btnZoomIn.setOnClickListener(v -> {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        });

        btnChooseDestination.setOnClickListener(v -> {
            if (selectedLocation != null) {
                // Return selected location to previous activity
                // For now, just show a toast
                Toast.makeText(this, "Destination selected: " + tvLocationName.getText(),
                        Toast.LENGTH_SHORT).show();

                // In future: Set result and finish
                // Intent intent = new Intent();
                // intent.putExtra("SELECTED_LOCATION", locationName);
                // intent.putExtra("SELECTED_ADDRESS", locationAddress);
                // intent.putExtra("SELECTED_LAT", selectedLocation.latitude);
                // intent.putExtra("SELECTED_LNG", selectedLocation.longitude);
                // setResult(RESULT_OK, intent);
                // finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set default location (Ho Chi Minh City) if location permission is not granted
        LatLng defaultLocation = new LatLng(10.7769, 106.7009);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f));

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

        // Listen for camera idle to get the center location
        mMap.setOnCameraIdleListener(() -> {
            LatLng center = mMap.getCameraPosition().target;
            selectedLocation = center;

            // Update the marker position
            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
            }

            // Create a new marker at the center
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(center)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            currentLocationMarker = mMap.addMarker(markerOptions);

            // Get address for the location (in a real app, use Geocoder)
            // For simplicity, we'll use a fixed address for now
            tvLocationName.setText("142/43 An Binh St.");
            tvLocationAddress.setText("An Binh, P.6, Q.5, Hồ Chí Minh, 70000, Vietnam");
            tvDistanceInfo.setText("0.0km");
        });
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
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
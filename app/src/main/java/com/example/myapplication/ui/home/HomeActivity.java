package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvServices, rvOffers, rvHotspots;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        rvServices = findViewById(R.id.rvServices);
        rvOffers = findViewById(R.id.rvOffers);
        rvHotspots = findViewById(R.id.rvHotspots);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        Log.d("HomeActivity", "Views initialized");

        // Setup bottom navigation
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Already on home, do nothing
                return true;
            } else if (itemId == R.id.navigation_activity) {
                // TODO: Navigate to Activity screen
                return true;
            } else if (itemId == R.id.navigation_payment) {
                // TODO: Navigate to Payment screen
                return true;
            } else if (itemId == R.id.navigation_messages) {
                // TODO: Navigate to Messages screen
                return true;
            }
            return false;
        });
        setupServices();
        setupOffers();
        setupHotspots();
        bottomNavigation.setSelectedItemId(R.id.navigation_home);
        }

    private void setupServices() {
        List<ServiceItem> services = new ArrayList<>();
        services.add(new ServiceItem(R.drawable.ic_car, "Car"));
        services.add(new ServiceItem(R.drawable.ic_bike, "Bike"));

        Log.d("HomeActivity", "Services count: " + services.size());

        ServiceAdapter serviceAdapter = new ServiceAdapter(services);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvServices.setLayoutManager(layoutManager);
        rvServices.setAdapter(serviceAdapter);
    }

    private void setupOffers() {
        List<OfferItem> offers = new ArrayList<>();
        // Sử dụng drawable có sẵn thay vì những drawable chưa tồn tại
        offers.add(new OfferItem(R.drawable.ic_car, "Book Grab to station, upto 50K* off", "See more"));
        offers.add(new OfferItem(R.drawable.ic_car, "To Kumho station, discount upto 50K", "Book a ride now!"));

        OfferAdapter offerAdapter = new OfferAdapter(offers);
        rvOffers.setAdapter(offerAdapter);
    }

    private void setupHotspots() {
        List<HotspotItem> hotspots = new ArrayList<>();
        // Sử dụng drawable có sẵn thay vì những drawable chưa tồn tại
        hotspots.add(new HotspotItem(R.drawable.ic_car, "VAN HANH MALL", "8.3", "25% off"));
        hotspots.add(new HotspotItem(R.drawable.ic_bike, "Hùng Vương Plaza", "8.3", "-20%"));
        hotspots.add(new HotspotItem(R.drawable.ic_car, "AEON MALL TÂN PHÚ", "8.3", "-20%"));

        HotspotAdapter hotspotAdapter = new HotspotAdapter(hotspots);
        rvHotspots.setAdapter(hotspotAdapter);
    }
}
package com.example.myapplication.ui.home;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvServices, rvOffers, rvHotspots;
    private BottomNavigationView bottomNavigation;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        rvServices = findViewById(R.id.rvServices);
        rvOffers = findViewById(R.id.rvOffers);
        rvHotspots = findViewById(R.id.rvHotspots);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        ivProfile = findViewById(R.id.ivProfile);

        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
            // Animation khi mở màn hình profile
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

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

    public class EqualSpacingItemDecoration extends RecyclerView.ItemDecoration {
        public static final int HORIZONTAL = 0;
        public static final int VERTICAL = 1;

        private final int spacing;
        private final int displayMode;

        public EqualSpacingItemDecoration(int spacing, int displayMode) {
            this.spacing = spacing;
            this.displayMode = displayMode;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int itemCount = parent.getAdapter().getItemCount();

            if (displayMode == HORIZONTAL) {
                outRect.left = spacing;
                outRect.right = position == itemCount - 1 ? spacing : 0;
            } else {
                outRect.top = spacing;
                outRect.bottom = position == itemCount - 1 ? spacing : 0;
            }
        }
    }

    private void setupServices() {
        List<ServiceItem> services = new ArrayList<>();
        services.add(new ServiceItem(R.drawable.ic_car, "Car"));
        services.add(new ServiceItem(R.drawable.ic_bike, "Bike"));

        ServiceAdapter serviceAdapter = new ServiceAdapter(services, (serviceType, position) -> {
            // Handle service item click - navigate to TransportActivity
            Intent intent = new Intent(HomeActivity.this, com.example.myapplication.ui.transport.TransportActivity.class);
            intent.putExtra("TRANSPORT_TYPE", serviceType.toLowerCase());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Use a horizontal LinearLayoutManager instead of GridLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvServices.setLayoutManager(layoutManager);

        // Add equal spacing decoration
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int itemWidth = getResources().getDimensionPixelSize(R.dimen.service_item_width); // Define this in dimens.xml
        int totalItemsWidth = itemWidth * services.size();
        int totalSpacing = screenWidth - totalItemsWidth;
        int spaceBetweenItems = totalSpacing / (services.size() + 1);

        rvServices.addItemDecoration(new EqualSpacingItemDecoration(spaceBetweenItems, EqualSpacingItemDecoration.HORIZONTAL));
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
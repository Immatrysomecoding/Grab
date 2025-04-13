package com.example.myapplication.ui.subscription;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubscriptionBrowseFragment extends Fragment {

    private CardView cardGrabBike;
    private CardView cardGrabCar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription_browse, container, false);

        initViews(view);
        setupFirebase();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        cardGrabBike = view.findViewById(R.id.cardGrabBike);
        cardGrabCar = view.findViewById(R.id.cardGrabCar);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://grab-741f2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    }

    private void setupListeners() {
        cardGrabBike.setOnClickListener(v -> {
            showSubscriptionDetails("bike");
        });

        cardGrabCar.setOnClickListener(v -> {
            showSubscriptionDetails("car");
        });
    }

    private void showSubscriptionDetails(String vehicleType) {
        // Placeholder for subscription details dialog
        // In a real app, you would show a dialog with subscription details and payment options

        // For demo purposes, we'll just subscribe the user directly
        subscribeUser(vehicleType);
    }

    private void subscribeUser(String vehicleType) {
        if (currentUser == null) {
            Toast.makeText(getContext(), "You must be logged in to subscribe", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String subscriptionId = vehicleType.equals("bike") ? "bike_subscription" : "car_subscription";

        // Calculate start and end dates (15 days from now)
        Date startDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_YEAR, 15);
        Date endDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

        // Create subscription data
        Map<String, Object> subscriptionData = new HashMap<>();
        subscriptionData.put("subscriptionId", subscriptionId);
        subscriptionData.put("vehicleType", vehicleType);
        subscriptionData.put("startDate", dateFormat.format(startDate));
        subscriptionData.put("endDate", dateFormat.format(endDate));
        subscriptionData.put("status", "active");
        subscriptionData.put("discountPercentage", vehicleType.equals("bike") ? 25 : 20);
        subscriptionData.put("price", "17500");
        subscriptionData.put("autoRenew", false);

        // Store in Firebase
        mDatabase.child("user_subscriptions").child(userId).child(subscriptionId)
                .setValue(subscriptionData)
                .addOnSuccessListener(aVoid -> {
                    // Update user's hasActiveSubscription flag
                    mDatabase.child("users").child(userId).child("hasActiveSubscription").setValue(true);

                    Toast.makeText(getContext(), "Successfully subscribed to " +
                                    (vehicleType.equals("bike") ? "GrabBike" : "GrabCar") + " plan",
                            Toast.LENGTH_SHORT).show();

                    // Switch to My Subscriptions tab
                    if (getActivity() instanceof SubscriptionsActivity) {
                        ((SubscriptionsActivity) getActivity()).switchToMySubscriptionsTab();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to subscribe: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
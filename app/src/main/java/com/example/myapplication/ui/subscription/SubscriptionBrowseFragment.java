package com.example.myapplication.ui.subscription;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionBrowseFragment extends Fragment {

    private CardView cardGrabBike;
    private CardView cardGrabCar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private static final int REQUEST_SUBSCRIPTION = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription_browse, container, false);

        initViews(view);
        setupFirebase();
        addSubscriptionsToDatabase(); // Add default subscriptions if not exist
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    // Add sample subscriptions to database if they don't exist
    private void addSubscriptionsToDatabase() {
        mDatabase.child("subscriptions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (!snapshot.hasChild("bike_subscription")) {
//                    Map<String, Object> bikeSubscription = new HashMap<>();
//                    bikeSubscription.put("title", "Gói GrabBike GrabUnlimited");
//                    bikeSubscription.put("price", "17.500đ");
//                    bikeSubscription.put("duration", "15 days");
//                    bikeSubscription.put("discountPercent", "25%");
//                    bikeSubscription.put("worthAmount", "3.470.000đ");
//                    bikeSubscription.put("bikeDiscount", "25%");
//                    bikeSubscription.put("carDiscount", "10%");
//                    bikeSubscription.put("bikeLimit", "x99");
//                    bikeSubscription.put("carLimit", "x10");
//                    mDatabase.child("subscriptions").child("bike_subscription").setValue(bikeSubscription);
//                }
//
//                if (!snapshot.hasChild("car_subscription")) {
//                    Map<String, Object> carSubscription = new HashMap<>();
//                    carSubscription.put("title", "Gói GrabCar GrabUnlimited");
//                    carSubscription.put("price", "17.500đ");
//                    carSubscription.put("duration", "15 days");
//                    carSubscription.put("discountPercent", "20%");
//                    carSubscription.put("worthAmount", "3.000.000đ");
//                    carSubscription.put("bikeDiscount", "10%");
//                    carSubscription.put("carDiscount", "20%");
//                    carSubscription.put("bikeLimit", "x10");
//                    carSubscription.put("carLimit", "x99");
//                    mDatabase.child("subscriptions").child("car_subscription").setValue(carSubscription);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing
            }
        });
    }

    private void setupListeners() {
        cardGrabBike.setOnClickListener(v -> {
            openSubscriptionDetails("bike");
        });

        cardGrabCar.setOnClickListener(v -> {
            openSubscriptionDetails("car");
        });
    }

    private void openSubscriptionDetails(String vehicleType) {
        Intent intent = new Intent(getActivity(), SubscriptionDetailActivity.class);
        intent.putExtra("subscription_type", vehicleType);
        startActivityForResult(intent, REQUEST_SUBSCRIPTION);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SUBSCRIPTION && resultCode == getActivity().RESULT_OK) {
            // Switch to My Subscriptions tab after successful subscription
            if (getActivity() instanceof SubscriptionsActivity) {
                ((SubscriptionsActivity) getActivity()).switchToMySubscriptionsTab();
            }
        }
    }
}
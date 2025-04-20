package com.example.myapplication.ui.subscription;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubscriptionReviewActivity extends AppCompatActivity {

    private String subscriptionType;
    private String subscriptionId;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // UI Components
    private ImageButton btnBack;
    private TextView tvSubscriptionTitle, tvDuration, tvEndDate, tvPrice, tvTotal;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_review);

        // Get subscription info from intent
        subscriptionType = getIntent().getStringExtra("subscription_type");
        subscriptionId = getIntent().getStringExtra("subscription_id");

        if (subscriptionType == null || subscriptionId == null) {
            finish(); // Exit if no subscription data
            return;
        }

        initViews();
        setupFirebase();
        loadSubscriptionDetails();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvSubscriptionTitle = findViewById(R.id.tvSubscriptionTitle);
        tvDuration = findViewById(R.id.tvDuration);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvPrice = findViewById(R.id.tvPrice);
        tvTotal = findViewById(R.id.tvTotal);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    private void setupFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void loadSubscriptionDetails() {
        mDatabase.child("subscriptions").child(subscriptionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String title = snapshot.child("title").getValue(String.class);
                    String price = snapshot.child("price").getValue(String.class);
                    String duration = snapshot.child("duration").getValue(String.class);

                    // Calculate end date
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(duration.split(" ")[0]));
                    Date endDate = calendar.getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    String formattedEndDate = dateFormat.format(endDate);

                    // Set data to views
                    tvSubscriptionTitle.setText(title);
                    tvDuration.setText(duration);
                    tvEndDate.setText("Package will end on " + formattedEndDate);
                    tvPrice.setText(price);
                    tvTotal.setText(price); // Same as price for now (can add tax etc. if needed)

                    // Update confirm button text
                    btnConfirm.setText("Confirm • " + price);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SubscriptionReviewActivity.this, "Failed to load subscription details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        btnConfirm.setOnClickListener(v -> {
            if (currentUser != null) {
                subscribeUser();
            } else {
                Toast.makeText(this, "You must be logged in to subscribe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subscribeUser() {
        String userId = currentUser.getUid();

        // Generate start and end dates
        Date startDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        mDatabase.child("subscriptions").child(subscriptionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String duration = snapshot.child("duration").getValue(String.class);
                    int days = Integer.parseInt(duration.split(" ")[0]);

                    calendar.add(Calendar.DAY_OF_YEAR, days);
                    Date endDate = calendar.getTime();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

                    // Create subscription data
                    Map<String, Object> subscriptionData = new HashMap<>();
                    subscriptionData.put("subscriptionId", subscriptionId);
                    subscriptionData.put("vehicleType", subscriptionType);
                    subscriptionData.put("startDate", dateFormat.format(startDate));
                    subscriptionData.put("endDate", dateFormat.format(endDate));
                    subscriptionData.put("status", "active");
                    subscriptionData.put("discountPercentage",
                            subscriptionType.equals("bike") ? 25 : 20); // 25% for bike, 20% for car
                    subscriptionData.put("price", snapshot.child("price").getValue(String.class));
                    subscriptionData.put("autoRenew", false);

                    // Store in Firebase Database
                    mDatabase.child("user_subscriptions").child(userId).child(subscriptionId)
                            .setValue(subscriptionData)
                            .addOnSuccessListener(aVoid -> {
                                // Update user's hasActiveSubscription flag
                                mDatabase.child("users").child(userId).child("hasActiveSubscription").setValue(true);

                                Toast.makeText(SubscriptionReviewActivity.this,
                                        "Successfully subscribed!", Toast.LENGTH_SHORT).show();

                                // Go back to SubscriptionsActivity
                                setResult(RESULT_OK);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(SubscriptionReviewActivity.this,
                                        "Failed to subscribe: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SubscriptionReviewActivity.this, "Failed to process subscription", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
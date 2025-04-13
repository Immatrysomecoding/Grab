package com.example.myapplication.ui.subscription;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MySubscriptionsFragment extends Fragment {

    // UI elements
    private LinearLayout emptyStateLayout;
    private Button btnBrowse;
    private RecyclerView rvMySubscriptions;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    // Data
    private List<SubscriptionItem> subscriptionItems;
    private SubscriptionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_subscriptions, container, false);

        initViews(view);
        setupFirebase();
        setupRecyclerView();
        setupListeners();
        loadSubscriptions();

        return view;
    }

    private void initViews(View view) {
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        btnBrowse = view.findViewById(R.id.btnBrowse);
        rvMySubscriptions = view.findViewById(R.id.rvMySubscriptions);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void setupRecyclerView() {
        subscriptionItems = new ArrayList<>();
        adapter = new SubscriptionAdapter(getContext(), subscriptionItems);
        rvMySubscriptions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMySubscriptions.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBrowse.setOnClickListener(v -> {
            if (getActivity() instanceof SubscriptionsActivity) {
                ((SubscriptionsActivity) getActivity()).switchToBrowseTab();
            }
        });
    }

    private void loadSubscriptions() {
        if (currentUser == null) {
            showEmptyState();
            return;
        }

        String userId = currentUser.getUid();

        mDatabase.child("user_subscriptions").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        subscriptionItems.clear();

                        if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
                            showEmptyState();
                            return;
                        }

                        for (DataSnapshot subscriptionSnapshot : dataSnapshot.getChildren()) {
                            SubscriptionItem item = subscriptionSnapshot.getValue(SubscriptionItem.class);

                            if (item != null) {
                                // Check if subscription is still active
                                if (isSubscriptionActive(item.getEndDate())) {
                                    subscriptionItems.add(item);
                                }
                            }
                        }

                        if (subscriptionItems.isEmpty()) {
                            showEmptyState();
                        } else {
                            showSubscriptionsList();
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to load subscriptions", Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                });
    }

    private boolean isSubscriptionActive(String endDateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date endDate = dateFormat.parse(endDateStr);
            Date currentDate = new Date();

            return endDate != null && endDate.after(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showEmptyState() {
        if (emptyStateLayout != null && rvMySubscriptions != null) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            rvMySubscriptions.setVisibility(View.GONE);
        }
    }

    private void showSubscriptionsList() {
        if (emptyStateLayout != null && rvMySubscriptions != null) {
            emptyStateLayout.setVisibility(View.GONE);
            rvMySubscriptions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSubscriptions();
    }
}
package com.example.myapplication.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityFragment extends Fragment {

    private RecyclerView rvActivityHistory;
    private TextView tvAll, tvCar, tvBike;
    private ActivityAdapter adapter;
    private List<ActivityItem> allActivityItems;
    private List<ActivityItem> filteredActivityItems;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        initViews(view);
        setupFirebase();
        setupListeners();

        // Thêm dữ liệu mẫu nếu chưa có
//        addSampleTripsIfNeeded();

        // Tải lịch sử chuyến đi
        loadTripHistory();

        return view;
    }

    private void initViews(View view) {
        rvActivityHistory = view.findViewById(R.id.rvActivityHistory);
        tvAll = view.findViewById(R.id.tvAll);
        tvCar = view.findViewById(R.id.tvCar);
        tvBike = view.findViewById(R.id.tvBike);

        // Khởi tạo RecyclerView
        allActivityItems = new ArrayList<>();
        filteredActivityItems = new ArrayList<>();
        adapter = new ActivityAdapter(filteredActivityItems);
        rvActivityHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvActivityHistory.setAdapter(adapter);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://grab-741f2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    }

    private void setupListeners() {
        tvAll.setOnClickListener(v -> {
            setFilterSelected(tvAll, true);
            setFilterSelected(tvCar, false);
            setFilterSelected(tvBike, false);
            filterTrips("all");
        });

        tvCar.setOnClickListener(v -> {
            setFilterSelected(tvAll, false);
            setFilterSelected(tvCar, true);
            setFilterSelected(tvBike, false);
            filterTrips("car");
        });

        tvBike.setOnClickListener(v -> {
            setFilterSelected(tvAll, false);
            setFilterSelected(tvCar, false);
            setFilterSelected(tvBike, true);
            filterTrips("bike");
        });
    }

    private void filterTrips(String vehicleType) {
        filteredActivityItems.clear();

        if ("all".equals(vehicleType)) {
            filteredActivityItems.addAll(allActivityItems);
        } else {
            for (ActivityItem item : allActivityItems) {
                if (vehicleType.equals(item.getVehicleType())) {
                    filteredActivityItems.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void setFilterSelected(TextView textView, boolean isSelected) {
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.filter_bg_selected);
            textView.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            textView.setBackgroundResource(R.drawable.filter_bg_unselected);
            textView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void loadTripHistory() {
        if (currentUser == null) {
            return;
        }

        String userId = currentUser.getUid();

        // Tải lịch sử chuyến đi từ Firebase
        mDatabase.child("trips").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allActivityItems.clear();

                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    ActivityItem item = tripSnapshot.getValue(ActivityItem.class);
                    if (item != null) {
                        allActivityItems.add(item);
                    }
                }

                // Sắp xếp theo thời gian mới nhất
                Collections.sort(allActivityItems, (t1, t2) -> t2.getTimestampValue().compareTo(t1.getTimestampValue()));

                // Áp dụng bộ lọc hiện tại
                if (tvAll.getCurrentTextColor() == getResources().getColor(android.R.color.white)) {
                    filterTrips("all");
                } else if (tvCar.getCurrentTextColor() == getResources().getColor(android.R.color.white)) {
                    filterTrips("car");
                } else if (tvBike.getCurrentTextColor() == getResources().getColor(android.R.color.white)) {
                    filterTrips("bike");
                } else {
                    filterTrips("all"); // Mặc định hiển thị tất cả
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load trip history", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void addSampleTripsIfNeeded() {
//        if (currentUser == null) {
//            return;
//        }
//
//        String userId = currentUser.getUid();
//
//        mDatabase.child("trips").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Nếu không có dữ liệu chuyến đi, thêm dữ liệu mẫu
//                if (!dataSnapshot.exists()) {
//                    // Thêm dữ liệu mẫu
//                    List<Map<String, Object>> sampleTrips = new ArrayList<>();
//
//                    // Chuyến đi 1 - Car
//                    Map<String, Object> trip1 = new HashMap<>();
//                    trip1.put("id", "trip1");
//                    trip1.put("vehicleType", "car");
//                    trip1.put("startLocation", "523 Hong Bang St.");
//                    trip1.put("endLocation", "143/9 An Binh St.");
//                    trip1.put("price", "43.000đ");
//                    trip1.put("timestamp", "26 Mar 2025, 05:25 PM");
//                    trip1.put("timestampValue", "2025-03-26T17:25:00.000Z");
//
//                    // Chuyến đi 2 - Car
//                    Map<String, Object> trip2 = new HashMap<>();
//                    trip2.put("id", "trip2");
//                    trip2.put("vehicleType", "car");
//                    trip2.put("startLocation", "9 Dong Son St.");
//                    trip2.put("endLocation", "523 Hong Bang St.");
//                    trip2.put("price", "56.000đ");
//                    trip2.put("timestamp", "26 Mar 2025, 03:44 PM");
//                    trip2.put("timestampValue", "2025-03-26T15:44:00.000Z");
//
//                    // Chuyến đi 3 - Car
//                    Map<String, Object> trip3 = new HashMap<>();
//                    trip3.put("id", "trip3");
//                    trip3.put("vehicleType", "car");
//                    trip3.put("startLocation", "143/9 An Binh St.");
//                    trip3.put("endLocation", "9 Dong Son St.");
//                    trip3.put("price", "59.000đ");
//                    trip3.put("timestamp", "26 Mar 2025, 01:55 PM");
//                    trip3.put("timestampValue", "2025-03-26T13:55:00.000Z");
//
//                    // Chuyến đi 4 - Bike
//                    Map<String, Object> trip4 = new HashMap<>();
//                    trip4.put("id", "trip4");
//                    trip4.put("vehicleType", "bike");
//                    trip4.put("startLocation", "University Of Science");
//                    trip4.put("endLocation", "Nguyen Van Cu Campus - Pickup/Drop-off");
//                    trip4.put("price", "18.000đ");
//                    trip4.put("timestamp", "14 Jan 2025, 11:12 AM");
//                    trip4.put("timestampValue", "2025-01-14T11:12:00.000Z");
//
//                    // Chuyến đi 5 - Bike
//                    Map<String, Object> trip5 = new HashMap<>();
//                    trip5.put("id", "trip5");
//                    trip5.put("vehicleType", "bike");
//                    trip5.put("startLocation", "143/9 An Binh St.");
//                    trip5.put("endLocation", "Trường Đại Học Khoa Học Tự Nhiên - Cơ sở Nguyễn Văn Cừ");
//                    trip5.put("price", "18.000đ");
//                    trip5.put("timestamp", "14 Jan 2025, 07:26 AM");
//                    trip5.put("timestampValue", "2025-01-14T07:26:00.000Z");
//
//                    // Thêm vào danh sách
//                    sampleTrips.add(trip1);
//                    sampleTrips.add(trip2);
//                    sampleTrips.add(trip3);
//                    sampleTrips.add(trip4);
//                    sampleTrips.add(trip5);
//
//                    // Thêm vào Firebase
//                    for (Map<String, Object> trip : sampleTrips) {
//                        String tripId = (String) trip.get("id");
//                        mDatabase.child("trips").child(userId).child(tripId).setValue(trip);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getContext(), "Failed to check trip data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    // Adapter cho RecyclerView
    private class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

        private List<ActivityItem> items;

        public ActivityAdapter(List<ActivityItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_activity, parent, false);
            return new ActivityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
            ActivityItem item = items.get(position);

            // Thiết lập route
            String route = item.getStartLocation() + " to " + item.getEndLocation();
            holder.tvRoute.setText(route);

            // Thiết lập ngày giờ
            holder.tvDateTime.setText(item.getTimestamp());

            // Thiết lập giá tiền
            holder.tvPrice.setText(item.getPrice());

            // Thiết lập icon xe
            if ("bike".equals(item.getVehicleType())) {
                holder.ivVehicle.setImageResource(R.drawable.ic_bike);
            } else {
                holder.ivVehicle.setImageResource(R.drawable.ic_car);
            }

            // Thiết lập sự kiện click cho nút Rebook
            holder.tvRebook.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Rebook feature coming soon!", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
        class ActivityViewHolder extends RecyclerView.ViewHolder {
            ImageView ivVehicle;
            TextView tvRoute, tvDateTime, tvPrice, tvRebook;

            public ActivityViewHolder(@NonNull View itemView) {
                super(itemView);
                ivVehicle = itemView.findViewById(R.id.ivVehicle);
                tvRoute = itemView.findViewById(R.id.tvRoute);
                tvDateTime = itemView.findViewById(R.id.tvDateTime);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvRebook = itemView.findViewById(R.id.tvRebook);
            }
        }
    }

    // Model class cho ActivityItem
    public static class ActivityItem {
        private String id;
        private String vehicleType;
        private String startLocation;
        private String endLocation;
        private String price;
        private String timestamp;
        private String timestampValue;

        public ActivityItem() {
            // Constructor rỗng cần thiết cho Firebase
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public String getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(String startLocation) {
            this.startLocation = startLocation;
        }

        public String getEndLocation() {
            return endLocation;
        }

        public void setEndLocation(String endLocation) {
            this.endLocation = endLocation;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getTimestampValue() {
            return timestampValue;
        }

        public void setTimestampValue(String timestampValue) {
            this.timestampValue = timestampValue;
        }
    }
}
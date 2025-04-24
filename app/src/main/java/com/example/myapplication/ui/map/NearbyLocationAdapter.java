package com.example.myapplication.ui.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class NearbyLocationAdapter extends RecyclerView.Adapter<NearbyLocationAdapter.LocationViewHolder> {

    public interface OnLocationSelectedListener {
        void onLocationSelected(NearbyLocation location);
    }

    private List<NearbyLocation> locations;
    private OnLocationSelectedListener listener;
    private int selectedPosition = 0;

    public NearbyLocationAdapter(List<NearbyLocation> locations, OnLocationSelectedListener listener) {
        // Đảm bảo danh sách không null
        this.locations = locations != null ? locations : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_map_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        NearbyLocation location = locations.get(position);

        // Tất cả card có background thông thường (không có card nào được highlight)
        holder.itemView.setBackgroundResource(R.drawable.bg_location_item);

        // Set data to views
        holder.tvLocationName.setText(location.getName());
        holder.tvLocationAddress.setText(location.getAddress());
        holder.tvDistance.setText(location.getDistance() + "km");

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLocationSelected(location);
            }
        });

        // Set click listener cho nút More Options
        holder.btnMoreOptions.setOnClickListener(v -> {
            // Xử lý khi nhấn vào nút more options
        });
    }

    @Override
    public int getItemCount() {
        // Kiểm tra null để tránh lỗi
        return locations != null ? locations.size() : 0;
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLocationIcon;
        TextView tvLocationName, tvLocationAddress, tvDistance;
        ImageButton btnMoreOptions;

        LocationViewHolder(View itemView) {
            super(itemView);
            ivLocationIcon = itemView.findViewById(R.id.ivLocationIcon);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvLocationAddress = itemView.findViewById(R.id.tvLocationAddress);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            btnMoreOptions = itemView.findViewById(R.id.btnMoreOptions);
        }
    }

    // Phương thức để cập nhật danh sách địa điểm
    public void updateLocations(List<NearbyLocation> newLocations) {
        if (newLocations == null) {
            newLocations = new ArrayList<>();
        }
        this.locations.clear();
        this.locations.addAll(newLocations);
        notifyDataSetChanged();
    }

    // Phương thức để đặt lại vị trí được chọn
    public void setSelectedPosition(int position) {
        if (position >= 0 && position < locations.size()) {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
        }
    }

    // Phương thức để lấy vị trí đang được chọn
    public int getSelectedPosition() {
        return selectedPosition;
    }

    // Phương thức để lấy địa điểm đang được chọn
    public NearbyLocation getSelectedLocation() {
        if (selectedPosition >= 0 && selectedPosition < locations.size()) {
            return locations.get(selectedPosition);
        }
        return null;
    }
}
package com.example.myapplication.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private List<TripItem> tripItems;

    public ActivityAdapter(List<TripItem> tripItems) {
        this.tripItems = tripItems;
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
        TripItem item = tripItems.get(position);

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
            Toast.makeText(holder.itemView.getContext(),
                    "Rebook feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return tripItems.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        ImageView ivVehicle;
        TextView tvRoute, tvDateTime, tvPrice, tvRebook;

        ActivityViewHolder(View itemView) {
            super(itemView);
            ivVehicle = itemView.findViewById(R.id.ivVehicle);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRebook = itemView.findViewById(R.id.tvRebook);
        }
    }
}
package com.example.myapplication.ui.transport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class RecentLocationAdapter extends RecyclerView.Adapter<RecentLocationAdapter.ViewHolder> {

    private List<RecentLocation> locations;
    private OnLocationClickListener listener;

    public interface OnLocationClickListener {
        void onLocationClick(RecentLocation location);
    }

    public RecentLocationAdapter(List<RecentLocation> locations, OnLocationClickListener listener) {
        this.locations = locations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentLocation location = locations.get(position);
        holder.tvLocationName.setText(location.getName());
        holder.tvLocationAddress.setText(location.getFullAddress());

        // Set icon based on location type
        switch (location.getType()) {
            case "home":
                holder.ivLocationIcon.setImageResource(R.drawable.ic_home);
                break;
            case "work":
                holder.ivLocationIcon.setImageResource(R.drawable.ic_rebook); // Use a work icon if available
                break;
            default:
                holder.ivLocationIcon.setImageResource(R.drawable.ic_rebook); // Use a history icon if available
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLocationClick(location);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardIcon;
        ImageView ivLocationIcon;
        TextView tvLocationName;
        TextView tvLocationAddress;

        ViewHolder(View itemView) {
            super(itemView);
            cardIcon = itemView.findViewById(R.id.cardIcon);
            ivLocationIcon = itemView.findViewById(R.id.ivLocationIcon);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvLocationAddress = itemView.findViewById(R.id.tvLocationAddress);
        }
    }
}
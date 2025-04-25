package com.example.myapplication.ui.transport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    public interface OnLocationClickListener {
        void onLocationClick(LocationItem location);
    }

    private List<LocationItem> locations;
    private OnLocationClickListener listener;

    public LocationAdapter(List<LocationItem> locations, OnLocationClickListener listener) {
        this.locations = locations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationItem location = locations.get(position);

        // Thiết lập tiêu đề và địa chỉ
        holder.tvTitle.setText(location.getTitle());
        holder.tvAddress.setText(location.getAddress());
        holder.tvDistance.setText(location.getDistance());

        // Thiết lập icon phù hợp với loại địa điểm
        switch (location.getType()) {
            case LocationItem.TYPE_HOME:
                holder.ivIcon.setImageResource(R.drawable.ic_house);
                holder.ivIcon.setBackgroundResource(R.drawable.circle_light_blue);
                holder.ivIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.colorGreen));
                break;
            case LocationItem.TYPE_RECENT:
            default:
                holder.ivIcon.setImageResource(R.drawable.ic_clock);
                holder.ivIcon.setBackgroundResource(R.drawable.circle_light_blue);
                holder.ivIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.colorBlue));
                break;
        }

        // Thiết lập sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLocationClick(location);
            }
        });

        holder.btnMore.setOnClickListener(v -> {
            // Xử lý khi click vào nút More
        });
    }


    @Override
    public int getItemCount() {
        return locations != null ? locations.size() : 0;
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvAddress, tvDistance;
        ImageButton btnMore;

        LocationViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
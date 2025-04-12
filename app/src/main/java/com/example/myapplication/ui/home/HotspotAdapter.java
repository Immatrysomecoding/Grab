package com.example.myapplication.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class HotspotAdapter extends RecyclerView.Adapter<HotspotAdapter.HotspotViewHolder> {

    private List<HotspotItem> hotspotItems;

    public HotspotAdapter(List<HotspotItem> hotspotItems) {
        this.hotspotItems = hotspotItems;
    }

    @NonNull
    @Override
    public HotspotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotspot, parent, false);
        return new HotspotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotspotViewHolder holder, int position) {
        HotspotItem item = hotspotItems.get(position);
        holder.ivHotspotImage.setImageResource(item.getImageResId());
        holder.tvHotspotName.setText(item.getName());
        holder.tvRating.setText(item.getRating());
        holder.tvDiscount.setText(item.getDiscount());
    }

    @Override
    public int getItemCount() {
        return hotspotItems.size();
    }

    static class HotspotViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHotspotImage;
        TextView tvHotspotName, tvRating, tvDiscount, tvBookNow;

        public HotspotViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHotspotImage = itemView.findViewById(R.id.ivHotspotImage);
            tvHotspotName = itemView.findViewById(R.id.tvHotspotName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvBookNow = itemView.findViewById(R.id.tvBookNow);
        }
    }
}
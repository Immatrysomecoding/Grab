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

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private List<OfferItem> offerItems;

    public OfferAdapter(List<OfferItem> offerItems) {
        this.offerItems = offerItems;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        OfferItem item = offerItems.get(position);
        holder.ivOfferImage.setImageResource(item.getImageResId());
        holder.tvOfferTitle.setText(item.getTitle());
        holder.tvOfferSubtitle.setText(item.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return offerItems.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        ImageView ivOfferImage;
        TextView tvOfferTitle, tvOfferSubtitle;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOfferImage = itemView.findViewById(R.id.ivOfferImage);
            tvOfferTitle = itemView.findViewById(R.id.tvOfferTitle);
            tvOfferSubtitle = itemView.findViewById(R.id.tvOfferSubtitle);
        }
    }
}
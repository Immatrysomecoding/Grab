package com.example.myapplication.ui.transport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class FeatureCardAdapter extends RecyclerView.Adapter<FeatureCardAdapter.ViewHolder> {

    private List<FeatureCard> featureCards;
    private OnFeatureCardClickListener listener;

    public interface OnFeatureCardClickListener {
        void onFeatureCardClick(FeatureCard card, int position);
    }

    public FeatureCardAdapter(List<FeatureCard> featureCards, OnFeatureCardClickListener listener) {
        this.featureCards = featureCards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feature_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeatureCard card = featureCards.get(position);
        holder.tvTitle.setText(card.getTitle());
        holder.tvDescription.setText(card.getDescription());

        if (card.getSubDescription() != null && !card.getSubDescription().isEmpty()) {
            holder.tvSubdescription.setVisibility(View.VISIBLE);
            holder.tvSubdescription.setText(card.getSubDescription());
        } else {
            holder.tvSubdescription.setVisibility(View.GONE);
        }

        holder.ivImage.setImageResource(card.getImageResId());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFeatureCardClick(card, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return featureCards.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvSubdescription;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSubdescription = itemView.findViewById(R.id.tvSubdescription);
        }
    }
}
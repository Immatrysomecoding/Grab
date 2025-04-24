package com.example.myapplication.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class InsightPagerAdapter extends RecyclerView.Adapter<InsightPagerAdapter.InsightViewHolder> {

    private List<InsightCard> insightCards;

    public InsightPagerAdapter(List<InsightCard> insightCards) {
        this.insightCards = insightCards;
    }

    @NonNull
    @Override
    public InsightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_insight_card, parent, false);
        return new InsightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsightViewHolder holder, int position) {
        InsightCard card = insightCards.get(position);

        holder.tvTitle.setText(card.getTitle());
        holder.tvSubtitle.setText(card.getSubtitle());
        holder.tvAction.setText(card.getActionText());

        // Set background color
        holder.cardView.setCardBackgroundColor(card.getBackgroundColor());
    }

    @Override
    public int getItemCount() {
        return insightCards.size();
    }

    static class InsightViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle;
        TextView tvSubtitle;
        TextView tvAction;

        InsightViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvTitle = itemView.findViewById(R.id.tvInsightTitle);
            tvSubtitle = itemView.findViewById(R.id.tvInsightSubtitle);
            tvAction = itemView.findViewById(R.id.tvInsightAction);
        }
    }
}
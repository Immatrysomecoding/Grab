package com.example.myapplication.ui.subscription;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder> {

    private final List<SubscriptionItem> items;
    private final Context context;

    public SubscriptionAdapter(Context context, List<SubscriptionItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_subscription, parent, false);
        return new SubscriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewHolder holder, int position) {
        SubscriptionItem item = items.get(position);

        // Set subscription name
        String subscriptionName = item.getVehicleType().equals("bike") ?
                "GrabBike Unlimited" : "GrabCar Unlimited";
        holder.tvSubscriptionName.setText(subscriptionName);

        // Set discount text
        holder.tvDiscount.setText(item.getDiscountPercentage() + "% off on all rides");

        // Set icon based on vehicle type
        if (item.getVehicleType().equals("bike")) {
            holder.ivVehicleIcon.setImageResource(R.drawable.ic_bike);
        } else {
            holder.ivVehicleIcon.setImageResource(R.drawable.ic_car);
        }

        // Format and set end date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            Date endDate = inputFormat.parse(item.getEndDate());
            if (endDate != null) {
                holder.tvEndDate.setText(outputFormat.format(endDate));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.tvEndDate.setText(item.getEndDate());
        }

        // Set renew button click listener
        holder.btnRenew.setOnClickListener(v -> {
            Toast.makeText(context, "Renewal feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SubscriptionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivVehicleIcon;
        TextView tvSubscriptionName, tvDiscount, tvStatus, tvEndDate;
        Button btnRenew;

        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivVehicleIcon = itemView.findViewById(R.id.ivVehicleIcon);
            tvSubscriptionName = itemView.findViewById(R.id.tvSubscriptionName);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            btnRenew = itemView.findViewById(R.id.btnRenew);
        }
    }
}
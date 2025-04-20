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

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceItem> serviceItems;
    private OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onServiceClick(String serviceName, int position);
    }

    public ServiceAdapter(List<ServiceItem> serviceItems, OnServiceClickListener listener) {
        this.serviceItems = serviceItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceItem item = serviceItems.get(position);
        holder.ivServiceIcon.setImageResource(item.getImageResId());
        holder.tvServiceName.setText(item.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceClick(item.getName(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceItems.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivServiceIcon;
        TextView tvServiceName;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivServiceIcon = itemView.findViewById(R.id.ivServiceIcon);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
        }
    }
}
package com.satnamsinghmaggo.paathapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BaniAdapter extends RecyclerView.Adapter<BaniAdapter.BaniViewHolder> {

    private final Context context;
    private final List<Bani> baniList;

    public BaniAdapter(Context context, List<Bani> baniList) {
        this.context = context;
        this.baniList = baniList;
    }

    @NonNull
    @Override
    public BaniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bani, parent, false);
        return new BaniViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaniViewHolder holder, int position) {
        Bani bani = baniList.get(position);
        holder.titleTextView.setText(bani.getTitle());
        holder.timeTextView.setText(bani.getDescription());
        
        holder.itemView.setOnClickListener(v -> {
            // Handle item click
            // You can start a new activity or show details here
        });
    }

    @Override
    public int getItemCount() {
        return baniList.size();
    }

    static class BaniViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView timeTextView;

        BaniViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvBaniName);
            timeTextView = itemView.findViewById(R.id.tvBaniTime);
        }
    }
} 
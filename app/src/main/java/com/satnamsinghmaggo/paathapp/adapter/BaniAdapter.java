package com.satnamsinghmaggo.paathapp.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.satnamsinghmaggo.paathapp.App;
import com.satnamsinghmaggo.paathapp.R;
import com.satnamsinghmaggo.paathapp.model.Bani;

import java.util.List;

public class BaniAdapter extends RecyclerView.Adapter<BaniAdapter.BaniViewHolder> {

    private List<Bani> banis;
    private final OnItemClickListener onItemClickListener;

    private float fontSize = App.userFontSize;

    public interface OnItemClickListener {
        void onItemClick(Bani bani);
    }

    public BaniAdapter(List<Bani> banis, OnItemClickListener onItemClickListener) {
        this.banis = banis;
        this.onItemClickListener = onItemClickListener;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }


    public static class BaniViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public BaniViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBaniName);



        }
    }

    @NonNull
    @Override
    public BaniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bani, parent, false);
        return new BaniViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaniViewHolder holder, int position) {
        Bani bani = banis.get(position);
        holder.tvName.setText(bani.getName());
        holder.tvName.setTextSize(fontSize); // 👈 Apply font size


        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(bani));
    }

    @Override
    public int getItemCount() {
        return banis.size();
    }


    public void updateBanis(List<Bani> newBanis) {
        this.banis = newBanis;
        notifyDataSetChanged();
    }
}

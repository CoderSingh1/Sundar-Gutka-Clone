package com.satnamsinghmaggo.paathapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.satnamsinghmaggo.paathapp.R;
import com.satnamsinghmaggo.paathapp.util.BaniOrderTouchHelper;

import java.util.List;

public class BaniOrderAdapter extends RecyclerView.Adapter<BaniOrderAdapter.BaniViewHolder> implements BaniOrderTouchHelper.MoveListener {

    private List<String> banis;
    private OnItemMoveListener moveListener;

    public interface OnItemMoveListener {
        void onItemMove(int fromPosition, int toPosition);
    }

    public BaniOrderAdapter(List<String> banis, OnItemMoveListener moveListener) {
        this.banis = banis;
        this.moveListener = moveListener;
    }

    @NonNull
    @Override
    public BaniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bani_order, parent, false);
        return new BaniViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaniViewHolder holder, int position) {
        holder.bind(banis.get(position));
    }

    @Override
    public int getItemCount() {
        return banis.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (moveListener != null) {
            moveListener.onItemMove(fromPosition, toPosition);
            return true;
        }
        return false;
    }

    public BaniOrderTouchHelper.MoveListener getMoveListener() {
        return this;
    }

    public List<String> getBaniIds() {
        return banis;
    }

    static class BaniViewHolder extends RecyclerView.ViewHolder {
        private final TextView baniName;

        BaniViewHolder(@NonNull View itemView) {
            super(itemView);
            baniName = itemView.findViewById(R.id.bani_name);
        }

        void bind(String bani) {
            baniName.setText(bani);
        }
    }
} 
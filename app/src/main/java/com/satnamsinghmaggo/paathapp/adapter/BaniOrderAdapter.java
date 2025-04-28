package com.satnamsinghmaggo.paathapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.satnamsinghmaggo.paathapp.databinding.ItemBaniOrderBinding;
import com.satnamsinghmaggo.paathapp.model.Bani;
import java.util.ArrayList;
import java.util.List;

public class BaniOrderAdapter extends RecyclerView.Adapter<BaniOrderAdapter.BaniViewHolder> {

    private List<Bani> banis;
    private final OnOrderChangedListener onOrderChanged;

    public interface OnOrderChangedListener {
        void onOrderChanged(List<Bani> updatedList);
    }

    public BaniOrderAdapter(List<Bani> banis, OnOrderChangedListener listener) {
        this.banis = banis;
        this.onOrderChanged = listener;
    }

    public static class BaniViewHolder extends RecyclerView.ViewHolder {
        private final ItemBaniOrderBinding binding;

        public BaniViewHolder(ItemBaniOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Bani bani) {
            binding.baniName.setText(bani.getName());
        }
    }

    @NonNull
    @Override
    public BaniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemBaniOrderBinding binding = ItemBaniOrderBinding.inflate(inflater, parent, false);
        return new BaniViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaniViewHolder holder, int position) {
        holder.bind(banis.get(position));
    }

    @Override
    public int getItemCount() {
        return banis.size();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        List<Bani> updatedList = new ArrayList<>(banis);
        Bani movedItem = updatedList.remove(fromPosition);
        updatedList.add(toPosition, movedItem);
        banis = updatedList;
        notifyItemMoved(fromPosition, toPosition);
        onOrderChanged.onOrderChanged(banis);
    }

    public void updateList(List<Bani> newList) {
        banis = newList;
        notifyDataSetChanged();
    }
}

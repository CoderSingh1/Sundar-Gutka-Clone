package com.satnamsinghmaggo.paathapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.satnamsinghmaggo.paathapp.R;
import com.satnamsinghmaggo.paathapp.adapter.BaniOrderAdapter;
import com.satnamsinghmaggo.paathapp.util.BaniOrderTouchHelper;
import com.satnamsinghmaggo.paathapp.util.BaniPreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class BaniOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private BaniOrderAdapter adapter;
    private BaniPreferenceManager preferenceManager;
    private List<String> baniIds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bani_order, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view);
        preferenceManager = new BaniPreferenceManager(requireContext());
        
        setupRecyclerView();
        
        return view;
    }

    private void setupRecyclerView() {
        baniIds = preferenceManager.getBaniOrder();
        if (baniIds.isEmpty()) {
            // Initialize with default order if no saved order exists
            baniIds = new ArrayList<>();
            baniIds.add("japji");
            baniIds.add("jaap");
            baniIds.add("tavprasad");
            // Add more default banis as needed
        }

        adapter = new BaniOrderAdapter(baniIds, (fromPosition, toPosition) -> {
            String movedItem = baniIds.remove(fromPosition);
            baniIds.add(toPosition, movedItem);
            adapter.notifyItemMoved(fromPosition, toPosition);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new BaniOrderTouchHelper(adapter.getMoveListener());
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save the current order when fragment is paused
        if (adapter != null) {
            preferenceManager.saveBaniOrder(adapter.getBaniIds());
        }
    }
} 
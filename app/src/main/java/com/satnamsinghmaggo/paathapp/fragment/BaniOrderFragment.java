package com.satnamsinghmaggo.paathapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.satnamsinghmaggo.paathapp.R;
import com.satnamsinghmaggo.paathapp.adapter.BaniOrderAdapter;
import com.satnamsinghmaggo.paathapp.adapter.BaniOrderTouchHelper;
import com.satnamsinghmaggo.paathapp.model.Bani;
import com.satnamsinghmaggo.paathapp.util.BaniPreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class BaniOrderFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private MaterialButton resetButton;
    private BaniOrderAdapter adapter;
    private BaniPreferenceManager preferenceManager;
    private final List<Bani> banis = new ArrayList<>();

    private static final String KEY_BANI_ORDER = "bani_order";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bani_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        preferenceManager = BaniPreferenceManager.getInstance(requireContext());

        recyclerView = view.findViewById(R.id.recyclerView);
        resetButton = view.findViewById(R.id.resetButton);

        setupRecyclerView();
        setupResetButton();
        loadBaniOrder();
    }

    private void setupRecyclerView() {
        List<Bani> currentOrder = preferenceManager.getBaniOrder();
        if (currentOrder == null) {
            currentOrder = getDefaultBaniOrder();
        }

        adapter = new BaniOrderAdapter(currentOrder, newOrder -> {
            preferenceManager.saveBaniOrder(newOrder);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new BaniOrderTouchHelper(adapter));
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void setupResetButton() {
        resetButton.setOnClickListener(v -> showResetConfirmationDialog());
    }

    private void showResetConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.reset_order_title)
                .setMessage(R.string.reset_order_message)
                .setPositiveButton(R.string.reset, (dialog, which) -> resetBaniOrder())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void resetBaniOrder() {
        List<Bani> defaultOrder = getDefaultBaniOrder();
        adapter.updateList(defaultOrder);
        preferenceManager.saveBaniOrder(defaultOrder);
        Toast.makeText(requireContext(), R.string.order_reset, Toast.LENGTH_SHORT).show();
    }

    private void loadBaniOrder() {
        List<Bani> savedBanis = preferenceManager.getBaniOrder();
        if (savedBanis != null) {
            banis.clear();
            banis.addAll(savedBanis);
        } else {
            resetBaniOrder();
        }
        adapter.notifyDataSetChanged();
    }

    private List<Bani> getDefaultBaniOrder() {
        List<Bani> defaultList = new ArrayList<>();
        defaultList.add(new Bani("Hukamnama", "Daily Order from Sri Harmandir Sahib"));
        defaultList.add(new Bani("Japji Sahib", "Morning (3:00 AM - 6:00 AM)"));
        defaultList.add(new Bani("Jaap Sahib", "Morning (3:00 AM - 6:00 AM)"));
        defaultList.add(new Bani("Chaupai Sahib", "Morning"));
        defaultList.add(new Bani("Anand Sahib", "Morning"));
        defaultList.add(new Bani("Tav Prasad Savaiye", "Morning"));
        defaultList.add(new Bani("Rehras Sahib", "Evening (6:00 PM)"));
        defaultList.add(new Bani("Kirtan Sohila", "Night (Before Sleep)"));
        defaultList.add(new Bani("Sukhmani Sahib", "Anytime"));
        defaultList.add(new Bani("Dukh Bhanjani Sahib", "Anytime"));
        defaultList.add(new Bani("Ardaas", "Anytime"));
        return defaultList;
    }
}

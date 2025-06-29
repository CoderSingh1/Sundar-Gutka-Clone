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
    private String selectedLang = "en";

    public static BaniOrderFragment newInstance(String lang) {
        BaniOrderFragment fragment = new BaniOrderFragment();
        Bundle args = new Bundle();
        args.putString("selected_language", lang);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String selectedLang = getArguments() != null ? getArguments().getString("selected_language", "en") : "en";

        return inflater.inflate(R.layout.fragment_bani_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        preferenceManager = BaniPreferenceManager.getInstance(requireContext());
        selectedLang = getArguments() != null ? getArguments().getString("selected_language", "en") : "en";

        recyclerView = view.findViewById(R.id.recyclerView);
        resetButton = view.findViewById(R.id.resetButton);

        setupRecyclerView();
        setupResetButton();
        loadBaniOrder();
    }

    private void setupRecyclerView() {
        List<Bani> currentOrder = preferenceManager.getBaniOrder(selectedLang);
        if (currentOrder == null) {
            currentOrder = getDefaultBaniOrder(selectedLang);
        }

        adapter = new BaniOrderAdapter(currentOrder, newOrder -> {
            preferenceManager.saveBaniOrder(newOrder,selectedLang);
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
        List<Bani> defaultOrder = getDefaultBaniOrder(selectedLang);
        adapter.updateList(defaultOrder);
        String selectedLang = getArguments() != null ? getArguments().getString("selected_language", "en") : "en";
        preferenceManager.saveBaniOrder(defaultOrder, selectedLang);
        Toast.makeText(requireContext(), R.string.order_reset, Toast.LENGTH_SHORT).show();
    }

    private void loadBaniOrder() {
        String selectedLang = getArguments() != null ? getArguments().getString("selected_language", "en") : "en";
        List<Bani> savedBanis = preferenceManager.getBaniOrder(selectedLang);
        if (savedBanis != null) {
            banis.clear();
            banis.addAll(savedBanis);
        } else {
            resetBaniOrder();
        }
        adapter.notifyDataSetChanged();
    }

    private List<Bani> getDefaultBaniOrder(String lang) {
        List<Bani> defaultList = new ArrayList<>();

        switch (lang) {
            case "pa":  // Punjabi
                defaultList.add(new Bani("ਹੁਕਮਨਾਮਾ"));
                defaultList.add(new Bani("ਜਪੁਜੀ ਸਾਹਿਬ"));
                defaultList.add(new Bani("ਜਾਪ ਸਾਹਿਬ"));
                defaultList.add(new Bani("ਚੌਪਈ ਸਾਹਿਬ"));
                defaultList.add(new Bani("ਆਨੰਦ ਸਾਹਿਬ"));
                defaultList.add(new Bani("ਤਵ ਪ੍ਰਸਾਦ ਸਵੱਯੇ"));
                defaultList.add(new Bani("ਰਹਿਰਾਸ ਸਾਹਿਬ"));
                defaultList.add(new Bani("ਕੀਰਤਨ ਸੋਹਿਲਾ"));
                defaultList.add(new Bani("ਸੁਖਮਨੀ ਸਾਹਿਬ"));
                defaultList.add(new Bani("ਦੁੱਖ ਭੰਜਨੀ ਸਾਹਿਬ"));
                defaultList.add(new Bani("ਆਸਾ ਦੀ ਵਾਰ"));
                defaultList.add(new Bani("ਸ਼ਬਦ ਹਜ਼ਾਰੇ"));
                defaultList.add(new Bani("ਆਰਤੀ"));
                defaultList.add(new Bani("ਲਾਵਾਂ"));
                defaultList.add(new Bani("ਅਰਦਾਸ"));
                break;

            case "hi":  // Hindi
                defaultList.add(new Bani("हुकमनामा"));
                defaultList.add(new Bani("जपजी साहिब"));
                defaultList.add(new Bani("जाप साहिब"));
                defaultList.add(new Bani("चौपाई साहिब"));
                defaultList.add(new Bani("आनंद साहिब"));
                defaultList.add(new Bani("तव प्रसाद सवैये"));
                defaultList.add(new Bani("रहिरास साहिब"));
                defaultList.add(new Bani("कीर्तन सोहिला"));
                defaultList.add(new Bani("सुखमनी साहिब"));
                defaultList.add(new Bani("दुख भंजनि साहिब"));
                defaultList.add(new Bani("आसा दी वार"));
                defaultList.add(new Bani("शबद हजारे"));
                defaultList.add(new Bani("आरती"));
                defaultList.add(new Bani("लावाँ"));
                defaultList.add(new Bani("अरदास"));
                break;

            default:  // English (default)
                defaultList.add(new Bani("Hukamnama"));
                defaultList.add(new Bani("Japji Sahib"));
                defaultList.add(new Bani("Jaap Sahib"));
                defaultList.add(new Bani("Chaupai Sahib"));
                defaultList.add(new Bani("Anand Sahib"));
                defaultList.add(new Bani("Tav Prasad Savaiye"));
                defaultList.add(new Bani("Rehras Sahib"));
                defaultList.add(new Bani("Kirtan Sohila"));
                defaultList.add(new Bani("Sukhmani Sahib"));
                defaultList.add(new Bani("Dukh Bhanjani Sahib"));
                defaultList.add(new Bani("Asa Di Var"));
                defaultList.add(new Bani("Shabad Hazare"));
                defaultList.add(new Bani("Aarti"));
                defaultList.add(new Bani("Laavaan"));
                defaultList.add(new Bani("Ardaas"));
                break;
        }

        return defaultList;
    }
}

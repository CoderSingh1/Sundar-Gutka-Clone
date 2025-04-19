package com.satnamsinghmaggo.paathapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.satnamsinghmaggo.paathapp.R;
import com.satnamsinghmaggo.paathapp.util.BaniPreferenceManager;

public class FontSizeFragment extends Fragment {

    private SeekBar seekBar;
    private TextView sampleText;
    private BaniPreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_font_size, container, false);
        
        seekBar = view.findViewById(R.id.seek_bar);
        sampleText = view.findViewById(R.id.sample_text);
        preferenceManager = new BaniPreferenceManager(requireContext());
        
        setupSeekBar();
        
        return view;
    }

    private void setupSeekBar() {
        float currentSize = preferenceManager.getFontSize();
        int progress = (int) ((currentSize - 12) / 2); // Convert font size to progress (12-24sp)
        seekBar.setProgress(progress);
        sampleText.setTextSize(currentSize);
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float newSize = 12 + (progress * 2); // Convert progress to font size (12-24sp)
                sampleText.setTextSize(newSize);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float newSize = 12 + (seekBar.getProgress() * 2);
                preferenceManager.saveFontSize(newSize);
            }
        });
    }
} 
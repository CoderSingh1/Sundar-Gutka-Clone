package com.satnamsinghmaggo.paathapp.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.satnamsinghmaggo.paathapp.R;

public class FontSizeFragment extends Fragment {

    private static final String KEY_FONT_SIZE = "font_size";
    private static final float DEFAULT_FONT_SIZE = 16f;
    private static final float MIN_FONT_SIZE = 12f;
    private static final float MAX_FONT_SIZE = 24f;

    private SharedPreferences sharedPreferences;
    private SeekBar seekBar;
    private TextView previewText;
    private TextView sizeText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_font_size, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        seekBar = view.findViewById(R.id.seekBar);
        previewText = view.findViewById(R.id.previewText);
        sizeText = view.findViewById(R.id.sizeText);

        setupSeekBar();
        updateFontSize();
    }

    private void setupSeekBar() {
        float currentSize = sharedPreferences.getFloat(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
        int progress = (int) ((currentSize - MIN_FONT_SIZE) * 10);
        seekBar.setProgress(progress);
        sizeText.setText((int) currentSize + "sp");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float newSize = MIN_FONT_SIZE + (progress / 10f);
                previewText.setTextSize(newSize);
                sizeText.setText((int) newSize + "sp");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float newSize = MIN_FONT_SIZE + (seekBar.getProgress() / 10f);
                sharedPreferences.edit().putFloat(KEY_FONT_SIZE, newSize).apply();
            }
        });
    }

    private void updateFontSize() {
        float currentSize = sharedPreferences.getFloat(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
        previewText.setTextSize(currentSize);
    }
}

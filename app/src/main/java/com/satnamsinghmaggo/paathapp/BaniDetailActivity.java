package com.satnamsinghmaggo.paathapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.satnamsinghmaggo.paathapp.databinding.ActivityBaniDetailBinding;

public class BaniDetailActivity extends AppCompatActivity {

    private ActivityBaniDetailBinding binding;
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekBar;
    private boolean isPlaying = false;
    private Bani bani;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaniDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bani = (Bani) getIntent().getSerializableExtra("bani");
        if (bani == null) {
            Toast.makeText(this, "Error loading Bani", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        setupViews();
        setupFontSizeSpinner();
        loadBaniContent();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(bani.getTitle());
        }
    }

    private void setupViews() {
        binding.play.setOnClickListener(v -> togglePlayback());
        binding.skipnext.setOnClickListener(v -> seekBy(5000));
        binding.skipprev.setOnClickListener(v -> seekBy(-5000));
        
        binding.seekBar.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}
        });
    }

    private void setupFontSizeSpinner() {
        String[] fontSizes = {
            getString(R.string.bani_font_size_small),
            getString(R.string.bani_font_size_medium),
            getString(R.string.bani_font_size_large)
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, fontSizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        binding.fontSizeSpinner.setAdapter(adapter);
        binding.fontSizeSpinner.setSelection(1); // Default to medium
        
        binding.fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                float textSize;
                switch (position) {
                    case 0: textSize = 16f; break; // Small
                    case 1: textSize = 20f; break; // Medium
                    default: textSize = 24f; break; // Large
                }
                binding.gurmukhiText.setTextSize(textSize);
                binding.translationText.setTextSize(textSize);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadBaniContent() {
        binding.gurmukhiText.setText(bani.getContent());
        binding.translationText.setText(bani.getDescription());
        
        // Initialize media player
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(bani.getAudioUrl());
            mediaPlayer.prepareAsync();
            
            mediaPlayer.setOnPreparedListener(mp -> {
                binding.seekBar.setMax(mediaPlayer.getDuration());
                updateSeekBar = new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null && isPlaying) {
                            binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            handler.postDelayed(this, 1000);
                        }
                    }
                };
            });
            
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                binding.play.setImageResource(R.drawable.ic_play);
                handler.removeCallbacks(updateSeekBar);
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing audio player", Toast.LENGTH_SHORT).show();
        }
    }

    private void togglePlayback() {
        if (mediaPlayer != null) {
            if (!isPlaying) {
                mediaPlayer.start();
                isPlaying = true;
                binding.play.setImageResource(R.drawable.ic_pause);
                handler.post(updateSeekBar);
            } else {
                mediaPlayer.pause();
                isPlaying = false;
                binding.play.setImageResource(R.drawable.ic_play);
                handler.removeCallbacks(updateSeekBar);
            }
        }
    }

    private void seekBy(int milliseconds) {
        if (mediaPlayer != null) {
            int newPosition = Math.max(0, Math.min(
                mediaPlayer.getCurrentPosition() + milliseconds,
                mediaPlayer.getDuration()
            ));
            mediaPlayer.seekTo(newPosition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            binding.play.setImageResource(R.drawable.ic_play);
            handler.removeCallbacks(updateSeekBar);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (updateSeekBar != null) {
            handler.removeCallbacks(updateSeekBar);
        }
    }
} 
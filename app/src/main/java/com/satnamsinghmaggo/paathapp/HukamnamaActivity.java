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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.satnamsinghmaggo.paathapp.databinding.ActivityHukamnamaBinding;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HukamnamaActivity extends AppCompatActivity {

    private ActivityHukamnamaBinding binding;
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateSeekBar;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHukamnamaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupViews();
        setupFontSizeSpinner();
        loadHukamnama();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.hukamnama_title));
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
            getString(R.string.hukamnama_font_size_small),
            getString(R.string.hukamnama_font_size_medium),
            getString(R.string.hukamnama_font_size_large)
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
                binding.punjabiText.setTextSize(textSize);
                binding.englishText.setTextSize(textSize);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadHukamnama() {
        showLoading(true);
        String url = "https://old.sikhitothemax.org/api/random";
        
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // Format current date
                        String currentDate = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
                            .format(new Date());
                        binding.normalDate.setText(currentDate);
                        
                        // Set the texts
                        binding.gurmukhiText.setText(response.getString("gurbani"));
                        binding.punjabiText.setText(response.getString("punjabi"));
                        binding.englishText.setText(response.getString("english"));
                        binding.punjabiAng.setText(getString(R.string.hukamnama_ang, response.getInt("ang")));
                        
                        showLoading(false);
                    } catch (Exception e) {
                        String msg = "Error parsing response: " + e.getMessage();
                        showToast(msg);
                        handleError(msg);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage;
                    if (error.networkResponse == null) {
                        errorMessage = "No internet connection. Please check your connection and try again.";
                    } else if (error.networkResponse.statusCode == 404) {
                        errorMessage = "Could not load Hukamnama. Please try again later.";
                    } else if (error.networkResponse.statusCode >= 500) {
                        errorMessage = "Server error. Please try again later.";
                    } else {
                        errorMessage = "Error loading Hukamnama. Please try again.";
                    }
                    handleError(errorMessage);
                }
            }
        );

        request.setShouldCache(false);
        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
            15000, // 15 seconds timeout
            3,     // 3 retries
            1.5f   // 1.5f backoff multiplier
        ));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void togglePlayback() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPlaying = true;
                binding.play.setImageResource(R.drawable.ic_pause);
                handler.post(updateSeekBar);
                showToast("Playing Audio");
            } else {
                mediaPlayer.pause();
                isPlaying = false;
                binding.play.setImageResource(R.drawable.ic_play);
                handler.removeCallbacks(updateSeekBar);
                showToast("Paused");
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

    private void resetPlaybackState() {
        isPlaying = false;
        binding.seekBar.setProgress(0);
        binding.play.setImageResource(R.drawable.ic_play);
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.Scrollview.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void handleError(String message) {
        showLoading(false);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
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
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resetPlaybackState();
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
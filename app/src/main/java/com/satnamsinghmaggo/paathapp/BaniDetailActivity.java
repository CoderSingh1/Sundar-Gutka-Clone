// Converted and updated Java code for BaniDetailActivity with dynamic audio playback support

package com.satnamsinghmaggo.paathapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.widget.NestedScrollView;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BaniDetailActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener {

    private TextView tvBaniTitle, tvCurrentTime, tvTotalTime,BaniText;
    private ImageButton btnPrevious, btnPlayPause, btnNext;
    private SeekBar seekBar;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    NestedScrollView scrollView;

    LinearLayout mediaControls,timeLayout,linearLayout;

    ConstraintLayout mainLayout;

    private static final int SKIP_DURATION = 5000;
    private static final String KEY_POSITION = "position";
    private static final String KEY_IS_PLAYING = "is_playing";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final float DEFAULT_FONT_SIZE = 16f;

    private boolean controlsVisible = true;

    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying() && !isFinishing()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                updateTimeDisplay();
            }
            if (!isFinishing()) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bani_detail);

        handler = new Handler(Looper.getMainLooper());

        try {

            initializeViews();
            applyFontSize();
            hidePlayerOnScroll();
            FloatingActionButton bookmarkFab = findViewById(R.id.bookmarkFab);
            SharedPreferences prefs = getSharedPreferences("BaniPrefs", MODE_PRIVATE);
            String baniTitle = getIntent().getStringExtra("bani_name");
            String bookmarkKey = baniTitle + "_scrollY";
            int savedScroll = prefs.getInt(bookmarkKey, -1);

// Set icon based on whether a bookmark exists
            if (savedScroll != -1) {
                bookmarkFab.setImageResource(R.drawable.ic_send); // icon for "Go to Bookmark"
            } else {
                bookmarkFab.setImageResource(R.drawable.ic_star); // icon for "Save Bookmark"
            }

            bookmarkFab.setOnClickListener(v -> {
                int currentScroll = scrollView.getScrollY();
                int storedScroll = prefs.getInt(bookmarkKey, -1);

                if (storedScroll == -1) {
                    // Save new bookmark
                    prefs.edit().putInt(bookmarkKey, currentScroll).apply();
                    bookmarkFab.setImageResource(R.drawable.ic_send);
                    Toast.makeText(this, "Bookmark saved!", Toast.LENGTH_SHORT).show();
                } else {
                    // Go to saved bookmark
                    scrollView.post(() -> scrollView.scrollTo(0, storedScroll));
                    Toast.makeText(this, "Jumped to bookmark!", Toast.LENGTH_SHORT).show();
                }
            });

            bookmarkFab.setOnLongClickListener(v -> {
                prefs.edit().remove(bookmarkKey).apply();
                bookmarkFab.setImageResource(R.drawable.ic_star);
                Toast.makeText(this, "Bookmark cleared!", Toast.LENGTH_SHORT).show();
                return true;
            });
            initializeAudioSystem();
            setupBaniDetails();
            setupMediaPlayer();
            setupMediaControls();

            if (savedInstanceState != null) {
                int position = savedInstanceState.getInt(KEY_POSITION, 0);
                boolean wasPlaying = savedInstanceState.getBoolean(KEY_IS_PLAYING, false);
                mediaPlayer.seekTo(position);
                if (wasPlaying) playAudio();
            }
        } catch (Exception e) {
            handleError("Error initializing: " + e.getMessage());
            finish();
        }
    }

    private void hideControls() {
        mediaControls.setAlpha(1f);
        timeLayout.setAlpha(1f);
        mediaControls.animate()
                .translationY(mediaControls.getHeight())
                .alpha(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        timeLayout.animate()
                .translationY(timeLayout.getHeight())
                .alpha(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLayout);
        constraintSet.clear(R.id.scrollView, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.scrollView, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.applyTo(mainLayout);

        controlsVisible = false;
    }

    private void showControls() {
        mediaControls.setAlpha(0f);
        timeLayout.setAlpha(0f);
        mediaControls.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        timeLayout.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLayout);
        constraintSet.clear(R.id.scrollView, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.scrollView, ConstraintSet.BOTTOM, R.id.timeLayout, ConstraintSet.TOP);
        constraintSet.applyTo(mainLayout);

        controlsVisible = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFontSize();  // Re-apply in case user changed it in settings
        if (isPlaying) handler.post(updateSeekBar); // continue seek bar update
    }

    private void hidePlayerOnScroll(){
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                // scrolling down
                if (controlsVisible) hideControls();
            } else if (scrollY < oldScrollY) {
                // scrolling up
                if (!controlsVisible) showControls();
            }
        });


        linearLayout.setOnClickListener(v -> {
            if (!controlsVisible) {
                showControls();
            }
            else {
                hideControls();
            }
        });


    }

    private void applyFontSize() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        float fontSize = prefs.getFloat(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
        BaniText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    private void setupBaniDetails() {
        String baniTitle = getIntent().getStringExtra("bani_name");
        String filename = baniTitle + ".txt";

        tvBaniTitle.setText(baniTitle != null ? baniTitle : getString(R.string.default_bani_title));
        if (baniTitle != null) {

            String baniText = loadBaniFromAssets(filename);
            BaniText.setText(baniText); // assuming tvBaniText is your TextView for the bani content
        }


    }

    private void setupMediaPlayer() {
        String baniName = getIntent().getStringExtra("bani_name");
        Integer audioResId = getAudioResIdForBani(baniName);

        if (audioResId == null) {
            handleError("Audio for '" + baniName + "' not available");
            return;
        }

        mediaPlayer = MediaPlayer.create(this, audioResId);
        if (mediaPlayer == null) {
            handleError("Failed to create MediaPlayer");
            return;
        }

        mediaPlayer.setOnPreparedListener(mp -> {
            seekBar.setMax(mp.getDuration());
            tvTotalTime.setText(formatTime(mp.getDuration()));
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            btnPlayPause.setImageResource(R.drawable.ic_play);
            handler.removeCallbacks(updateSeekBar);
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            handleError("Media player error occurred");
            return true;
        });
    }

    private Integer getAudioResIdForBani(String baniName) {
        if (baniName == null) return null;
        return switch (baniName.toLowerCase(Locale.ROOT)) {
            case "japji sahib", "ਜਪੁਜੀ ਸਾਹਿਬ", "जपजी साहिब" -> R.raw.japji_sahib;
            case "jaap sahib", "ਜਾਪ ਸਾਹਿਬ", "जाप साहिब" -> R.raw.jaap_sahib;
            case "rehras sahib", "ਰਹਿਰਾਸ ਸਾਹਿਬ", "रहिरास साहिब" -> R.raw.rehras_sahib;
            case "chaupai sahib", "ਚੌਪਈ ਸਾਹਿਬ", "चौपाई साहिब" -> R.raw.chaupai_sahib;
            case "anand sahib", "ਆਨੰਦ ਸਾਹਿਬ", "आनंद साहिब" -> R.raw.anand_sahib;
            case "tav prasad savaiye", "ਤਵ ਪ੍ਰਸਾਦ ਸਵੱਯੇ", "तव प्रसाद सवैये" ->
                    R.raw.tav_prasad_savaiye;
            case "kirtan sohila", "ਕੀਰਤਨ ਸੋਹਿਲਾ", "कीर्तन सोहिला" -> R.raw.kirtan_sohila;
            case "sukhmani sahib", "ਸੁਖਮਨੀ ਸਾਹਿਬ", "सुखमनी साहिब" -> R.raw.sukhmani_sahib;
            case "dukh bhanjani sahib", "ਦੁੱਖ ਭੰਜਨੀ ਸਾਹਿਬ", "दुख भंजनि साहिब" ->
                    R.raw.dukh_bhanjani_sahib;
            case "ardas", "ਅਰਦਾਸ", "अरदास" -> R.raw.ardas;
            case "asa di var", "ਆਸਾ ਦੀ ਵਾਰ", "आसा दी वार" -> R.raw.asa_di_var;
            case "shabad hazare", "ਸ਼ਬਦ ਹਜ਼ਾਰੇ", "शबद हजारे" -> R.raw.shabad_hazare;
            case "aarti", "ਆਰਤੀ", "आरती" -> R.raw.aarti;
            case "laavaan", "ਲਾਵਾਂ", "लावाँ" -> R.raw.laavan;
            default -> null;
        };
    }

    private void initializeViews() {
        tvBaniTitle = findViewById(R.id.BaniTitle);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        BaniText = findViewById(R.id.BaniText);
        scrollView = findViewById(R.id.scrollView);
        mediaControls = findViewById(R.id.mediaControls);
        mainLayout = findViewById(R.id.mainLayout);
        timeLayout = findViewById(R.id.timeLayout);
        linearLayout = findViewById(R.id.linearLayout);
    }

    private void initializeAudioSystem() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setOnAudioFocusChangeListener(this)
                    .build();
        }
    }

    private void setupMediaControls() {
        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) pauseAudio(); else playAudio();
        });

        btnPrevious.setOnClickListener(v -> skipBackward());
        btnNext.setOnClickListener(v -> skipForward());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    updateTimeDisplay();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void skipForward() {
        int newPosition = Math.min(mediaPlayer.getCurrentPosition() + SKIP_DURATION, mediaPlayer.getDuration());
        mediaPlayer.seekTo(newPosition);
        updateTimeDisplay();
    }

    private void skipBackward() {
        int newPosition = Math.max(mediaPlayer.getCurrentPosition() - SKIP_DURATION, 0);
        mediaPlayer.seekTo(newPosition);
        updateTimeDisplay();
    }

    private void playAudio() {
        if (requestAudioFocus()) {
            mediaPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            handler.post(updateSeekBar);
        }
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.ic_play);
        handler.removeCallbacks(updateSeekBar);
    }

    private void updateTimeDisplay() {
        tvCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
    }

    private String formatTime(int milliseconds) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60);
    }

    private boolean requestAudioFocus() {
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            result = audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void handleError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            if (isPlaying) pauseAudio();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            if (!isPlaying) playAudio();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlaying) pauseAudio();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(updateSeekBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            abandonAudioFocus();
        }
        handler.removeCallbacks(updateSeekBar);
    }

    private void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
        } else {
            audioManager.abandonAudioFocus(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mediaPlayer != null) {
            outState.putInt(KEY_POSITION, mediaPlayer.getCurrentPosition());
            outState.putBoolean(KEY_IS_PLAYING, isPlaying);
        }
    }
    private String loadBaniFromAssets(String fileName) {

        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open(fileName)))) {

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading bani", Toast.LENGTH_SHORT).show();
        }

        return stringBuilder.toString();
    }
}
package com.satnamsinghmaggo.paathapp;

import android.animation.Animator;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HukamnamaActivity extends AppCompatActivity {


    private LottieAnimationView lottieLoader;
    private ImageView playButton, skipNext, skipPrev;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private LinearLayout mediaPlayerLayout,linearLayout,timeLayout;
    NestedScrollView scrollView;
    ConstraintLayout mainLayout;
    private boolean isPlaying = false;
    private static final OkHttpClient client = new OkHttpClient();
    private TextView gurmukhiTitle, gurmukhiText, punjabiTranslation, punjabiText, englishTranslation, englishText, Maintitle1, MainTitle2, PunjabiDate, PunjabiAng, EnglishDate, EnglishAng, NormalDate, tvCurrentTime, tvTotalTime;
    private static final String KEY_FONT_SIZE = "font_size";
    private static final float DEFAULT_FONT_SIZE = 16f;
    private boolean controlsVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hukamnama);
        initViews();
        applyFontSize();
        hidePlayerOnScroll();
        applyControlIconTint();
        setupLottieAnimation();
        applyWindowInsets();
        fetchAndDisplayHukamnama();
        //fetchDailyPdfLink();
        fetchMp3AndPlay();
    }


    private void initViews() {
        lottieLoader = findViewById(R.id.lottieLoader);
        seekBar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.btnPlayPause);
        skipNext = findViewById(R.id.btnNext);
        skipPrev = findViewById(R.id.btnPrevious);
        mediaPlayerLayout = findViewById(R.id.mediaControls);
        gurmukhiTitle = findViewById(R.id.gurmukhiTitle);
        gurmukhiText = findViewById(R.id.gurmukhiText);
        punjabiTranslation = findViewById(R.id.punjabiTranslation);
        punjabiText = findViewById(R.id.punjabiText);
        englishTranslation = findViewById(R.id.englishTranslation);
        englishText = findViewById(R.id.englishText);
        Maintitle1 = findViewById(R.id.mainTitle1);
        MainTitle2 = findViewById(R.id.mainTitle2);
        PunjabiDate = findViewById(R.id.punjabiDate);
        PunjabiAng = findViewById(R.id.punjabiAng);
        EnglishDate = findViewById(R.id.englishDate);
        EnglishAng = findViewById(R.id.englishAng);
        NormalDate = findViewById(R.id.normalDate);
        scrollView = findViewById(R.id.Scrollview);
        timeLayout = findViewById(R.id.timeLayout);
        linearLayout = findViewById(R.id.linearLayout);
        mainLayout = findViewById(R.id.main);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);

    }

    private void hideControls() {
        mediaPlayerLayout.setAlpha(1f);
        timeLayout.setAlpha(1f);
        mediaPlayerLayout.animate()
                .translationY(mediaPlayerLayout.getHeight())
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
        constraintSet.clear(R.id.Scrollview, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.Scrollview, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.applyTo(mainLayout);

        controlsVisible = false;
    }

    private void showControls() {
        mediaPlayerLayout.setAlpha(0f);
        timeLayout.setAlpha(0f);
        mediaPlayerLayout.animate()
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
        constraintSet.clear(R.id.Scrollview, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.Scrollview, ConstraintSet.BOTTOM, R.id.timeLayout, ConstraintSet.TOP);
        constraintSet.applyTo(mainLayout);

        controlsVisible = true;
    }

    private void hidePlayerOnScroll() {
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    // scrolling down
                    if (controlsVisible) hideControls();
                } else if (scrollY < oldScrollY) {
                    // scrolling up
                    if (!controlsVisible) showControls();
                }
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


    private void setupLottieAnimation() {
        lottieLoader.setVisibility(View.VISIBLE);
        lottieLoader.bringToFront();

        lottieLoader.setAnimation("Animation1.json");

        if (isDarkModeEnabled()) {
            // This applies white tint to all paths and strokes
            lottieLoader.addValueCallback(
                    new com.airbnb.lottie.model.KeyPath("**"),
                    com.airbnb.lottie.LottieProperty.COLOR_FILTER,
                    frame -> new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            );
        }

        lottieLoader.playAnimation();

        lottieLoader.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                mediaPlayerLayout.setVisibility(View.VISIBLE);
                seekBar.setAlpha(0f);
                seekBar.animate().alpha(1f).setDuration(600).start();
                mediaPlayerLayout.setAlpha(0f);
                mediaPlayerLayout.animate().alpha(1f).setDuration(600).start();
            }

            @Override public void onAnimationEnd(@NonNull Animator animation) {}
            @Override public void onAnimationCancel(@NonNull Animator animation) {}
            @Override public void onAnimationRepeat(@NonNull Animator animation) {}
        });
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void fetchDailyPdfLink() {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect("https://hs.sgpc.net/").get();
                Element link = doc.select("div.pdf-section a.pdf-button[href]").first();
                if (link != null) {
                    String pdfUrl = "https://hs.sgpc.net/" + link.attr("href");
                    runOnUiThread(() -> downloadAndDisplayPdf(pdfUrl));
                } else {
                    showToast("No PDF link found");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Error fetching PDF link");
            }
        }).start();
    }
    private void applyFontSize() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        float fontSize = prefs.getFloat(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
        gurmukhiText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        gurmukhiTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        punjabiText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        punjabiTranslation.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        englishText.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        englishTranslation.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        Maintitle1.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        MainTitle2.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        PunjabiDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        PunjabiAng.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        EnglishDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        EnglishAng.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        NormalDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

    }

    private void downloadAndDisplayPdf(String url) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                showToast("Failed to download PDF");
                hideLoader();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    File pdfFile = new File(getCacheDir(), "downloaded.pdf");
                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        runOnUiThread(() -> {
                            //pdfView.fromFile(pdfFile).load();
                            hideLoader();
                        });
                    }
                } else {
                    showToast("Failed to download PDF");
                    hideLoader();
                }
            }
        });
    }

    private void fetchMp3AndPlay() {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect("https://hs.sgpc.net/").get();
                Element audioElement = doc.select("div.audio-card audio.audio-player[src]").first();
                if (audioElement != null) {
                    String audioUrl = audioElement.attr("src");
                    runOnUiThread(() -> initializeMediaPlayer(audioUrl));
                } else {
                    showToast("Audio link not found");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Error fetching audio");
            }
        }).start();
    }

    private void initializeMediaPlayer(String audioUrl) {
        mediaPlayer = new MediaPlayer();
        playButton.setEnabled(false);
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Audio file error");
            return;
        }

        mediaPlayer.setOnPreparedListener(mp -> {
            seekBar.setMax(mp.getDuration());
            playButton.setEnabled(true);
            tvTotalTime.setText(formatTime(mp.getDuration()));

            updateSeekBar = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        tvCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                        handler.postDelayed(this, 500);

                    }
                }
            };
        });

        playButton.setOnClickListener(v -> togglePlayback());
        skipNext.setOnClickListener(v -> seekBy(5000));
        skipPrev.setOnClickListener(v -> seekBy(-5000));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    int pos = seekBar.getProgress();
                    mediaPlayer.seekTo(pos);
                    tvCurrentTime.setText(formatTime(pos));
                }
            }
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void togglePlayback() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPlaying = true;
                playButton.setImageResource(R.drawable.ic_pause);
                handler.post(updateSeekBar);
                showToast("Playing Audio");
            } else {
                mediaPlayer.pause();
                isPlaying = false;
                playButton.setImageResource(R.drawable.ic_play);
                handler.removeCallbacks(updateSeekBar);
                showToast("Paused");
            }
        }
    }

    private void seekBy(int milliseconds) {
        if (mediaPlayer != null) {
            int newPosition = mediaPlayer.getCurrentPosition() + milliseconds;
            newPosition = Math.max(0, Math.min(newPosition, mediaPlayer.getDuration()));
            mediaPlayer.seekTo(newPosition);
        }
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private void hideLoader() {
        runOnUiThread(() -> lottieLoader.setVisibility(View.GONE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }
    private void applyControlIconTint() {
        TypedValue typedValue = new TypedValue();
        // Use your app's theme attribute instead of androidx.appcompat
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);

        int color;
        if (typedValue.resourceId != 0) {
            color = ContextCompat.getColor(this, typedValue.resourceId);
        } else {
            color = typedValue.data;
        }

        playButton.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        skipNext.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        skipPrev.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        seekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
    private boolean isDarkModeEnabled() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
    private void fetchAndDisplayHukamnama() {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect("https://hs.sgpc.net/").get();


                // mukhwak sri harminder sahib heading
                Element mainTitle1 = doc.selectFirst("div.text-center h2.hukamnama-title:nth-of-type(1)");
                Element mainTitle2 = doc.selectFirst("div.text-center h2.hukamnama-title:nth-of-type(2)");

                Element normalDate = doc.select("p.customDate strong").get(0);

                // Gurmukhi Title & Text
                Element title = doc.selectFirst("div.hukamnama-card h4.hukamnama-title");
                Element hukamText = doc.selectFirst("div.hukamnama-card p.hukamnama-text");


                Element punjabiDate = doc.select("p.customDate strong").get(1);
                Element punjabiAng = doc.select("p.customDate strong").get(2);



                // Punjabi Explanation
                Element punjabiTitle = doc.selectFirst("div.hukamnama-card2 h4.hukamnama-title");
                Element punjabiBody = doc.select("div.hukamnama-card2").get(0).selectFirst("p.hukamnama-text");

                // English Translation
                Element englishTitle = doc.select("div.hukamnama-card2").get(1).selectFirst("h4.hukamnama-title");
                Element englishBody = doc.select("div.hukamnama-card2").get(1).selectFirst("p.hukamnama-text");

                //english date and ang

                Element englishDate = doc.select("p.customDate strong").get(3);
                Element englishAng = doc.select("p.customDate strong").get(4);

                runOnUiThread(() -> {
                    lottieLoader.setVisibility(View.GONE);
                    if (title != null) gurmukhiTitle.setText(title.text());
                    if (hukamText != null) gurmukhiText.setText(hukamText.text());

                    if (punjabiTitle != null) {punjabiTranslation.setText(punjabiTitle.text());
                        punjabiTranslation.setPaintFlags(punjabiTranslation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);}

                    if (punjabiBody != null) punjabiText.setText(punjabiBody.text());

                    if (englishTitle != null) {englishTranslation.setText(englishTitle.text());
                        englishTranslation.setPaintFlags(englishTranslation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);}

                    if (englishBody != null) englishText.setText(englishBody.text());
                    if (mainTitle1 != null) Maintitle1.setText(mainTitle1.text());
                    if (mainTitle2 != null) MainTitle2.setText(mainTitle2.text());
                    if (punjabiDate != null) PunjabiDate.setText(punjabiDate.text());
                    if (punjabiAng != null) PunjabiAng.setText(punjabiAng.text());
                    if (englishDate != null) EnglishDate.setText(englishDate.text());
                    if (englishAng != null) EnglishAng.setText(englishAng.text());
                    if (normalDate != null) NormalDate.setText(normalDate.text());
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to load Hukamnama", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause(); // or mediaPlayer.stop() if you want to completely stop
            playButton.setImageResource(R.drawable.ic_play); // update UI
            isPlaying = false;
            handler.removeCallbacks(updateSeekBar);
        }
    }

    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}

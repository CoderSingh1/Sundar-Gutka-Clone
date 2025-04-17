package com.satnamsinghmaggo.paathapp

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class BaniDetailActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var seekBar: SeekBar
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnPrevious: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var tvBaniContent: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var isPlaying = false
    private var seekBarUpdateRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bani_detail)

        try {
            initializeViews()
            setupMediaPlayer()
            setupListeners()
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun initializeViews() {
        seekBar = findViewById(R.id.seekBar)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalTime = findViewById(R.id.tvTotalTime)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        tvBaniContent = findViewById(R.id.tvBaniContent)

        val baniName = intent.getStringExtra("bani_name")
        title = baniName
        tvBaniContent.text = "Bani content for $baniName"
    }

    private fun setupMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.japji_sahib)
            mediaPlayer?.setOnCompletionListener {
                isPlaying = false
                btnPlayPause.setImageResource(R.drawable.ic_play)
                seekBar.progress = 0
            }
            seekBar.max = mediaPlayer?.duration ?: 0
            tvTotalTime.text = formatTime(mediaPlayer?.duration ?: 0)
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun setupListeners() {
        btnPlayPause.setOnClickListener {
            try {
                if (isPlaying) {
                    pauseAudio()
                } else {
                    playAudio()
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    tvCurrentTime.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        startSeekBarUpdate()
    }

    private fun playAudio() {
        try {
            mediaPlayer?.start()
            isPlaying = true
            btnPlayPause.setImageResource(R.drawable.ic_pause)
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun pauseAudio() {
        try {
            mediaPlayer?.pause()
            isPlaying = false
            btnPlayPause.setImageResource(R.drawable.ic_play)
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun startSeekBarUpdate() {
        seekBarUpdateRunnable = object : Runnable {
            override fun run() {
                try {
                    if (isPlaying) {
                        val currentPosition = mediaPlayer?.currentPosition ?: 0
                        seekBar.progress = currentPosition
                        tvCurrentTime.text = formatTime(currentPosition)
                    }
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    handleError(e)
                }
            }
        }
        seekBarUpdateRunnable?.let { handler.post(it) }
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) -
                TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun handleError(e: Exception) {
        Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
        // You might want to log the error to a crash reporting service here
    }

    override fun onPause() {
        super.onPause()
        try {
            mediaPlayer?.pause()
            isPlaying = false
            btnPlayPause.setImageResource(R.drawable.ic_play)
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            seekBarUpdateRunnable?.let { handler.removeCallbacks(it) }
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isPlaying", isPlaying)
        outState.putInt("currentPosition", mediaPlayer?.currentPosition ?: 0)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isPlaying = savedInstanceState.getBoolean("isPlaying", false)
        val position = savedInstanceState.getInt("currentPosition", 0)
        mediaPlayer?.seekTo(position)
        seekBar.progress = position
        if (isPlaying) {
            playAudio()
        }
    }
} 
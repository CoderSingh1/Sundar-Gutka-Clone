package com.satnamsinghmaggo.paathapp

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class BaniDetailActivity : AppCompatActivity(), AudioManager.OnAudioFocusChangeListener {

    private lateinit var tvBaniTitle: TextView
    private lateinit var btnPrevious: ImageButton
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    private var isPlaying = false
    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBar = object : Runnable {
        override fun run() {
            if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying && !isFinishing) {
                if (::seekBar.isInitialized) {
                    seekBar.progress = mediaPlayer.currentPosition
                    updateTimeDisplay()
                }
            }
            if (!isFinishing) {
                handler.postDelayed(this, 1000)
            }
        }
    }

    companion object {
        private const val SKIP_DURATION = 5000 // 5 seconds in milliseconds
        private const val KEY_POSITION = "position"
        private const val KEY_IS_PLAYING = "is_playing"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bani_detail)

        try {
            initializeViews()
            initializeAudioSystem()
            setupBaniDetails()
            setupMediaPlayer()
            setupMediaControls()

            // Restore state if available
            savedInstanceState?.let { bundle ->
                val position = bundle.getInt(KEY_POSITION, 0)
                val wasPlaying = bundle.getBoolean(KEY_IS_PLAYING, false)
                if (::mediaPlayer.isInitialized) {
                    mediaPlayer.seekTo(position)
                    if (wasPlaying) playAudio()
                }
            }
        } catch (e: Exception) {
            handleError("Error initializing: ${e.message}")
            finish() // Close the activity if initialization fails
        }
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            pauseAudio()
        }
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateSeekBar)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::mediaPlayer.isInitialized) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                abandonAudioFocus()
                mediaPlayer.release()
            }
            handler.removeCallbacks(updateSeekBar)
        } catch (e: Exception) {
            handleError("Error cleaning up: ${e.message}")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::mediaPlayer.isInitialized) {
            outState.putInt(KEY_POSITION, mediaPlayer.currentPosition)
            outState.putBoolean(KEY_IS_PLAYING, isPlaying)
        }
    }

    private fun initializeViews() {
        tvBaniTitle = findViewById(R.id.tvBaniTitle) ?: throw Exception("tvBaniTitle not found")
        btnPrevious = findViewById(R.id.btnPrevious) ?: throw Exception("btnPrevious not found")
        btnPlayPause = findViewById(R.id.btnPlayPause) ?: throw Exception("btnPlayPause not found")
        btnNext = findViewById(R.id.btnNext) ?: throw Exception("btnNext not found")
        seekBar = findViewById(R.id.seekBar) ?: throw Exception("seekBar not found")
        tvCurrentTime = findViewById(R.id.tvCurrentTime) ?: throw Exception("tvCurrentTime not found")
        tvTotalTime = findViewById(R.id.tvTotalTime) ?: throw Exception("tvTotalTime not found")
    }

    private fun initializeAudioSystem() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        setupAudioFocus()
    }

    private fun setupAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(this)
                .build()
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (isPlaying) pauseAudio()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (!isPlaying) playAudio()
            }
        }
    }

    private fun abandonAudioFocus() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { request ->
                    audioManager.abandonAudioFocusRequest(request)
                }
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(this)
            }
        } catch (e: Exception) {
            handleError("Error abandoning audio focus: ${e.message}")
        }
    }

    private fun setupMediaPlayer() {
        if (!::seekBar.isInitialized || !::tvTotalTime.isInitialized) {
            throw Exception("Views not initialized")
        }

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.japji_sahib) ?: 
                throw Exception("Failed to create MediaPlayer")
            
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
                if (::btnPlayPause.isInitialized) {
                    btnPlayPause.setImageResource(R.drawable.ic_play)
                }
                handler.removeCallbacks(updateSeekBar)
            }

            mediaPlayer.setOnPreparedListener { mp ->
                seekBar.max = mp.duration
                tvTotalTime.text = formatTime(mp.duration)
            }

            mediaPlayer.setOnErrorListener { _, _, _ ->
                handleError("Media player error occurred")
                true
            }

        } catch (e: Exception) {
            throw Exception("Error setting up media player: ${e.message}")
        }
    }

    private fun setupMediaControls() {
        if (!::btnPlayPause.isInitialized || !::btnPrevious.isInitialized || 
            !::btnNext.isInitialized || !::seekBar.isInitialized) {
            throw Exception("Media control views not initialized")
        }

        btnPlayPause.setOnClickListener {
            if (isPlaying) pauseAudio() else playAudio()
        }

        btnPrevious.setOnClickListener {
            skipBackward()
        }

        btnNext.setOnClickListener {
            skipForward()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && ::mediaPlayer.isInitialized) {
                    mediaPlayer.seekTo(progress)
                    updateTimeDisplay()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun skipForward() {
        if (!::mediaPlayer.isInitialized) return
        try {
            val newPosition = (mediaPlayer.currentPosition + SKIP_DURATION)
                .coerceAtMost(mediaPlayer.duration)
            mediaPlayer.seekTo(newPosition)
            updateTimeDisplay()
        } catch (e: Exception) {
            handleError("Error skipping forward: ${e.message}")
        }
    }

    private fun skipBackward() {
        if (!::mediaPlayer.isInitialized) return
        try {
            val newPosition = (mediaPlayer.currentPosition - SKIP_DURATION)
                .coerceAtLeast(0)
            mediaPlayer.seekTo(newPosition)
            updateTimeDisplay()
        } catch (e: Exception) {
            handleError("Error skipping backward: ${e.message}")
        }
    }

    private fun playAudio() {
        if (!::mediaPlayer.isInitialized || !::btnPlayPause.isInitialized) {
            handleError("Media player not initialized")
            return
        }

        try {
            if (requestAudioFocus()) {
                mediaPlayer.start()
                isPlaying = true
                btnPlayPause.setImageResource(R.drawable.ic_pause)
                handler.post(updateSeekBar)
            }
        } catch (e: Exception) {
            handleError("Error playing audio: ${e.message}")
        }
    }

    private fun pauseAudio() {
        if (!::mediaPlayer.isInitialized || !::btnPlayPause.isInitialized) {
            handleError("Media player not initialized")
            return
        }

        try {
            mediaPlayer.pause()
            isPlaying = false
            btnPlayPause.setImageResource(R.drawable.ic_play)
            handler.removeCallbacks(updateSeekBar)
        } catch (e: Exception) {
            handleError("Error pausing audio: ${e.message}")
        }
    }

    private fun updateTimeDisplay() {
        if (!::mediaPlayer.isInitialized || !::tvCurrentTime.isInitialized) return
        try {
            tvCurrentTime.text = formatTime(mediaPlayer.currentPosition)
        } catch (e: Exception) {
            handleError("Error updating time display: ${e.message}")
        }
    }

    private fun formatTime(milliseconds: Int): String {
        return try {
            String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
            )
        } catch (e: Exception) {
            "00:00"
        }
    }

    private fun setupBaniDetails() {
        if (!::tvBaniTitle.isInitialized) {
            throw Exception("Bani title view not initialized")
        }
        
        val baniTitle = intent.getStringExtra("bani_name")
        tvBaniTitle.text = baniTitle ?: getString(R.string.default_bani_title)
    }

    private fun requestAudioFocus(): Boolean {
        return try {
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { request ->
                    audioManager.requestAudioFocus(request)
                } ?: AudioManager.AUDIOFOCUS_REQUEST_FAILED
            } else {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
            }
            result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } catch (e: Exception) {
            handleError("Error requesting audio focus: ${e.message}")
            false
        }
    }

    private fun handleError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
} 
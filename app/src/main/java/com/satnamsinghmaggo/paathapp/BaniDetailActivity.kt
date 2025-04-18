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
    private lateinit var tvBaniTime: TextView
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
            if (mediaPlayer.isPlaying) {
                seekBar.progress = mediaPlayer.currentPosition
                updateTimeDisplay()
            }
            handler.postDelayed(this, 1000)
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
            setupBaniDetails()
            setupMediaPlayer()
            setupMediaControls()
            setupAudioFocus()
        } catch (e: Exception) {
            handleError("Error initializing: ${e.message}")
        }
    }

    private fun initializeViews() {
        try {
            tvBaniTitle = findViewById(R.id.tvBaniTitle)
            tvBaniTime = findViewById(R.id.tvBaniTime)
            btnPrevious = findViewById(R.id.btnPrevious)
            btnPlayPause = findViewById(R.id.btnPlayPause)
            btnNext = findViewById(R.id.btnNext)
            seekBar = findViewById(R.id.seekBar)
            tvCurrentTime = findViewById(R.id.tvCurrentTime)
            tvTotalTime = findViewById(R.id.tvTotalTime)
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        } catch (e: Exception) {
            handleError("Error finding views: ${e.message}")
        }
    }

    private fun setupAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(this)
                .build()

            val result = audioManager.requestAudioFocus(audioFocusRequest!!)
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                handleError("Could not get audio focus")
            }
        } else {
            @Suppress("DEPRECATION")
            val result = audioManager.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                handleError("Could not get audio focus")
            }
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (isPlaying) {
                    mediaPlayer.setVolume(1.0f, 1.0f)
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (isPlaying) {
                    pauseAudio()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (isPlaying) {
                    pauseAudio()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (isPlaying) {
                    mediaPlayer.setVolume(0.1f, 0.1f)
                }
            }
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let {
                audioManager.abandonAudioFocusRequest(it)
            }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(this)
        }
    }

    private fun setupMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.japji_sahib)
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
                btnPlayPause.setImageResource(R.drawable.ic_play)
                handler.removeCallbacks(updateSeekBar)
            }
            seekBar.max = mediaPlayer.duration
            tvTotalTime.text = formatTime(mediaPlayer.duration)
        } catch (e: Exception) {
            handleError("Error setting up media player: ${e.message}")
        }
    }

    private fun setupMediaControls() {
        try {
            btnPlayPause.setOnClickListener {
                if (isPlaying) {
                    pauseAudio()
                } else {
                    playAudio()
                }
            }

            btnPrevious.setOnClickListener {
                skipBackward()
            }

            btnNext.setOnClickListener {
                skipForward()
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress)
                        updateTimeDisplay()
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        } catch (e: Exception) {
            handleError("Error setting up media controls: ${e.message}")
        }
    }

    private fun skipForward() {
        try {
            val newPosition = mediaPlayer.currentPosition + SKIP_DURATION
            if (newPosition <= mediaPlayer.duration) {
                mediaPlayer.seekTo(newPosition)
                seekBar.progress = newPosition
                updateTimeDisplay()
            } else {
                // If we're at the end, just go to the end
                mediaPlayer.seekTo(mediaPlayer.duration)
                seekBar.progress = mediaPlayer.duration
                updateTimeDisplay()
            }
        } catch (e: Exception) {
            handleError("Error skipping forward: ${e.message}")
        }
    }

    private fun skipBackward() {
        try {
            val newPosition = mediaPlayer.currentPosition - SKIP_DURATION
            if (newPosition >= 0) {
                mediaPlayer.seekTo(newPosition)
                seekBar.progress = newPosition
                updateTimeDisplay()
            } else {
                // If we're at the beginning, just go to the start
                mediaPlayer.seekTo(0)
                seekBar.progress = 0
                updateTimeDisplay()
            }
        } catch (e: Exception) {
            handleError("Error skipping backward: ${e.message}")
        }
    }

    private fun playAudio() {
        try {
            mediaPlayer.start()
            isPlaying = true
            btnPlayPause.setImageResource(R.drawable.ic_pause)
            handler.post(updateSeekBar)
        } catch (e: Exception) {
            handleError("Error playing audio: ${e.message}")
        }
    }

    private fun pauseAudio() {
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
        try {
            tvCurrentTime.text = formatTime(mediaPlayer.currentPosition)
        } catch (e: Exception) {
            handleError("Error updating time display: ${e.message}")
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setupBaniDetails() {
        try {
            val baniName = intent.getStringExtra("bani_name") ?: run {
                handleError("No bani name provided")
                return
            }
            tvBaniTitle.text = baniName

            // Set the time based on bani name
            val baniTime = when (baniName) {
                "Japji Sahib" -> "Morning (3:00 AM - 6:00 AM)"
                "Jaap Sahib" -> "Morning (3:00 AM - 6:00 AM)"
                "Chaupai Sahib" -> "Morning"
                "Anand Sahib" -> "Morning"
                "Tav Prasad Savaiye" -> "Morning"
                "Rehras Sahib" -> "Evening (6:00 PM)"
                "Kirtan Sohila" -> "Night (Before Sleep)"
                else -> "Anytime"
            }
            tvBaniTime.text = baniTime
        } catch (e: Exception) {
            handleError("Error setting up bani details: ${e.message}")
        }
    }

    private fun handleError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_POSITION, mediaPlayer.currentPosition)
        outState.putBoolean(KEY_IS_PLAYING, isPlaying)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val position = savedInstanceState.getInt(KEY_POSITION)
        val wasPlaying = savedInstanceState.getBoolean(KEY_IS_PLAYING)
        mediaPlayer.seekTo(position)
        seekBar.progress = position
        if (wasPlaying) {
            playAudio()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            abandonAudioFocus()
            mediaPlayer.release()
            handler.removeCallbacks(updateSeekBar)
        } catch (e: Exception) {
            handleError("Error cleaning up: ${e.message}")
        }
    }
} 
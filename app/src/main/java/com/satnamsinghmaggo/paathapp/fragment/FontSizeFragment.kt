package com.satnamsinghmaggo.paathapp.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.satnamsinghmaggo.paathapp.R

class FontSizeFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var seekBar: SeekBar
    private lateinit var previewText: TextView
    private lateinit var sizeText: TextView

    companion object {
        private const val KEY_FONT_SIZE = "font_size"
        private const val DEFAULT_FONT_SIZE = 16f
        private const val MIN_FONT_SIZE = 12f
        private const val MAX_FONT_SIZE = 24f
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_font_size, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        
        seekBar = view.findViewById(R.id.seekBar)
        previewText = view.findViewById(R.id.previewText)
        sizeText = view.findViewById(R.id.sizeText)

        setupSeekBar()
        updateFontSize()
    }

    private fun setupSeekBar() {
        val currentSize = sharedPreferences.getFloat(KEY_FONT_SIZE, DEFAULT_FONT_SIZE)
        val progress = ((currentSize - MIN_FONT_SIZE) * 10).toInt()
        seekBar.progress = progress
        sizeText.text = "${currentSize.toInt()}sp"

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newSize = MIN_FONT_SIZE + (progress / 10f)
                previewText.textSize = newSize
                sizeText.text = "${newSize.toInt()}sp"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val newSize = MIN_FONT_SIZE + (seekBar?.progress ?: 0) / 10f
                sharedPreferences.edit().putFloat(KEY_FONT_SIZE, newSize).apply()
            }
        })
    }

    private fun updateFontSize() {
        val currentSize = sharedPreferences.getFloat(KEY_FONT_SIZE, DEFAULT_FONT_SIZE)
        previewText.textSize = currentSize
    }
} 
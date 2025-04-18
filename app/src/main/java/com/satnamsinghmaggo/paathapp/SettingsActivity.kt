package com.satnamsinghmaggo.paathapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.satnamsinghmaggo.paathapp.adapter.SettingsPagerAdapter
import com.satnamsinghmaggo.paathapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Set up ViewPager2 with adapter
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        
        val adapter = SettingsPagerAdapter(this)
        viewPager.adapter = adapter

        // Set up TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Font Size"
                1 -> "Bani Order"
                else -> null
            }
        }.attach()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
} 
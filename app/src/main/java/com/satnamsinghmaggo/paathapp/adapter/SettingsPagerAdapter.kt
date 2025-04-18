package com.satnamsinghmaggo.paathapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.satnamsinghmaggo.paathapp.fragment.BaniOrderFragment
import com.satnamsinghmaggo.paathapp.fragment.FontSizeFragment

class SettingsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FontSizeFragment()
            1 -> BaniOrderFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
} 
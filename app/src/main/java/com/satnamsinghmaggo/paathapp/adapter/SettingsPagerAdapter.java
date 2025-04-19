package com.satnamsinghmaggo.paathapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.satnamsinghmaggo.paathapp.fragment.BaniOrderFragment;
import com.satnamsinghmaggo.paathapp.fragment.FontSizeFragment;

public class SettingsPagerAdapter extends FragmentStateAdapter {

    private static final int PAGE_COUNT = 2;

    public SettingsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BaniOrderFragment();
            case 1:
                return new FontSizeFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }
} 
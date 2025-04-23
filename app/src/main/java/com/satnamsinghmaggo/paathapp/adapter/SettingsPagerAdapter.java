package com.satnamsinghmaggo.paathapp.adapter;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.satnamsinghmaggo.paathapp.fragment.BaniOrderFragment;
import com.satnamsinghmaggo.paathapp.fragment.FontSizeFragment;
import com.satnamsinghmaggo.paathapp.fragment.NotificationFragment;

public class SettingsPagerAdapter extends FragmentStateAdapter {

    private final String selectedLang;

    public SettingsPagerAdapter(@NonNull FragmentActivity activity, String selectedLang) {
        super(activity);

        this.selectedLang = selectedLang;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FontSizeFragment();
            case 1:
                return BaniOrderFragment.newInstance(selectedLang);
            case 2:
                return new NotificationFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

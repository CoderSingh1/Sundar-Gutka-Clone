package com.satnamsinghmaggo.paathapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.satnamsinghmaggo.paathapp.adapter.SettingsPagerAdapter;
import com.satnamsinghmaggo.paathapp.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Drawable upArrow = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back);
        if (upArrow != null) {
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }


        // Set up ViewPager2 with adapter
        viewPager = binding.viewPager;
        tabLayout = binding.tabLayout;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedLang = prefs.getString("selected_language", "en");
        viewPager.setAdapter(new SettingsPagerAdapter(this, selectedLang));

        // Set up TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Font Size");
                    break;
                case 1:
                    tab.setText("Bani Order");
                    break;
                case 2:
                    tab.setText("Notifications");
                    break;
            }
        }).attach();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

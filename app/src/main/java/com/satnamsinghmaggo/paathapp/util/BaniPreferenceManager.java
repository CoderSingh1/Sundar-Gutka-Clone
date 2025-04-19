package com.satnamsinghmaggo.paathapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaniPreferenceManager {
    private static final String PREF_NAME = "BaniPreferences";
    private static final String KEY_BANI_ORDER = "bani_order";
    private static final String KEY_FONT_SIZE = "font_size";
    
    private final SharedPreferences preferences;
    
    public BaniPreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveBaniOrder(List<String> baniIds) {
        Set<String> set = new HashSet<>(baniIds);
        preferences.edit().putStringSet(KEY_BANI_ORDER, set).apply();
    }
    
    public List<String> getBaniOrder() {
        Set<String> set = preferences.getStringSet(KEY_BANI_ORDER, new HashSet<>());
        return new ArrayList<>(set);
    }
    
    public void saveFontSize(float size) {
        preferences.edit().putFloat(KEY_FONT_SIZE, size).apply();
    }
    
    public float getFontSize() {
        return preferences.getFloat(KEY_FONT_SIZE, 16f); // Default to 16sp
    }
} 
package com.satnamsinghmaggo.paathapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.satnamsinghmaggo.paathapp.model.Bani;

import java.lang.reflect.Type;
import java.util.List;

public class BaniPreferenceManager {

    private static final String KEY_BANI_ORDER = "bani_order";
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    private static volatile BaniPreferenceManager INSTANCE;

    private BaniPreferenceManager(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        this.gson = new Gson();
    }

    public static BaniPreferenceManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BaniPreferenceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BaniPreferenceManager(context);
                }
            }
        }
        return INSTANCE;
    }

    public void saveBaniOrder(List<Bani> banis) {
        String json = gson.toJson(banis);
        sharedPreferences.edit().putString(KEY_BANI_ORDER, json).apply();
    }

    public List<Bani> getBaniOrder() {
        String json = sharedPreferences.getString(KEY_BANI_ORDER, null);
        if (json == null) return null;

        try {
            Type type = new TypeToken<List<Bani>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            sharedPreferences.edit().remove(KEY_BANI_ORDER).apply();
            return null;
        }
    }
}

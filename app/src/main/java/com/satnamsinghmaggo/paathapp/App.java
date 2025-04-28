// App.java
package com.satnamsinghmaggo.paathapp;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class App extends Application {
    public static float userFontSize = 20f;
    @Override
    public void onCreate() {
        super.onCreate();

        // Always set the saved theme before anything
        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        int themeMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(themeMode);

        SharedPreferences fontPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        userFontSize = fontPrefs.getFloat("font_size", 20f); // default 20sp
    }
}

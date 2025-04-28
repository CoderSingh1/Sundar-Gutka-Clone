package com.satnamsinghmaggo.paathapp;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;


import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.satnamsinghmaggo.paathapp.adapter.BaniAdapter;
import com.satnamsinghmaggo.paathapp.model.Bani;
import com.satnamsinghmaggo.paathapp.util.BaniPreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String KEY_BANIS = "banis";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private BaniAdapter adapter;
    private Toolbar toolbar;
    private BaniPreferenceManager preferenceManager;
    private List<Bani> banis = new ArrayList<>();
    private String selectedLang = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateThemeIcon();

        selectedLang = getIntent().getStringExtra("selected_language");
        if (selectedLang == null) selectedLang = "en";

// Persist language
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString("selected_language", selectedLang)
                .apply();
        Toast.makeText(this, "Selected Language: " + selectedLang, Toast.LENGTH_SHORT).show();

        preferenceManager = BaniPreferenceManager.getInstance(this);

        initializeViews();
        createNotificationChannel();
        setupToolbar();
        setupDrawer();
        setupRecyclerView();
        setupBackPressedCallback();

        if (savedInstanceState == null) {
            loadBanis();
        } else {
            banis = savedInstanceState.getParcelableArrayList(KEY_BANIS);
            if (banis == null) banis = new ArrayList<>();
            adapter.updateBanis(banis);
        }
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "bani_reminders",
                    "Bani Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for Bani reminder notifications");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupBackPressedCallback() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    MainActivity.super.onBackPressed();
                }
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new BaniAdapter(banis, bani -> {
            try {
                if (bani.getName().equalsIgnoreCase("Hukamnama") | bani.getName().equalsIgnoreCase("ਹੁਕਮਨਾਮਾ") | bani.getName().equalsIgnoreCase("हुकमनामा")) {
                    Intent intent = new Intent(MainActivity.this, HukamnamaActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, BaniDetailActivity.class);
                    intent.putExtra("bani_name", bani.getName());
                    intent.putExtra("selected_language", selectedLang);
                    startActivity(intent);
                }
            } catch (Exception e) {
                handleError(e);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadBanis() {
        List<Bani> customOrder = preferenceManager.getBaniOrder(selectedLang);
        if (customOrder != null) {
            banis = customOrder;
        } else {
            banis = getDefaultBaniOrder(selectedLang);
        }
        adapter.updateBanis(banis);
    }

    private List<Bani> getDefaultBaniOrder(String lang) {
        if (lang.equals("hi")) {
            return Arrays.asList(
                    new Bani("हुकमनामा"),
                    new Bani("जपजी साहिब"),
                    new Bani("जाप साहिब"),
                    new Bani("चौपाई साहिब"),
                    new Bani("आनंद साहिब"),
                    new Bani("तव प्रसाद सवैये"),
                    new Bani("रहिरास साहिब"),
                    new Bani("कीर्तन सोहिला"),
                    new Bani("सुखमनी साहिब"),
                    new Bani("दुख भंजनि साहिब"),
                    new Bani("अरदास")
            );
        } else if (selectedLang.equals("pa")) {
            return Arrays.asList(
                    new Bani("ਹੁਕਮਨਾਮਾ"),
                    new Bani("ਜਪੁਜੀ ਸਾਹਿਬ"),
                    new Bani("ਜਾਪ ਸਾਹਿਬ"),
                    new Bani("ਚੌਪਈ ਸਾਹਿਬ"),
                    new Bani("ਆਨੰਦ ਸਾਹਿਬ"),
                    new Bani("ਤਵ ਪ੍ਰਸਾਦ ਸਵੱਯੇ"),
                    new Bani("ਰਹਿਰਾਸ ਸਾਹਿਬ"),
                    new Bani("ਕੀਰਤਨ ਸੋਹਿਲਾ"),
                    new Bani("ਸੁਖਮਨੀ ਸਾਹਿਬ"),
                    new Bani("ਦੁੱਖ ਭੰਜਨੀ ਸਾਹਿਬ"),
                    new Bani("ਅਰਦਾਸ")
            );
        } else {
            // English default
            return Arrays.asList(
                    new Bani("Hukamnama"),
                    new Bani("Japji Sahib"),
                    new Bani("Jaap Sahib"),
                    new Bani("Chaupai Sahib"),
                    new Bani("Anand Sahib"),
                    new Bani("Tav Prasad Savaiye"),
                    new Bani("Rehras Sahib"),
                    new Bani("Kirtan Sohila"),
                    new Bani("Sukhmani Sahib"),
                    new Bani("Dukh Bhanjani Sahib"),
                    new Bani("Ardaas")
            );
        }
    }

    private void handleError(Exception e) {
        Toast.makeText(this, getString(R.string.error_occurred, e.getMessage()), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);

        try {

            if(id == R.id.nav_home){
                Intent intent = new Intent(this, LanguageSelectionActivity.class);
                startActivity(intent);
                return true;
            }

            if (id == R.id.nav_banis) {
                // Already on this page
                return true;
            }
            else if (id == R.id.nav_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("selected_language", selectedLang); // pass the language
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_about) {
                Toast.makeText(this, "About section coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_feedback || id == R.id.nav_rate) {
               openUrl("https://forms.gle/rcow4cE1Nmvcr4FR8");
                return true;
            } else if (id == R.id.nav_other_apps) {
                openUrl("https://play.google.com/store/apps/developer?id=Smart+Solutions+IT&hl=en-US");
                return true;
            } else if (id == R.id.nav_share) {
                shareApp();
                return true;
            } else if (id == R.id.nav_youtube) {
                openUrl("https://www.youtube.com/Nitnem%20Path");
                return true;
            }
            else if (id == R.id.nav_dark_mode) {
                toggleTheme();
                return true;
            }

        } catch (Exception e) {
            handleError(e);
        }

        return false;
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Check out this app: https://play.google.com/store/apps/details?id=" + getPackageName());
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_BANIS, new ArrayList<>(banis));
    }

    @Override
    protected void onResume() {
        super.onResume();
            super.onResume();
            if (adapter != null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                float fontSize = prefs.getFloat("font_size", 16f);
                adapter.setFontSize(fontSize);
                adapter.notifyDataSetChanged();
            }

        loadBanis(); // Reload in case order changed in Settings
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up if needed
    }
    private void toggleTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "Light Mode On ☀", Toast.LENGTH_SHORT).show();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putInt("theme_mode", AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, "Dark Mode On 🌙", Toast.LENGTH_SHORT).show();
        }

        editor.apply();
        recreate(); // recreate activity to apply theme change
    }


    private void updateThemeIcon() {
        NavigationView navView = findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        MenuItem themeItem = menu.findItem(R.id.nav_dark_mode);
        if (themeItem != null) {
            if (isDarkModeEnabled()) {
                themeItem.setIcon(R.drawable.light_mode);
                themeItem.setTitle("Light Mode");
            } else {
                themeItem.setIcon(R.drawable.mode_night);
                themeItem.setTitle("Dark Mode");
            }
        }

    }

    private boolean isDarkModeEnabled() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }
}

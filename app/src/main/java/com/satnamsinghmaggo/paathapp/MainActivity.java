package com.satnamsinghmaggo.paathapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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
                    new Bani("हुकमनामा", "हरमंदिर साहिब से दैनिक आदेश"),
                    new Bani("जपजी साहिब", "सुबह (3:00 AM - 6:00 AM)"),
                    new Bani("जाप साहिब", "सुबह (3:00 AM - 6:00 AM)"),
                    new Bani("चौपाई साहिब", "सुबह"),
                    new Bani("आनंद साहिब", "सुबह"),
                    new Bani("तव प्रसाद सवैये", "सुबह"),
                    new Bani("रहिरास साहिब", "शाम (6:00 PM)"),
                    new Bani("कीर्तन सोहिला", "रात (सोने से पहले)"),
                    new Bani("सुखमनी साहिब", "कभी भी"),
                    new Bani("दुख भंजनि साहिब", "कभी भी"),
                    new Bani("अरदास", "कभी भी")
            );
        } else if (selectedLang.equals("pa")) {
            return Arrays.asList(
                    new Bani("ਹੁਕਮਨਾਮਾ", "ਸ੍ਰੀ ਹਰਿਮੰਦਰ ਸਾਹਿਬ ਤੋਂ ਰੋਜ਼ਾਨਾ ਹੁਕਮ"),
                    new Bani("ਜਪੁਜੀ ਸਾਹਿਬ", "ਸਵੇਰੇ (3:00 AM - 6:00 AM)"),
                    new Bani("ਜਾਪ ਸਾਹਿਬ", "ਸਵੇਰੇ (3:00 AM - 6:00 AM)"),
                    new Bani("ਚੌਪਈ ਸਾਹਿਬ", "ਸਵੇਰੇ"),
                    new Bani("ਆਨੰਦ ਸਾਹਿਬ", "ਸਵੇਰੇ"),
                    new Bani("ਤਵ ਪ੍ਰਸਾਦ ਸਵੱਯੇ", "ਸਵੇਰੇ"),
                    new Bani("ਰਹਿਰਾਸ ਸਾਹਿਬ", "ਸ਼ਾਮ (6:00 PM)"),
                    new Bani("ਕੀਰਤਨ ਸੋਹਿਲਾ", "ਰਾਤ (ਸੁੱਤੋਂ ਪਹਿਲਾਂ)"),
                    new Bani("ਸੁਖਮਨੀ ਸਾਹਿਬ", "ਕਦੇ ਵੀ"),
                    new Bani("ਦੁੱਖ ਭੰਜਨੀ ਸਾਹਿਬ", "ਕਦੇ ਵੀ"),
                    new Bani("ਅਰਦਾਸ", "ਕਦੇ ਵੀ")
            );
        } else {
            // English default
            return Arrays.asList(
                    new Bani("Hukamnama", "Daily Order from Sri Harmandir Sahib"),
                    new Bani("Japji Sahib", "Morning (3:00 AM - 6:00 AM)"),
                    new Bani("Jaap Sahib", "Morning (3:00 AM - 6:00 AM)"),
                    new Bani("Chaupai Sahib", "Morning"),
                    new Bani("Anand Sahib", "Morning"),
                    new Bani("Tav Prasad Savaiye", "Morning"),
                    new Bani("Rehras Sahib", "Evening (6:00 PM)"),
                    new Bani("Kirtan Sohila", "Night (Before Sleep)"),
                    new Bani("Sukhmani Sahib", "Anytime"),
                    new Bani("Dukh Bhanjani Sahib", "Anytime"),
                    new Bani("Ardaas", "Anytime")
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
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("selected_language", selectedLang); // pass the language
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_about) {
                Toast.makeText(this, "About section coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_feedback || id == R.id.nav_rate) {
                openUrl("https://play.google.com/store/apps/details?id=com.smartsolution.nitnempathpro&hl=en-US");
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
}

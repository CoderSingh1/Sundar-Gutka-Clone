package com.satnamsinghmaggo.paathapp;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                if (bani.getName().equalsIgnoreCase("Hukamnama")) {
                    Intent intent = new Intent(MainActivity.this, HukamnamaActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, BaniDetailActivity.class);
                    intent.putExtra("bani_name", bani.getName());
                    startActivity(intent);
                }
            } catch (Exception e) {
                handleError(e);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadBanis() {
        List<Bani> customOrder = preferenceManager.getBaniOrder();
        if (customOrder != null) {
            banis = customOrder;
        } else {
            banis = getDefaultBaniOrder();
        }
        adapter.updateBanis(banis);
    }

    private List<Bani> getDefaultBaniOrder() {
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

    private void handleError(Exception e) {
        Toast.makeText(this, getString(R.string.error_occurred, e.getMessage()), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);

        try {

            if (id == R.id.nav_home || id == R.id.nav_banis) {
                // Already on this page
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
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
        loadBanis(); // Reload in case order changed in Settings
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up if needed
    }
}

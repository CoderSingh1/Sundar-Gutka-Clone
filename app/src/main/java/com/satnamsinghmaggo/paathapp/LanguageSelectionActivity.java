package com.satnamsinghmaggo.paathapp;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationBarMenuView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class LanguageSelectionActivity extends AppCompatActivity {
      CardView punjabiCV,hindiCV,englishCV;
//    TextView textView;
//    TextView punjabiTv;
//    TextView hindiTv;
//    TextView englishTv;
    Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_language_selection);
        punjabiCV = findViewById(R.id.punjabiCV);
        hindiCV = findViewById(R.id.hindiCV);
        englishCV = findViewById(R.id.englishCV);

        punjabiCV.setOnClickListener(v -> goToMain("pa"));
        hindiCV.setOnClickListener(v -> goToMain("hi"));
        englishCV.setOnClickListener(v -> goToMain("en"));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void goToMain(String languageCode) {
        Intent intent = new Intent(LanguageSelectionActivity.this, MainActivity.class);
        intent.putExtra("selected_language", languageCode);
        startActivity(intent);
    }

}

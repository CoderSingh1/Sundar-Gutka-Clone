package com.satnamsinghmaggo.paathapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

public class AboutUsActivity extends AppCompatActivity {

    TextView textView, textView1;
    float fontSize = App.userFontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_us);
        textView1 = findViewById(R.id.aboutus_heading);
        textView = findViewById(R.id.aboutus);
        textView.setTextSize( fontSize);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
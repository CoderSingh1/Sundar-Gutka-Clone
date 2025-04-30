package com.satnamsinghmaggo.paathapp;



import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LanguageSelectionActivity extends AppCompatActivity {
      CardView punjabiCV,hindiCV,englishCV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

      //  EdgeToEdge.enable(this);
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

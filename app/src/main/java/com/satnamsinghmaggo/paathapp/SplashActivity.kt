package com.satnamsinghmaggo.paathapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.satnamsinghmaggo.paathapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_splash)

            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 2000) // 2 seconds delay
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 
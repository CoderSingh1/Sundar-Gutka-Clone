package com.satnamsinghmaggo.paathapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.satnamsinghmaggo.paathapp.adapter.BaniAdapter
import com.satnamsinghmaggo.paathapp.model.Bani

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BaniAdapter
    private var banis: List<Bani> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupDrawer()
        setupRecyclerView()
        setupBackPressedCallback()
        loadBanis()
    }

    private fun initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupDrawer() {
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = BaniAdapter(banis) { bani ->
            try {
                val intent = Intent(this, BaniDetailActivity::class.java)
                intent.putExtra("bani_name", bani.name)
                startActivity(intent)
            } catch (e: Exception) {
                handleError(e)
            }
        }
        recyclerView.adapter = adapter
    }

    private fun loadBanis() {
        banis = listOf(
            Bani("Japji Sahib", "Morning (3:00 AM - 6:00 AM)"),
            Bani("Jaap Sahib", "Morning (3:00 AM - 6:00 AM)"),
            Bani("Chaupai Sahib", "Morning"),
            Bani("Anand Sahib", "Morning"),
            Bani("Tav Prasad Savaiye", "Morning"),
            Bani("Rehras Sahib", "Evening (6:00 PM)"),
            Bani("Kirtan Sohila", "Night (Before Sleep)"),
            Bani("Sukhmani Sahib", "Anytime"),
            Bani("Dukh Bhanjani Sahib", "Anytime"),
            Bani("Ardaas", "Anytime")
        )
        adapter.updateBanis(banis)
    }

    private fun handleError(e: Exception) {
        Toast.makeText(this, getString(R.string.error_occurred, e.message), Toast.LENGTH_LONG).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Already on home
            }
            R.id.nav_banis -> {
                // Already showing banis
            }
            R.id.nav_settings -> {
                // TODO: Open settings
            }
            R.id.nav_about -> {
                // TODO: Open about
            }
            R.id.nav_feedback -> {
                // TODO: Open feedback
            }
            R.id.nav_rate -> {
                // TODO: Open rate app
            }
            R.id.nav_other_apps -> {
                // TODO: Open other apps
            }
            R.id.nav_share -> {
                // TODO: Implement share
            }
            R.id.nav_youtube -> {
                // TODO: Open YouTube channel
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save any necessary state
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore any necessary state
    }
}
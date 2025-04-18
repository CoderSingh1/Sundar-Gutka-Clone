package com.satnamsinghmaggo.paathapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.satnamsinghmaggo.paathapp.adapter.BaniAdapter
import com.satnamsinghmaggo.paathapp.model.Bani
import com.satnamsinghmaggo.paathapp.util.BaniPreferenceManager

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BaniAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var preferenceManager: BaniPreferenceManager
    private var banis: List<Bani> = emptyList()

    companion object {
        private const val KEY_BANIS = "banis"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferenceManager = BaniPreferenceManager.getInstance(this)
        initializeViews()
        setupToolbar()
        setupDrawer()
        setupRecyclerView()
        setupBackPressedCallback()

        if (savedInstanceState == null) {
            loadBanis()
        } else {
            @Suppress("DEPRECATION")
            banis = savedInstanceState.getParcelableArrayList<Bani>(KEY_BANIS) ?: emptyList()
            adapter.updateBanis(banis)
        }
    }

    private fun initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        recyclerView = findViewById(R.id.recyclerView)
        toolbar = findViewById(R.id.toolbar)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
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
                val intent = Intent(this, BaniDetailActivity::class.java).apply {
                    putExtra("bani_name", bani.name)
                }
                startActivity(intent)
            } catch (e: Exception) {
                handleError(e)
            }
        }
        recyclerView.adapter = adapter
    }

    private fun loadBanis() {
        // Try to load custom order from preferences
        val customOrder = preferenceManager.getBaniOrder()
        banis = if (customOrder != null) {
            customOrder
        } else {
            // If no custom order exists, use default order
            getDefaultBaniOrder()
        }
        adapter.updateBanis(banis)
    }

    private fun getDefaultBaniOrder(): List<Bani> {
        return listOf(
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
    }

    private fun handleError(e: Exception) {
        Toast.makeText(this, getString(R.string.error_occurred, e.message), Toast.LENGTH_LONG).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Already on home
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_banis -> {
                // Already showing banis
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_about -> {
                Toast.makeText(this, "About section coming soon", Toast.LENGTH_SHORT).show()
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_feedback -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://play.google.com/store/apps/details?id=com.smartsolution.nitnempathpro&hl=en-US")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    handleError(e)
                }
            }
            R.id.nav_rate -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://play.google.com/store/apps/details?id=com.smartsolution.nitnempathpro&hl=en-US")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    handleError(e)
                }
            }
            R.id.nav_other_apps -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://play.google.com/store/apps/developer?id=Smart+Solutions+IT&hl=en-US")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    handleError(e)
                }
            }
            R.id.nav_share -> {
                try {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Check out this app: https://play.google.com/store/apps/details?id=$packageName")
                    }
                    startActivity(Intent.createChooser(intent, "Share via"))
                } catch (e: Exception) {
                    handleError(e)
                }
            }
            R.id.nav_youtube -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://www.youtube.com/Nitnem%20Path")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    handleError(e)
                }
            }
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(KEY_BANIS, ArrayList(banis))
    }

    override fun onResume() {
        super.onResume()
        // Reload banis in case order was changed in settings
        loadBanis()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up any resources if needed
    }
}
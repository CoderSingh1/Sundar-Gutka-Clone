package com.satnamsinghmaggo.paathapp.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.satnamsinghmaggo.paathapp.R
import com.satnamsinghmaggo.paathapp.adapter.BaniOrderAdapter
import com.satnamsinghmaggo.paathapp.adapter.BaniOrderTouchHelper
import com.satnamsinghmaggo.paathapp.model.Bani
import com.satnamsinghmaggo.paathapp.util.BaniPreferenceManager

class BaniOrderFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var resetButton: MaterialButton
    private lateinit var adapter: BaniOrderAdapter
    private lateinit var preferenceManager: BaniPreferenceManager
    private var banis: MutableList<Bani> = mutableListOf()

    companion object {
        private const val KEY_BANI_ORDER = "bani_order"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bani_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        preferenceManager = BaniPreferenceManager.getInstance(requireContext())
        
        recyclerView = view.findViewById(R.id.recyclerView)
        resetButton = view.findViewById(R.id.resetButton)

        setupRecyclerView()
        setupResetButton()
        loadBaniOrder()
    }

    private fun setupRecyclerView() {
        // Get the current order from preferences or use default order
        val currentOrder = preferenceManager.getBaniOrder() ?: getDefaultBaniOrder()
        
        adapter = BaniOrderAdapter(currentOrder) { newOrder ->
            // Save the new order to preferences
            preferenceManager.saveBaniOrder(newOrder)
        }
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Set up drag and drop
        val touchHelper = ItemTouchHelper(BaniOrderTouchHelper(adapter))
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupResetButton() {
        resetButton.setOnClickListener {
            showResetConfirmationDialog()
        }
    }

    private fun showResetConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.reset_order_title)
            .setMessage(R.string.reset_order_message)
            .setPositiveButton(R.string.reset) { _, _ ->
                resetBaniOrder()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun resetBaniOrder() {
        val defaultOrder = getDefaultBaniOrder()
        adapter.updateList(defaultOrder)
        preferenceManager.saveBaniOrder(defaultOrder)
        Toast.makeText(requireContext(), R.string.order_reset, Toast.LENGTH_SHORT).show()
    }

    private fun loadBaniOrder() {
        val savedOrder = sharedPreferences.getString(KEY_BANI_ORDER, null)
        if (savedOrder != null) {
            try {
                val baniNames = savedOrder.split(",")
                banis.clear()
                banis.addAll(baniNames.map { name ->
                    Bani(name, getDefaultBaniOrder().find { it.name == name }?.time ?: "")
                })
            } catch (e: Exception) {
                resetBaniOrder()
            }
        } else {
            resetBaniOrder()
        }
        adapter.notifyDataSetChanged()
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
} 
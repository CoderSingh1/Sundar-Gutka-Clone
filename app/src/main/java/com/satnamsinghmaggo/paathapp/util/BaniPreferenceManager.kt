package com.satnamsinghmaggo.paathapp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.satnamsinghmaggo.paathapp.model.Bani

class BaniPreferenceManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val gson = Gson()
    private val KEY_BANI_ORDER = "bani_order"

    fun saveBaniOrder(banis: List<Bani>) {
        val json = gson.toJson(banis)
        sharedPreferences.edit().putString(KEY_BANI_ORDER, json).apply()
    }

    fun getBaniOrder(): List<Bani>? {
        val json = sharedPreferences.getString(KEY_BANI_ORDER, null)
        if (json == null) return null
        
        val type = object : TypeToken<List<Bani>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            // If deserialization fails, clear the corrupted data
            sharedPreferences.edit().remove(KEY_BANI_ORDER).apply()
            null
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: BaniPreferenceManager? = null

        fun getInstance(context: Context): BaniPreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BaniPreferenceManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
} 
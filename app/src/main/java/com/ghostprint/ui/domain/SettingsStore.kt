package com.ghostprint.ui.domain

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// Extension property for DataStore
val Context.dataStore by preferencesDataStore(name = "ghostprint_settings")

class SettingsStore(private val context: Context) {
    private val keyThreshold = floatPreferencesKey("threshold")

    fun getThreshold(): Float = runBlocking {
        val prefs = context.dataStore.data.first()
        prefs[keyThreshold] ?: 1.5f
    }

    suspend fun setThreshold(value: Float) {
        context.dataStore.edit { prefs ->
            prefs[keyThreshold] = value
        }
    }
}
package com.ghostprint.ui.domain

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// Extension property for DataStore
val Context.dataStore by preferencesDataStore(name = "ghostprint_settings")

class SettingsStore(private val context: Context) {

    private val keyThreshold = floatPreferencesKey("threshold")
    private val keyConsent = booleanPreferencesKey("consent_enabled")
    private val keyPaused = booleanPreferencesKey("logging_paused")

    private val defaultThreshold = 1.5f

    /** ---- Threshold ---- **/
    fun getThreshold(): Float = runBlocking {
        val prefs = context.dataStore.data.first()
        prefs[keyThreshold] ?: defaultThreshold
    }

    suspend fun setThreshold(value: Float) {
        context.dataStore.edit { prefs ->
            prefs[keyThreshold] = value
        }
    }

    /** ---- Consent ---- **/
    fun isConsentEnabled(): Boolean = runBlocking {
        val prefs = context.dataStore.data.first()
        prefs[keyConsent] ?: false
    }

    suspend fun setConsentEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[keyConsent] = enabled
        }
    }

    /** ---- Logging Paused ---- **/
    fun isLoggingPaused(): Boolean = runBlocking {
        val prefs = context.dataStore.data.first()
        prefs[keyPaused] ?: false
    }

    suspend fun setLoggingPaused(paused: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[keyPaused] = paused
        }
    }
}
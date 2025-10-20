// domain/AnomalyDetector.kt
package com.ghostprint.ui.domain

import android.content.Context
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.abs

class AnomalyDetector(context: Context) {
    private val mutex = Mutex()
    private var lastEventTs: Long = 0L
    private var lastKeyDownTs: Long = 0L
    private var threshold: Float = SettingsStore(context).getThreshold() // e.g., 1.5f

    // Simple EWMA-based interval deviation
    private var ewmaInterval: Double = 0.0
    private var ewmaVar: Double = 0.0
    private val alpha = 0.2

    suspend fun setThreshold(value: Float) = mutex.withLock { threshold = value }

    fun onEventTimestamp(ts: Long): Boolean {
        return updateIntervalAndCheck(ts, isKey = false)
    }

    fun onKeyTimestamp(ts: Long, isDown: Boolean): Boolean {
        if (isDown) lastKeyDownTs = ts
        return updateIntervalAndCheck(ts, isKey = true)
    }

    private fun updateIntervalAndCheck(ts: Long, isKey: Boolean): Boolean {
        val prev = if (lastEventTs == 0L) ts else lastEventTs
        val interval = (ts - prev).toDouble().coerceAtLeast(1.0)
        lastEventTs = ts

        // EWMA update
        ewmaInterval = if (ewmaInterval == 0.0) interval else alpha * interval + (1 - alpha) * ewmaInterval
        val diff = interval - ewmaInterval
        ewmaVar = if (ewmaVar == 0.0) abs(diff) else alpha * abs(diff) + (1 - alpha) * ewmaVar

        // Anomaly if deviation exceeds threshold ratio
        val score = if (ewmaInterval > 0) ewmaVar / ewmaInterval else 0.0
        return score > threshold
    }
}
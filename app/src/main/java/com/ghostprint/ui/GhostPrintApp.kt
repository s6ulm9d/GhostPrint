package com.ghostprint.ui

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ghostprint.ui.work.FeatureExtractionWorker
import java.util.concurrent.TimeUnit

class GhostPrintApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val request = PeriodicWorkRequestBuilder<FeatureExtractionWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "feature_extraction",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
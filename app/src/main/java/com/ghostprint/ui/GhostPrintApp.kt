package com.ghostprint.ui

import android.app.Application
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ghostprint.ui.data.AppDatabase
import com.ghostprint.ui.work.FeatureExtractionWorker
import java.util.concurrent.TimeUnit

class GhostPrintApp : Application() {

    lateinit var db: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "ghostprint.db"
        )
            .fallbackToDestructiveMigration()
            .build()

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

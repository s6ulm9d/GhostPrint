package com.ghostprint.ui.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ghostprint.ui.data.AppDatabase

class FeatureExtractionWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)

        // TODO: load your recent touch events from db
        val events: List<TouchEvent> = emptyList() // replace with DAO call

        return try {
            val vector = FeatureExtractor().extractFeatures(events)
            // TODO: persist or send vector
            Result.success()
        } catch (t: Throwable) {
            Result.failure()
        }
    }
}
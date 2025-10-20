package com.ghostprint.ui.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ghostprint.ui.R
import com.ghostprint.ui.work.FeatureVector

private const val CHANNEL_ID = "ghostprint_alerts"

fun showAnomalyAlert(context: Context, vector: FeatureVector) {
    ensureChannel(context)

    val title = "Anomaly score: ${"%.2f".format(vector.anomalyScore)}"
    val text = "Events in window: ${vector.eventCount}"

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    NotificationManagerCompat.from(context).notify(1001, builder.build())
}

private fun ensureChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "GhostPrint Alerts"
        val desc = "Anomaly and status notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = desc
        }
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }
}
package com.ghostprint.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ghostprint.ui.R

object NotificationUtils {

    // Channel IDs (public so services can reference them)
    const val CHANNEL_ALERT_ID = "ghostprint_alerts"
    const val CHANNEL_STATUS_ID = "ghostprint_status"

    private const val CHANNEL_ALERT_NAME = "GhostPrint Alerts"
    private const val CHANNEL_STATUS_NAME = "GhostPrint Status"

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Alerts channel (for anomalies)
            val alertChannel = NotificationChannel(
                CHANNEL_ALERT_ID,
                CHANNEL_ALERT_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Anomaly detection and security alerts"
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }

            // Status channel (for foreground service)
            val statusChannel = NotificationChannel(
                CHANNEL_STATUS_ID,
                CHANNEL_STATUS_NAME,
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Background monitoring status"
                lockscreenVisibility = Notification.VISIBILITY_SECRET
            }

            nm.createNotificationChannel(alertChannel)
            nm.createNotificationChannel(statusChannel)
        }
    }

    fun showAnomaly(context: Context, title: String, message: String) {
        val notif = NotificationCompat.Builder(context, CHANNEL_ALERT_ID)
            .setSmallIcon(R.drawable.ic_notification) // ensure this exists
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notif)
    }

    fun buildStatusNotification(context: Context, title: String, message: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_STATUS_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }
}
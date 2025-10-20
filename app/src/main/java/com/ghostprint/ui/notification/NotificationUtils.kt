package com.ghostprint.ui.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ghostprint.ui.R

object NotificationUtils {
    const val CHANNEL_STATUS_ID = "ghostprint_status"
    const val CHANNEL_ANOMALY_ID = "ghostprint_anomaly"
    private const val ANOMALY_NOTIFICATION_ID = 2001

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val status = NotificationChannel(
                CHANNEL_STATUS_ID,
                "GhostPrint Status",
                NotificationManager.IMPORTANCE_MIN
            ).apply { lockscreenVisibility = Notification.VISIBILITY_SECRET }

            val anomaly = NotificationChannel(
                CHANNEL_ANOMALY_ID,
                "GhostPrint Anomalies",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { lockscreenVisibility = Notification.VISIBILITY_PRIVATE }

            nm.createNotificationChannel(status)
            nm.createNotificationChannel(anomaly)
        }
    }

    fun showAnomaly(context: Context, title: String, message: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notif = NotificationCompat.Builder(context, CHANNEL_ANOMALY_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        nm.notify(ANOMALY_NOTIFICATION_ID, notif)
    }
}
package com.ghostprint.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ghostprint.ui.R
import com.ghostprint.ui.notify.Channels

/**
 * Foreground service that keeps GhostPrint alive while monitoring.
 * It shows a persistent notification so the system does not kill it.
 */
class LoggerForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Ensure notification channels exist
        Channels.ensure(this)

        val notif = NotificationCompat.Builder(this, Channels.LOGGING)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("GhostPrint running")
            .setContentText("Monitoring with your consent")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        // Start in foreground mode
        startForeground(1001, notif)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
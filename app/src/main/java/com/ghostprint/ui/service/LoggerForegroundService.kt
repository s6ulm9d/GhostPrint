package com.ghostprint.ui.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ghostprint.ui.R

class LoggerForegroundService : Service() {
    private val channelId = "ghostprint_status"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(channelId, "GhostPrint Status", NotificationManager.IMPORTANCE_MIN)
        )

        val notif = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("GhostPrint running")
            .setContentText("Monitoring with your consent")
            .setOngoing(true)
            .build()

        startForeground(1001, notif)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
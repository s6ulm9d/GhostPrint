package com.ghostprint.ui.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ghostprint.ui.R
import com.ghostprint.ui.notification.NotificationUtils

class LoggerForegroundService : Service() {
    private val channelId = NotificationUtils.CHANNEL_STATUS_ID

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ensureChannel()
        val notif = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("GhostPrint running")
            .setContentText("Monitoring with your consent")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        startForeground(1001, notif)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(
                channelId,
                "GhostPrint Status",
                NotificationManager.IMPORTANCE_MIN
            ).apply { lockscreenVisibility = Notification.VISIBILITY_SECRET }
            nm.createNotificationChannel(ch)
        }
    }
}